//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {

    // Свойства класса

    private String postUrl;//URL Запроса
    private String query; //Запрос Query, тело запроса POST

    private String postResult;  //Результат (ответ) в виде JSON

    private String Token_Result;
    private String Teams_Name = "";
    private String Teams_ID = "";
    //------------Заголовок HTTP------------
    private List<String> HeaderFieldName;
    private List<String> HeaderFieldValue;
    private HttpsURLConnection con;

    public HttpRequest(String postUrl) throws MalformedURLException {
        this.postUrl = postUrl;
        URL url = new URL(postUrl);
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        try {
            con = (HttpsURLConnection) url.openConnection();
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, new java.security.SecureRandom());
            con.setSSLSocketFactory(sc.getSocketFactory());
            con.setHostnameVerifier(hv);
            con.setRequestMethod("POST");
        } catch (Exception e) {
            String trace = "";
            for (int i = 0; i < e.getStackTrace().length; i++) {
                trace = trace + e.getStackTrace()[i];
            }
            postResult = "catch " + e.getMessage() + " ##" + trace;

        }
    }

    public void addPairRequestProperty(String name, String value) {

    }

    public void setHeadersFieldValue(String[] inHeadersFV) {
        System.out.println("setHeadersFieldValue");
        //Задаём параметры заголовка (Header). Значение заголовка
        HeaderFieldValue = new ArrayList<String>();
        this.HeaderFieldValue.addAll(Arrays.asList(inHeadersFV));
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    //-----------------------------------------------------------------------------------
    public String InvokeHTTP_POST() {

//                for (int i = 0; i < HeaderFieldName.size(); i++) {
//                    con.setRequestProperty(HeaderFieldName.get(i), HeaderFieldValue.get(i));
//                    System.out.println(HeaderFieldName.get(i)+" ->"+ HeaderFieldValue.get(i));
//                }
        try{
            con.setRequestProperty("authorization", "0fdbf21e69e34f7dab68bb207dbe45eb");
            con.setRequestProperty("Content-Type", "application/xml");
            con.setDoOutput(true);
            con.setUseCaches(false);
            OutputStream os = con.getOutputStream();
            os.write(query.getBytes());
            os.flush();
            os.close();
            if (con.getResponseCode() == 200) {
                BufferedReader rd;
                String line;
                rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                while ((line = rd.readLine()) != null) {
                    postResult += line;
                }
                rd.close();
                return con.getResponseCode() + "\n" + postResult + "\n" + con.getResponseMessage();
            }
            return con.getResponseCode() + "----" + con.getResponseMessage() + " ~~ " + query;

        } catch (Exception e) {
            String trace = "";
            for (int i = 0; i < e.getStackTrace().length; i++) {
                trace = trace + e.getStackTrace()[i];
            }
            postResult = "catch " + e.getMessage() + " ##" + trace;
            return postResult;
        }
    }
}
