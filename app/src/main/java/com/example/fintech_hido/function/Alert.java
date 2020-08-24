package com.example.fintech_hido.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

public class Alert extends AppCompatActivity {

    static AlertDialog.Builder builder;
    public static void alert_function(Context context, String mode) {
        builder = new AlertDialog.Builder(context);

        if(mode.equals("loading")){
            builder.setTitle("알림").setMessage("서버의 응답이 없습니다");
            setneutralButton("exit",context);
        }
        else if(mode.equals("fail")){
            builder.setTitle("알림").setMessage("요청이 실패했습니다");
            setneutralButton("normal",context);
        }
        else if(mode.equals("register")){
            builder.setTitle("알림").setMessage("지문정보가 이미 존재합니다");
            setneutralButton("register",context);
        }
        else if(mode.equals("register_result")){
            builder.setTitle("알림").setMessage("지문정보 등록에 실패했습니다");
            setneutralButton("register_result",context);
        }
        else if(mode.equals("auth")){
            builder.setTitle("알림").setMessage("지문정보가 없습니다");
            setneutralButton("auth_function",context);
        }
        else if(mode.equals("auth_result")){
            builder.setTitle("알림").setMessage("인증에 실패했습니다");
            setneutralButton("result",context);
        }
    }

    private static void setneutralButton(String mode, Context para_context)
    {
        final Context context = para_context;
        if(mode.equals("exit")){
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.runFinalization();
                    System.exit(0);
                }
            });
        }
        else if(mode.equals("normal")){
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        else if(mode.equals("register")){
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fingerprint_function fingerprint_function = (Fingerprint_function)context;
                    fingerprint_function.call_back(false);
                }
            });
        }
        else if(mode.equals("result")){
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fingerprint_function fingerprint_function = (Fingerprint_function)context;
                    fingerprint_function.return_result(false);
                }
            });
        }
        else if(mode.equals("auth_function")){
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fingerprint_function fingerprint_function = (Fingerprint_function)context;
                    fingerprint_function.auth_function(false);
                }
            });
        }
        alert_show();
    }

    private static void alert_show(){
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}