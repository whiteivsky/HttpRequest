import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpRequest {
    // Свойства класса
    private URL url;
    private String query;                //Запрос Query, тело запроса POST
    //Результат (ответ) в виде JSON
    private HttpsURLConnection httpsConnection;
    private String authorization = "authorization";
    private String authorizationToken = "0fdbf21e69e34f7dab68bb207dbe45eb";


    public HttpRequest(String postUrl) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        this.url = new URL(postUrl);
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        httpsConnection = (HttpsURLConnection) url.openConnection();
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, null, new java.security.SecureRandom());
        httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
        httpsConnection.setHostnameVerifier(hv);
        httpsConnection.setRequestMethod("POST");
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void addProperty(String name, String value) {
        httpsConnection.setRequestProperty(name, value);
    }

    public String InvokeHTTP_POST() {
        StringBuilder postResult = new StringBuilder();
        try {

            httpsConnection.setDoOutput(true);
            OutputStream os = httpsConnection.getOutputStream();
            os.write(query.getBytes());
            os.flush();
            os.close();

            if (httpsConnection.getResponseCode() == 200) {
                BufferedReader rd;
                String line;
                rd = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream(), "UTF-8"));

                postResult.append(httpsConnection.getResponseCode());
                while ((line = rd.readLine()) != null) {
                    postResult.append(postResult).append(line);
                }
                rd.close();
                httpsConnection.disconnect();
                return postResult.toString();
            }

            postResult.append(httpsConnection.getResponseCode() + "----" + httpsConnection.getResponseMessage() + " ~~ " + query.toString());
            httpsConnection.disconnect();
            return postResult.toString();

        } catch (Exception e) {
            postResult.append(e.getMessage() + " _ " + url + " \n" + e.getStackTrace().toString());
            httpsConnection.disconnect();
            return postResult.toString();
        }
    }
}