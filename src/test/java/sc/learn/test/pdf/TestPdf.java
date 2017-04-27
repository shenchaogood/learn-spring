package sc.learn.test.pdf;

import java.io.FileOutputStream;
import java.util.List;

import org.junit.Test;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import sc.learn.common.util.PdfUtil;

public class TestPdf {

	@Test
	public void createPdf1() throws Exception {
		String dest = "chapter_title.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
		Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
		Chunk chunk = new Chunk("This is the title", chapterFont);
		Chapter chapter = new Chapter(new Paragraph(chunk), 1);
		chapter.setNumberDepth(0);
		chapter.add(new Paragraph("This is the paragraph", paragraphFont));
		document.add(chapter);
		document.close();
	}

	@Test
	public void createPdf2() throws Exception {
		String dest = "chunk_background.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		Font f = new Font(FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.WHITE);
		Chunk c = new Chunk("White text on red background", f);
		c.setBackground(BaseColor.RED);
		Paragraph p = new Paragraph(c);
		document.add(p);
		document.close();
	}

	@Test
	public void createPdf3() throws Exception {
		String dest = "sub_superscript.pdf";
		String font = "resources/fonts/Cardo-Regular.ttf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		BaseFont bf = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font f = new Font(bf, 10);
		Paragraph p = new Paragraph("H\u2082SO\u2074", f);
		document.add(p);
		document.close();
	}

	@Test
	public void createPdf4() throws Exception {
		String dest = "ordinal_numbers.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		Font small = new Font(FontFamily.HELVETICA, 6);
		Chunk st = new Chunk("st", small);
		st.setTextRise(7);
		Chunk nd = new Chunk("nd", small);
		nd.setTextRise(7);
		Chunk rd = new Chunk("rd", small);
		rd.setTextRise(7);
		Chunk th = new Chunk("th", small);
		th.setTextRise(7);
		Paragraph first = new Paragraph();
		first.add("The 1");
		first.add(st);
		first.add(" of May");
		document.add(first);
		Paragraph second = new Paragraph();
		second.add("The 2");
		second.add(nd);
		second.add(" and the 3");
		second.add(rd);
		second.add(" of June");
		document.add(second);
		Paragraph fourth = new Paragraph();
		fourth.add("The 4");
		fourth.add(rd);
		fourth.add(" of July");
		document.add(fourth);
		document.close();
	}

	@Test
	public void createPdf5() throws Exception {
		String dest = "standard_deviation.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		document.add(new Paragraph("The standard deviation symbol doesn't exist in Helvetica."));
		Font symbol = new Font(FontFamily.SYMBOL);
		Paragraph p = new Paragraph("So we use the Symbol font: ");
		p.add(new Chunk("s", symbol));
		document.add(p);
		document.close();
	}

	@Test
	public void createPdf6() throws Exception {
		String dest = "bullets.pdf";
		String[] items = { "Insurance system", "Agent", "Agency", "Agent Enrollment", "Agent Settings", "Appointment", "Continuing Education", "Hierarchy",
				"Recruiting", "Contract", "Message", "Correspondence", "Licensing", "Party" };
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 8);
		Font font = new Font();
		Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);

		Paragraph p = new Paragraph("Items can be split if they don't fit at the end: ", font);
		for (String item : items) {
			p.add(bullet);
			p.add(new Phrase(" " + item + " ", font));
		}
		document.add(p);
		document.add(Chunk.NEWLINE);

		p = new Paragraph("Items can't be split if they don't fit at the end: ", font);
		for (String item : items) {
			p.add(bullet);
			p.add(new Phrase("\u00a0" + item.replace(' ', '\u00a0') + " ", font));
		}
		document.add(p);
		document.add(Chunk.NEWLINE);

		BaseFont bf = BaseFont.createFont("resources/fonts/FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font f = new Font(bf, 12);
		p = new Paragraph("Items can't be split if they don't fit at the end: ", f);
		for (String item : items) {
			p.add(new Phrase("\u2022\u00a0" + item.replace(' ', '\u00a0') + " ", f));
		}
		document.add(p);

		document.close();
	}

	@Test
	public void createPdf7() throws Exception {
		String dest = "list_alignment.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		String text = "test 1 2 3 ";
		for (int i = 0; i < 5; i++) {
			text = text + text;
		}
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
		ListItem item = new ListItem(text);
		item.setAlignment(Element.ALIGN_JUSTIFIED);
		list.add(item);
		text = "a b c align ";
		for (int i = 0; i < 5; i++) {
			text = text + text;
		}
		item = new ListItem(text);
		item.setAlignment(Element.ALIGN_JUSTIFIED);
		list.add(item);
		text = "supercalifragilisticexpialidocious ";
		for (int i = 0; i < 3; i++) {
			text = text + text;
		}
		item = new ListItem(text);
		item.setAlignment(Element.ALIGN_JUSTIFIED);
		list.add(item);
		document.add(list);
		document.close();
	}

	@Test
	public void createPdf8() throws Exception {
		String dest = "simple_table.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		PdfPTable table = new PdfPTable(8);
		for (int aw = 0; aw < 16; aw++) {
			table.addCell("hi");
		}
		document.add(table);
		document.close();
	}

	@Test
	public void createPdf9() throws Exception {
		String dest = "small_table.pdf";
		Rectangle small = new Rectangle(290, 100);
		Font smallfont = new Font(FontFamily.HELVETICA, 10);
		Document document = new Document(small, 5, 5, 5, 5);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(new float[] { 160, 120 });
		table.setLockedWidth(true);
		PdfContentByte cb = writer.getDirectContent();
		// first row
		PdfPCell cell = new PdfPCell(new Phrase("Some text here"));
		cell.setFixedHeight(30);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(2);
		table.addCell(cell);
		// second row
		cell = new PdfPCell(new Phrase("Some more text", smallfont));
		cell.setFixedHeight(30);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		Barcode128 code128 = new Barcode128();
		code128.setCode("14785236987541");
		code128.setCodeType(Barcode128.CODE128);
		Image code128Image = code128.createImageWithBarcode(cb, null, null);
		cell = new PdfPCell(code128Image, true);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setFixedHeight(30);
		table.addCell(cell);
		// third row
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("and something else here", smallfont));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		document.add(table);
		document.close();

	}

	@Test
	public void createPdf10() throws Exception {
		class ImageBackgroundEvent implements PdfPCellEvent {

			protected Image image;

			public ImageBackgroundEvent(Image image) {
				this.image = image;
			}

			public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
				try {
					PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
					image.scaleAbsolute(position);
					image.setAbsolutePosition(position.getLeft(), position.getBottom());
					cb.addImage(image);
				} catch (DocumentException e) {
					throw new ExceptionConverter(e);
				}
			}

		}

		String dest = "imagebackground.pdf";
		String IMG1 = "resources/images/bruno.jpg";

		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(400);
		table.setLockedWidth(true);
		PdfPCell cell = new PdfPCell();
		Font font = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
		Paragraph p = new Paragraph("A cell with an image as background color.", font);
		cell.addElement(p);
		Image image = Image.getInstance(IMG1);
		cell.setCellEvent(new ImageBackgroundEvent(image));
		cell.setFixedHeight(600 * image.getScaledHeight() / image.getScaledWidth());
		table.addCell(cell);
		document.add(table);
		document.close();
	}

	@Test
	public void createPdf11() throws Exception {
		final String dest = "splitting_and_rowspan.pdf";
		Document document = new Document(new Rectangle(300, 150));
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		document.add(new Paragraph("Table with setSplitLate(true):"));
		PdfPTable table = new PdfPTable(2);
		table.setSpacingBefore(10);
		PdfPCell cell = new PdfPCell();
		cell.addElement(new Paragraph("G"));
		cell.addElement(new Paragraph("R"));
		cell.addElement(new Paragraph("P"));
		cell.setRowspan(3);
		table.addCell(cell);
		table.addCell("row 1");
		table.addCell("row 2");
		table.addCell("row 3");
		document.add(table);
		document.add(new Paragraph("Table with setSplitLate(false):"));
		table.setSplitLate(false);
		document.add(table);
		document.close();
	}

	@Test
	public void createPdf12() throws Exception {
		final String dest = "colspan_rowspan.pdf";
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		PdfPTable table = new PdfPTable(4);
		PdfPCell cell = new PdfPCell(new Phrase(" 1,1 "));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(" 1,2 "));
		table.addCell(cell);
		PdfPCell cell23 = new PdfPCell(new Phrase("multi 1,3 and 1,4"));
		cell23.setColspan(2);
		cell23.setRowspan(2);
		table.addCell(cell23);
		cell = new PdfPCell(new Phrase(" 2,1 "));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(" 2,2 "));
		table.addCell(cell);
		document.add(table);
		document.close();

	}

	@Test
	public void createPdfTest() throws Exception {
		final String dest = "wenjian.pdf";
		final int defaultSize = PdfUtil.DEFAULT_FONT_SIZE;
		Document document = PdfUtil.createDocument(new FileOutputStream(dest), null);
		float[] columnWidths = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

		PdfPTable table = PdfUtil.createTable(100, columnWidths);
		int[][] coordinates = { { 0, 1, 0, 10 }, // 1-a
				{ 1, 2, 0, 10 }, // 2-a
				{ 2, 3, 0, 10 }, // 3-a
				{ 3, 4, 0, 10 }, // 4-a
				{ 4, 5, 0, 10 }, // 5-a
				{ 9, 10, 2, 10 }, // 10-c
				{ 10, 11, 2, 10 }, // 11-c
				{ 12, 13, 0, 10 }, // 11-c
				{ 15, 16, 2, 10 }, // 16-c
				{ 16, 17, 2, 10 } // 17-c
		};
		List<List<PdfPCell>> cells = PdfUtil.createAndMergeCell(17, 10, coordinates);

		PdfUtil.modifyCell(cells.get(0).get(0), "XXX确认书", Element.ALIGN_CENTER, PdfUtil.createChineseFont(null, -1, Font.BOLD));
		PdfUtil.modifyCell(cells.get(1).get(0), "XXXX年XX月XX日", Element.ALIGN_CENTER, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(3).get(0), "致XXXX有限公司：", Element.ALIGN_LEFT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(4).get(0), "XXXX个人XXXX项目XXX期", Element.ALIGN_LEFT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		
		String[] row6Contents = { "DKLX", "YBJE", "FKDZ", "FWF", "DKHTJE", "YHSXF", "SFZFSXF", "HBJE", "WQRJE", "SFDZ" };
		for (int i = 0; i < row6Contents.length; i++) {
			PdfUtil.modifyCell(cells.get(5).get(i),row6Contents[i], Element.ALIGN_CENTER, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		}
		
		String[] row7Contents = { "XFDK", "", "", "", "", "", "", "", "", "" };
		for (int i = 0; i < row7Contents.length; i++) {
			PdfUtil.modifyCell(cells.get(6).get(i),row7Contents[i], Element.ALIGN_CENTER, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		}

		PdfUtil.modifyCell(cells.get(9).get(2), "XXXX公司（公章/财务章/业务章）", Element.ALIGN_RIGHT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(10).get(2), "XXXX年XX月XX日", Element.ALIGN_RIGHT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(11).get(0), "备注：", Element.ALIGN_LEFT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(12).get(0), "xxxx汇总数据和明细数据进行确认，数据无误。", Element.ALIGN_LEFT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(13).get(1), "复核：", Element.ALIGN_LEFT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(13).get(4), "经办：", Element.ALIGN_RIGHT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		
		PdfUtil.modifyCell(cells.get(15).get(2), "xxxxx事业部（业务专用章）", Element.ALIGN_RIGHT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));
		PdfUtil.modifyCell(cells.get(16).get(2), "XXXX年XX月XX日", Element.ALIGN_RIGHT, PdfUtil.createChineseFont(null, defaultSize, Font.NORMAL));

		
		for (List<PdfPCell> line : cells) {
			for (PdfPCell c : line) {
				table.addCell(c);
			}
		}

		document.add(table);
		document.close();
	}

}
