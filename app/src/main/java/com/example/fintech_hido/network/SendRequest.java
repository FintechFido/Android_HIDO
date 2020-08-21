package com.example.fintech_hido.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.fintech_hido.MainActivity;
import com.example.fintech_hido.function.Alert;
import com.example.fintech_hido.function.Fingerprint_function;
import com.example.fintech_hido.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendRequest extends Activity {
/*
Request.Method.~
GET = 0
POST = 1
DELETE = 3
PATCH = 7
 */
    public JSONObject jsonObject;
    public Context context;

    public void send(String url, int method, final HashMap<String, String> hashMap, Context context)
    {
        this.context = context;
        StringRequest request = new StringRequest(
                method,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("SUCCESS : " + response);
                        try {
                            jsonObject = new JSONObject(response);
                            find_mode();
                        }catch (Exception e){
                            e.printStackTrace();
                            find_mode();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR : "+ error.getMessage());
                        find_mode();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMap;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue.add(request);
    }

    // 결과 처리
    public void find_mode()
    {
        try {
            if(jsonObject.has("mode")) {
                // 접속 //
                if (jsonObject.getString("mode").toString().equals("access")) {
                    if (jsonObject.getString("result").toString().equals("true")) {
                        // 접속 성공
                        Handler handler = new Handler();
                        final Intent next_intent = new Intent(context, MainActivity.class);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                next_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                next_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(next_intent);
                            }
                        }, 1000);
                    } else {
                        // 접속 실패
                        Alert.alert_function(context, "loading");
                    }
                }
                else if (jsonObject.getString("mode").toString().equals("register_check")) {
                    //  지문등록 사전확인
                    if (jsonObject.getString("result").toString().equals("true")) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.do_fingerprint();
                    } else {
                        Alert.alert_function(context, "register");
                    }
                }
                else if (jsonObject.getString("mode").toString().equals("register_key")) {
                    //  지문등록 결과
                    if (jsonObject.getString("result").toString().equals("true")) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.return_result(true);
                    } else {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.call_back(false);
                        //Alert.alert_function(context, "register");
                    }
                }
                else if (jsonObject.getString("mode").toString().equals("fingerprint_valid")) {
                    //  지문인증 전 유무 확인
                    if (jsonObject.getString("result").toString().equals("true")) {
                        User.getInstance().set_challenge_number(jsonObject.getString("challenge_number"));
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.auth_function(true);
                    } else {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.auth_function(false);
                    }
                }
                else if (jsonObject.getString("mode").toString().equals("auth")) {
                    //  지문인증 결과
                    if (jsonObject.getString("result").toString().equals("true")) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.return_result(true);
                    } else {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.return_result(false);
                    }
                }
                else {
                    Alert.alert_function(context, "fail");
                }
            }
        }catch (Exception e) {
            // 온갖 오류
            e.printStackTrace();
            Alert.alert_function(context, "fail");
        }
    }
}
