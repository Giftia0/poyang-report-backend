package com.example.poyangreportbackend.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CosUtil {

    private static String appId = "<REDACTED_TENCENT_APP_ID>";
    private static String secretId = "<REDACTED_TENCENT_SECRET_ID>";
    private static String secretKey = "<REDACTED_TENCENT_SECRET_KEY>";
    private static String bucketName = "<REDACTED_BUCKET_NAME>";
    private static String CosPath = "<REDACTED_COS_URL>";
    private static String regionName = "<REDACTED_REGION>";

    private static COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    public static String uploadImg(File file, String basePath) {
        COSClient cosClient = getCosClient();
        String path = file.getPath();
        String type = path.substring(path.lastIndexOf('.'));
        String key = basePath + "/" + UUID.randomUUID() + type;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult result = cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return key;
    }

    public static void deleteImg(String url) {
        COSClient cosClient = getCosClient();
        cosClient.deleteObject(bucketName, url);
    }
}
