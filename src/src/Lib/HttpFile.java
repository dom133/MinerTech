package Lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpFile {
	 
    int length, code;
    String type, encoding, msg;
    Map<String, List<String>> headers;
    byte[] data;
    InputStream errorStream = null;
    public final int READ_CHUNK_SIZE = 8192; // 8kB
     
    @SuppressWarnings("unused")
	public HttpFile(URL url) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("URL protocol must be HTTP.");
        }
        // Set up a request.
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-agent", "Java Spider");
 
        try {
            conn.connect();
 
            code      = conn.getResponseCode();
            msg       = conn.getResponseMessage();
            headers  = conn.getHeaderFields();
            length    = conn.getContentLength();
            type      = conn.getContentType();
            encoding = conn.getContentEncoding();
 
            if (encoding == null) {
                Pattern p = Pattern.compile("(; )?charset=(.+)");
                Matcher m = p.matcher(type);
                if (m.find()) {
                    encoding = m.group(2);
                }
            }
            InputStream stream = conn.getInputStream();
            errorStream   = conn.getErrorStream();
            data = new byte[Math.max(length, stream.available())];
            byte[] buffer = new byte[READ_CHUNK_SIZE];
            int total, n;
            total = n = stream.read(data);
            for (n = stream.read(buffer); n != -1; n = stream.read(buffer), total += n) {
                byte[] new_data = new byte[data.length + n];
                System.arraycopy(data, 0, new_data, 0, data.length);
                System.arraycopy(buffer, 0, new_data, data.length, n);
                data = new_data;
            }
        } finally {
            conn.disconnect();
        }
    }
 
    public String getEncoding() {
        return encoding;
    }
 
    public String getContentType() {
        return type;
    }
 
    public int getContentLength() {
        return length;
    }
 
    public int getResponseCode() {
        return code;
    }
 
    public Map<String, List<String>> getHeaderFields() {
        return headers;
    }
 
    public byte[] getData() {
        return data;
    }
 
    public InputStream getErrorStream() {
        return errorStream;
    }
}