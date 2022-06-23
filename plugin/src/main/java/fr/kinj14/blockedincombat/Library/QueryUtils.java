package fr.kinj14.blockedincombat.Library;

import fr.kinj14.blockedincombat.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 *
 * @author Kinj14
 * Allows easy management of query
 *
 */
public class QueryUtils {
    protected final Main main = Main.getInstance();

    public static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }

    public String query_POST(String URL, Map<String, String> parameters) {
        try
        {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            StringBuilder response = new StringBuilder();
            String responseLine = null;
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            con.disconnect();
            return response.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public String query_GET(String URL, Map<String, String> parameters) {
        try
        {
            HttpURLConnection con = (HttpURLConnection)new URL(URL).openConnection();
            con.setRequestMethod("GET");

            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1");

            int reponseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
