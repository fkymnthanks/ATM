package com.graduate.zl.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件处理工具
 */
public class FileUtil {

    /**
     * 清除文件内容
     * @param filePath
     */
    public static void clearContent(String filePath) {
        File file = new File(filePath);
        FileWriter writer = null;
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
