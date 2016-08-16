package cn.ecpark.czhinstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @className: Util
 * @classDescription: 工具类
 * @author: swallow
 * @createTime: 2015/10/29
 */
public class Util {

    public static void doRequest(final String url, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL connectionUrl = null;
                try {
                    connectionUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                        callback.fail(connection.getResponseCode());
                    }else {
                        BufferedReader reader = new BufferedReader(isr);

                        if (url.endsWith("/Android%2FProduct")) {
                            //The main page, get the apk types
                            ArrayList<String> types = analyseType(reader);
                            callback.success(types, connection.getResponseCode());
                        } else {
                            //The detail page, get the apk list
                            ArrayList<String> apks = analyseApk(reader);
                            callback.success(apks, connection.getResponseCode());
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static ArrayList<String> analyseApk(BufferedReader reader){
        ArrayList<String> result = new ArrayList<>();
        try{
            do {
                String line = reader.readLine();
                if (line.contains("<a href=\"../../../blob/iAuto360-Release.git/master/Android%2FProduct%2F")){
                    line = line.substring(line.indexOf("/blob/iAuto360-Release.git"), line.indexOf(".apk")) + ".apk";
                    line = line.replace("blob", "raw");
                    line = "http://code.net.ecpark.cn:8888"+line;
                    line = URLDecoder.decode(line, "utf-8");
                    result.add(line);
                }
            } while (reader.read() != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(result);
        return result;
    }

    private static ArrayList<String> analyseType(BufferedReader reader) {
        ArrayList<String> result = new ArrayList<>();
        try {
            do {
                String line = reader.readLine();
                if (line.contains("<a href=\"../../../tree/iAuto360-Release.git/master/Android%2FProduct%2F")) {
                    line = line.substring(line.indexOf("%2FProduct%2F")+"%2FProduct%2F".length(), line.indexOf(";"));
                    if (!result.contains(line))
                        result.add(line);
                }
            } while (reader.read() != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface Callback{
        void success(ArrayList<String> content, int code);
        void fail(int code);
    }
}
