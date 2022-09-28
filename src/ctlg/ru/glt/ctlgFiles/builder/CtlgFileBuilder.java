package ru.glt.ctlgFiles.builder;

import com.google.common.base.Charsets;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.glt.Constants;
import ru.glt.ctlgFiles.CtlgFileType;
import ru.glt.imgBuilder.ImgBuilder;
import ru.glt.imgBuilder.ImgBuilderMode;


import java.io.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static ru.glt.Constants.*;

public class CtlgFileBuilder {

    protected final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
    private final ImgBuilder imgBuilder;

    public static CtlgFileBuilder getBuilder(CtlgFileType fileType) {
        switch (fileType) {
            case OPT_PDF:
                return new OptCtlgPdfFileBuilder();
            case OPT_WORD:
                return new OptCtlgWordFileBuilder();
            case OPT_WORD2:
                return new OptCtlgWordFileBuilder2();
            default:
                return new CtlgFileBuilder();
        }
    }

    protected CtlgFileBuilder() {
        imgBuilder = new ImgBuilder();
    }

    public void write(OutputStream outputStream) throws IOException, ParseException, InvalidFormatException {
        JSONParser parser = new JSONParser();
        JSONObject parse = (JSONObject) parser.parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(CATALOG_ITEMS), Charsets.UTF_8));
        JSONArray nodes = (JSONArray) parse.get("node");
        JSONObject node = (JSONObject) nodes.get(0);

        XWPFDocument document = createDocument((JSONArray) node.get("products"));
        document.write(outputStream);
    }

    private XWPFDocument createDocument(JSONArray products) throws InvalidFormatException, IOException {
        XWPFDocument document = new XWPFDocument();
        createTopPage(document);
        Iterator iterator = products.iterator();
        while (iterator.hasNext()) {
            JSONObject product = (JSONObject) iterator.next();
            createNameLine(document, product.get("name").toString());

            if (product.containsKey("descr")) {
                createDecrsiptionBlock(document, product.get("descr").toString());
            }
            createOptionsTable(document, (JSONArray) product.get("options"));
            createImagesBlock(document, (JSONArray) product.get("imgs"));
            createSectionBreak(document);
        }
        return document;
    }

    private void createTopPage(XWPFDocument document) throws IOException, InvalidFormatException {
        createLogo(document);
        createContacts(document);
        createTitleImage(document);
        createTopPageBreak(document);
    }

    private void createTitleImage(XWPFDocument document) throws IOException, InvalidFormatException {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.addPicture(new FileInputStream(new File(IMAGES_PATH, TITLE_FILE_NAME)), Document.PICTURE_TYPE_PNG, "title",
                Units.toEMU(520), Units.toEMU(345));
    }

    private void createContacts(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setSpacingLineRule(LineSpacingRule.EXACT);
        paragraph.setSpacingAfter(0);
        paragraph.setSpacingBetween(1.5);
        createSiteLine(paragraph);
        createPhoneLine(paragraph, CtlFileBuilderConstants.PHONE1);
        createPhoneLine(paragraph, CtlFileBuilderConstants.PHONE2);
        createEmailLine(paragraph);
    }

    private void createEmailLine(XWPFParagraph paragraph) {
        XWPFHyperlinkRun run = paragraph.createHyperlinkRun("mailto:" + CtlFileBuilderConstants.COMPANY_EMAIL);
        run.setText(CtlFileBuilderConstants.COMPANY_EMAIL);
        run.setFontSize(15);
        run.setUnderline(UnderlinePatterns.SINGLE);
        run.addBreak();
    }

    private void createPhoneLine(XWPFParagraph paragraph, String phone) {
        XWPFRun run = paragraph.createRun();
        run.setText(phone);
        run.setFontSize(15);
        run.addBreak();
    }

    private void createSiteLine(XWPFParagraph paragraph) {
        XWPFHyperlinkRun run = paragraph.createHyperlinkRun(CtlFileBuilderConstants.COMPANY_SITE);
        run.setText(CtlFileBuilderConstants.COMPANY_SITE);
        run.setFontSize(20);
        run.setUnderline(UnderlinePatterns.SINGLE);
        run.addBreak();
    }

    private void createLogo(XWPFDocument document) throws InvalidFormatException, IOException {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingLineRule(LineSpacingRule.EXACT);
        paragraph.setSpacingAfter(0);
        paragraph.setSpacingBetween(1.5);
        XWPFRun run = paragraph.createRun();
        run.addPicture(new FileInputStream(new File(Constants.IMAGES_PATH, LOGO_FILE_NAME)), Document.PICTURE_TYPE_PNG, "logo",
                Units.toEMU(261), Units.toEMU(135));
        run.addBreak();
        run.setBold(true);
        run.setText(CtlFileBuilderConstants.COMPANY_NAME);
        run.setFontSize(28);
    }

    private void createNameLine(XWPFDocument document, String name) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.getCTP().addNewPPr();
        paragraph.setKeepNext(true);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(CtlFileBuilderConstants.TEXT_FONT_FAMILY);
        run.setFontSize(CtlFileBuilderConstants.NAME_FONT_SIZE);
        run.setColor(CtlFileBuilderConstants.NAME_FONT_COLOR);
        run.setText(name);
    }

    private void createDecrsiptionBlock(XWPFDocument document, String description) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.getCTP().addNewPPr();
        paragraph.setKeepNext(true);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(CtlFileBuilderConstants.TEXT_FONT_FAMILY);
        run.setFontSize(CtlFileBuilderConstants.TEXT_FONT_SIZE);
        run.setColor(CtlFileBuilderConstants.TEXT_FONT_COLOR);
        String[] descrs = description.split("<br/>");
        for (String descr : descrs) {
            run.setText(descr);
            run.addBreak();
        }
    }

    private void createImagesBlock(XWPFDocument document, JSONArray images) throws InvalidFormatException, IOException {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.getCTP().addNewPPr();
        paragraph.setKeepNext(true);
        CTOnOff state = CTOnOff.Factory.newInstance();
        state.setVal(STOnOff1.ON);
        paragraph.getCTP().addNewPPr().setKeepLines(state);
        paragraph.setSpacingBefore(100);
        paragraph.setSpacingLineRule(LineSpacingRule.EXACT);
        paragraph.setSpacingBetween(1.5);
        XWPFRun run = paragraph.createRun();
        Iterator iterator = images.iterator();
        while (iterator.hasNext()) {
            createImage(run, (String)iterator.next());
        }
    }

    private void createImage(XWPFRun imgr, String img) throws InvalidFormatException, IOException {
        imgr.setText("\t");
        imgr.addPicture(imgBuilder.getImage(img, ImgBuilderMode.CTLG), Document.PICTURE_TYPE_JPEG, img,
                Units.toEMU(CtlFileBuilderConstants.IMAGE_WIDTH), Units.toEMU(CtlFileBuilderConstants.IMAGE_HEIGHT));
    }

    private void createSectionBreak(XWPFDocument document) {
        CTBody body = document.getDocument().getBody();
        CTSectPr sectPr = body.addNewSectPr();
        configMargins(sectPr);
        sectPr.addNewType().setVal(STSectionMark.CONTINUOUS);
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(CtlFileBuilderConstants.SECTION_BREAK_LINE_SIZE);
        run.addBreak();
        paragraph.getCTP().addNewPPr().setSectPr(sectPr);
    }

    private void configMargins(CTSectPr sectPr) {
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(500L));
        pageMar.setRight(BigInteger.valueOf(500L));
        pageMar.setTop(BigInteger.valueOf(500L));
        pageMar.setBottom(BigInteger.valueOf(500L));
    }

    private void createTopPageBreak(XWPFDocument document) {
        CTBody body = document.getDocument().getBody();
        CTSectPr sectPr = body.addNewSectPr();
        configMargins(sectPr);
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.getCTP().addNewPPr().setSectPr(sectPr);
    }

    private void createOptionsTable(XWPFDocument document, JSONArray options) {
        XWPFTable table = createOptionsTable(document);
        preConfigOptionsTable(table);
        createOptionHeaderRow(table);
        Iterator iterator = options.iterator();
        while (iterator.hasNext()) {
            JSONObject option = (JSONObject) iterator.next();
            createOptionRow(table, option);
        }
        configOptionsTable(table);
    }

    private void configOptionsTable(XWPFTable table) {
        List<XWPFParagraph> paragraphs = table.getBody().getParagraphs();
        for (XWPFParagraph paragraph: paragraphs) {
            if (paragraph.getCTP().getPPr() == null) {
                paragraph.getCTP().addNewPPr();
            }
            paragraph.setKeepNext(true);
        }
    }

    protected XWPFTable createOptionsTable(XWPFDocument document) {
        XWPFTable table = document.createTable(1, 2);
        return table;
    }

    private void preConfigOptionsTable(XWPFTable table) {
        table.setWidthType(TableWidthType.DXA);
        table.setWidth(CtlFileBuilderConstants.OPTIONS_TABLE_WIDTH);
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 5,0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 5, 0, CtlFileBuilderConstants.OPTIONS_TABLE_BORDER);

    }

    protected void createOptionRow(XWPFTable table, JSONObject option) {
        XWPFTableRow row = table.createRow();
        createOptionDescription(row, option.get("descr").toString());
        createOptionPrice(row, formatter.format(Double.parseDouble(option.get("price").toString())));
    }

    protected void createOptionHeaderRow(XWPFTable table) {
        XWPFTableRow row = table.getRow(0);
        createOptionDescriptionHeader(row);
        createOptionPriceHeader(row);
    }

    protected void createOptionDescription(XWPFTableRow row, String text) {
        XWPFTableCell cell = row.getCell(0);
        configOptionsCell(cell, text);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth(CtlFileBuilderConstants.OPTION_DESCRIPTION_WIDTH);

    }

    protected void createOptionPrice(XWPFTableRow row, String text) {
        XWPFTableCell cell = row.getCell(1);
        configOptionsCell(cell, text);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth("2000");
    }

    protected void createOptionPriceHeader(XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(1);
        configOptionsHeaderCell(cell, CtlFileBuilderConstants.OPTION_PRICE_HEADER);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth("2000");
    }

    protected void createOptionDescriptionHeader(XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(0);
        configOptionsHeaderCell(cell, CtlFileBuilderConstants.OPTION_DESCRIPTION_HEADER);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth(CtlFileBuilderConstants.OPTION_DESCRIPTION_WIDTH);
    }

    protected void configOptionsHeaderCell(XWPFTableCell cell, String headerText) {
        configOptionsCell(cell, headerText);
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
    }

    protected void configOptionsCell(XWPFTableCell cell, String text) {
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell.getCTTc().addNewTcPr().addNewShd().setFill(CtlFileBuilderConstants.OPTON_BG);
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.getCTP().addNewPPr();
        paragraph.setKeepNext(true);
        paragraph.setSpacingBefore(50);
        paragraph.setIndentationLeft(50);
        XWPFRun run = paragraph.createRun();
        run.setFontSize(CtlFileBuilderConstants.OPTION_TEXT_SIZE);
        run.setColor(CtlFileBuilderConstants.OPTION_TEXT_COLOR);
        run.setText(text);
    }
}
