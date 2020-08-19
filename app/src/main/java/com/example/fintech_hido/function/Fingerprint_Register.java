package com.example.fintech_hido.function;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fintech_hido.R;

public class Fingerprint_Register extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_register);

        Fingerprint_function fingerprint_function = new Fingerprint_function();
        //fingerprint_function.do_fingerprint(Fingerprint_Register.this, this, "register");
        //User.getInstance().set_info(getIntent().getExtras().getString("session_key").toString(), getIntent().getExtras().getString("package_name").toString());


/*
        Intent intent = new Intent();
        intent.putExtra("result", "true");
        setResult(1000,intent);
        finish();
    }
*/
    }
}
