package com.huan.percy.okhttptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    static final String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            httpGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void httpGet() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        //异步请求
        //client.newCall(request).enqueue(null);

        //将得到的结果转换为纯文本
        if (response.isSuccessful()) {
            String content = response.body().string();
            System.out.println(content);

            //从响应体中提取头信息
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                String name = headers.name(i);
                System.out.println(response.header(name));
            }
        }

//        //将得到的结果转换为字节流（例如图片）
//        if(response.isSuccessful()) {
//            InputStream in = response.body().byteStream();
//            FileOutputStream fos = new FileOutputStream(new File("test.jpg"));
//
//            int length;
//            byte [] buf = new byte[1024];
//
//            while ((length = in.read(buf)) != -1) {
//                System.out.println(length);
//                fos.write(buf, 0, length);
//            }
//        }

    }

    private void httpGetWithCancel () throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okHttp", "request failed!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //.....
                Log.d("okHttp", "request success!");
                call.cancel();
            }
        });
        call.cancel();
        client.dispatcher().cancelAll();//取消所有请求
    }

    private void httpGetWithCache () throws IOException {
        //设置缓存目录以及缓存大小
        String cachePath = this.getFilesDir().toString();
        Cache cache = new Cache(new File(cachePath), 1024 * 1000);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.cache(cache);

        OkHttpClient client = clientBuilder.build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        //将得到的结果转换为纯文本
        if (response.isSuccessful()) {
            String content = response.body().string();
            System.out.println(content);

            //从响应体中提取头信息
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                String name = headers.name(i);
                System.out.println(response.header(name));
            }
        }
    }

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("headerKey1", "headerValue1")//设置请求头
                .addHeader("headerKey2", "headerValue2")//追加请求头
                .post(body)
                .build();

        //以键值对的形式提交数据
//        RequestBody formBody = new FormBody.Builder()
//                .add("platform", "android")
//                .add("name", "bug")
//                .add("subject", "Test")
//                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();

    }
}
