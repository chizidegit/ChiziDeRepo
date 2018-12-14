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

先上一张[Stay](https://www.jianshu.com/p/45cb536be2f4)画的整体流程图：

![](https://upload-images.jianshu.io/upload_images/625299-29a632638d9f518f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/891/format/webp)

### 5.1 创建Api实例

Retrofit使用动态代理技术创建Api实例。

```java
public <T> T create(final Class<T> service) {
  Utils.validateServiceInterface(service);
  if (validateEagerly) {
    eagerlyValidateMethods(service);
  }
  return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
      new InvocationHandler() {
        private final Platform platform = Platform.get();
        @Override public Object invoke(Object proxy, Method method, Object... args)
            throws Throwable {
          // If the method is a method from Object then defer to normal invocation.
          if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
          }
          if (platform.isDefaultMethod(method)) {
            return platform.invokeDefaultMethod(method, service, proxy, args);
          }
          ServiceMethod serviceMethod = loadServiceMethod(method);
          OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
          return serviceMethod.callAdapter.adapt(okHttpCall);
        }
      });
}
```

### 5.2 ServiceMethod

> Adapts an invocation of an interface method into an HTTP call.

把对接口的调用转换成真正的Http调用。

loadServiceMethod()方法负责加载ServiceMethod：

```java
ServiceMethod loadServiceMethod(Method method) {
  ServiceMethod result;
  synchronized (serviceMethodCache) {
    result = serviceMethodCache.get(method);
    if (result == null) {
      result = new ServiceMethod.Builder(this, method).build();
      serviceMethodCache.put(method, result);
    }
  }
  return result;
}
```

看一下ServiceMethod构造方法：

```java
ServiceMethod(Builder<T> builder) {
  this.callFactory = builder.retrofit.callFactory();
  this.callAdapter = builder.callAdapter;
  this.baseUrl = builder.retrofit.baseUrl();
  this.responseConverter = builder.responseConverter;
  this.httpMethod = builder.httpMethod;
  this.relativeUrl = builder.relativeUrl;
  this.headers = builder.headers;
  this.contentType = builder.contentType;
  this.hasBody = builder.hasBody;
  this.isFormEncoded = builder.isFormEncoded;
  this.isMultipart = builder.isMultipart;
  this.parameterHandlers = builder.parameterHandlers;
}
```

重点有callFactory、callAdapter、responseConverter和parameterHandlers这4个成员变量。

#### 5.2.1 callFactory
  
负责创建Http请求，Http请求被抽象为okhttp3.Call类，它表示一个已经准备好，可以随时执行的Http请求。

```java
okhttp3.Call.Factory callFactory = this.callFactory;
if (callFactory == null) {
  callFactory = new OkHttpClient();
}
```

callFactory实际上由Retrofit类提供，而我们在构造Retrofit对象时，可以指定callFactory，如果不指定，将默认设置为一个okhttp3.OkHttpClient。

#### 5.2.2 callAdapter

负责把retrofit2.Call<T>转为T（注意和okhttp3.Call区分开来，retrofit2.Call<T>表示的是对一个 Retrofit方法的调用），这个过程会发送一个Http请求，拿到服务器返回的数据（通过okhttp3.Call实现），并把数据转换为声明的T类型对象（通过Converter<F,T> 实现）。

```java
// Make a defensive copy of the adapters and add the default Call adapter.
List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
callAdapterFactories.addAll(platform.defaultCallAdapterFactories(callbackExecutor));
```

#### 5.2.3 responseConverter

responseConverter是Converter<ResponseBody,T> 类型，负责把服务器返回的数据（JSON、XML、二进制或者其他格式，由ResponseBody封装）转化为T类型的对象。

```java
// Make a defensive copy of the converters.
List<Converter.Factory> converterFactories = new ArrayList<>(
    1 + this.converterFactories.size() + platform.defaultConverterFactoriesSize());
// Add the built-in converter factory first. This prevents overriding its behavior but also
// ensures correct behavior when using converters that consume all types.
converterFactories.add(new BuiltInConverters());
converterFactories.addAll(this.converterFactories);
converterFactories.addAll(platform.defaultConverterFactories());
```

#### 5.2.4 parameterHandlers

负责解析Api定义时每个方法的参数，并在构造Http请求时设置相应参数。每个参数都会有一个ParameterHandler，由ServiceMethod#parseParameter方法负责创建，其主要内容就是解析每个参数使用的注解类型（诸如Path，Query，Field 等），对每种类型进行单独的处理。构造Http请求时，我们传递的参数都是字符串，那Retrofit是如何把我们传递的各种参数都转化为String的呢？还是由Retrofit类提供的converter。

Converter.Factory除了提供上一小节提到的responseBodyConverter，还提供requestBodyConverter和stringConverter，Api方法中除了@Body和@Part类型的参数，都利用stringConverter进行转换，而@Body和@Part类型的参数则利用requestBodyConverter进行转换。

### 5.3 OkHttpCall

OkHttpCall实现了retrofit2.Call，我们通常会使用它的execute()和enqueue(Callback<T> callback)接口。前者用于同步执行请求，后者用于异步执行请求。

#### 5.3.1 execute

```java
@Override public Response<T> execute() throws IOException {
  okhttp3.Call call;
  synchronized (this) {
    if (executed) throw new IllegalStateException("Already executed.");
    executed = true;
    if (creationFailure != null) {
      if (creationFailure instanceof IOException) {
        throw (IOException) creationFailure;
      } else if (creationFailure instanceof RuntimeException) {
        throw (RuntimeException) creationFailure;
      } else {
        throw (Error) creationFailure;
      }
    }
    call = rawCall;
    if (call == null) {
      try {
        call = rawCall = createRawCall();
      } catch (IOException | RuntimeException | Error e) {
        throwIfFatal(e); //  Do not assign a fatal error to creationFailure.
        creationFailure = e;
        throw e;
      }
    }
  }
  if (canceled) {
    call.cancel();
  }
  return parseResponse(call.execute());
}
```

主要有三个步骤：
- 创建okhttp3.Call
- 执行网络请求
- 解析响应数据

具体来说。在createRawCall()函数中，调用serviceMethod.toRequest(args)来创建okhttp3.Request，而在后者中，之前准备好的parameterHandlers就派上了用场。

然后再调用serviceMethod.callFactory.newCall(request)来创建okhttp3.Call，这里之前准备好的callFactory同样也派上了用场，由于工厂在构造Retrofit对象时可以指定，所以也可以指定其他的工厂（例如使用过时的HttpURLConnection的工厂），来使用其它的底层HttpClient实现。

调用okhttp3.Call#execute()来执行网络请求，这个方法是阻塞的，执行完毕之后将返回收到的响应数据。收到响应数据之后，首先进行状态码的检查，通过检查之后调用serviceMethod.toResponse(catchingBody)来把响应数据转化为用户指定的数据模型。在toResponse函数中，之前准备好的responseConverter也派上了用场。

#### 5.3.2 enqueue

```java
@Override public void enqueue(final Callback<T> callback) {
  checkNotNull(callback, "callback == null");
  okhttp3.Call call;
  Throwable failure;
  synchronized (this) {
    if (executed) throw new IllegalStateException("Already executed.");
    executed = true;
    call = rawCall;
    failure = creationFailure;
    if (call == null && failure == null) {
      try {
        call = rawCall = createRawCall();
      } catch (Throwable t) {
        throwIfFatal(t);
        failure = creationFailure = t;
      }
    }
  }
  if (failure != null) {
    callback.onFailure(this, failure);
    return;
  }
  if (canceled) {
    call.cancel();
  }
  call.enqueue(new okhttp3.Callback() {
    @Override public void onResponse(okhttp3.Call call, okhttp3.Response 
      Response<T> response;
      try {
        response = parseResponse(rawResponse);
      } catch (Throwable e) {
        throwIfFatal(e);
        callFailure(e);
        return;
      }
      try {
        callback.onResponse(OkHttpCall.this, response);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    @Override public void onFailure(okhttp3.Call call, IOException e) {
      callFailure(e);
    }
    private void callFailure(Throwable e) {
      try {
        callback.onFailure(OkHttpCall.this, e);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  });
}
```

这里的异步交给了okhttp3.Call#enqueue(Callback responseCallback)来实现，并在它的callback中调用parseResponse解析响应数据，并转发给传入的callback。

### 5.4 CallAdapter

CallAdapter<T>#adapt(Call<R> call)函数负责把retrofit2.Call<R>转为T。这里T当然可以就是 retrofit2.Call<R>，这时我们直接返回参数就可以了，实际上这正是DefaultCallAdapterFactory创建的CallAdapter的行为。

## 6 扩展

### 6.1 retrofit-adapters

Retrofit内置了DefaultCallAdapterFactory和ExecutorCallAdapterFactory，它们都适用于Api方法得到的类型为retrofit2.Call的情形，前者生产的adapter啥也不做，直接把参数返回，后者生产的adapter则会在异步调用时在指定的Executor上执行回调。

retrofit-adapters的各个子模块则实现了更多的工厂：

- GuavaCallAdapterFactory
- Java8CallAdapterFactory
- RxJavaCallAdapterFactory

### 6.2 retrofit-converters

Retrofit内置了BuiltInConverters，只能处理ResponseBody，RequestBody和String类型的转化（实际上不需要转）。而retrofit-converters中的子模块则提供了Json，Xml，ProtoBuf等类型数据的转换功能，而且还有多种转换方式可以选择。这里以平时开发中常用的GsonConverterFactory举例：

```java
public final class GsonConverterFactory extends Converter.Factory {
  /**
   * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use UTF-8.
   */
  public static GsonConverterFactory create() {
    return create(new Gson());
  }

  /**
   * Create an instance using {@code gson} for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use UTF-8.
   */
  @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
  public static GsonConverterFactory create(Gson gson) {
    if (gson == null) throw new NullPointerException("gson == null");
    return new GsonConverterFactory(gson);
  }

  private final Gson gson;

  private GsonConverterFactory(Gson gson) {
    this.gson = gson;
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
    return new GsonResponseBodyConverter<>(gson, adapter);
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
    return new GsonRequestBodyConverter<>(gson, adapter);
  }
}
```

原理很简单，根据目标类型，利用Gson#getAdapter获取相应的adapter，转换时利用Gson Api即可。

## 7 结论

### 7.1 工厂让各个模块得以高度解耦

上面提到的三种工厂：okhttp3.Call.Factory，CallAdapter.Factory和Converter.Factory，分别负责提供不同的模块，至于怎么提供、提供何种模块，统统交给工厂，Retrofit完全不掺和，它只负责提供用于决策的信息，例如参数/返回值类型、注解等。

这不正是我们苦苦追求的高内聚低耦合吗？

解耦的第一步就是面向接口编程，模块之间、类之间通过接口进行依赖，创建怎样的实例，则交给工厂负责，工厂同样也是接口，添加什么工厂，则在最初构造Retrofit对象时决定，各个模块之间完全解耦，每个模块只专注于自己的职责。

### 7.2 设计模式

- 外观模式

> 外观模式（Facade Pattern）隐藏系统的复杂性，并向客户端提供了一个客户端可以访问系统的接口。

![](http://www.runoob.com/wp-content/uploads/2014/08/facade_pattern_uml_diagram.jpg)

Retrofit暴露的方法和类不多。核心类就是Retrofit，用户只管配置Retrofit，然后做请求。剩下的事情就不需要关心了，只需要等待请求结果的回调。

封装公共模块的时候可以考虑使用外观模式，用户只需要和这个外观类打交道，模块内部的细节不需要关心。

- 代理模式

> 在代理模式（Proxy Pattern）中，一个类代表另一个类的功能。在代理模式中，我们创建具有现有对象的对象，以便向外界提供功能接口。

![](http://www.runoob.com/wp-content/uploads/2014/08/proxy_pattern_uml_diagram.jpg)

Retrofit Api实例就是通过动态代理创建的。

- 适配器模式

> 适配器模式（Adapter Pattern）是作为两个不兼容的接口之间的桥梁。这种类型的设计模式属于结构型模式，它结合了两个独立接口的功能。

![](http://www.runoob.com/wp-content/uploads/2014/08/adapter_pattern_uml_diagram.jpg)

再Retrofit，为什么需要使用适配器模式呢。那个被转换的是谁？我们看看CallAdapter的定义。Adapts a {@link Call} into the type of {@code T}. 这个Call是OkHttpCall，它不能被我们直接使用吗？被转换后要去实现什么特殊的功能吗？
我们假设下。一开始，retrofit只打算在android上使用，那就通过静态代理ExecutorCallbackCall来切换线程。但是后来发现rxjava挺好用啊，这样就不需要Handler来切换线程了嘛。想要实现，那得转换一下。将OkHttpCall转换成rxjava(Scheduler)的写法。再后来又支持了java8(CompletableFuture)甚至居然还有iOS支持。大概就是这样一个套路。当然我相信square的大神肯定一开始就考虑了这种情况，从而设计了CallAdapter。

### 7.3 注解与泛型