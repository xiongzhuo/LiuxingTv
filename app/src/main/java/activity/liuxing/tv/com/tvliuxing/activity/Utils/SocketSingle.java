package activity.liuxing.tv.com.tvliuxing.activity.Utils;

import java.io.IOException;
import java.net.Socket;

public class SocketSingle {
    private static Socket instance = null;

    public static Socket getInstance(String ip, int port, boolean isTool) {
        if (instance == null || isTool) {
            try {
                instance = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}
