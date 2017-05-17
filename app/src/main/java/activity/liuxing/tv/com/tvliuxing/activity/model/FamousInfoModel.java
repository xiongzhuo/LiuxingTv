package activity.liuxing.tv.com.tvliuxing.activity.model;

import android.content.Context;

import activity.liuxing.tv.com.tvliuxing.activity.entity.FamousInfoReq;
import activity.liuxing.tv.com.tvliuxing.activity.request.RequestServes;
import activity.liuxing.tv.com.tvliuxing.activity.request.RetrofitWrapper;
import retrofit2.Call;

/**
 * Created by Xiho on 2016/3/14.
 */
public class FamousInfoModel {
    private static FamousInfoModel famousInfoModel;
    private RequestServes mFamousService;

    /**
     * 单例模式
     *
     * @return
     */
    public static FamousInfoModel getInstance(Context context) {
        if (famousInfoModel == null) {
            famousInfoModel = new FamousInfoModel(context);
        }
        return famousInfoModel;
    }


    private FamousInfoModel(Context context) {
        mFamousService = (RequestServes) RetrofitWrapper.getInstance().create(RequestServes.class);
    }

    /**
     * 查询名人名言
     *
     * @param famousInfoReq
     * @return
     */
    public Call<String> queryLookUp(FamousInfoReq famousInfoReq) {
        Call<String> infoCall = mFamousService.getString("1914", "1");
        return infoCall;
    }
}
