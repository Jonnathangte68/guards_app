package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
/*import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;*/
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ListaPuntosCriticos extends AppCompatActivity {

    //private final ScheduledExecutorService scheduler =
    //        Executors.newScheduledThreadPool(1);

    public String usuario;
    public String ses;
    public String sucursal;
    public String Holder;
    public int esta_entrando;
    boolean GpsStatus = false;
    private static Handler handler1=null;
    Context x;

    public static final String PREFS_NAME = "MyPrefsFile";


    // Elementos de la Ventana

    private TextView txtUsuario;
    private TextView txtSucursal;
    private ListView lv;
    public static ImageView imgMapa;

    static Activity activityLista;
    static Handler handler;

    private FusedLocationProviderClient mFusedLocationClient;

    Future longRunningTaskFuture;
    boolean RunnableStopped;



    public static final String KEY_USER = "usuario";

    public static int[] prgmImages = {R.drawable.rsz_1red_ball, R.drawable.rsz_camera, R.drawable.rsz_plus, R.drawable.rsz_chat_comments};
    public static int[] imgVariar = {R.drawable.rsz_camera, R.drawable.rsz_plus, R.drawable.rsz_chat_comments};
    public static List<String> prgmNameList = new ArrayList<String>();
    ; //= {"checkpoint1","checkpoint2","checkpoint3", "check4", "ch5", "ch6","ch7","ch2"};
    public static int[] canvas = {R.drawable.rsz_1red_ball, R.drawable.rsz_2green_ball};

    NotificationCompat.Builder notification;
    public static final int uniqueId = 432423;
    //private Date current_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_puntos_criticos);

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
            wl.acquire();
        }catch(Exception e) {
            e.printStackTrace();
        }
        // Dejalo Servicio
        /*Intent serviceIntent = new Intent(ListaPuntosCriticos.this, MyService.class);
        serviceIntent.putExtra("u",usuario);
        startService(serviceIntent);*/

        activityLista = this;
        txtUsuario = (TextView)findViewById(R.id.txtNombreUsuario);

        lv = (ListView) findViewById(R.id.listViewOpciones);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        try {
            if(extras.get("sesion-usuario") != null) {
                this.usuario = extras.get("sesion-usuario").toString();
                txtUsuario.setText(this.usuario);
            }
        }catch (Exception e) {
            System.out.println(e + " Error de usuario");
        }

        nuevoCargaLista();


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                Date currentTime = Calendar.getInstance().getTime();
                System.out.print(currentTime.getTime());
                String tmp = settings.getString("bK","n");
                if(tmp != "n") {

                    /*try {
                        Intent serviceIntent = new Intent(ListaPuntosCriticos.this, MyService.class);
                        serviceIntent.putExtra("u",usuario);
                        startService(serviceIntent);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }*/

                    long hora_fin = Long.parseLong(tmp);// Obtener el long limite
                    long currentLong = currentTime.getTime();


                    if (currentLong > hora_fin && settings.getInt("thread_status",0) == 1) {

                        // Cambiar el estado del Thread
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("thread_status",0);

                        // Actualizar tambien beginTime ponerla en 0
                        editor.putString("bK", "n");


                        // Tratar de enviar datos a servicio

                        /*try {
                            String token = settings.getString("token","y");
                            String puntos = settings.getString("puntos","z");
                            String string_escaped = puntos.replaceAll("&", "!!!!");
                            asyncExecuteStoreBackg(token,usuario,string_escaped,settings.getString("LoadedTimes","0"));

                        }catch(Exception e) {
                            e.printStackTrace();
                        }*/

                        // Borrar actividad en la sucursal
                        editor.putString("puntos","");
                        editor.commit();

                        // Soltar la Bateria

                        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
                        //wl.release();

                        // Guardar toda la Data reportandola al web-service y luego Salir

                        Toast.makeText(x, "Su Ronda ha finalizado", Toast.LENGTH_SHORT).show();
                        Intent loginPageIntent = new Intent(getApplicationContext(), MainActivity.class);
                        loginPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loginPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginPageIntent);
                        finish();
                    }else if(currentLong > (hora_fin-(1*60*1000)) && settings.getInt("thread_status",0) == 1 && settings.getInt("show_alarm",0) == 1){
                        try{
                            Alarma.getInstance().finish();
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                        Intent alarmPageIntent = new Intent(getApplicationContext(), Alarma.class);
                        alarmPageIntent.putExtra("sesion-usuario", usuario);
                        startActivity(alarmPageIntent);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("show_alarm",0);
                        editor.commit();
                    }
                    else {
                        //Toast.makeText(x, "Todavia Falta", Toast.LENGTH_SHORT).show();
                    }
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);

        x=this;
    }

    private void asyncExecuteStoreBackg(String token, String sucursal, final String info, final String timestamps) throws Exception {
        System.out.println("Aqui mis valores");
        System.out.println(token);
        System.out.println(sucursal);
        System.out.println(timestamps);
        final String KEY_INFO = "info";
        final String KEY_SUCURSAL = "sucursal";
        final String KEY_TIMESTAMPS = "tmzs";
        String url = "http://agromovil.agrovizion.com/gservicio_nuevo/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=fetch_data";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ListaPuntosCriticos.this, "Su comentario ha sido registrado", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaPuntosCriticos.this, error.toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, "Datos Incorrectos", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_INFO,info);
                map.put(KEY_SUCURSAL,usuario);
                map.put(KEY_TIMESTAMPS,timestamps);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent serviceIntent = new Intent(ListaPuntosCriticos.this, MyService.class);
        serviceIntent.putExtra("u",usuario);
        startService(serviceIntent);

        try {
            if(intent.getStringExtra("cargarLista").equals("1")){
                nuevoCargaLista();
            }else {
                // Esta entrando
                this.usuario = intent.getStringExtra("sesion-usuario");
                lv.setAdapter(null);
                txtUsuario.setText(usuario);
                nuevoCargaLista();
            }
        } catch (Exception e) {
            System.out.print("Error el intent no venia para refrescar lista");
        }

    }

    private void nuevoCargaLista() {
        List listaNombres = new ArrayList();
        //List listaEstatuss = new ArrayList();
        List listaBolas = new ArrayList();
        listaNombres.clear();
        listaBolas.clear();
        //listaEstatuss.clear();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String result = settings.getString("puntos","empty");

        String[] bolitas = new String[checkpointQty()];
        System.out.println("Esteeeeeeeeeeee -----------------------");
        System.out.println(checkpointQty());

        int i = 0;
        try {
            if (!result.equals("empty") && !result.equals(null)){
                String[] parts = result.split("&;"); // Romper cada cp
                for (String p : parts) {
                    String[] other_parts = p.split(";");

                    for (String op : other_parts) {
                        if (i==0){ // nombre punto
                            listaNombres.add(op);
                            i++;
                        }
                        else if(i==1){ // chequeado
                            if (op.equalsIgnoreCase("1") || op.equalsIgnoreCase("2")) {
                                //Toast.makeText(x, "Val bolita - "+op, Toast.LENGTH_SHORT).show();
                                listaBolas.add("v");
                                bolitas[i] = "verde";
                            } else {
                                //Toast.makeText(x, "Val bolita - "+op, Toast.LENGTH_SHORT).show();
                                listaBolas.add("r");
                                bolitas[i] = "roja";
                            }
                            i++;
                        }
                        else { // activo
                            i = 0;
                        }
                    }
                    //lv.setAdapter(new CustomAdapter(ListaPuntosCriticos.this, listaNombres,prgmImages, bolitas, canvas, usuario,usuario));
                }
            }
        }catch(Exception e) {
            System.out.println(e);
        }

        lv.setAdapter(new CustomAdapter(ListaPuntosCriticos.this, listaNombres,prgmImages, bolitas, canvas, usuario,usuario,listaBolas));

        // Prueba ver como van los valores de las bolas

        for (int h = 0; h < listaNombres.size(); h++) {
            System.out.print(listaNombres.get(h));
            //Toast.makeText(x, listaNombres.get(h).toString(), Toast.LENGTH_SHORT).show();
        }



    }

    public int checkpointQty() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String result = settings.getString("puntos","empty");

        int count = 0;

        try {
            if (!result.equals("empty") && !result.equals(null)){
                String[] parts = result.split("&;"); // Romper cada cp
                for (String p : parts) {
                    count += 1;
                }

            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return count;
    }

    public String getBodyContentType()
    {
        return "application/json; charset=utf-8";
    }

    public static Activity getInstance() {
        return activityLista;
    }
}



