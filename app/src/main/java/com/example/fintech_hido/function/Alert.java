package com.example.fintech_hido.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

public class Alert extends AppCompatActivity {

    static AlertDialog.Builder builder;
    public static void alert_function(Context context, String mode) {
        builder = new AlertDialog.Builder(context);

        switch (mode) {
            case "loading":
                builder.setTitle("알림").setMessage("서버의 응답이 없습니다");
                setneutralButton("exit", context);
                break;
            case "fail":
                builder.setTitle("알림").setMessage("요청이 실패했습니다");
                setneutralButton("normal", context);
                break;
            case "register":
                builder.setTitle("알림").setMessage("지문정보가 이미 존재합니다");
                setneutralButton("register", context);
                break;
            case "register_result":
                builder.setTitle("알림").setMessage("지문정보 등록에 실패했습니다");
                setneutralButton("register_result", context);
                break;
            case "auth":
                builder.setTitle("알림").setMessage("지문정보가 없습니다");
                setneutralButton("auth_function", context);
                break;
            case "auth_result":
                builder.setTitle("알림").setMessage("인증에 실패했습니다");
                setneutralButton("result", context);
                break;
        }
    }

    private static void setneutralButton(String mode, Context para_context)
    {
        final Context context = para_context;
        switch (mode) {
            case "exit":
                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.runFinalization();
                        System.exit(0);
                    }
                });
                break;
            case "normal":
                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;
            case "register":
                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.call_back(false);
                    }
                });
                break;
            case "result":
                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.return_result(false);
                    }
                });
                break;
            case "auth_function":
                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fingerprint_function fingerprint_function = (Fingerprint_function) context;
                        fingerprint_function.auth_function(false);
                    }
                });
                break;
        }
        alert_show();
    }

    private static void alert_show(){
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}