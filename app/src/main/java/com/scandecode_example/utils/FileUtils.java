package com.scandecode_example.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author xu
 * @date 2016/4/18
 */
public class FileUtils {

    /**
     * 单个导出
     *Single export
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
                //Take string
                String str = list.get(i);

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

}








