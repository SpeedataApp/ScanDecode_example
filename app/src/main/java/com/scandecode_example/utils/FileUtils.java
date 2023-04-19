package com.scandecode_example.utils;


import com.scandecode_example.model.DataBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author xu
 * @date 2016/4/18
 */
public class FileUtils {

    /**
     * 单个导出
     * Single export
     *
     * @param list
     * @param filename
     * @return
     */
    public int outputOnefile(List<DataBean> list, String filename) {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < list.size(); i++) {
                //拿字符串
                //Take string
                String str = list.get(i).getBarcode();

                String over = str + "\r\n";
                // 写文件
                //Write file
                bw.write(over, 0, over.length());
                // 刷新流
                // Refresh stream
                bw.flush();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件流
                // Close file stream
                Objects.requireNonNull(bw).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public static String aimidToType(String aimid) {
        String type = "";
        switch (aimid) {
            case "]C0":
                type = "Code128 (Code 16K)";
                break;
            case "]C1":
                type = "GS1-128 (UCC/EAN-128)";
                break;
            case "]E4":
                type = "EAN-8";
                break;
            case "]E3":
                type = "(EAN-8/EAN-13/UPC-E/UPC-A) with Addon";
                break;
            case "]E0":
                type = "EAN-13/UPC-E/UPC-A";
                break;
            case "]I3":
                type = "ITF-14";
                break;
            case "]Im":
                type = "Interleaved 2 of 5/ITF-14/ITF-6";
                break;
            case "]Xm":
                type = "Matrix 2 of 5";
                break;
            case "]Am":
            case "]A0":
                type = "Code 39";
                break;
            case "]Fm":
            case "]F0":
                type = "Codabar";
                break;
            case "]G0":
                type = "Code 93";
                break;
            case "]C2":
                type = "AIM 128";
                break;
            case "]C4":
                type = "ISBT 128";
                break;
            case "]X5":
                type = "ISSN";
                break;
            case "]X4":
                type = "ISBN";
                break;
            case "]X0":
                type = "ISBN/ISSN";
                break;
            case "]S0":
                type = "Industrial 25";
                break;
            case "]Rm":
                type = "Standard 25";
                break;
            case "]R0":
                type = "RSS/Standard 25";
                break;
            case "]P0":
                type = "Plessey";
                break;
            case "]Hm":
            case "]H0":
                type = "Code 11";
                break;
            case "]Mm":
            case "]M0":
                type = "MSI Plessey";
                break;
            case "]e0":
                type = "GS1 Databar (RSS)";
                break;
            case "]L0":
                type = "PDF417";
                break;
            case "]Qm":
                type = "QR Code";
                break;
            case "]Q1":
                type = "Micro QR Code";
                break;
            case "]L2":
                type = "QR Code/PDF417";
                break;
            case "]dm":
            case "]d1":
                type = "Data Matrix";
                break;
            default:
                break;
        }
        return type;
    }
}








