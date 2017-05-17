package activity.liuxing.tv.com.tvliuxing.activity.request;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import activity.liuxing.tv.com.tvliuxing.activity.config.Constant;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit 网络接口服务的包装类
 * Created by Xiho on 2016/3/14.
 */
public class RetrofitWrapper {
    private static RetrofitWrapper instance;
    private Context mContext;
    private Retrofit retrofit;

    private RetrofitWrapper() {
        File httpCacheDirectory = new File(Environment.getExternalStorageDirectory(), "HttpCache");//这里为了方便直接把文件放在了SD卡根目录的HttpCache中，一般放在context.getCacheDir()中
        int cacheSize = 10 * 1024 * 1024;//设置缓存文件大小为10M
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时
                .readTimeout(10, TimeUnit.SECONDS)//读取超时
                .writeTimeout(10, TimeUnit.SECONDS)//写入超时
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)//添加自定义缓存拦截器（后面讲解），注意这里需要使用.addNetworkInterceptor
                .cache(cache)//把缓存添加进来
                .build();
        retrofit = new Retrofit.Builder().baseUrl(Constant.HOST)
                .client(httpClient)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static RetrofitWrapper getInstance() {
        if (instance == null) {
            synchronized (RetrofitWrapper.class) {
                if (instance == null) {
                    instance = new RetrofitWrapper();
                }
            }
        }
        return instance;
    }


    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

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
