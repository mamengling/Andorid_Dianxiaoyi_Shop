package com.junjia.testoss;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.lidroid.xutils.BitmapUtils;

import java.io.FileNotFoundException;

public class MainActivity extends Activity {
    private BitmapUtils bitmapUtils;
    private ImageView imageView;
    static final String accessKey = "DJihTBRadj60gMLd"; // 测试代码没有考虑AK/SK的安全性
    static final String screctKey = "lbXHtsAAVMsCNs8DYEYcsyS4U8bjkX";
    /**
     * 获取SD卡标识
     */
    public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
    public static final String srcFileDir = SDCARD_ROOT + "/Dianxiaoyi/Head/" + "head.jpg";

    public static final String bucketName = "mmlproject";

    public static OSSService ossService = OSSServiceProvider.getService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bitmapUtils=new BitmapUtils(MainActivity.this);
        imageView= (ImageView) findViewById(R.id.img_logo);
        bitmapUtils.display(imageView,"http://mmlproject.oss-cn-qingdao.aliyuncs.com/test.jpg");
        // 初始化设置
        ossService.setApplicationContext(this.getApplicationContext());
        ossService.setGlobalDefaultHostId("oss-cn-hangzhou.aliyuncs.com"); // 设置region host 即 endpoint
        ossService.setGlobalDefaultACL(AccessControlList.PRIVATE); // 默认为private
        ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK); // 设置加签类型为原始AK/SK加签
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(accessKey, screctKey, content);
            }
        });
        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);

        OSSBucket sampleBucket = ossService.getOssBucket(bucketName);
        sampleBucket.setBucketACL(AccessControlList.PRIVATE); // 声明该Bucket的访问权限
        sampleBucket.setBucketHostId("oss-cn-qingdao.aliyuncs.com"); // 指明该Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名
        resumableUpload(sampleBucket);
    }

    // 断点上传
    public void resumableUpload(OSSBucket bucket) {
        OSSFile bigfFile = ossService.getOssFile(bucket, "bbbb/");
        try {
            bigfFile.setUploadFilePath(srcFileDir, "image/jpg");
            bigfFile.ResumableUploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    Log.d("TAG", "[onSuccess] - " + objectKey + " upload success!");
                }

                @Override
                public void onProgress(String objectKey, int byteCount, int totalSize) {
                    Log.d("TAG", "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
                }

                @Override
                public void onFailure(String objectKey, OSSException ossException) {
                    Log.e("TAG", "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
                    ossException.printStackTrace();
                    ossException.getException().printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
