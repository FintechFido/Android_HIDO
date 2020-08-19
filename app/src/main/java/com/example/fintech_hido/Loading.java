package com.example.fintech_hido;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.toolbox.Volley;
import com.example.fintech_hido.network.AppHelper;
import com.example.fintech_hido.network.SSL_Connection;
import com.example.fintech_hido.network.Server_Connection;

public class Loading extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        start_connection();
    }

    private void start_connection()
    {
        SSL_Connection sslConnection = SSL_Connection.getSsl_connection();
        sslConnection.postHttps(1000, 1000);

        Server_Connection server_connection = new Server_Connection(sslConnection.get_url(),"", Loading.this);
        server_connection.connection();
    }

}
