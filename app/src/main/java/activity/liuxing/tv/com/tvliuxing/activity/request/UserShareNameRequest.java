package activity.liuxing.tv.com.tvliuxing.activity.request;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import activity.liuxing.tv.com.tvliuxing.activity.Utils.JsonUtils;
import activity.liuxing.tv.com.tvliuxing.activity.Utils.SharedPreferencesDB;
import activity.liuxing.tv.com.tvliuxing.activity.Utils.ToastUtil;
import activity.liuxing.tv.com.tvliuxing.activity.config.Constant;
import activity.liuxing.tv.com.tvliuxing.activity.entity.JsonResult;
import activity.liuxing.tv.com.tvliuxing.activity.entity.UserShareName;
import activity.liuxing.tv.com.tvliuxing.activity.entity.Userdevs;
import activity.liuxing.tv.com.tvliuxing.activity.interfaces.StatisConstans;
import activity.liuxing.tv.com.tvliuxing.activity.view.DialogView;

/**
 * Created by Administrator on 2017/3/15.
 */

public class UserShareNameRequest {
    private Context context;
    private SharedPreferencesDB sharedPreferencesDB;
    private Handler handler;
    private DialogView dialogView;

    public UserShareNameRequest(Context context, SharedPreferencesDB sharedPreferencesDB, Handler handler) {
        this.context = context;
        this.sharedPreferencesDB = sharedPreferencesDB;
        this.handler = handler;
    }

    public void requestCode() throws Exception {
        if (null == dialogView) {
            dialogView = new DialogView(context);
            dialogView.show();
            dialogView.setMessage("加载中");
        }
        RequestParams params = new RequestParams(Constant.URL_TVSNBINGUSERSHARENAME);
        params.addBodyParameter("tvSn", sharedPreferencesDB.getString(StatisConstans.TVSN, ""));
        params.addBodyParameter("tvUuid", sharedPreferencesDB.getString(StatisConstans.TVUUID, ""));
        params.addBodyParameter("tvToken", sharedPreferencesDB.getString(StatisConstans.TVTOKEN, ""));
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String json) {
                JsonResult<UserShareName> result = new JsonResult<UserShareName>();
                try {
                    if (null != dialogView) {
                        dialogView.cancel();
                    }
                    Log.i("首页返回", json);
                    if (TextUtils.isEmpty(json)) {
                        return;
                    }
                    result = JsonUtils.parseJson(json,
                            new TypeToken<UserShareName>() {
                            }.getType());
                    //手机号码已经被其它账号绑定
                    if (!result.isFlag()) {
                        ToastUtil.show(context, result.getMsg());
                        handler.sendEmptyMessage(StatisConstans.MSG_RECEIVED_BOUND);
                    }
                    //手机号码正常
                    else {
                        handler.sendMessage(handler.obtainMessage(StatisConstans.MSG_RECEIVED_REGULAR, result.getData()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.show(context, "网络请求失败");
                handler.sendEmptyMessage(StatisConstans.MSG_RECEIVED_BOUND);
                if (null != dialogView) {
                    dialogView.cancel();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("logTest_xz", "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.i("logTest_xz", "onFinished");
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
}
