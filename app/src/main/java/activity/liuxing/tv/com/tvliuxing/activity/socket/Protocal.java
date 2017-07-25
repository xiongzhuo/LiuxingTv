package activity.liuxing.tv.com.tvliuxing.activity.socket;

import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Protocal {

    // 保存从服务器响应的结果
//    private String data;

//    public String getData() {
//        return data;
//    }

    /********
     * 发送开关机
     ***************************************************/
    /**
     * 单例对象实例
     */
    private volatile static Protocal instance;

    private Protocal() {
    }

    public static Protocal getInstance() {
        if (instance == null) {
            synchronized (Protocal.class) {
                if (instance == null)
                    instance = new Protocal();
            }
        }
        return instance;
    }

    /********
     * 发送查询指令
     ***************************************************/

    public void sendRequest(OutputStream out, String mac) throws Exception {
        Log.d("ConnectionManager", "AbsClient===in" + mac);
        out.write(Protocol.questServer(mac));
        out.flush();
//        if (null != out) {
//            out.close();
//        }
    }

    /***********
     * 接收并解析响应数据
     ***********************************/
    public void receiveResponse(Socket socket, Handler handler) throws Exception {
        DataInputStream input = null;
        byte[] tempbytes = null;
        int tmeplen = 0;
        try {
            Log.d("ConnectionManager", "socket" + socket.toString());
            input = new DataInputStream(socket.getInputStream());
            byte[] inData = new byte[1024];
            while (true) {
                Log.d("ConnectionManager", "read" + inData.length);
                int len = input.read(inData);
                Log.d("ConnectionManager", "过来了");
                byte[] bytes = new byte[len];
                System.arraycopy(inData, 0, bytes, 0, len);
                if (tmeplen > 0) {
                    Log.d("ConnectionManager", "过来了");
                    byte[] inUnPacketData = new byte[tmeplen + len];
                    System.arraycopy(tempbytes, 0, inUnPacketData, 0, tmeplen);
                    System.arraycopy(bytes, 0, inUnPacketData, tmeplen - 1, len);
                    tempbytes = Protocol.DataUnPacket_Server(bytes, socket, handler);
                } else {
                    Log.d("ConnectionManager", "过来了");
                    tempbytes = Protocol.DataUnPacket_Server(bytes, socket, handler);
                    tmeplen = tempbytes.length;
                }
            }


        } catch (SocketException e) {
            Log.d("ConnectionManager", "Socket失败了");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("ConnectionManager", "失败了");
            e.printStackTrace();

        } finally {
            Log.d("ConnectionManager", "失败了");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }
}
