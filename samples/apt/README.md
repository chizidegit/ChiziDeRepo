# How to Debug AnnotationProcessor

## 挂起Gradle进程

Gradle本身也是个JVM进程，通过执行：

```
$ ./gradlew --no-daemon -Dorg.gradle.debug=true :app:clean :app:compileDebugJavaWithJavac
```gradle

启动Gradle进程，执行后进程将被挂起，直到被某个Debugger附着。

## Add Remote Debug Configuration

通过配置Android Studio增加一个Remote Debug Configuration便可附着到该进程上。

![](http://ww1.sinaimg.cn/large/6f97245dgy1fytpxnwan8j20tu0a20ty.jpg)

