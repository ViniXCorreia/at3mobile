package com.example.at3mobile;

public class HttpMock {

    static class URL {
        public URL(String url) {}
        public HttpURLConnection openConnection() {
            return new HttpURLConnection();
        }
    }

    static class OutputStream {
        public void write(byte[] data) {}
    }

    static class HttpURLConnection {
        public void setRequestMethod(String method) {}
        public void setRequestProperty(String key, String value) {}
        public void setDoOutput(boolean doOutput) {};

        public OutputStream getOutputStream() {
            return new OutputStream();
        };

        public int getResponseCode() {
            return 200;
        }
    }
}
