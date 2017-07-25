package activity.liuxing.tv.com.tvliuxing.activity.socket;

import android.os.Handler;
import android.util.Log;

import java.net.Socket;

/**
 * Created by Administrator on 2017/3/30.
 */

public class ScoketOFFeON {
    static Protocal p = Protocal.getInstance();

    //发送查询
    public static void sendMessage(Socket s, String mac) throws Exception {
        // 2.创建Protocal对像
        // 3.用Protocal生成并发送请求数据
        Log.d("ConnectionManager", "AbsClient*****发送请求");
        p.sendRequest(s.getOutputStream(), mac);
        Log.d("ConnectionManager", "AbsClient*****请求发送成功");
    }

    //接受数据
    public static void receMessage(Socket s, Handler handler) {
        try {
            // 4.用Protocal接收并解析响应数据
            Log.d("ConnectionManager", "AbsClient*****接收响应");
            p.receiveResponse(s, handler);
            Log.d("ConnectionManager", "AbsClient*****响应接收完成");
            // 2.创建Protocal对像
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
