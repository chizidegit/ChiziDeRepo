package com.chizi.retrofit;

import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Chenll on 2018/12/6.
 */

public class GithubService {

    private Retrofit mRetrofit;
    private GithubApi mGithubApi;

    public static GithubService newInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return new GithubService(retrofit);
    }

    private GithubService(Retrofit retrofit) {
        mRetrofit = retrofit;
        mGithubApi = mRetrofit.create(GithubApi.class);
    }

    public List<RepoBean> getRepos(String user) throws IOException {
        return mGithubApi.getRepos(user).execute().body();
    }

}
