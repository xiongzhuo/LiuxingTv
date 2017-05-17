package activity.liuxing.tv.com.tvliuxing.activity.request;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/4/28.
 */

public class InitRetrofit {
    private static String baseUrl = "http://202.171.212.154:8080/hh/";
    private static OkHttpClient httpClient;
    private static Retrofit retrofit;

    public static Retrofit initRetrofit() {
        //缓存路径和大小
        File httpCacheDirectory = new File(Environment.getExternalStorageDirectory(), "HttpCache");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //日志拦截器
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时
                .readTimeout(10, TimeUnit.SECONDS)//读取超时
                .writeTimeout(10, TimeUnit.SECONDS)//写入超时
//                .addInterceptor(interceptor)//添加日志拦截器
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)//添加缓存拦截器
                .cache(cache)//把缓存添加进来
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

//    public static RetrofitAPI getService() {
//        return initRetrofit().create(RetrofitAPI.class);
//    }

// //缓存拦截器，不同接口不同缓存
// static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
//  @Override
//  public Response intercept(Chain chain) throws IOException {
//
//   Request request = chain.request();
//   Response response = chain.proceed(request);
//
//   if (NetworkUtil.getInstance().isConnected()) {
//    String cacheControl =request.cacheControl().toString();
//    return response.newBuilder()
//      .removeHeader("Pragma")//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
//      .header("Cache-Control", cacheControl)
//      .build();
//   }
//   return response;
//  }
// };

    //缓存拦截器，统一缓存60s
    static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);

//            if (NetworkUtil.getInstance().isConnected()) {
            int maxAge = 60 * 60 * 24 * 2;//缓存失效时间，单位为秒
            return response.newBuilder()
                    .removeHeader("Pragma")//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public ,max-age=" + maxAge)
                    .build();
//            }
//            return response;
        }
    };
}
