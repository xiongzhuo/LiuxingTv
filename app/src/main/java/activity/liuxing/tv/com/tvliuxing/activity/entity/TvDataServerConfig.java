package activity.liuxing.tv.com.tvliuxing.activity.entity;

/**
 * Created by Administrator on 2017/5/11.
 */

public class TvDataServerConfig {
    private String secondary_server_address;

    private int primary_server_port;

    private int secondary_server_port;

    private String primary_server_address;

    public void setSecondary_server_address(String secondary_server_address) {
        this.secondary_server_address = secondary_server_address;
    }

    public String getSecondary_server_address() {
        return this.secondary_server_address;
    }

    public void setPrimary_server_port(int primary_server_port) {
        this.primary_server_port = primary_server_port;
    }

    public int getPrimary_server_port() {
        return this.primary_server_port;
    }

    public void setSecondary_server_port(int secondary_server_port) {
        this.secondary_server_port = secondary_server_port;
    }

    public int getSecondary_server_port() {
        return this.secondary_server_port;
    }

    public void setPrimary_server_address(String primary_server_address) {
        this.primary_server_address = primary_server_address;
    }

    public String getPrimary_server_address() {
        return this.primary_server_address;
    }
}
