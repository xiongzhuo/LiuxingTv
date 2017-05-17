package activity.liuxing.tv.com.tvliuxing.activity.request;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/4/28.
 */

public interface RequestServes {
    @POST("mobileLogin/submit.html")
    Call<String> getString(@Query("loginname") String loginname,
                           @Query("nloginpwd") String nloginpwd);
}
