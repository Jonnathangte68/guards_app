package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jonnathan on 22/11/17.
 */

public class CustomAdapter extends BaseAdapter {

    String [] result;
    Context context;
    int [] imageId;
    int [] canvas;
    String usuario;
    String[] bolitas;

    String[] auxBolas;


    public ImageView imgView1;
    //ImageView imgMapa;
    public TextView txtView1;
    public ImageView imgButton1;
    public ImageView imgButton2;
    public ImageView imgButton3;

    //ImageView imgView;
    //ImageView imgMap;

    String sucurs;

    private static LayoutInflater inflater=null;
    public CustomAdapter(ListaPuntosCriticos mainActivity, List<String> prgmNameList, int[] prgmImages, String[] bolitas, int[] canvas,String usuario, String sucurs,List<String> bolitas2) {
        // TODO Auto-generated constructor stub
        result = new String[prgmNameList.size()];
        this.canvas = canvas;
        prgmNameList.toArray(result);
        this.usuario = usuario;
        this.bolitas = bolitas;

        this.sucurs = sucurs;

        auxBolas = new String[bolitas2.size()];
        bolitas2.toArray(auxBolas);

        //imgMap = (ImageView)imgMap;

        //result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public boolean compruebaConexion(Handler handler) {

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


    public void cargaImagen(int i,int m){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup);
        dialog.setTitle("Custom Alert Dialog");
        final TextView titulo=(TextView)dialog.findViewById(R.id.titulo);

        final ImageView pantalla=(ImageView)dialog.findViewById(R.id.pantalla);
        pantalla.setImageResource(m);
        titulo.setText(result[i]);
       /* Button cerrar=(Button)dialog.findViewById(R.id.cerrar);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });*/
        dialog.show();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //final Holder holder=new Holder();
        final View rowView;

        ImageButton imgButton2;

        rowView = inflater.inflate(R.layout.checkpoint_item_in_list, null);


        imgView1 =(ImageView) rowView.findViewById(R.id.imgView1);
        txtView1 =(TextView) rowView.findViewById(R.id.txtView1);
        txtView1.setText(result[i]);
        imgButton1 =(ImageButton) rowView.findViewById(R.id.imgButton1);
        imgButton2 =(ImageButton) rowView.findViewById(R.id.imgButton2);
        imgButton3 =(ImageButton) rowView.findViewById(R.id.imgButton3);
        if (auxBolas[i].equalsIgnoreCase("r")) {imgView1.setImageResource(R.drawable.red);}
        else {imgView1.setImageResource(R.drawable.green);}

        imgButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int st = 0;

                if (bolitas != null){
                    if (bolitas[i]=="verde"){
                        global.estado="verde";
                        st = canvas[0];
                    }
                    else {
                        global.estado="rojo";
                        st = canvas[1];
                    }
                }

                Intent intent = new Intent(view.getContext(),ScanNfc.class);
                intent.putExtra("checkpoint_selected",result[i]);
                intent.putExtra("usuario",usuario);
                intent.putExtra("status",st);
                context.startActivity(intent);


            }
        });


        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comparacion = txtView1.getText().toString().replaceAll("\\s+","");


                switch (sucurs.toUpperCase().replaceAll("\\s+","")) {
                    case "CHIHUAHUA":


                            if(comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.ifcnadmins);
                            }
                            else if(comparacion.equalsIgnoreCase("TallerdeMantenimiento")) {
                                cargaImagen(i,R.drawable.tallermantenimiento);
                            }
                            else if(comparacion.equalsIgnoreCase("AmoniacoycuartodeAntenas")) {
                                cargaImagen(i,R.drawable.amoniacycuartodenatechichhua);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralNoreste")) {
                                cargaImagen(i,R.drawable.cercoperimetralnorchihua);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralEste")) {
                                cargaImagen(i,R.drawable.cercoperimestechs);
                            }
                            else if(comparacion.equalsIgnoreCase("CasetaVigilancia")) {
                                cargaImagen(i,R.drawable.casetavigchihua);
                            }

                            break;

                    case "CORAZON":

                       if(comparacion.equalsIgnoreCase("CasetadeSeguridad")) {
                            cargaImagen(i,R.drawable.cstavigilancia);
                        }
                        else if(comparacion.equalsIgnoreCase("PuertaLateraloficina")) {
                            cargaImagen(i,R.drawable.plateralofic);
                        }
                        else if(comparacion.equalsIgnoreCase("CercoperimetralSureste")) {
                            cargaImagen(i,R.drawable.cercoperimsureste);
                        }
                        else if(comparacion.equalsIgnoreCase("Amoniaco")) {
                            cargaImagen(i,R.drawable.amoniac);
                        }
                        else if(comparacion.equalsIgnoreCase("Tanqueamoniaco")) {
                            cargaImagen(i,R.drawable.tanqueamoniacc);
                        }
                        else if(comparacion.equalsIgnoreCase("CercoperimetralzonaNoroeste")) {
                            cargaImagen(i,R.drawable.cercoperimzonnor);
                        }
                        else if(comparacion.equalsIgnoreCase("Silo4y5")) {
                            cargaImagen(i,R.drawable.silo45);
                        }
                        else if(comparacion.equalsIgnoreCase("CercoperimetralzonaNoroeste")) {
                            cargaImagen(i,R.drawable.cercoperzonaoest);
                        }
                        else if(comparacion.trim().equalsIgnoreCase("CercoperimetralSureste")) {
                            cargaImagen(i,R.drawable.cercopersur);
                        }
                        else if(comparacion.equalsIgnoreCase("PostePtzEstacionamiento")) {
                            cargaImagen(i,R.drawable.postestac);
                        }
                        else if(comparacion.equalsIgnoreCase("LaboratorioyOficinasOperacionales")) {
                            cargaImagen(i,R.drawable.laboratyoficns);
                        }

                            break;


                    case "DURANGO":

                            if(comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.oficinasadmins);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoperimetralZonaNorte")) {
                                cargaImagen(i,R.drawable.cercoperimzonnor);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoperimetralNoreste")) {
                                cargaImagen(i,R.drawable.cercoperimnoreste);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoperimetralOeste")) {
                                cargaImagen(i,R.drawable.cercoperimoeste);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoperimetralZonaSuroeste")) {
                                cargaImagen(i,R.drawable.cercoperimsureste);
                            }
                            else if(comparacion.equalsIgnoreCase("Amoniaco")) {
                                cargaImagen(i,R.drawable.amoniaco);
                            }
                            else if(comparacion.equalsIgnoreCase("CasetaVigilancia")) {
                                cargaImagen(i,R.drawable.cstavigilancia);
                            }

                            break;
                        case "MEXICALI":

                            //Log.d("aaa",holder.txtView1.getText().toString().replaceAll("\\s+",""));
                            //System.out.println("aaaa- "+holder.txtView1.getText().toString().replaceAll("\\s+",""));

                            if (comparacion.equalsIgnoreCase("Puertaentradadeltren")){
                                cargaImagen(i,R.drawable.pentradacamiones);
                            }
                            else if(comparacion.equalsIgnoreCase("Bodega#1partefrente")) {
                                cargaImagen(i,R.drawable.bodeganro1partefrente);
                            }
                            else if(comparacion.equalsIgnoreCase("Topedecontenciondeltren")) {
                                cargaImagen(i,R.drawable.topecontencion);
                            }
                            else if(comparacion.equalsIgnoreCase("ParteTraceraBodega#3y4")) {
                                cargaImagen(i,R.drawable.partetraceranro3y4);
                            }
                            else if(comparacion.equalsIgnoreCase("EstacionamientodecamionesSK")) {
                                cargaImagen(i,R.drawable.estacionamientosk);
                            }
                            else if(comparacion.equalsIgnoreCase("cercoperimetral(Jasco)")) {
                                cargaImagen(i,R.drawable.cercoperimetraljasco);
                            }
                            else if(comparacion.equalsIgnoreCase("Puertaentradacamiones(Callejon)")) {
                                cargaImagen(i,R.drawable.pentradacamiones);
                            }
                            else if(comparacion.equalsIgnoreCase("CribaMaizBlanco")) {
                                cargaImagen(i,R.drawable.cribamaizblanc);
                            }
                            else if(comparacion.equalsIgnoreCase("Tallerdemantenimiento")) {
                                cargaImagen(i,R.drawable.tallermantenimiento);
                            }
                            else if(comparacion.equalsIgnoreCase("Almacen")) {
                                    cargaImagen(i,R.drawable.alamcenn);
                            }
                            else if(comparacion.equalsIgnoreCase("Casetaseguridadymonitoreomxl")) {
                                cargaImagen(i,R.drawable.casetaseguymonmexcl);
                            }


                            break;
                        case "NAVOLATO":

                            if(comparacion.equalsIgnoreCase("OficinaAdministrativa")) {
                                cargaImagen(i,R.drawable.oficiadminss);
                            }
                            else if(comparacion.equalsIgnoreCase("CreibaYFabricadeCostales")) {
                                cargaImagen(i,R.drawable.creibafabricacostals);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaSureste")) {
                                cargaImagen(i,R.drawable.cercoperimzonsur);
                            }
                            else if(comparacion.equalsIgnoreCase("TallerMantenimiento")) {
                                cargaImagen(i,R.drawable.tallermanten);
                            }
                            else if(comparacion.equalsIgnoreCase("MecanizadoZonabodegaGranelera")) {
                                cargaImagen(i,R.drawable.mecanizadozonbodeg);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaNoreste")) {
                                cargaImagen(i,R.drawable.cercoperimetralzonanorests);
                            }
                            else if(comparacion.equalsIgnoreCase("Cuartodebomba")) {
                                cargaImagen(i,R.drawable.cuartodebomba);
                            }
                            else if(comparacion.equalsIgnoreCase("Amoniaco")) {
                                cargaImagen(i,R.drawable.amoniacc);
                            }
                            else if(comparacion.equalsIgnoreCase("Silo#15")) {
                                cargaImagen(i,R.drawable.silonro155);
                            }
                            else if(comparacion.equalsIgnoreCase("silo18")) {
                                cargaImagen(i,R.drawable.silo18);
                            }
                            else if(comparacion.equalsIgnoreCase("Mecanizadonuevo")) {
                                cargaImagen(i,R.drawable.mecaniznuevo);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaOeste")) {
                                cargaImagen(i,R.drawable.cercoperimzonaoests);
                            }
                            else if(comparacion.equalsIgnoreCase("Laboratorio")) {
                                cargaImagen(i,R.drawable.laboratorio);
                            }
                            else if(comparacion.equalsIgnoreCase("casetadeseguridad")) {
                                cargaImagen(i,R.drawable.casetasegur);
                            }
                            break;


                    case "OBREGON":
                            if(comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.oficinasadminsobggg);
                            }
                            else if(comparacion.equalsIgnoreCase("TallerdeMantenimiento")) {
                                cargaImagen(i,R.drawable.tallermtoobregon);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaNoroeste")) {
                                cargaImagen(i,R.drawable.cercoperimzonnorrobreggg);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaNorte")) {
                                cargaImagen(i,R.drawable.cercoperimzonnorrobreggg);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaNoreste")) {
                                cargaImagen(i,R.drawable.cercoperimzzznorestobregg);
                            }
                            else if(comparacion.equalsIgnoreCase("Mecanizado")) {
                                cargaImagen(i,R.drawable.mecanizadoobregg);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaEste")) {
                                cargaImagen(i,R.drawable.cercoperimzonaeste);
                            }
                            else if(comparacion.equalsIgnoreCase("Amoniaco")) {
                                cargaImagen(i,R.drawable.amoniacoobreggg);
                            }
                            else if(comparacion.equalsIgnoreCase("CercoPerimetralZonaSureste")) {
                                cargaImagen(i,R.drawable.cercoperimzonasurobreggg);
                            }
                            else if(comparacion.equalsIgnoreCase("EntradaPrincipalSucursal")) {
                                cargaImagen(i,R.drawable.entradaprincsucurl);
                            }


                            break;
                        case "SANLUIS":



                            if(comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.san_luis_oficinas_administ);
                            }
                            else if (comparacion.equalsIgnoreCase("TallerdeMantenimiento")) {
                                cargaImagen(i,R.drawable.san_luis_taller_de_manten);
                            }
                            else if (comparacion.equalsIgnoreCase("AreadeAmoniaco")) {
                                cargaImagen(i,R.drawable.san_luis_area_amonic);
                            }
                            else if (comparacion.equalsIgnoreCase("ComedordeManiobras")) {
                                cargaImagen(i,R.drawable.san_luis_comedor_maniob);
                            }
                            else if (comparacion.equalsIgnoreCase("FertilizantesLiquidos")) {
                                cargaImagen(i,R.drawable.san_luis_fertiliz_liq);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralEsquinaZonaNorte")) {
                                cargaImagen(i,R.drawable.san_luis_cerco_perim_esq_zon_norte);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralCentroZonaNorte")) {
                                cargaImagen(i,R.drawable.san_luis_cerco_perim_cerco_zon_nort);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralZoneEste")) {
                                cargaImagen(i,R.drawable.san_luis_perimtrl_este);
                            }
                            else if (comparacion.equalsIgnoreCase("FertilizantesLiquidos")) {
                                cargaImagen(i,R.drawable.san_luis_fertiliz_liq);
                            }
                            else if (comparacion.equalsIgnoreCase("Pilas#10")) {
                                cargaImagen(i,R.drawable.san_luis_pilas_10);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralZonaSur")) {
                                cargaImagen(i,R.drawable.san_luis_perimetra_sur);
                            }
                            else if (comparacion.equalsIgnoreCase("CasetadeVigilancia")) {
                                cargaImagen(i,R.drawable.san_luis_caseta_vig);
                            }


                            break;
                        case "TORREON":


                            if(comparacion.equalsIgnoreCase("Pila#10y20")) {
                                cargaImagen(i,R.drawable.pila1020);
                            }
                            else if (comparacion.equalsIgnoreCase("Pila#40")) {
                                cargaImagen(i,R.drawable.plia40);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralZonaOeste")) {
                                cargaImagen(i,R.drawable.cercoperimetral_zona_oeste);
                            }
                            else if (comparacion.equalsIgnoreCase("AreadePilasdeSemilla")) {
                                cargaImagen(i,R.drawable.areadepilasdesemillasa);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralZonaNoroeste")) {
                                cargaImagen(i,R.drawable.cerco_perimetral_zona_norist);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralNorteareaMaquinaria")) {
                                cargaImagen(i,R.drawable.cerco_perimetral_nort_area_maqqq);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralesteAreaMaquinaria")) {
                                cargaImagen(i,R.drawable.cerco_perimetral_cerco_maquinnn);
                            }
                            else if (comparacion.equalsIgnoreCase("PozodelAgua")) {
                                cargaImagen(i,R.drawable.pozoaguasss);
                            }
                            else if (comparacion.equalsIgnoreCase("AreadeAmoniaco")) {
                                cargaImagen(i,R.drawable.amoniaccsk);
                            }
                            else if (comparacion.equalsIgnoreCase("TallerMantenimiento")) {
                                cargaImagen(i,R.drawable.tallllmanten);
                            }
                            else if (comparacion.equalsIgnoreCase("PuertaOesteplantaGim")) {
                                cargaImagen(i,R.drawable.puerta_oeste_plat_gim);
                            }
                            else if (comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.oficiadminss);
                            }




                            break;
                        case "SILVA":


                            if (comparacion.equalsIgnoreCase("OficinasAdministrativas")) {
                                cargaImagen(i,R.drawable.silva_oficns_administ);
                            }
                            else if (comparacion.equalsIgnoreCase("SubestacionElectrica")) {
                                cargaImagen(i,R.drawable.silva_sub_electrica);
                            }
                            else if (comparacion.equalsIgnoreCase("PuertaOestedelgim")) {
                                cargaImagen(i,R.drawable.silva_puerta_oeste_del_gim);
                            }
                            else if (comparacion.equalsIgnoreCase("Puerta este del Gim")) {
                                cargaImagen(i,R.drawable.silva_puerta_este_del_gim);
                            }
                            else if (comparacion.equalsIgnoreCase("Prensa de Borra")) {
                                cargaImagen(i,R.drawable.silva_prensa_borra);
                            }
                            else if (comparacion.equalsIgnoreCase("Tallerdemantenimiento")) {
                                cargaImagen(i,R.drawable.silva_talll_manten);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralOesteenpila#40")) {
                                cargaImagen(i,R.drawable.silva_perimetral_oeste_en_nro_40);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralSurOesteenpila#30")) {
                                cargaImagen(i,R.drawable.silva_preim_sur_oest_pila_30);
                            }
                            else if (comparacion.equalsIgnoreCase("Mecanizado")) {
                                cargaImagen(i,R.drawable.silva_mecanizado);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralSurentrepila#20y60")) {
                                cargaImagen(i,R.drawable.silva_cerco_perim_sur_20_y_60);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralSureste")) {
                                cargaImagen(i,R.drawable.silva_perim_aal_sur_este);
                            }
                            else if (comparacion.equalsIgnoreCase("CercoPerimetralAleste")) {
                                cargaImagen(i,R.drawable.silva_perim_al_este);
                            }
                            else if (comparacion.equalsIgnoreCase("AreadeInsumos")) {
                                cargaImagen(i,R.drawable.silva_area_insums);
                            }
                            else if (comparacion.equalsIgnoreCase("AreadeAmoniaco")) {
                                cargaImagen(i,R.drawable.silva_area_amonic);
                            }
                            else if (comparacion.equalsIgnoreCase("FertilizantesLiquidos")) {
                                cargaImagen(i,R.drawable.silva_fertilizantes_liquidossss);
                            }
                            else if (comparacion.equalsIgnoreCase("CasetadeSeguridad")) {
                                cargaImagen(i,R.drawable.casetasegur);
                            }
                            break;

                }




            }
        });

        imgButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Limpia eliminar existentes
                try {
                    ComentarioRelevante.getInstance().finish();
                }catch(Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(view.getContext(),ComentarioRelevante.class);
                intent.putExtra("checkpoint_selected",result[i]);
                intent.putExtra("usuario",usuario);
                context.startActivity(intent);



            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, result[i], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }
}
