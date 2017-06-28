package sc.learn.test.common;

import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

public class TestHadoop {
	
	@Test
	public void testPutMerge() throws Exception{
		Configuration conf=new Configuration();
		FileSystem hdfs=FileSystem.get(URI.create("hdfs://localhost:9000/"),conf);
		FileSystem local=FileSystem.getLocal(conf);
		
		for(FileStatus fileStatus:local.listStatus(new Path("/home/shenchao3"))){
			System.out.println(fileStatus.getPath().getName());
		}
		for(FileStatus fileStatus:hdfs.listStatus(new Path("/"))){
			System.out.println(fileStatus.getPath().getName());
		}
	}

}
