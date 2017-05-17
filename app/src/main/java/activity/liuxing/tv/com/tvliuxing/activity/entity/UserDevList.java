package activity.liuxing.tv.com.tvliuxing.activity.entity;

/**
 * Created by Administrator on 2017/5/11.
 */

public class UserDevList {
    private String device_mac;

    private String device_sn;

    private String user_key;
    private int ison;

    private String device_nickname;

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    public String getDevice_mac() {
        return this.device_mac;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getDevice_sn() {
        return this.device_sn;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getUser_key() {
        return this.user_key;
    }

    public void setDevice_nickname(String device_nickname) {
        this.device_nickname = device_nickname;
    }

    public String getDevice_nickname() {
        return this.device_nickname;
    }


    public int getIson() {
        return ison;
    }

    public void setIson(int ison) {
        this.ison = ison;
    }
}

