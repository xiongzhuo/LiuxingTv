package activity.liuxing.tv.com.tvliuxing.activity.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/11.
 */

public class UserData implements Serializable {
    String tv_token;

    public UserData(String tv_token) {
        this.tv_token = tv_token;
    }

    public String getTv_token() {
        return tv_token;
    }

    public void setTv_token(String tv_token) {
        this.tv_token = tv_token;
    }
}
