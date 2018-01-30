package NetUtils;


import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;


/**
 *  Given a URI, makes a HttpRequest and stores the result.
 */
public class HttpRequest {

    // TODO Implement other HttpMethods
    public enum HttpMethod {
        GET {
            public String toString() {
                return "GET";
            }
        }
    }

    private URI mUri;
    private String mResult;

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:57.0) Gecko/20100101 Firefox/57.0";
    private static final int CONNECTION_TIMEOUT = 6000;

    public HttpRequest(URI searchUri) {
        mUri = searchUri;
    }


    /**
     * Runs a HttpRequest on this objects' URI.
     *
     * @param httpMethod    The Http Method to use {GET, POST, HEAD etc}
     *
     * @return a boolean indicating whether the request was successful or not.
     */
    public boolean makeRequest(HttpMethod httpMethod) {
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        StringBuilder requestResultStringBuilder = new StringBuilder();

        try {
            conn = (HttpURLConnection) mUri.toURL().openConnection();
            conn.setRequestMethod(httpMethod.toString());
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            // TODO determine course of action for the other response codes, if time.
            switch(conn.getResponseCode()) {

                case HttpURLConnection.HTTP_OK:

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        requestResultStringBuilder.append(line);
                    }
                    bufferedReader.close();
                    mResult = requestResultStringBuilder.toString();
                    return true;

                /*
                    If you land here, you are probably getting Google's `are you a robot?` page.
                    In this case, the code will fall back to the `/Data` folder.
                 */
                case HttpURLConnection.HTTP_UNAVAILABLE:
//                    System.out.println(conn.getResponseCode());
//                    System.out.println(conn.getResponseMessage());
//                    System.out.println(conn.getURL().toString());
                    return false;

                default:
//                    System.out.println("Error code not handled.");
                    return false;
            }

        }
        catch (ProtocolException ex) {
            //TODO Handle Exception -- running silent
            return false;
        }
        catch (IOException ex) {
            //TODO Handle Exception -- running silent
            return false;
        }
        finally {
            conn.disconnect();
        }
    }


    public String getResponseBody() {
        return mResult;
    }


    private void logResponseHeaders(HttpURLConnection conn) {
        Map<String, List<String>> map = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() +
                    " : " + entry.getValue());
        }
    }

}
