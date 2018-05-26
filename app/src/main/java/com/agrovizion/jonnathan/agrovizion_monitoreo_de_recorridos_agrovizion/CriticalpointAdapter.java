package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonnathan on 9/11/17.
 */

public class CriticalpointAdapter extends ArrayAdapter {

    private RequestQueue requestQueue;
    private String URLBASE = global.direccion+"/servicio/model/app_guardias/ctrlSesion/sesion_guardias.php?name_function=get_data_for_list";
    private static final String TAG = "CriticalpointAdapter";
    List<Criticalpoint> items;
    JsonObjectRequest jsArrayRequest;

    public CriticalpointAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        // Crear nueva cola de peticiones
        requestQueue= Volley.newRequestQueue(context);

        // Nueva petición JSONObject

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URLBASE,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        items = parseJson(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                    }
                }
        );

        // Añadir petición a la cola
        requestQueue.add(jsArrayRequest);
    }


    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View listItemView;

        //Comprobando si el View no existe
        listItemView = null == convertView ? layoutInflater.inflate(
                R.layout.critical_point_detail_layout,
                parent,
                false) : convertView;

        // Obtener el item actual
        Criticalpoint item = items.get(position);

        // Obtener Views
        TextView textoTitulo = (TextView) listItemView.
                findViewById(R.id.textoTitulo);
        final ImageView imagen = (ImageView) listItemView.
                findViewById(R.id.imagenDePuntoCritico);

        // Actualizar los Views
        textoTitulo.setText(item.getTitulo());
        /*if (item.getEstadoVisitado()==1){
            this.image
        }
        else {
            imagen.setImageDrawable();
        }*/



        return listItemView;
    }

    public List<Criticalpoint> parseJson(JSONObject jsonObject){
        // Variables locales
        List<Criticalpoint> posts = new ArrayList<>();
        JSONArray jsonArray= null;

        try {
            // Obtener el array del objeto
            jsonArray = jsonObject.getJSONArray("items");

            for(int i=0; i<jsonArray.length(); i++){

                try {
                    JSONObject objeto= jsonArray.getJSONObject(i);

                    Criticalpoint post = new Criticalpoint();


                    posts.add(post);

                } catch (JSONException e) {
                    Log.e(TAG, "Error de parsing: "+ e.getMessage());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return posts;
    }
}
