package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class CreateNotificationActivity extends AppCompatActivity {

    TextView txtContenido;
    Button regresar;
    String usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.usuario = extras.get("usuario").toString();
        }

        txtContenido = (TextView)findViewById(R.id.contenidoNotificacion);
        regresar = (Button)findViewById(R.id.btnRegresar);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNotificationActivity.this,ListaPuntosCriticos.class);
                intent.putExtra("sesion", "1");
                intent.putExtra("sesion-usuario", usuario);
                startActivity(intent);
            }
        });
        cargarContenido();
    }

    public void cargarContenido() {
        String STATUS_NOTIFICATION = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=get_pcc_notificaciones_guardia_superior";
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
        } else {
            Ion.with(CreateNotificationActivity.this)
                    .load(STATUS_NOTIFICATION)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {

                            if (result != "") {
                                txtContenido.setText(result);
                            }
                        }
                    });
        }
    }

    public boolean compruebaConexion(CreateNotificationActivity handler) {

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
}
