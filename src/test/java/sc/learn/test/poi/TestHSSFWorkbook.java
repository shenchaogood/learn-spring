package sc.learn.test.poi;

import static sc.learn.common.util.ExcelUtil.aroundBorder;
import static sc.learn.common.util.ExcelUtil.createCell;
import static sc.learn.common.util.ExcelUtil.drawBorder;
import static sc.learn.common.util.ExcelUtil.initCell;
import static sc.learn.common.util.ExcelUtil.mergeCell;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.junit.Test;

public class TestHSSFWorkbook {
	
	@Test
	public void genExcel() throws Exception {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("xxxxx确认书");
		sheet.setDefaultRowHeight((short) 500);
		sheet.setDefaultColumnWidth(16);
		sheet.setAutobreaks(true);
		sheet.setDisplayGridlines(true);

		int[][] coordinates={
				{0, 1, 0, 10},	//1-a
				{1, 2, 0, 10},	//2-a
				{2, 3, 0, 10},	//3-a
				{3, 4, 0, 10},	//4-a
				{4, 5, 0, 10},	//5-a
				{9, 10, 2, 10},	//10-c
				{10, 11, 2, 10},//11-c
				{15, 16, 2, 10},//16-c
				{16, 17, 2, 10} //17-c
		};
		mergeCell(sheet,coordinates);
		
		Cell titleCell=createCell(sheet, 0, 0, HorizontalAlignment.CENTER ,"个人xxxxxx确认书");
		HSSFRichTextString titleContent = new HSSFRichTextString(titleCell.getStringCellValue());
        Font tilteFont = wb.createFont();
        tilteFont.setBold(true);
        titleContent.applyFont(tilteFont);
        titleCell.setCellValue(titleContent);
		
		createCell(sheet, 1, 0, HorizontalAlignment.CENTER, "XXXX年XX月XX日");
		createCell(sheet, 3, 0, HorizontalAlignment.LEFT, "致xxxxx有限公司：");

		Cell cell_5_A=createCell(sheet, 4, 0, HorizontalAlignment.LEFT, "XXXX个人项目XXX期（项目名称）");
		HSSFRichTextString text = new HSSFRichTextString(cell_5_A.getStringCellValue());
		Font font = wb.createFont();
		font.setColor(Font.COLOR_RED);
		text.applyFont(text.getString().length() - 1 - 5, text.getString().length(), font);
		cell_5_A.setCellValue(text);
		
		String[] row6Contexts={"Q","W","E","R","T","Y","U","I","I","P"};
		for(int i=0;i<row6Contexts.length;i++){
			aroundBorder(createCell(sheet, 5, i, HorizontalAlignment.CENTER, row6Contexts[i]),BorderStyle.THIN);
		}
		String[] row7Contexts={"A","B","C","","D","","","","",""};
		for(int i=0;i<row7Contexts.length;i++){
			aroundBorder(createCell(sheet, 6, i, HorizontalAlignment.CENTER, row7Contexts[i]),BorderStyle.THIN);
		}

		createCell(sheet, 9, 2,  HorizontalAlignment.RIGHT, "XXXX公司（公章/财务章/业务章）");
		createCell(sheet, 10, 2, HorizontalAlignment.RIGHT, "XXXX年XX月XX日");
		createCell(sheet, 11, 0, HorizontalAlignment.LEFT, "备注：");
		createCell(sheet, 12, 0, HorizontalAlignment.LEFT, "渤xxxx数据进行确认，数据无误。");
		createCell(sheet, 13, 1, HorizontalAlignment.LEFT, "复核：");
		createCell(sheet, 13, 4, HorizontalAlignment.LEFT, "经办：");
		createCell(sheet, 15, 2, HorizontalAlignment.RIGHT, "xxxx事业部（业务专用章）");
		createCell(sheet, 16, 2, HorizontalAlignment.RIGHT, "XXXX年XX月XX日");

		Map<String,Object> properties=new HashMap<>();
		properties.put(CellUtil.VERTICAL_ALIGNMENT, VerticalAlignment.CENTER);
		properties.put(CellUtil.WRAP_TEXT, false);
		initCell(sheet, properties, 0, sheet.getLastRowNum()+1, 0, 10);
		
		drawBorder(sheet, HSSFColorPredefined.DARK_BLUE.getIndex(), BorderStyle.THICK, 0, sheet.getLastRowNum()+1, 0, 10);

		try(OutputStream os = new FileOutputStream("ZJ.xls");){
			wb.write(os);
			wb.close();
		}
		
	}
}
