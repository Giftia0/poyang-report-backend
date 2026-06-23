package com.example.poyangreportbackend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    // 图片允许的后缀扩展名
    public static String[] IMAGE_FILE_EXTD = new String[] { "png", "bmp", "jpg", "jpeg","pdf" };

    public static boolean isFileAllowed(String fileName) {
        for (String ext : IMAGE_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static File inputStreamToFile(InputStream ins, String name) throws Exception{
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }

    public File getFile(String url, String fileName){
        File file = null;
        try {
            String Url = "http://cdn.giftia.cn/animal/test.jpg";
            HttpURLConnection httpUrl = (HttpURLConnection) new URL(Url).openConnection();
            httpUrl.connect();
            file = FileUtil.inputStreamToFile(httpUrl.getInputStream(),fileName);
            System.out.println("111====>>>>"+file.getPath());
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}

