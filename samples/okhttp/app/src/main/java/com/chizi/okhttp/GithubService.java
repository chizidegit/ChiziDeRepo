package com.chizi.okhttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Chenll on 2018/12/5.
 */

public class GithubService {

    // 1.OkHttpClient
    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();

    private GithubService() {
    }

    public static GithubService newInstance() {
        return new GithubService();
    }

    public String getRepos(String url) throws IOException {
        // 2.Request
        Request request = new Request.Builder().url(url).build();
        // 3.Call
        Call call = mOkHttpClient.newCall(request);
        // 4. Response
        Response response = call.execute();
        return response.body().string();
    }

}
