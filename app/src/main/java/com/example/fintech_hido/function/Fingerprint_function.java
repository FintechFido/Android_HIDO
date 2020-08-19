package com.example.fintech_hido.function;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.fintech_hido.R;
import com.example.fintech_hido.model.User;
import com.example.fintech_hido.network.SSL_Connection;
import com.example.fintech_hido.network.SendRequest;

import java.util.HashMap;
import java.util.concurrent.Executor;

public class Fingerprint_function extends AppCompatActivity
{
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String mode;
    private Intent call_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        call_intent = new Intent();
        mode = getIntent().getExtras().getString("mode");
        if(mode.equals("register")) {
            setContentView(R.layout.fingerprint_register);
            register_function();
        }
        else if(mode.equals("auth")){
            setContentView(R.layout.fingerprint_auth);

        }
        else if(mode.equals("auth_check")) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("session_key", getIntent().getExtras().getString("session_key").toString());
            hashMap.put("imei", getIntent().getExtras().getString("imei").toString());
            hashMap.put("running", getIntent().getExtras().getString("running").toString());
            hashMap.put("saved", getIntent().getExtras().getString("saved").toString());
            hashMap.put("mode", "auth_check");
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
        //set_info(String session_key, String package_name, int running_code, int saved_code, String imei)
        User.getInstance().set_info(getIntent().getExtras().getString("session_key"),
                getIntent().getExtras().getInt("running"),
                0,
                getIntent().getExtras().getString("imei"));

        SendRequest sendRequest = new SendRequest();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("session_key", User.getInstance().get_session_key());
        hashMap.put("running", String.valueOf(User.getInstance().get_running_code()));
        hashMap.put("imei", User.getInstance().get_imei());
        System.out.println("HASH MAP check : "+hashMap);
        // send(String url, int method, final HashMap<String, String> hashMap, Context context)
        sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/registration/fingerprint",
                1, hashMap, Fingerprint_function.this);
    }

    public void auth_function(boolean result)
    {
        if(result) {
            mode = "auth";
            do_fingerprint();
        }
        else {
            call_intent.putExtra("result", "true");
            setResult(4000, call_intent);
            finish();
        }
    }

    public void do_fingerprint() {
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

        if(result) {
            call_intent.putExtra("result", "true");

            if (mode.equals("register")) {
                setResult(1000,  call_intent);

                /*
                1. 지문 정보 생성
                 */

                // 서버에 Session key, 구동 앱 은행 코드, public key, imei 전송
                SendRequest sendRequest = new SendRequest();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("session_key", User.getInstance().get_session_key());
                hashMap.put("running", String.valueOf(User.getInstance().get_running_code()));
                hashMap.put("imei", User.getInstance().get_imei());
                hashMap.put("public_key", "key"); // 생성한 키 보내기
                System.out.println("HASH MAP check : "+hashMap);
                // send(String url, int method, final HashMap<String, String> hashMap, Context context)
                sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/registration/key",
                        1, hashMap, Fingerprint_function.this);
            }
            else if(mode.equals("auth")){

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("session_key", getIntent().getExtras().getString("session_key").toString());
                hashMap.put("imei", getIntent().getExtras().getString("imei").toString());
                hashMap.put("running", getIntent().getExtras().getString("running").toString());
                hashMap.put("saved", getIntent().getExtras().getString("saved").toString());
                hashMap.put("mode", "auth");

                //
                hashMap.put("challenge_number", User.getInstance().get_challenge_number());
                // key로 암호화해서 전송해야 한다!


                //

                SendRequest sendRequest = new SendRequest();
                // send(String url, int method, final HashMap<String, String> hashMap, Context context)
                sendRequest.send("https://"+SSL_Connection.getSsl_connection().get_url()+"/auth/challenge",
                        1, hashMap , Fingerprint_function.this);
            }
        }
        else {
            call_intent.putExtra("result", "false");
            if (mode.equals("register"))
                setResult(1000,  call_intent);
            else if(mode.equals("auth"))
                setResult(2000,  call_intent);
            finish();
        }
    }

    public void return_result(boolean result) {

        if(mode.equals("register")) {
            if (result) {
                call_intent.putExtra("result", "true");
                finish();
            } else {
                Alert.alert_function(Fingerprint_function.this, "register");
            }
        }
        else if(mode.equals("auth")) {
            if (result) {
                call_intent.putExtra("result", "true");
                finish();
            } else {
                Alert.alert_function(Fingerprint_function.this, "register");
                // 이 부분 따로 만들어줘야 한다
            }

        }

    }
}
