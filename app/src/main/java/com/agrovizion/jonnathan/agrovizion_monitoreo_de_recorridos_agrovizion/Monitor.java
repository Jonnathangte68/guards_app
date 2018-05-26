package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

/**
 * Created by admin on 19/04/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

public class Monitor extends BroadcastReceiver{
    public void onReceive(Context context,Intent intent) {
   /*   Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/

      /*  Intent serviceIntent = new Intent();
        serviceIntent.setAction("gps2");
                context.startService(serviceIntent);*/

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent serviceIntent = new Intent(context,MyService.class);
        if(!MyService.isRunning())context.startService(serviceIntent);
    }
}