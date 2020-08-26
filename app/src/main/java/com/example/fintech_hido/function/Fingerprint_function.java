package com.example.fintech_hido.function;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.Volley;
import com.example.fintech_hido.R;
import com.example.fintech_hido.model.User;
import com.example.fintech_hido.network.AppHelper;
import com.example.fintech_hido.network.SSL_Connection;
import com.example.fintech_hido.network.SendRequest;

import java.util.HashMap;
import java.util.concurrent.Executor;

public class Fingerprint_function extends AppCompatActivity
{
    private static final String TAG = "Fingerprint_function";
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String mode;
    private Intent call_intent;
    private User user=  User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        call_intent = new Intent();
        SSL_Connection sslConnection = SSL_Connection.getSsl_connection();
        sslConnection.postHttps(1000, 1000);
        AppHelper.requestQueue = Volley.newRequestQueue(Fingerprint_function.this);
        mode = getIntent().getExtras().getString("mode");

        if(mode.equals("register")) {
            setContentView(R.layout.fingerprint_register);
            register_function();
        }

        else if(mode.equals("auth_check")) {
            setContentView(R.layout.fingerprint_auth);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("session_key", getIntent().getExtras().getString("session_key").toString());
            hashMap.put("imei", getIntent().getExtras().getString("imei").toString());
            hashMap.put("running", String.valueOf(getIntent().getExtras().getInt("running")));
            if(getIntent().getExtras().getString("saved").toString().equals("A 은행"))
                hashMap.put("saved", "1");
            else if(getIntent().getExtras().getString("saved").toString().equals("B 은행"))
                hashMap.put("saved", "2");
            hashMap.put("mode", "auth_check");
            System.out.println(hashMap);
            SendRequest sendRequest = new SendRequest();
            // send(String url, int method, final HashMap<String, String> hashMap, Context context)
            sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/fingerprint/valid",
                    1, hashMap , Fingerprint_function.this);
        }
    }

    @Override
    public void onBackPressed(){
        call_back(false);
    }

    public void register_function()
    {
        System.out.println("CHECK : register function");
        //set_info(String session_key, String package_name, int running_code, int saved_code, String imei)
        user.set_info(getIntent().getExtras().getString("session_key"),
                getIntent().getExtras().getInt("running"),
                0,
                getIntent().getExtras().getString("imei"));

        SendRequest sendRequest = new SendRequest();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("session_key", user.get_session_key());
        hashMap.put("running", String.valueOf(user.get_running_code()));
        hashMap.put("imei", user.get_imei());
        System.out.println("HASH MAP check : "+hashMap);
        // send(String url, int method, final HashMap<String, String> hashMap, Context context)
        sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/registration/fingerprint",
                1, hashMap, Fingerprint_function.this);
    }


    protected String getPublicKey() {
        RSACryptor rsaCryptor= RSACryptor.getInstance();
        rsaCryptor.init(this);
        return rsaCryptor.getPublicKeyStr();
    }

    private String getSignedCN(String CN) {
        RSACryptor rsaCryptor= RSACryptor.getInstance();
        rsaCryptor.init(this);
        return rsaCryptor.getDigitalSignature(this.getPackageName(), CN);
    }


    protected  String Encrypt(String text) {

        RSACryptor rsaCryptor= RSACryptor.getInstance();
        rsaCryptor.init(this);
        String privateKey = rsaCryptor.decryptTest(text);
        return privateKey;

    }


    public void auth_function(boolean result)
    {
        if(result) {
            mode = "auth";
            do_fingerprint();
        }
        else {
            mode = "auth";
            call_intent.putExtra("result", "false");
            setResult(4000, call_intent);
            finish();
        }
    }

    public void do_fingerprint() {
        System.out.println("CHECK : do fingerprint");
        executor = ContextCompat.getMainExecutor(Fingerprint_function.this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                call_back(false);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                call_back(true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Fingerprint_function.this, "지문 인식에 실패했습니다",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("HIDO 지문 등록")
                .setNegativeButtonText("취소")
                .build();
        biometricPrompt.authenticate(promptInfo);

    }


    public void call_back(boolean result)
    {
        System.out.println("CHECK : call back");
        if(result) {
            call_intent.putExtra("result", "true");

            if (mode.equals("register")) {
                setResult(1000,  call_intent);

                // 서버에 Session key, 구동 앱 은행 코드, public key, imei 전송
                SendRequest sendRequest = new SendRequest();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("session_key", user.get_session_key());
                hashMap.put("running", String.valueOf(user.get_running_code()));
                hashMap.put("imei", user.get_imei());
                hashMap.put("public_key", getPublicKey());
                System.out.println("HASH MAP check : " + hashMap);
                // send(String url, int method, final HashMap<String, String> hashMap, Context context)
                sendRequest.send("https://" + SSL_Connection.getSsl_connection().get_url() + "/registration/key",
                        1, hashMap, Fingerprint_function.this);
            }
            else if(mode.equals("auth")){

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("session_key", getIntent().getExtras().getString("session_key").toString());
                hashMap.put("imei", getIntent().getExtras().getString("imei").toString());
                hashMap.put("running", String.valueOf(getIntent().getExtras().getInt("running")));
                //hashMap.put("saved", getIntent().getExtras().getString("saved").toString());
                if(getIntent().getExtras().getString("saved").toString().equals("A 은행"))
                    hashMap.put("saved", "1");
                else if(getIntent().getExtras().getString("saved").toString().equals("B 은행"))
                    hashMap.put("saved", "2");
                hashMap.put("mode", "auth");
                hashMap.put("challenge_number", getSignedCN(user.get_challenge_number()));

                SendRequest sendRequest = new SendRequest();
                // send(String url, int method, final HashMap<String, String> hashMap, Context context)
                sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/auth",
                        1, hashMap , Fingerprint_function.this);
            }
        }
        else {
            call_intent.putExtra("result", "false");
            if (mode.equals("register"))
                setResult(1000,  call_intent);
            else if(mode.equals("auth"))
                setResult(4000,  call_intent);
            finish();
        }
    }


    public void return_result(boolean result) {

        if(mode.equals("register")) {

            if (result) {
                setResult(1000,  call_intent);
                call_intent.putExtra("result", "true");
                finish();
            } else {
                setResult(1000,  call_intent);
                call_intent.putExtra("result", "false");
                finish();
            }
        }
        else if(mode.equals("auth")) {
            if (result) {
                call_intent.putExtra("result","true");
                setResult(4000, call_intent);
                finish();
            } else {
                call_intent.putExtra("result","false");
                setResult(4000, call_intent);
                finish();
            }
        }
        System.out.println(mode);
    }
}