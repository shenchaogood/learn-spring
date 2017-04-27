package sc.learn.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class PdfUtil {
	
	// 默认字体大小
	public final static int DEFAULT_FONT_SIZE = 6;
	

	/**
	 * 创建一个可读写的Document
	 * 
	 * @param os
	 *            pdf文件
	 * @param rectangle
	 *            文件大小
	 * @return 创建一个可读写的Document
	 * @throws DocumentException
	 *             on error
	 */
	public static Document createDocument(OutputStream os, Rectangle rectangle) throws DocumentException {
		if (rectangle == null) {
			rectangle = PageSize.A4;
		}
		Document document = new Document(rectangle);
		PdfWriter.getInstance(document, os);
		document.open();
		return document;
	}

	/**
	 * 创建一个表格并追加到文档中
	 * 
	 * @param widthPercentage
	 *            宽度站整个页面宽的的百分比
	 * @param relativeWidths
	 *            数组和为页面宽度的百分比
	 * @return PDF表格
	 * @throws DocumentException
	 *             文档没有打开或已经关闭
	 */
	public static PdfPTable createTable(final float widthPercentage, final float[] relativeWidths) throws DocumentException {
		PdfPTable table = new PdfPTable(relativeWidths);
		table.setWidthPercentage(widthPercentage);
		table.getDefaultCell().setUseAscender(true);
		table.getDefaultCell().setUseDescender(true);
		return table;
	}

	/**
	 * 创建一个单元格并追加到表格中
	 * 
	 * @param text
	 *            单元格文本内容
	 * @param horizontalAlignment
	 *            水平对齐方式 eg：Element.ALIGN_CENTER
	 * @param font
	 *            字体
	 * @return 新单元格
	 * @throws DocumentException
	 */
	public static PdfPCell createCell(String text, int horizontalAlignment, Font font) {
		// 否则此单元格不会被添加上
		if (StringUtils.isBlank(text)) {
			text = " ";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(horizontalAlignment);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		return cell;
	}
	
	/**
	 * 更新表格单元格
	 * @param cell 被更新的单元格
	 * @param text 新内容
	 * @param horizontalAlignment 水平对齐方式
	 * @param font 字体
	 * @return 被更新的单元格
	 */
	public static PdfPCell modifyCell(PdfPCell cell,String text, int horizontalAlignment, Font font){
		cell.getColumn().setText(new Phrase(text, font));
		cell.setHorizontalAlignment(horizontalAlignment);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		return cell;
	}
	

	/**
	 * 创建一个支持中文的字体
	 * 
	 * @param color
	 *            颜色 null为默认颜色
	 * @param size
	 *            大小 -1默认大小
	 * @param style
	 *            风格 eg Font.BOLD -1默认风格
	 * @return 新字体
	 */
	public static Font createChineseFont(BaseColor color, double size, int style) {
		try {
			Font f = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
			f.setColor(color);
			f.setSize((float) size);
			f.setStyle(style);
			return f;
		} catch (DocumentException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按坐标coordinates 合并单元格，0 based
	 * 
	 * @param row
	 *            几行
	 * @param col
	 *            几列
	 * @param sheet
	 *            当前页
	 * @param coordinates
	 *            坐标规则[firstRow(包含),lastRow（不包含）, firstCol（包含）, lastCol（不包含）]
	 */
	public static List<List<PdfPCell>> createAndMergeCell(int row, int col, int[][] coordinates){
		List<List<PdfPCell>> lines = new LinkedList<>();
		for (int i = 0; i < row; i++) {
			List<PdfPCell> line=new LinkedList<>();;
			for (int j = 0; j < col; j++) {
				PdfPCell cell=createCell("", Element.ALIGN_CENTER, PdfUtil.createChineseFont(null, DEFAULT_FONT_SIZE, Font.NORMAL));	
				line.add(cell);
			}
			lines.add(line);
		}
		
		for(int i=0;i<coordinates.length;i++){
			for(int j=coordinates[i][0];j<coordinates[i][1];j++){
				for(int m=coordinates[i][2];m<coordinates[i][3]-1;m++){
					lines.get(j).remove(0);
				}
			}
			PdfPCell cell=lines.get(coordinates[i][0]).get(coordinates[i][2]);
			if(coordinates[i][3]-coordinates[i][2]>1){
				cell.setColspan(coordinates[i][3]-coordinates[i][2]);
			}
			if(coordinates[i][1]-coordinates[i][0]>1){
				cell.setRowspan(coordinates[i][1]-coordinates[i][0]);
			}
		}
		return lines;
	}
}
