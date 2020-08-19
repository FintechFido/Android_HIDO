package com.example.fintech_hido.network;

import android.content.Context;

public class Server_Connection{

    private String url;
    private String path;
    private Context context;

    public Server_Connection(String url, String path, Context context) {
        this.context = context;
        this.path = path;
        this.url = "https://" + url + path;
    }

    public void connection() {
        SendRequest sendRequest = new SendRequest();
        sendRequest.send(url, 0, null, context);
    }


}
