# Retrofit原理解析

## 1 HelloWorld

- 添加依赖

```gradle
implementation 'com.squareup.okhttp3:okhttp:3.12.0'
implementation 'com.google.code.gson:gson:2.8.5'
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
```

- Api接口类

```java
public interface GithubApi {

    @GET("users/{user}/repos")
    Call<List<RepoBean>> getRepos(@Path("user") String user);

}
```

- Retrofit实例

```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
```

- Api实例

```java
GithubApi githubApi = retrofit.create(GithubApi.class);
```

- 发起请求

```java
List<RepoBean> repos = githubApi.getRepos(user).execute().body();
```

## 2 More

### 2.1 Method

在Retrofit中，使用在接口的方法上加注解的方式来描述一个请求。比如：@GET, @POST, @PUT, @DELETE, @PATCH or @HEAD。

```java
public interface FutureStudioClient {  
    @GET("/user/info")
    Call<UserInfo> getUserInfo();
    @PUT("/user/info")
    Call<UserInfo> updateUserInfo(
        @Body UserInfo userInfo
    );
    @DELETE("/user")
    Call<Void> deleteUser();
}
```

### 2.2 Resource Location

每个请求都需要指定一个相对路径，方法注解中有个String参数，就是用来制定资源相对路径。Retrofit也支持使用完整的路径。

```java
public interface FutureStudioClient {  
    @GET("/user/info")
    Call<UserInfo> getUserInfo();
    @PUT("/user/info")
    Call<UserInfo> updateUserInfo(
        @Body UserInfo userInfo
    );
    @DELETE("/user")
    Call<Void> deleteUser();
    // example for passing a full URL
    @GET("https://futurestud.io/tutorials/rss/")
    Call<FutureStudioRssFeed> getRssFeed();
}
```

### 2.3 Function Name & Return Type

```java
Call<UserInfo> getUserInfo();
```

一个Api接口有三部分组成：

- Method Name
- Method Return Type
- Method Parameters

其中方法名随意，Retrofit并不关心。方法的返回值有严格的格式规定，需要和服务端数据模型进行约定。方法参数有三个相关注解。

- @Body：将Java对象做为请求体
- @Url：动态url
- @Field：指定请求头

```java
public interface FutureStudioClient {  
    @GET("/user/info")
    Call<UserInfo> getUserInfo();

    @PUT("/user/info")
    Call<Void> updateUserInfo(@Body UserInfo userInfo);

    @GET
    Call<ResponseBody> getUserProfilePhoto(@Url String profilePhotoUrl);
}
```

### 2.4 Path Parameters

Retrofit提供了一种简单的方式来实现替换url种的某部分。

```java
public interface GitHubClient {  
    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForUser(@Path("user") String user);
}
```

### 2.5 Query Parameters

使用@Query注解添加url的查询参数。

```java
public interface FutureStudioClient {  
    @GET("/tutorials")
    Call<List<Tutorial>> getTutorials(@Query("page") Integer page);

    @GET("/tutorials")
    Call<List<Tutorial>> getTutorials(
            @Query("page") Integer page,
            @Query("order") String order,
            @Query("author") String author,
            @Query("published_at") Date date
    );
}
```

> 如果参数赋值null的话，Retrofit会忽略该参数。

更多用法可以参考[Retrofit-Tutorials](https://github.com/square/retrofit/wiki/Retrofit-Tutorials)。

## 3 关键类

- Api接口类
- Retrofit
- Converter.Factory
- CallAdapter

## 4 相关技术

### 4.1 动态代理

在某些情况下，我们不希望或是不能直接访问对象 A，而是通过访问一个中介对象 B，由 B 去访问 A 达成目的，这种方式我们就称为代理。

这里对象 A 所属类我们称为委托类，也称为被代理类，对象 B 所属类称为代理类。

根据程序运行前代理类是否已经存在，可以将代理分为静态代理和动态代理。Retrofit使用的是动态代理技术，这里重点介绍动态代理。

- 创建委托类

```java
public interface Operate {

    public void operateMethod1();

    public void operateMethod2();

    public void operateMethod3();
}

public class OperateImpl implements Operate {

    @Override
    public void operateMethod1() {
        System.out.println("Invoke operateMethod1");
        sleep(110);
    }

    @Override
    public void operateMethod2() {
        System.out.println("Invoke operateMethod2");
        sleep(120);
    }

    @Override
    public void operateMethod3() {
        System.out.println("Invoke operateMethod3");
        sleep(130);
    }

    private static void sleep(long millSeconds) {
        try {
            Thread.sleep(millSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- 实现InvocationHandler接口

```java
public class TimingInvocationHandler implements InvocationHandler {

    private Object target;

    public TimingInvocationHandler() {}

    public TimingInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = method.invoke(target, args);
        System.out.println(method.getName() + " cost time is:" + (System.currentTimeMillis() - start));
        return obj;
    }
}
```

- 通过Proxy类创建委托类实例

```java
public class Main {
    public static void main(String[] args) {
        // create proxy instance
        TimingInvocationHandler timingInvocationHandler = new TimingInvocationHandler(new OperateImpl());
        Operate operate = (Operate)(Proxy.newProxyInstance(Operate.class.getClassLoader(), new Class[] {Operate.class},
                timingInvocationHandler));

        // call method of proxy instance
        operate.operateMethod1();
        System.out.println();
        operate.operateMethod2();
        System.out.println();
        operate.operateMethod3();
    }
}
```

### 4.2 注解

### 4.3 OkHttp

## 5 原理



## 6 结论