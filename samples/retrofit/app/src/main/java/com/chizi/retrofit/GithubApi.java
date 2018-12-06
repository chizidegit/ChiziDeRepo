package com.chizi.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Chenll on 2018/12/6.
 */

public interface GithubApi {

    @GET("users/{user}/repos")
    Call<List<RepoBean>> getRepos(@Path("user") String user);

}
