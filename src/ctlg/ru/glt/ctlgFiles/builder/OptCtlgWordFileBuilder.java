package ru.glt.ctlgFiles.builder;

import org.apache.poi.xwpf.usermodel.*;
import org.json.simple.JSONObject;

public class OptCtlgWordFileBuilder extends CtlgFileBuilder {

    public OptCtlgWordFileBuilder() {
        super();
    }

    protected XWPFTable createOptionsTable(XWPFDocument document) {
        XWPFTable table = document.createTable(1, 3);
        return table;
    }

    protected void createOptionHeaderRow(XWPFTable table) {
        XWPFTableRow row = table.getRow(0);
        createOptionDescriptionHeader(row);
        createOptionPriceHeader(row);
        createOptionNdsPriceHeader(row);
    }

    protected void createOptionRow(XWPFTable table, JSONObject option) {
        XWPFTableRow row = table.createRow();
        createOptionDescription(row, option.get("descr").toString());
        createOptionPrice(row, formatter.format(Double.parseDouble(option.get("optPrice").toString())));
        createOptionNdsPrice(row, formatter.format((Double.parseDouble(option.get("optPrice").toString()) * 1.2)));
    }

    protected void createOptionNdsPrice(XWPFTableRow row, String text) {
        XWPFTableCell cell = row.getCell(2);
        configOptionsCell(cell, text);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth("2000");
    }

    protected void createOptionPriceHeader(XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(1);
        configOptionsHeaderCell(cell, CtlFileBuilderConstants.OPTION_PRICE2_HEADER);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth("2000");
    }

    protected void createOptionNdsPriceHeader(XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(2);
        configOptionsHeaderCell(cell, CtlFileBuilderConstants.OPTION_PRICE3_HEADER);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth("2000");
    }

    protected void createOptionDescriptionHeader(XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(0);
        configOptionsHeaderCell(cell, CtlFileBuilderConstants.OPTION_DESCRIPTION_HEADER);
        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth(CtlFileBuilderConstants.OPTION_DESCRIPTION2_WIDTH);
    }
}
