package com.example.fintech_hido.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSL_Connection
{
    // 싱글톤 패턴
    private String url;
    private static SSL_Connection ssl_connection = new SSL_Connection();

    private SSL_Connection()
    {
        //url = "172.30.1.55";
        url = "172.30.1.14";
    }

    public static SSL_Connection getSsl_connection() {
        return ssl_connection;
    }

    private void Ignore_CA()
    {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if(hostname.equalsIgnoreCase(url))
                    return true;
                else
                    return false;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get_url()
    {
        return url;
    }

    public HttpsURLConnection postHttps(int connTimeout, int readTimeout)
    {
        Ignore_CA();
        trustAllHosts();
        HttpsURLConnection https = null;
        try {
            https = (HttpsURLConnection) new URL("https://"+url).openConnection();
            https.setConnectTimeout(connTimeout);
            https.setReadTimeout(readTimeout);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return https;
    }
}
