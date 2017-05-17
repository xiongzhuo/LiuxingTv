package activity.liuxing.tv.com.tvliuxing.activity.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import activity.liuxing.tv.com.tvliuxing.R;
import activity.liuxing.tv.com.tvliuxing.activity.Utils.ToastUtil;
import activity.liuxing.tv.com.tvliuxing.activity.base.BaseActivity;
import activity.liuxing.tv.com.tvliuxing.activity.entity.UserData;
import activity.liuxing.tv.com.tvliuxing.activity.interfaces.StatisConstans;
import activity.liuxing.tv.com.tvliuxing.activity.request.LodingRequest;
import activity.liuxing.tv.com.tvliuxing.activity.util.AddressUtils;
import butterknife.BindView;
import butterknife.BindViews;

public class LodingActivity extends BaseActivity {
    @BindViews({R.id.et_user})
    List<EditText> clearEditTexts;
    @BindView(R.id.btn_loding)
    Button btnLoding;
    String user;
    String imei;
    UserData userData;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //成功
                case StatisConstans.MSG_RECEIVED_REGULAR:
                    userData = (UserData) msg.obj;
                    sharedPreferencesDB.setString(StatisConstans.TVSN, user);
                    sharedPreferencesDB.setString(StatisConstans.TVUUID, imei);
                    sharedPreferencesDB.setString(StatisConstans.TVTOKEN, userData.getTv_token());
                    startActivity(new Intent(LodingActivity.this, MainActivity.class));
                    finish();
                    break;
                case StatisConstans.MSG_RECEIVED_BOUND:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected int getContentLayoutId() {
        return R.layout.loding;
    }

    @Override
    @SuppressLint("WrongConstant")
    protected void initView(Bundle savedInstanceState) {
        imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        btnLoding.setOnClickListener(this);
    }


    @Override
    public void onMultiClick(View view) {
        switch (view.getId()) {
            case R.id.btn_loding:
                confirmation();
                break;
            default:
                break;
        }
    }

    //验证信息是否通过
    private void confirmation() {
        user = clearEditTexts.get(0).getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            ToastUtil.show(this, "设备序列号不能为空!");
            return;
        }
        try {
            LodingRequest lodingRequest = new LodingRequest(LodingActivity.this, user, imei, handler);
            lodingRequest.requestCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
