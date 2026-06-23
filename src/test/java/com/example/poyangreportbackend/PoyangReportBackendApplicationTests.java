package com.example.poyangreportbackend;

import com.example.poyangreportbackend.domain.ReportForm;
import com.example.poyangreportbackend.domain.ReportImg;
import com.example.poyangreportbackend.mapper.ReportFormMapper;
import com.example.poyangreportbackend.service.reportForm.ReportFormService;
import com.example.poyangreportbackend.service.reportImg.ReportImgService;
import com.example.poyangreportbackend.util.CosUtil;
import com.example.poyangreportbackend.util.OcrUtil;
import com.example.poyangreportbackend.util.OpencvUtil;
import org.junit.jupiter.api.Test;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class PoyangReportBackendApplicationTests {

    @Autowired
    ReportImgService reportImgService;

    @Autowired
    ReportFormMapper reportFormMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void changeImg () throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.awt.headless","false");
        System.load("C:\\Giftia\\opencv\\build\\java\\x64\\opencv_java3410.dll");
        Mat src = Imgcodecs.imread("C:\\Users\\asus\\Desktop\\R-C.jpg");

        MatOfPoint2f point1 = new MatOfPoint2f();
        List<Point> before = new ArrayList<>();
        before.add(new Point(0, 0));
        before.add(new Point(src.cols(), 0));
        before.add(new Point(0, src.rows()));
        before.add(new Point(src.cols(), src.rows()));
        point1.fromList(before);

        MatOfPoint2f point2 = new MatOfPoint2f();
        List<Point> after = new ArrayList<>();
        after.add(new Point(99, 97));
        after.add(new Point(330, 67));
        after.add(new Point(109, 245));
        after.add(new Point(350, 221));
        point2.fromList(after);
        // 获取 透视变换 矩阵
        Mat dst = Imgproc.getPerspectiveTransform(point2, point1);
        // 进行 透视变换
        Mat image = new Mat();
        Imgproc.warpPerspective(src, image, dst, src.size());
        HighGui.imshow("原图", src);
        HighGui.imshow("透视变换", image);
        HighGui.waitKey(0);

    }

    @Test
    void opencv() throws IOException {
        System.setProperty("java.awt.headless","false");
        System.load("C:\\Giftia\\opencv\\build\\java\\x64\\opencv_java3410.dll");
//        Mat src = Imgcodecs.imread("C:\\Users\\asus\\Desktop\\R-C.jpg");
        Mat src = OpencvUtil.readImageFromUrl("https://poyang-report-1314510469.cos.ap-guangzhou.myqcloud.com/test%2FR-C.jpg");
        if (src.empty()) {
            System.out.println("Error loading image");
            return;
        }

// 假设你已经有了正确的身份证四个顶点坐标
        List<Point> before = new ArrayList<>();
        before.add(new Point(50, 50)); // 左上角点
        before.add(new Point(400, 50)); // 右上角点
        before.add(new Point(400, 250)); // 右下角点
        before.add(new Point(50, 250)); // 左下角点
        MatOfPoint2f point1 = new MatOfPoint2f();
        point1.fromList(before);

        List<Point> after = new ArrayList<>();
        after.add(new Point(0, 0)); // 变换后左上角点
        after.add(new Point(450, 0)); // 变换后右上角点
        after.add(new Point(450, 300)); // 变换后右下角点
        after.add(new Point(0, 300)); // 变换后左下角点
        MatOfPoint2f point2 = new MatOfPoint2f();
        point2.fromList(after);

// 计算透视变换矩阵
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(point1, point2);
        if (perspectiveTransform.empty()) {
            System.out.println("Error computing perspective transform");
            return;
        }

// 计算变换后图像的大小
        Size outputSize = new Size(450, 300);
        Mat image = new Mat(outputSize, src.type());

// 进行透视变换
        Imgproc.warpPerspective(src, image, perspectiveTransform, outputSize);

        HighGui.imshow("原图", src);
        HighGui.imshow("透视变换", image);
        HighGui.waitKey(0);
// 在此处处理变换后的图像...

// 释放资源
        src.release();
        image.release();
        point1.release();
        point2.release();
        perspectiveTransform.release();
    }

    @Test
    void testOpencvUtil() throws IOException {
        //解决awt报错
        System.setProperty("java.awt.headless","false");
        //加载opencv本地库
        System.load("C:\\Giftia\\opencv\\build\\java\\x64\\opencv_java3410.dll");
        List<Point> after = new ArrayList<>();
        after.add(new Point(18, 98));
        after.add(new Point(247, 69));
        after.add(new Point(26, 244));
        after.add(new Point(267, 220));
        Mat src = Imgcodecs.imread("C:\\Users\\asus\\Desktop\\R-C.jpg");
//        Mat src = OpencvUtil.readImageFromUrl("https://poyang-report-1314510469.cos.ap-guangzhou.myqcloud.com/test%2FR-C.jpg");
        File file = OpencvUtil.correctImg(src, after);
        CosUtil.uploadImg(file,"/id-card");
        file.delete();
    }

    @Test
    void testFile() throws IOException {
        Integer a = 3;
        Integer b =2;
        Double c = 1.0 * a / b;
        System.out.println(c);
    }

    @Test
    void testOcr(){
//        OcrUtil.idcardOcr();
    }

    @Test
    void testService(){
    }
}
