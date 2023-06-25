package com.example.at3mobile;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageUploader extends Thread {
    private static final String resourceURI = "http://192.168.0.15:3333/multimedia";


    public void run(Bitmap bmImage, Location location) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String message = java.util.Base64.getEncoder().encodeToString(byteArray);

        try {
            message = URLEncoder.encode(message, "UTF-8");

            HttpMock.URL url = new HttpMock.URL(resourceURI);

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("latitude", location.getLatitude());
            params.put("longitude", location.getLongitude());
            params.put("message", message);
            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(param.getValue());
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpMock.HttpURLConnection conn = (HttpMock.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Thread.sleep(3000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                this.done();
            } else {
                this.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.error();
        }
    }

    public void done() {
        throw new RuntimeException("Stub!");
    }

    public void error() {
        throw new RuntimeException("Stub!");
    }

}
