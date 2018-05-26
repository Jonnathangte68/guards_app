package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class Alarma extends AppCompatActivity {

    public String usuario;
    public String sucursal;

    public TextView txtSucursalName;
    public TextView txtNombreUsuario;
    public TextView checks;

    public Button btnSeguir;
    static Activity alarmilla;


    public ArrayList<String> elementos;

    public ListView listView;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
            wl.acquire();
        }catch(Exception e) {
            e.printStackTrace();
        }

        alarmilla = this;

        elementos = new ArrayList<String>();
        listView = (ListView)findViewById(R.id.listaFaltantes);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String result = settings.getString("puntos","empty");

        MediaPlayer mPlayer = MediaPlayer.create(Alarma.this, R.raw.alert);
        mPlayer.start();
        txtSucursalName = (TextView)findViewById(R.id.txtSucursalName);
        txtNombreUsuario = (TextView)findViewById(R.id.txtNombreUsuario);
        btnSeguir = (Button)findViewById(R.id.btnSeguir);


        /*Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.usuario = extras.get("sesion-usuario").toString();
            txtSucursalName.setText(usuario);
            txtNombreUsuario.setText(usuario);
        }*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Alarma.this, ListaPuntosCriticos.class);
                intent.putExtra("sesion-usuario",usuario);
                startActivity(intent);
            }
        });

        int i = 0;
        if (!result.equalsIgnoreCase("empty")){
            String[] parts = result.split("&;"); // Romper cada cp
            for (String p : parts) {
                String[] other_parts = p.split(";");
                for (String op : other_parts) {
                    if (i==0){
                        elementos.add(op);
                        i++;
                    }
                    else if(i==1){
                        if (op.equalsIgnoreCase("1") || op.equalsIgnoreCase("2")) {
                            elementos.remove(elementos.size() - 1);
                        }
                        i++;
                    }
                    else {
                        i = 0;
                    }
                }
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                Alarma.this,
                android.R.layout.simple_list_item_1,
                elementos
        );
        listView.setAdapter(arrayAdapter);

        try {
            txtSucursalName.setText(settings.getString("sucursal",""));
            txtNombreUsuario.setText(settings.getString("sucursal",""));
        }catch(Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("show_alarm",0);
        editor.commit();
    }

    public static Activity getInstance() {
        return alarmilla;
    }
}
