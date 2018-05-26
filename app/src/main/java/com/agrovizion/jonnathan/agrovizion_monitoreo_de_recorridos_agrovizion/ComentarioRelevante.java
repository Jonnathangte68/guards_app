package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ComentarioRelevante extends AppCompatActivity implements View.OnClickListener{

    public static final String PREFS_NAME = "MyPrefsFile";

    private static final String SAVE_COMMENT_URL = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=set_add_comment";

    public String usuario;
    private String checkpoint;
    private String sucursal;
    private String rondin_id;
    private int escaneado = 0;
    // Elementos de la Ventana
    private TextView txtSucursal;
    private TextView txtCheckpointName;
    private TextView txtUsuario;
    private TextView txtComentario;
    private Button btnGuardar;

    public String Holder;
    boolean GpsStatus = false;

    static Activity comentarioRelevante;

    private FusedLocationProviderClient mFusedLocationClient;

    NotificationCompat.Builder notification;
    public static final int uniqueId = 5235234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario_relevante);
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

        comentarioRelevante = this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        notification = new NotificationCompat.Builder(this);

        txtCheckpointName = (TextView)findViewById(R.id.txtCheckpointName);
        txtSucursal = (TextView)findViewById(R.id.txtSucursal);
        txtCheckpointName.setText("");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String nmbCheck = extras.get("checkpoint_selected").toString();
            this.usuario = extras.get("usuario").toString();
            txtCheckpointName.setText(nmbCheck);
            if(extras.get("checkpoint_escaneado_status") != null) {
                escaneado = 1;
            }
        }

        txtUsuario = (TextView)findViewById(R.id.txtNombreUsuario); // Traerlo de Custom List View en el constructor hacerlo un atributo extra
        txtUsuario.setText("");
        txtUsuario.setText(usuario);


        txtComentario = (TextView)findViewById(R.id.txtComentario);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Ha presionado el boton de panico", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnGuardar.setOnClickListener(this);
    }

    public boolean compruebaConexion(Context handler) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public String getRondin() {
        return rondin_id;
    }

    public void setRondin(String r) {
        this.rondin_id = r;
    }

    @Override
    public void onClick(View v) {

        Guardar(txtSucursal.getText().toString(),txtCheckpointName.getText().toString(),usuario);
    }

    private void Guardar(final String sucursal, final String checkpoint, String created_by) {

        if(!txtComentario.getText().toString().isEmpty() && !txtComentario.getText().toString().equalsIgnoreCase("0")) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String result = settings.getString("puntos", "empty");
        SharedPreferences.Editor editor = settings.edit();
        //Toast.makeText(activityLista, result, Toast.LENGTH_LONG).show();
        //String[] bolitas = new String[];

        String nuevoStr = "";

        int i = 0;
        if (!result.equals("empty") && !result.equals(null)) {
            String[] parts = result.split("&;"); // Romper cada cp
            for (String p : parts) {
                String[] other_parts = p.split(";");
                String cname = "";

                System.out.println("Primera impresion lsls");
                System.out.println(nuevoStr);


                for (String op : other_parts) {

                    if (i == 0) {
                        cname = op;
                        nuevoStr += cname;
                        nuevoStr += ";";
                        i++;
                    } else if (i == 1) {
                        if (cname.equalsIgnoreCase(checkpoint)) {
                            //Toast.makeText(this, " Si es igual, Ch escaneado - " + checkpoint + " -- "+ cname, Toast.LENGTH_SHORT).show();
                            if (escaneado == 1) {
                                nuevoStr += "2";
                            } else {
                                nuevoStr += "1";
                            }

                            try {
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                                String auxstr = settings.getString("LoadedTimes","0")+timeStamp+";"+cname+"!";
                                editor.putString("LoadedTimes",auxstr);
                                editor.commit();
                            }catch(Exception e){
                                e.printStackTrace();
                            }


                        } else {
                            //Toast.makeText(this, " No es igual, Ch escaneado - " + checkpoint + " -- " + cname, Toast.LENGTH_SHORT).show();
                            nuevoStr += op;
                        }
                        nuevoStr += ";";
                        i++;
                    } else {

                        if (cname.equalsIgnoreCase(checkpoint)) {
                            nuevoStr += txtComentario.getText();
                        } else {
                            nuevoStr += op;
                        }
                        //nuevoStr += ";";
                        i = 0;
                    }
                }

                nuevoStr += "&;";


                System.out.println("Cadena complete");
                System.out.println(nuevoStr);
            }
        }

        System.out.println(nuevoStr);
        //Toast.makeText(this, nuevoStr, Toast.LENGTH_SHORT).show();


        //SharedPreferences.Editor editor = settings.edit();

        editor.putString("puntos", "");
        editor.commit();
        editor.putString("puntos", nuevoStr);
        editor.commit();

        Toast.makeText(this, "Punto Registrado puede proceder", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(ComentarioRelevante.this, ListaPuntosCriticos.class);
        intent.putExtra("sesion-usuario", usuario);
        intent.putExtra("cargarLista", "1");
        startActivity(intent);


        }else {
            Toast.makeText(this, "Comentario Invalido", Toast.LENGTH_SHORT).show();
        } // Fin del IF si no tiene texto

    }

    public static ComentarioRelevante getInstance(){
        return (ComentarioRelevante) comentarioRelevante;
    }
}
