package activity.liuxing.tv.com.tvliuxing.activity.entity;

/**
 * Created by Administrator on 2017/5/11.
 */

public class DataServer {
    private TvDataServerConfig tvDataServerConfig;

    public DataServer(TvDataServerConfig tvDataServerConfig) {
        this.tvDataServerConfig = tvDataServerConfig;
    }

    public void setTvDataServerConfig(TvDataServerConfig tvDataServerConfig) {
        this.tvDataServerConfig = tvDataServerConfig;
    }

    public TvDataServerConfig getTvDataServerConfig() {
        return this.tvDataServerConfig;
    }
}
