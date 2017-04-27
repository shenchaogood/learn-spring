package sc.learn.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public abstract class SFTPUtil {

	/**
	 * 构造基于密码认证的sftp对象
	 * 
	 * @param userName
	 * @param password
	 * @param host
	 * @param port
	 */
	public static SFTPClient createSFTPClient(String username, String password, String host, int port) {
		return new SFTPClient(username, password, host, port);
	}

	/**
	 * 构造基于秘钥认证的sftp对象
	 * 
	 * @param userName
	 * @param host
	 * @param port
	 * @param keyFilePath
	 */
	public static SFTPClient createSFTPClient(String username, String host, int port, String keyFilePath) {
		return new SFTPClient(username, host, port, keyFilePath);
	}

	/**
	 * sftp工具。注意：构造方法有两个：分别是基于密码认证、基于秘钥认证。
	 * 
	 */
	public static class SFTPClient {
		private final transient Logger log = LoggerFactory.getLogger(this.getClass());

		private ChannelSftp sftp;

		private Session session;
		/** FTP 登录用户名 */
		private String username;
		/** FTP 登录密码 */
		private String password;
		/** 私钥文件的路径 */
		private String keyFilePath;
		/** FTP 服务器地址IP地址 */
		private String host;
		/** FTP 端口 */
		private int port;

		/**
		 * 构造基于密码认证的sftp对象
		 * 
		 * @param userName
		 * @param password
		 * @param host
		 * @param port
		 */
		private SFTPClient(String username, String password, String host, int port) {
			this.username = username;
			this.password = password;
			this.host = host;
			this.port = port;
		}

		/**
		 * 构造基于秘钥认证的sftp对象
		 * 
		 * @param userName
		 * @param host
		 * @param port
		 * @param keyFilePath
		 */
		private SFTPClient(String username, String host, int port, String keyFilePath) {
			this.username = username;
			this.host = host;
			this.port = port;
			this.keyFilePath = keyFilePath;
		}

		/**
		 * 连接sftp服务器
		 * 
		 * @throws JSchException
		 */
		public void login() throws JSchException {
			JSch jsch = new JSch();
			if (StringUtils.isNotBlank(keyFilePath)) {
				jsch.addIdentity(keyFilePath);// 设置私钥
				log.info("sftp connect,path of private key file：{}", keyFilePath);
			}
			log.info("sftp connect by host:{} username:{}", host, username);

			session = jsch.getSession(username, host, port);
			log.info("Session is build");
			if (StringUtils.isNotBlank(password)) {
				session.setPassword(password);
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();
			log.info("Session is connected");

			Channel channel = session.openChannel("sftp");
			channel.connect();
			log.info("channel is connected");

			sftp = (ChannelSftp) channel;
			log.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));
		}

		/**
		 * 关闭连接 server
		 */
		public void logout() {
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
					log.info("sftp is closed");
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
					log.info("sshSession is closed");
				}
			}
		}

		/**
		 * 将输入流的数据上传到sftp作为文件
		 * 
		 * @param directory
		 *            上传到该目录
		 * @param sftpFileName
		 *            sftp端文件名
		 * @param in
		 *            输入流
		 * @throws SftpException
		 * @throws Exception
		 */
		public void upload(String directory, String sftpFileName, InputStream input) throws SftpException {
			try {
				sftp.cd(directory);
			} catch (SftpException e) {
				log.warn("directory is not exist create it "+directory);
				if(directory.startsWith(File.separator)){
					sftp.cd("/");
					directory=directory.substring(1);
				}
				if(directory.endsWith(File.separator)){
					directory=directory.substring(0, directory.length()-1);
				}
				String[] dirs=directory.split(File.separator);
				for(String dir:dirs){
					try{
						sftp.cd(dir);
					}catch(SftpException e1){
						sftp.mkdir(dir);
						sftp.cd(dir);
					}
				}
			}
			sftp.put(input, sftpFileName);
			log.info("file:{} is upload successful", sftpFileName);
		}

		/**
		 * 上传单个文件
		 * 
		 * @param directory
		 *            上传到sftp目录
		 * @param uploadFile
		 *            要上传的文件,包括路径
		 * @throws FileNotFoundException
		 * @throws SftpException
		 */
		public void upload(String directory, String uploadFile) throws IOException, SftpException {
			File file = new File(uploadFile);
			try (InputStream is = new FileInputStream(file);) {
				upload(directory, file.getName(), is);
			}

		}

		/**
		 * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
		 * 
		 * @param directory
		 *            上传到sftp目录
		 * @param sftpFileName
		 *            文件在sftp端的命名
		 * @param byteArr
		 *            要上传的字节数组
		 * @throws SftpException
		 * @throws IOException
		 */
		public void upload(String directory, String sftpFileName, byte[] byteArr) throws SftpException, IOException {
			try (InputStream is = new ByteArrayInputStream(byteArr);) {
				upload(directory, sftpFileName, is);
			}
		}

		/**
		 * 将字符串按照指定的字符编码上传到sftp
		 * 
		 * @param directory
		 *            上传到sftp目录
		 * @param sftpFileName
		 *            文件在sftp端的命名
		 * @param dataStr
		 *            待上传的数据
		 * @param charsetName
		 *            sftp上的文件，按该字符编码保存
		 * @throws UnsupportedEncodingException
		 * @throws SftpException
		 * @throws IOException
		 */
		public void upload(String directory, String sftpFileName, String dataStr, String charsetName)
				throws IOException, UnsupportedEncodingException, SftpException {
			try (InputStream is = new ByteArrayInputStream(dataStr.getBytes(charsetName));) {
				upload(directory, sftpFileName, is);
			}
		}

		/**
		 * 下载文件
		 * 
		 * @param directory
		 *            下载目录
		 * @param downloadFile
		 *            下载的文件
		 * @param saveFile
		 *            存在本地的路径
		 * @throws SftpException
		 * @throws FileNotFoundException
		 * @throws Exception
		 */
		public void download(String directory, String downloadFile, String saveFile) throws SftpException, IOException {
			if (directory != null && !"".equals(directory)) {
				sftp.cd(directory);
			}
			File file = new File(saveFile);
			try (OutputStream os = new FileOutputStream(file);) {
				sftp.get(downloadFile, os);
				log.info("file:{} is download successful", downloadFile);
			}
		}

		/**
		 * 下载文件
		 * 
		 * @param directory
		 *            下载目录
		 * @param downloadFile
		 *            下载的文件名
		 * @return 字节数组
		 * @throws SftpException
		 * @throws IOException
		 * @throws Exception
		 */
		public byte[] download(String directory, String downloadFile) throws SftpException, IOException {
			if (directory != null && !"".equals(directory)) {
				sftp.cd(directory);
			}
			try (InputStream is = sftp.get(downloadFile);) {
				byte[] fileData = IOUtils.toByteArray(is);

				log.info("file:{} is download successful", downloadFile);
				return fileData;
			}
		}

		/**
		 * 删除文件
		 * 
		 * @param directory
		 *            要删除文件所在目录
		 * @param deleteFile
		 *            要删除的文件
		 * @throws SftpException
		 * @throws Exception
		 */
		public void delete(String directory, String deleteFile) throws SftpException {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		}

		/**
		 * 列出目录下的文件
		 * 
		 * @param directory
		 *            要列出的目录
		 * @param sftp
		 * @return
		 * @throws SftpException
		 */
		public Vector<?> listFiles(String directory) throws SftpException {
			return sftp.ls(directory);
		}
	}
}
