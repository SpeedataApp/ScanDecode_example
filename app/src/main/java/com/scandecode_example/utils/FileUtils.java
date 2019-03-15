package com.scandecode_example.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author xu
 * @date 2016/4/18
 */
public class FileUtils {

    /**
     * 单个导出
     *
     * @param list
     * @param filename
     * @return
     */
    public int outputOnefile(List<String> list, String filename) {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < list.size(); i++) {
                //拿字符串
                String str = list.get(i);

                String over = str + "\r\n";
                // 写文件
                bw.write(over, 0, over.length());
                // 刷新流
                bw.flush();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件流
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

}








