package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EscaneoDePuntoCriticos extends AppCompatActivity {

    private String usuario;
    private String checkpoint;
    private String sucursal;

    //public static final String KEY_CHECKPOINT = "checkpoint";
    //public static final String KEY_SUCURSAL = "sucursal";
    //public static final String KEY_COMENTARIO = "comentario";
    public static final String KEY_USER = "usuario";

    private TextView txtNombreUsuario;
    private TextView txtNombreSucursal;
    private TextView txtNombreCheckpoint;
    private Button btnRegresar;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_de_punto_criticos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtNombreUsuario = (TextView)findViewById(R.id.txtNombreUsuario);
        txtNombreSucursal = (TextView)findViewById(R.id.txtNombreSucursal);
        txtNombreCheckpoint = (TextView)findViewById(R.id.txtCheckpointName);
        btnRegresar = (Button)findViewById(R.id.btnRegresar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.usuario = extras.get("sesion-usuario").toString();
            this.checkpoint = extras.get("sesion-checkpoint").toString();
        }

        txtNombreUsuario.setText(usuario);

        if (usuario != null){

            if (!compruebaConexion(this)) {
                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            } else {
                searchSucursal();
                searchCheckpoint();
            }
        }

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        c=this;

    }

    public boolean compruebaConexion(EscaneoDePuntoCriticos handler) {

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

    public void searchSucursal() {
        final String user = usuario;
        final String GET_SUCURSAL_URL = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=get_pcc_active_sucursal&usuario="+usuario;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_SUCURSAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            setSucursal("Sin sucursal");
                            txtNombreSucursal.setText("Sin sucursal");
                        }
                        else {
                            txtNombreSucursal.setText(response);
                            setSucursal(response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EscaneoDePuntoCriticos.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USER,usuario);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getSucursal() {
        return this.sucursal;
    }

    public void searchCheckpoint() {
        final String check = checkpoint;
        final String GET_CHKPOINT_URL = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=get_pcc_active_checkpoint&checkpoint="+check;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_CHKPOINT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ComentarioRelevante.this,response,Toast.LENGTH_LONG).show();
                        txtNombreCheckpoint.setText(response);
                        if (response.isEmpty()) {
                            //setSucursal("Sin sucursal");
                            //txtSucursal.setText("Sin sucursal");
                            txtNombreCheckpoint.setText(response);
                        }
                        else {
                            //txtSucursal.setText(response);
                            //setSucursal(response.toString());
                            txtNombreCheckpoint.setText(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EscaneoDePuntoCriticos.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USER,usuario);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }





}
