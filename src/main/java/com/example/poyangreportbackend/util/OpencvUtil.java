package com.example.poyangreportbackend.util;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpencvUtil {

    public static File correctImg(Mat src, List<Point> beforePoints) {

        MatOfPoint2f point1 = new MatOfPoint2f();
        point1.fromList(beforePoints);

        Integer width = src.rows(), length = src.cols();
        Double i = 1.0 * length / width;

        if (i < 1.585) width = (int) (length / 1.585);
        else length = (int) (width * 1.585);

        MatOfPoint2f point2 = new MatOfPoint2f();
        List<Point> after = new ArrayList<>();
        after.add(new Point(0, 0));
        after.add(new Point(length, 0));
        after.add(new Point(length, width));
        after.add(new Point(0, width));
        point2.fromList(after);

        // 获取 透视变换 矩阵
        Mat dst = Imgproc.getPerspectiveTransform(point1, point2);
        // 进行 透视变换
        Mat image = new Mat();
        Imgproc.warpPerspective(src, image, dst, src.size());
        // 定义裁剪区域的左上角和右下角坐标
        Point topLeft = new Point(0, 0); // 替换为你想要的坐标
        Point bottomRight = new Point(length, width); // 替换为你想要的坐标
        // 创建矩形对象，用于定义裁剪区域
        Rect rect = new Rect(topLeft, bottomRight);
        // 获取裁剪区域的子矩阵
        image = new Mat(image, rect);

        // 定义要保存的文件路径和名称
        String filePath = "./src/main/resources/temp/" + UUID.randomUUID() + ".jpg";
        File file = new File(filePath);

        // 使用Imgcodecs的imwrite方法将Mat对象保存到文件中
        Imgcodecs.imwrite(filePath, image);

        return file;
    }

    public static Mat readImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        try (InputStream inputStream = new BufferedInputStream(connection.getInputStream())) {
            byte[] imageBytes = inputStream.readAllBytes();
            MatOfByte matOfByte = new MatOfByte(imageBytes);
            Mat image = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
            return image;
        } finally {
            connection.disconnect();
        }
    }
}
