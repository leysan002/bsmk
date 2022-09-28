package ru.glt.ctlgFiles.builder;

import org.apache.poi.xwpf.usermodel.*;
import org.json.simple.JSONObject;

public class OptCtlgWordFileBuilder2 extends OptCtlgWordFileBuilder {
    public OptCtlgWordFileBuilder2() {
        super();
    }

    protected void createOptionRow(XWPFTable table, JSONObject option) {
        XWPFTableRow row = table.createRow();
        Double price = Double.parseDouble(option.get("optPrice").toString());
        if (option.containsKey("optPrice2")) {
            price = Double.parseDouble(option.get("optPrice2").toString());
        }
        createOptionDescription(row, option.get("descr").toString());
        createOptionPrice(row, formatter.format(price));
        createOptionNdsPrice(row, formatter.format(price * 1.2));
    }
}
