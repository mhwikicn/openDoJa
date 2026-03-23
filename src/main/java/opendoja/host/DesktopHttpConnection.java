package opendoja.host;

import com.nttdocomo.io.HttpConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class DesktopHttpConnection implements HttpConnection {
    private static final String DEFAULT_USER_AGENT = "DoCoMo/2.0 DOJA_INET_CLIENT(c100;TJ)";

    private final URL url;
    private final int mode;
    private final boolean timeouts;
    private HttpURLConnection connection;
    private String requestMethod = GET;
    private long ifModifiedSince = 0L;
    private boolean userAgentProvidedByGame;

    public DesktopHttpConnection(URL url, int mode, boolean timeouts) {
        this.url = url;
        this.mode = mode;
        this.timeouts = timeouts;
    }

    @Override
    public void connect() throws IOException {
        ensureConnection();
        debug(() -> "HTTP connect " + requestSummary());
        try {
            connection.connect();
        } catch (IOException exception) {
            OpenDoJaLog.warn(DesktopHttpConnection.class, "HTTP connect failed " + requestSummary(), exception);
            throw exception;
        }
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.disconnect();
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        ensureConnection();
        debug(() -> "HTTP openInputStream " + requestSummary());
        try {
            InputStream stream = connection.getInputStream();
            debug(() -> "HTTP input ready " + responseSummary());
            return stream;
        } catch (IOException exception) {
            OpenDoJaLog.warn(DesktopHttpConnection.class, "HTTP openInputStream failed " + requestSummary(), exception);
            throw exception;
        }
    }

    @Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        ensureConnection();
        connection.setDoOutput(true);
        debug(() -> "HTTP openOutputStream " + requestSummary());
        try {
            return connection.getOutputStream();
        } catch (IOException exception) {
            OpenDoJaLog.warn(DesktopHttpConnection.class, "HTTP openOutputStream failed " + requestSummary(), exception);
            throw exception;
        }
    }

    @Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    @Override
    public String getURL() {
        return url.toString();
    }

    @Override
    public void setRequestMethod(String method) throws IOException {
        this.requestMethod = method;
        debug(() -> "HTTP setRequestMethod method=" + method + " url=" + url);
        if (connection != null) {
            connection.setRequestMethod(method);
        }
    }

    @Override
    public void setRequestProperty(String key, String value) throws IOException {
        if (isUserAgentHeader(key)) {
            userAgentProvidedByGame = true;
        }
        ensureConnection();
        debug(() -> "HTTP setRequestProperty " + key + "=" + value + " url=" + url);
        connection.setRequestProperty(key, value);
    }

    @Override
    public int getResponseCode() throws IOException {
        ensureConnection();
        try {
            int code = connection.getResponseCode();
            debug(() -> "HTTP response " + responseSummary(code));
            return code;
        } catch (IOException exception) {
            OpenDoJaLog.warn(DesktopHttpConnection.class, "HTTP getResponseCode failed " + requestSummary(), exception);
            throw exception;
        }
    }

    @Override
    public String getResponseMessage() throws IOException {
        ensureConnection();
        return connection.getResponseMessage();
    }

    @Override
    public String getHeaderField(String name) {
        try {
            ensureConnection();
            return connection.getHeaderField(name);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long getDate() {
        try {
            ensureConnection();
            return connection.getDate();
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override
    public long getExpiration() {
        try {
            ensureConnection();
            return connection.getExpiration();
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override
    public long getLastModified() {
        try {
            ensureConnection();
            return connection.getLastModified();
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override
    public void setIfModifiedSince(long ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
        if (connection != null) {
            connection.setIfModifiedSince(ifModifiedSince);
        }
    }

    @Override
    public String getType() {
        try {
            ensureConnection();
            return connection.getContentType();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getEncoding() {
        try {
            ensureConnection();
            return connection.getContentEncoding();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long getLength() {
        try {
            ensureConnection();
            return connection.getContentLengthLong();
        } catch (IOException e) {
            return -1L;
        }
    }

    private void ensureConnection() throws IOException {
        if (connection != null) {
            return;
        }
        URLConnection raw = url.openConnection();
        if (!(raw instanceof HttpURLConnection httpURLConnection)) {
            throw new IOException("Unsupported non-HTTP URL: " + url);
        }
        connection = httpURLConnection;
        debug(() -> "HTTP create " + requestSummary());
        connection.setRequestMethod(requestMethod);
        connection.setInstanceFollowRedirects(true);
        connection.setUseCaches(false);
        connection.setDoInput(mode != javax.microedition.io.Connector.WRITE);
        connection.setDoOutput(mode != javax.microedition.io.Connector.READ);
        if (timeouts) {
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
        }
        if (ifModifiedSince > 0L) {
            connection.setIfModifiedSince(ifModifiedSince);
        }
        connection.setRequestProperty("Connection", "keep-alive");
        if (!userAgentProvidedByGame) {
            debug(() -> "HTTP default User-Agent " + DEFAULT_USER_AGENT + " url=" + url);
            connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
        }
    }

    private static boolean isUserAgentHeader(String key) {
        return key != null && "User-Agent".equalsIgnoreCase(key);
    }

    private void debug(java.util.function.Supplier<String> messageSupplier) {
        OpenDoJaLog.debug(DesktopHttpConnection.class, messageSupplier);
    }

    private String requestSummary() {
        return "method=" + requestMethod
                + " url=" + url
                + " mode=" + mode
                + " timeouts=" + timeouts;
    }

    private String responseSummary() {
        int code;
        try {
            code = connection.getResponseCode();
        } catch (IOException exception) {
            code = -1;
        }
        return responseSummary(code);
    }

    private String responseSummary(int code) {
        String message;
        try {
            message = connection.getResponseMessage();
        } catch (IOException exception) {
            message = "<unavailable:" + exception.getClass().getSimpleName() + ">";
        }
        return requestSummary()
                + " code=" + code
                + " message=" + message
                + " type=" + connection.getContentType()
                + " length=" + connection.getContentLengthLong();
    }
}
