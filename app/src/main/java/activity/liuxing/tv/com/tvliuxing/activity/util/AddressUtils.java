package activity.liuxing.tv.com.tvliuxing.activity.util;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 根据IP地址获取本地定位
 */
public class AddressUtils {
//    public static String GetNetIp() {
//        URL infoUrl = null;
//        InputStream inStream = null;
//        try {
//            // http://iframe.ip138.com/ic.asp
//            // infoUrl = new URL("http://city.ip138.com/city0.asp");
//            infoUrl = new URL("http://ip38.com");
//            URLConnection connection = infoUrl.openConnection();
//            HttpURLConnection httpConnection = (HttpURLConnection) connection;
//            int responseCode = httpConnection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                inStream = httpConnection.getInputStream();
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(inStream, "utf-8"));
//                StringBuilder strber = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null)
//                    strber.append(line + "\n");
//                inStream.close();
//                // 从反馈的结果中提取出IP地址
//                // int start = strber.indexOf("[");
//                // Log.d("zph", "" + start);
//                // int end = strber.indexOf("]", start + 1);
//                // Log.d("zph", "" + end);
//                line = strber.substring(378, 395);
//                line.replaceAll(" ", "");
//                return line;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static String GetNetIp() {
        String IP = "";
        try {
            String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();

// 将流转化为字符串
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String tmpString = "";
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null) {
                    retJSON.append(tmpString + "\n");
                }

                JSONObject jsonObject = new JSONObject(retJSON.toString());
                Log.d("ip", "jsonObject=======" + jsonObject.toString());
                String code = jsonObject.getString("code");
                if (code.equals("0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    IP = data.getString("ip") + "(" + data.getString("country")
                            + data.getString("area") + "区"
                            + data.getString("region") + data.getString("city")
                            + data.getString("isp") + ")";

                    Log.e("提示", "您的IP地址是：" + IP);
                } else {
                    IP = "";
                    Log.e("提示", "IP接口异常，无法获取IP地址！");
                }
            } else {
                IP = "";
                Log.e("提示", "网络连接异常，无法获取IP地址！");
            }
        } catch (Exception e) {
            IP = "";
            Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return IP;
    }

    /**
     * 得到本机的外网ip，出现异常时返回空串""
     *
     * @return
     */
    public static String getPublicIP() {
        String ip = "";

        org.jsoup.nodes.Document doc = null;
        Connection con = null;

        con = Jsoup.connect("http://www.ip138.com/ip2city.asp").timeout(10000);

        try {
            doc = con.get();

            // 获得包含本机ip的文本串：您的IP是：[xxx.xxx.xxx.xxx]
            org.jsoup.select.Elements els = doc.body().select("center");
            for (org.jsoup.nodes.Element el : els) {
                ip = el.text();
            }

            // 从文本串过滤出ip，用正则表达式将非数字和.替换成空串""
            ip = ip.replaceAll("[^0-9.]", "");
        } catch (IOException e) {
            e.printStackTrace();
            return ip;
        }
        return ip;
    }

    /**
     * @param urlStr   请求的地址
     * @param content  请求的参数 格式为：name=xxx&pwd=xxx
     * @param encoding 服务器端请求编码。如GBK,UTF-8等
     * @return
     */
    private String getResult(String urlStr, String content, String encoding) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();// 新建连接实例
            connection.setConnectTimeout(2000);// 设置连接超时时间，单位毫秒，如果运行时出现超时，可自行增大超时时间，如加到10000
            connection.setReadTimeout(2000);// 设置读取数据超时时间，单位毫秒
            connection.setDoOutput(true);// 是否打开输出流 true|false
            connection.setDoInput(true);// 是否打开输入流true|false
            connection.setRequestMethod("POST");// 提交方法POST|GET
            connection.setUseCaches(false);// 是否缓存true|false
            connection.connect();// 打开连接端口
            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());// 打开输出流往对端服务器写数据
            out.writeBytes(content);// 写数据,也就是提交你的表单 name=xxx&pwd=xxx
            out.flush();// 刷新
            out.close();// 关闭输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), encoding));// 往对端写完数据对端服务器返回数据
            // ,以BufferedReader流来读取
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();// 关闭连接
            }
        }
        return null;
    }

    /**
     * unicode 转换成 中文
     *
     * @param theString
     * @return
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * @param content 请求的参数 格式为：name=xxx&pwd=xxx
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getAddresses(String content, String encodingString)
            throws UnsupportedEncodingException {
        // 这里调用pconline的接口
        String urlStr = "http://ip.taobao.com/service/getIpInfo.php";
        // 从http://whois.pconline.com.cn取得IP所在的省市区信息
        Log.d("ip", "content----------" + content);
        String returnStr = this.getResult(urlStr, content, encodingString);
        Log.d("ip", "returnStr----------" + returnStr);
        if (returnStr != null) {
            // 处理返回的省市区信息
            System.out.println(returnStr);
            String[] temp = returnStr.split(",");
            if (temp.length < 3) {
                return "0";//无效IP，局域网测试
            }
            String country = "";
            String area = "";
            String region = "";
            String city = "";
            String county = "";
            String isp = "";
            for (int i = 0; i < temp.length; i++) {
                switch (i) {
                    case 1:
                        country =
                                (temp[i].split(":"))[2].replaceAll("\"", "");
                        country =
                                decodeUnicode(country);//国家
                        break;
                    case 3:
                        area =
                                (temp[i].split(":"))[1].replaceAll("\"", "");
                        area =
                                decodeUnicode(area);//地区
                        break;
                    case 5:
                        region =
                                (temp[i].split(":"))[1].replaceAll("\"", "");
                        region =
                                decodeUnicode(region);//省份
                        break;
                    case 7:
                        city =
                                (temp[i].split(":"))[1].replaceAll("\"", "");
                        city =
                                decodeUnicode(city);//市区
                        break;
                    case 9:
                        county =
                                (temp[i].split(":"))[1].replaceAll("\"", "");
                        county =
                                decodeUnicode(county);//地区
                        break;
                    case 11:
                        isp =
                                (temp[i].split(":"))[1].replaceAll("\"", "");
                        isp =
                                decodeUnicode(isp);//ISP公司
                        break;
                }
            }
            System.out.println(country + "=" + area + "=" + region + "=" + city + "=" + county + "=" + isp);
            return city + "|" + county;
        }
        return null;
    }

    // 这里我们举的例子是获取所在地省份名称，也可以改变上边getAddress的返回值，获取具体的市县名
    public static String getProvinceName() {
        AddressUtils addressUtils = new AddressUtils();
        String ip = "myip";
        String address = "";
        try {
//            if (TextUtils.isEmpty(ip)) {
//                ip = "myip";
//            }
            address = addressUtils.getAddresses("ip=" + ip, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return address;
    }

}