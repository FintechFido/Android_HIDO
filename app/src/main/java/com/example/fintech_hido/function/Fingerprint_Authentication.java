package com.example.fintech_hido.function;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fintech_hido.R;

public class Fingerprint_Authentication extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_auth);

        /*
        Intent intent = getIntent();
        System.out.println(intent.getExtras().getString("session_key")+" "+intent.getExtras().getString("package_name"));
        User.getInstance().set_info(intent.getExtras().getString("session_key").toString(), intent.getExtras().getString("package_name").toString());

        Fingerprint_function fingerprint_function = new Fingerprint_function(intent.getExtras().getString("session_key").toString(), intent.getExtras().getString("package_name").toString());
        fingerprint_function.do_fingerprint(Fingerprint_Authentication.this,this, "auth");

        Intent now_intent = new Intent();
            now_intent.putExtra("res", "SUCCESS");
        setResult(1000,now_intent);
        finish();

         */

    }

}