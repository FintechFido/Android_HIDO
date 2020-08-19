package com.example.fintech_hido.function;

import android.app.Activity;
import android.os.Bundle;

import com.example.fintech_hido.R;

public class fail_function extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fail);

        /*
        Intent intent = getPackageManager().getLaunchIntentForPackage(User.getInstance().get_package_name());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (getIntent().getExtras().getString("mode").equals("register"))
            intent.putExtra("mode", "register");
        else if (getIntent().getExtras().getString("mode").equals("auth"))
            intent.putExtra("mode", "auth");
        intent.putExtra("result", "FALSE");



        //startActivity(intent);
        ActivityCompat.finishAffinity(this);
        moveTaskToBack(true);
        finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
/*
        moveTaskToBack(true);
        finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());

 */
    }

}
