package com.scandecode_example.model;


import com.scandecode_example.utils.excel.Excel;

/**
 * @author xuyan  保存记录
 */

public class DataBean {

    @Excel(name = "barcode")
    private String barcode;


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
