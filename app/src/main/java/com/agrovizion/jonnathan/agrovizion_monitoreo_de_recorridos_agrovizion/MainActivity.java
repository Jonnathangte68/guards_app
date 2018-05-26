package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.app.Service.START_STICKY;
import static android.provider.Telephony.Mms.Part.FILENAME;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String usuario;

    private EditText pass;

    private String sucursal_seleccionada = "";

    public static final String PREFS_NAME = "MyPrefsFile";

    //private FusedLocationProviderClient mFusedLocationClient;

    //private TextView texto;
    private Spinner spinner;
    private Button guardar;

    //final String prepararSucursales = global.direccion + "/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=prepare_sucursals&username="+usuario;

   //NotificationCompat.Builder notification;
   //public static final int uniqueId = 84736;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        spinner = (Spinner) findViewById(R.id.sucursaless);
        spinner.setOnItemSelectedListener(this);
        pass =  (EditText)findViewById(R.id.editTextPassword);
        guardar = (Button)findViewById(R.id.guardar);

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
            wl.acquire();
        }catch(Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sucursales_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        guardar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   usuario = pass.getText().toString().toLowerCase();
                   if (pass.getText().toString().equalsIgnoreCase(spinner.getSelectedItem().toString().trim())) {
                       Toast.makeText(MainActivity.this, "Inicio de Sesion Exitoso!", Toast.LENGTH_SHORT).show();
                        cargaDatosSucursales(usuario);

                        try {
                            ListaPuntosCriticos.getInstance().finish();
                        }catch(Exception e) {
                            e.getStackTrace();
                        }

                       // Revisar del almacenamiento local

                       Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                       serviceIntent.putExtra("u",usuario);
                       startService(serviceIntent);

                       Intent intent = new Intent(MainActivity.this, ListaPuntosCriticos.class);
                       intent.putExtra("sesion-usuario", usuario);
                       intent.putExtra("cargarLista","0");
                       startActivity(intent);
                   }else {
                       Toast.makeText(MainActivity.this, "Usuario y/o contraseña incorrectos..", Toast.LENGTH_SHORT).show();
                   }
               }

            });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.sucursal_seleccionada = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        /*
        if (!shouldAllowBack()) {
            doSomething();
        } else {
            super.onBackPressed();
        }*/

    }

    public void cargaDatosSucursales(String sucursal){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("show_alarm",1);

        // Limpiar datos de horarios
        editor.putString("LoadedTimes","0");

        Date a=new Date();
        //a.setTime(System.currentTimeMillis()+(2*60*60*1000));                                  // Mas de 2 horas   2*60*60*1000
        a.setTime(System.currentTimeMillis()+(20*1000));
        long future_time = a.getTime();




        // Lista rondas per sucursal

        String chihuahua = "amoniaco y cuarto de antenas;0;0&;Cerco Perimetral Este;0;0&;Taller de mantenimiento;0;0&;Oficina administrativas;0;0&;Cerco Perimetral Noroeste;0;0&;Caseta Vigilancia;0;0&;";
        String corazon = "Cerco Perimetral Zona Noroeste;0;0&;Silo 4 y 5&;0;0&;Caseta de seguridad;0;0&;Cerco Perimetral Zona Oeste;0;0&;puerta lateral oficina;0;0&;Cerco Perimetral Zona Suroeste;0;0&;Cerco perimetral Sureste;0;0&;Tanque de amoniaco;0;0&;Amoniaco;0;0&;Poste Ptz Estacionamiento;0;0&;Laboratorio y Oficinas Operaciones;0;0&;";
        String durango = "Taller de Mantenimiento;0;0&;Cerco Perimetral Zona Norte;0;0&;Cerco Perimetral Noreste;0;0&;Cerco Perimetral Oeste;0;0&;Cerco Perimetral Zona Suroeste;0;0&;Amoniaco;0;0&;Oficina Administrativas;0;0&;Caseta;0;0&;";
        String mexicali = "Puerta entrada del tren;0;0&;Estacionamiento de camiones SK;0;0&;Taller de mantenimiento;0;0&;Almacen;0;0&;Tope de contencion del tren;0;0&;Cerco perimetral (Jasco);0;0&;Puerta Entrada Camiones (Callejon);0;0&;Bodega #1 parte frente;0;0&;Parte Trasera Bodega #3 y 4;0;0&;Criba Maiz Blanco;0;0&;Caseta Seguridad y monitoreo Mxl;0;0&;";
        String navolato = "OFICINA ADMINISTRATIVA;0;0&;CREIBA Y FABRICA DE COSTALES;0;0&;CERCO PERIMETRAL ZONA SURESTE;0;0&;TALLER MANTENIMIENTO;0;0&;MECANIZADO ZONA BODEGA GRANELERA;0;0&;CERCO PERIMETRAL ZONA NORESTE;0;0&;CUARTO DE BOMBA;0;0&;AMONIACO;0;0&;SILO # 15;0;0&;SILO 18;0;0&;MECANIZADO NUEVO;0;0&;CERCO PERIMETRAL NOEOSTE;0;0&;CERCO PERIMETRAL ZONA OESTE;0;0&;LABORATORIO;0;0&;CASETA DE SEGURIDAD;0;0&;";
        String obregon = "Oficinas Administrativas;0;0&;Taller de mantenimiento;0;0&;Cerco Perimetral Zona Noroeste;0;0&;Cerco Perimetral Zona Norte;0;0&;Cerco Perimetral Zona Noreste;0;0&;Macanizado;0;0&;Cerco Perimetral Zona Este;0;0&;Amoniaco;0;0&;Cerco Perimetral Zona Sureste;0;0&;Entrada  Principal Sucursal;0;0&;";
        String sanluis = "Oficina Administrativas;0;0&;Taller de mantenimiento;0;0&;Area De Amoniaco;0;0&;Comedor de Maniobras;0;0&;Fertilizantes Liquidos;0;0&;Cerco Perimetral  Esquina Zona Norte;0;0&;Cerco Perimetral Centro Zona Norte;0;0&;Cerco Perimetral Zona Este;0;0&;Mecanizado;0;0&;Pilas # 1;0;0&;Cerco perimetra Zona Sur;0;0&;Caseta de Vigilancia;0;0&;";
        String torreon = "Pila # 10 y 20;0;0;&;Cerco Perimetral Zona Oeste;0;0&;Area de Pilas De Semillas;0;0&;Cerco Perimetral Zona Noroeste;0;0&;Cerco Perimetral Norte Area Maquinaria;0;0&;Cerco Perimetral este;0;0&;Area Maquinaria;0;0&;Pozo del Aguas;0;0&;Area de Amoniaco;0;0&;Taller Mantenimieto;0;0&;Puerta Oeste planta Gim;0;0&;Oficinas Administrativas;0;0&;Pila 40;0;0&;";
        String silva = "Oficinas administrativas;0;0&;Subestacion Electrica;0;0&;Puerta Oeste del Gim;0;0&;Puerta Este del Gim;0;0&;Prensa de Borra;0;0&;Taller de mantenimiento;0;0&;Cerco Perimetral Oeste en pila # 40;0;0&;Cerco Perimetral SurOeste en Pila # 30;0;0&;Mecanizado;0;0&;Cerco Perimetral Sur entre Pila # 20 y 60;0;0&;Cerco   Perimetral Sureste;0;0&;Cerco Perimetral Al este;0;0&;Area De Insumos;0;0&;Area De Amoniaco;0;0&;Fertilizantes Liquidos;0;0&;Caseta De Seguridad;0;0&;";






        // Fin listado


        String ss = settings.getString("sucursal","x");
        if (usuario.equalsIgnoreCase(ss)) {
            // Este es el caso que no tiene
            editor.putString("sucursal",this.usuario);
            if (settings.getString("bK","n").equalsIgnoreCase("n")) {
                // No tiene ronda activa
                editor.putString("bK",String.valueOf(future_time));
                editor.putInt("thread_status",1);


                if (usuario.equalsIgnoreCase("chihuahua")) {
                    editor.putString("puntos",chihuahua);
                }else if(usuario.equalsIgnoreCase("corazon")) {
                    editor.putString("puntos",corazon);
                }else if(usuario.equalsIgnoreCase("durango")) {
                    editor.putString("puntos",durango);
                }else if(usuario.equalsIgnoreCase("mexicali")) {
                    editor.putString("puntos",mexicali);
                }else if(usuario.equalsIgnoreCase("navolato")) {
                    editor.putString("puntos",navolato);
                }else if(usuario.equalsIgnoreCase("obregon")) {
                    editor.putString("puntos",obregon);
                }else if(usuario.equalsIgnoreCase("san luis")) {
                    editor.putString("puntos",sanluis);
                }else if(usuario.equalsIgnoreCase("torreon")) {
                    editor.putString("puntos",torreon);
                }else if(usuario.equalsIgnoreCase("silva")) {
                    editor.putString("puntos",silva);
                }




                editor.commit();
                return;
            }else{
                // hora actual mayor que beginTime enviar valores defecto - comenzar de 0 enviar valores sino enviar valores como estan

                long hora_actual = System.currentTimeMillis();
                String tmp = settings.getString("bK","n");
                if(!tmp.equalsIgnoreCase("n")){
                    long hora_f = Long.parseLong(tmp);
                    if (hora_actual > hora_f) {
                        editor.putInt("thread_status",1);
                        //editor.putString("puntos",chihuahua);

                        if (usuario.equalsIgnoreCase("chihuahua")) {
                            editor.putString("puntos",chihuahua);
                        }else if(usuario.equalsIgnoreCase("corazon")) {
                            editor.putString("puntos",corazon);
                        }else if(usuario.equalsIgnoreCase("durango")) {
                            editor.putString("puntos",durango);
                        }else if(usuario.equalsIgnoreCase("mexicali")) {
                            editor.putString("puntos",mexicali);
                        }else if(usuario.equalsIgnoreCase("navolato")) {
                            editor.putString("puntos",navolato);
                        }else if(usuario.equalsIgnoreCase("obregon")) {
                            editor.putString("puntos",obregon);
                        }else if(usuario.equalsIgnoreCase("san luis")) {
                            editor.putString("puntos",sanluis);
                        }else if(usuario.equalsIgnoreCase("torreon")) {
                            editor.putString("puntos",torreon);
                        }else if(usuario.equalsIgnoreCase("silva")) {
                            editor.putString("puntos",silva);
                        }


                        editor.putString("bK",String.valueOf(future_time)); // Nuevo horario
                        editor.commit();
                        return;
                    }
                    else {
                        //Cargar valores como estan, o sea no hacer nada creo!
                        return;
                    }
                }
            }
        }
        else {
            editor.putString("sucursal",this.usuario);
            editor.putString("bK",String.valueOf(future_time)); // Nuevo horario
            editor.putInt("thread_status",1);
            editor.commit();
            // Cambio de sucursal cargar los puntos nuevos para la nueva sucursal
            if (usuario.equalsIgnoreCase("chihuahua")) {
                editor.putString("puntos",chihuahua);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("corazon")) {
                editor.putString("puntos",corazon);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("durango")) {
                editor.putString("puntos",durango);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("mexicali")) {
                editor.putString("puntos",mexicali);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("navolato")) {
                editor.putString("puntos",navolato);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("obregon")) {
                editor.putString("puntos",obregon);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("san luis")) {
                editor.putString("puntos",sanluis);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("torreon")) {
                editor.putString("puntos",torreon);
                editor.commit();
            }
            else if (usuario.equalsIgnoreCase("silva")) {
                editor.putString("puntos",silva);
                editor.commit();
            }
            return;

        }

        // Fin metodo
    }
}