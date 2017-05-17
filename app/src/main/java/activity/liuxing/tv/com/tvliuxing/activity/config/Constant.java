package activity.liuxing.tv.com.tvliuxing.activity.config;

/**
 * Created by Xiho on 2016/3/14.
 */
public class Constant {
    public static final String HOST = "http://smart.hnliuxing.com:8086";
    //    public static final String BASEURL = "http://192.168.0.88:8086";
//    public static final String BASEURL = "http://192.168.0.80:8080";
//    public static final String BASEURL = "http://hongren.bingjun.cn/";

    /**
     * TV设备注册
     */
    public static final String URL_TVREGISTER = HOST
            + "/finalapi/user/user/tvRegister";
    /**
     * 取TV设备邦定的用户共享名称
     */
    public static final String URL_TVSNBINGUSERSHARENAME = HOST
            + "/finalapi/user/user/getTvSNBingUserShareName";
    /**
     * 取TVSN邦定用户的设备List
     */
    public static final String URL_USERDEVSBYBINGEDTVSN = HOST
            + "/finalapi/user/user/getUserdevsBybingedTvSN";
    /**
     * 取远程dataserver信息
     */
    public static final String URL_TVGETDATASERVERCONFIG = HOST
            + "/finalapi/user/user/tvgetDataServerConfig";

    /**
     * 获取PM2.5室外
     */
    public static final String URL_OUTDOOR_PM = HOST
            + "/finalapi/user/user/getPM25_tv";
}
