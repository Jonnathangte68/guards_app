package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion.ScanNfc.TAG;


public class MyService extends Service {
    MyTask myTask;
    private static MyService instance = null;
    public static final String PREFS_NAME = "MyPrefsFile";

    public static boolean isRunning() {
        return instance != null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        if (!myTask.getStatus().equals(AsyncTask.Status.RUNNING)) {myTask.execute();}
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //Log.e(TAG, "onCreate");
        //initializeLocationManager();
        myTask = new MyTask();
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        /*if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }*/
    }

    private class MyTask extends AsyncTask<String, String, String> {

        private DateFormat dateFormat;
        private String date;
        private boolean cent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            cent = true;
        }

        @Override
        protected String doInBackground(String... params) {
            while (cent){
                date = dateFormat.format(new Date());
                try {
                    publishProgress(date);
                    // Stop 5s
                    Thread.sleep(180000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), "Ve al Checkpoint - Hora actual: " + values[0], Toast.LENGTH_SHORT).show();

            try{
                final String KEY_INFO = "info";
                final String KEY_SUCURSAL = "sucursal";
                final String KEY_TIMESTAMPS = "tmzs";
                //final String KEY_TOKENIZZZER = "tokenizzzer";
                String url = "http://agromovil.agrovizion.com/gservicio_nuevo/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=fetch_data";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                    //SharedPreferences.Editor editor = settings.edit();
                                    //editor.putString("tokenizzzer",response.toString());
                                    //editor.commit();
                                }catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(MainActivity.this, "Datos Incorrectos", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        String token = settings.getString("token","y");
                        String puntos = settings.getString("puntos","z");
                        //String tokenizzzer = settings.getString("tokenizzzer","null");
                        final String info = puntos.replaceAll("&", "!!!!");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put(KEY_INFO,info);
                        map.put(KEY_SUCURSAL,settings.getString("sucursal","0"));
                        map.put(KEY_TIMESTAMPS,settings.getString("LoadedTimes","0"));
                        //map.put(KEY_TOKENIZZZER,tokenizzzer);
                        return map;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

                stringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 200000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 2000000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }




        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
        }
    }
}