package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonnathan on 24/11/17.
 */

public class SegundoCustomAdapter extends BaseAdapter{

    class Aux {
        public String nombre;
        public String estatus;
    }

    Context context;
    List<Aux> valores_rondines;


    private static LayoutInflater inflater=null;
    int [] imgIds;

    public SegundoCustomAdapter(ListaPuntosCriticos mainActivity, int[] imagenes, String usuario){
        context=mainActivity;
        valores_rondines = new ArrayList<Aux>();
        this.imgIds = imagenes;

        String RONDINES_URL = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=traer_puntos_usuario&usuario="+usuario;

        if (!compruebaConexion(context)) {
            Toast.makeText(context, "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
        } else {
            Ion.with(context)
                    .load(RONDINES_URL)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            for (int i = 0; i < result.size(); i++) {
                                Aux tm = new Aux();
                                JsonObject obj = result.get(i).getAsJsonObject();
                                String imagen = obj.get("img").getAsString();
                                tm.estatus = imagen;
                                String nombre = obj.get("nombre").getAsString();
                                tm.nombre = nombre;
                                valores_rondines.add(tm);
                            }
                        }
                    });

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    public boolean compruebaConexion(Context handler) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

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

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return valores_rondines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder
    {
        ImageView imgView1;
        TextView txtView1;
        ImageView imgButton1;
        ImageView imgButton2;
        ImageView imgButton3;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.checkpoint_item_in_list, null);

        for (Aux checkpointtt:valores_rondines) {
            holder.imgView1 =(ImageView) rowView.findViewById(R.id.imgView1);
            holder.txtView1 =(TextView) rowView.findViewById(R.id.txtView1);
            holder.imgButton1 =(ImageButton) rowView.findViewById(R.id.imgButton1);
            holder.imgButton2 =(ImageButton) rowView.findViewById(R.id.imgButton2);
            holder.imgButton3 =(ImageButton) rowView.findViewById(R.id.imgButton3);

            holder.txtView1.setText(checkpointtt.nombre);
            if (checkpointtt.estatus == "1"){
                holder.imgView1.setImageResource(imgIds[0]);
            }else {
                holder.imgView1.setImageResource(imgIds[1]);
            }

            holder.imgButton1.setImageResource(imgIds[2]);
            holder.imgButton2.setImageResource(imgIds[3]);
            holder.imgButton3.setImageResource(imgIds[4]);
        }

        /*for (int k = 0; k < result.length; k++) {
            int j=0;


            holder.imgView1 =(ImageView) rowView.findViewById(R.id.imgView1);
            holder.txtView1 =(TextView) rowView.findViewById(R.id.txtView1);
            holder.imgButton1 =(ImageButton) rowView.findViewById(R.id.imgButton1);
            holder.imgButton2 =(ImageButton) rowView.findViewById(R.id.imgButton2);
            holder.imgButton3 =(ImageButton) rowView.findViewById(R.id.imgButton3);
            //holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
            holder.txtView1.setText(result[i]);
            holder.imgView1.setImageResource(imageId[j]);
            holder.imgButton1.setImageResource(imageId[j+1]);
            holder.imgButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),ScanNfc.class);
                    intent.putExtra("checkpoint_selected",result[i]);
                    context.startActivity(intent);
                }
            });
            holder.imgButton2.setImageResource(imageId[j+2]);
            holder.imgButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.imgButton3.setImageResource(imageId[j+3]);
            holder.imgButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),ComentarioRelevante.class);
                    intent.putExtra("checkpoint_selected",result[i]);
                    intent.putExtra("usuario",usuario);
                    context.startActivity(intent);
                }
            });
            j=0;
        }*/



        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        return rowView;
    }
}
