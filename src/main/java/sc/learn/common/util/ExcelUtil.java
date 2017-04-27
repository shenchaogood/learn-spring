package sc.learn.common.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.WorkbookUtil;

public abstract class ExcelUtil {

	/**
	 * 按指定区域画出边框，0 based
	 * 
	 * @param sheet
	 *            当前页
	 * @param color
	 *            边框颜色
	 * @param borderStyle
	 *            边框样式
	 * @param startRow
	 *            起始行（包含）
	 * @param endRow
	 *            结束行（不包含）
	 * @param startColumn
	 *            起始列（包含）
	 * @param endColumn
	 *            结束列（不包含）
	 */
	public static void drawBorder(Sheet sheet, short color, BorderStyle borderStyle, int startRow, int endRow, int startColumn, int endColumn) {
		Objects.requireNonNull(sheet);
		if (startRow >= endRow || endRow <= 0) {
			throw new IllegalArgumentException("应该 startRow < endRow");
		}
		if (startColumn >= endColumn || endColumn <= 0) {
			throw new IllegalArgumentException("应该 startColumn < endColumn");
		}

		for (int rowNum = startRow; rowNum < endRow; rowNum++) {
			Row r = sheet.getRow(rowNum);
			if (r == null) {
				r = sheet.createRow(rowNum);
			}
			for (int cn = 0; cn < endColumn; cn++) {
				Cell c = r.getCell(cn);
				if (c == null) {
					c = CellUtil.createCell(r, cn, "");
				}

				if (cn == 0) {
					CellUtil.setCellStyleProperty(c, CellUtil.BORDER_LEFT, borderStyle);
					CellUtil.setCellStyleProperty(c, CellUtil.LEFT_BORDER_COLOR, color);
				}
				if (cn == endColumn - 1) {
					CellUtil.setCellStyleProperty(c, CellUtil.BORDER_RIGHT, borderStyle);
					CellUtil.setCellStyleProperty(c, CellUtil.RIGHT_BORDER_COLOR, color);
				}
				if (rowNum == 0) {
					CellUtil.setCellStyleProperty(c, CellUtil.BORDER_TOP, borderStyle);
					CellUtil.setCellStyleProperty(c, CellUtil.TOP_BORDER_COLOR, color);
				}
				if (rowNum == endRow - 1) {
					CellUtil.setCellStyleProperty(c, CellUtil.BORDER_BOTTOM, borderStyle);
					CellUtil.setCellStyleProperty(c, CellUtil.BOTTOM_BORDER_COLOR, color);
				}
			}
		}
	}

	/**
	 * 按给定属性初始化单元格，0 based
	 * 
	 * @param sheet
	 *            当前页
	 * @param properties
	 *            明确的属性
	 * @param startRow
	 *            起始行（包含）
	 * @param endRow
	 *            结束行（不包含）
	 * @param startColumn
	 *            起始列（包含）
	 * @param endColumn
	 *            结束列（不包含）
	 */
	public static void initCell(Sheet sheet, Map<String, Object> properties, int startRow, int endRow, int startColumn, int endColumn) {
		Objects.requireNonNull(sheet);
		Objects.requireNonNull(properties);
		if (startRow >= endRow || endRow <= 0) {
			throw new IllegalArgumentException("应该 startRow < endRow");
		}
		if (startColumn >= endColumn || endColumn <= 0) {
			throw new IllegalArgumentException("应该 startColumn < endColumn");
		}

		for (int rowNum = startRow; rowNum < endRow; rowNum++) {
			Row r = sheet.getRow(rowNum);
			if (r == null) {
				r = sheet.createRow(rowNum);
			}
			for (int cn = 0; cn < endColumn; cn++) {
				Cell c = r.getCell(cn);
				if (c == null) {
					c = CellUtil.createCell(r, cn, "");
				}
				for (Entry<String, Object> property : properties.entrySet()) {
					CellUtil.setCellStyleProperty(c, property.getKey(), property.getValue());
				}
			}
		}
	}

	/**
	 * 按坐标coordinates 合并单元格，0 based
	 * 
	 * @param sheet
	 *            当前页
	 * @param coordinates
	 *            坐标规则[firstRow(包含),lastRow（不包含）, firstCol（包含）, lastCol（不包含）]
	 * @throws IllegalAccessException
	 */
	public static void mergeCell(Sheet sheet, int[][] coordinates) {
		Objects.requireNonNull(sheet);
		Objects.requireNonNull(coordinates);
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].length != 4) {
				throw new IllegalArgumentException("coordinates参数错误");
			}
			if (coordinates[i][0] >= coordinates[i][1] || coordinates[i][1] <= 0) {
				throw new IllegalArgumentException("firstRow < lastRow");
			}
			if (coordinates[i][2] >= coordinates[i][3] || coordinates[i][3] <= 0) {
				throw new IllegalArgumentException("firstCol < lastCol");
			}
			sheet.addMergedRegion(new CellRangeAddress(coordinates[i][0], coordinates[i][1] - 1, coordinates[i][2], coordinates[i][3] - 1));
		}
	}

	/**
	 * 修改单元格边框样式
	 * 
	 * @param cell
	 *            被修改的单元格
	 * @param borderStyle
	 *            新边框样式
	 */
	public static void aroundBorder(Cell cell, BorderStyle borderStyle) {
		Objects.requireNonNull(cell);
		CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_TOP, borderStyle);
		CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_RIGHT, borderStyle);
		CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_BOTTOM, borderStyle);
		CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_LEFT, borderStyle);
	}

	/**
	 * 创建制定位置的单元格， 0 based
	 * 
	 * @param sheet
	 *            所属页
	 * @param rowIndex
	 *            行索引
	 * @param colIndex
	 *            列索引
	 * @param horizontalAlign
	 *            水平对其方式
	 * @param value
	 *            单元格文本内容
	 * @return 新单元格
	 */
	public static Cell createCell(Sheet sheet, int rowIndex, int colIndex, HorizontalAlignment horizontalAlign, String value) {
		Objects.requireNonNull(sheet);
		if (rowIndex < 0 || colIndex < 0) {
			throw new IllegalArgumentException("应该 rowIndex>=0 && colIndex>=0");
		}

		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}
		Cell cell = CellUtil.createCell(row, colIndex, value);
		CellUtil.setAlignment(cell, horizontalAlign);
		return cell;
	}

	/**
	 * Create a new sheet for this Workbook and return the high level
	 * representation. Use this to create new sheets.
	 * 
	 * @param wb
	 *            High level representation of a Excel workbook
	 * @param sheetName
	 *            sheet页名字
	 * @param defaultColumnWidth
	 *            default column width measured in characters
	 * @param defaultRowHeight
	 *            default row height measured in twips (1/20 of a point)
	 * @return Sheet representing the new sheet.
	 */
	public static Sheet createSheet(Workbook wb, String sheetName, int defaultColumnWidth, int defaultRowHeight) {
		Objects.requireNonNull(wb);
		Objects.requireNonNull(sheetName);
		if (defaultColumnWidth <= 0 || defaultRowHeight <= 0) {
			throw new IllegalArgumentException("应该 defaultColumnWidth>0 && defaultRowHeight>0");
		}
		Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(sheetName));
		sheet.setDefaultRowHeight((short) defaultRowHeight);
		sheet.setDefaultColumnWidth(defaultColumnWidth);
		return sheet;
	}

	/**
	 * 创建excel列表
	 * @param sheet
	 * @param lines
	 * @param startRowIndex 开始行索引 0based
	 * @param startColumnIndex 开始列索引 0based
	 * @return
	 */
	public static List<List<Cell>> createList(Sheet sheet, List<List<CellHolder>> lines, int startRowIndex, int startColumnIndex) {
		List<List<Cell>> newLines=new LinkedList<>();
		for (int i = 0; i < lines.size(); i++) {
			List<CellHolder> row = lines.get(i);
			List<Cell> line=new LinkedList<>();
			for (int j = 0; j < row.size(); j++) {
				CellHolder holder = row.get(j);
				Cell cell = createCell(sheet, startRowIndex + i, startColumnIndex + j, holder.horizontalAlignment, holder.value);
				Font font = sheet.getWorkbook().createFont();
				font.setFontHeight((short) holder.fontSize);
				font.setColor((short) holder.fontColor);
				font.setBold(holder.fontBold);
				CellUtil.setCellStyleProperty(cell, CellUtil.FONT, font);
				line.add(cell);
			}
			newLines.add(line);
		}
		return newLines;
	}

	public static class CellHolder {
		public final String value;
		public final HorizontalAlignment horizontalAlignment;
		public final int fontSize;
		public final int fontColor;
		public final boolean fontBold;
		/**
		 * excel 列表单元格参数
		 * @param value 内容
		 * @param horizontalAlignment HorizontalAlignment枚举
		 * @param fontSize 字体大小
		 * @param fontColor  HSSFColor.index
		 * @param fontBold   粗体
		 */
		public CellHolder(String value, HorizontalAlignment horizontalAlignment, int fontSize, int fontColor, boolean fontBold) {
			this.value = value;
			this.horizontalAlignment = horizontalAlignment;
			this.fontSize = fontSize;
			this.fontColor = fontColor;
			this.fontBold = fontBold;
		}
	}
}
