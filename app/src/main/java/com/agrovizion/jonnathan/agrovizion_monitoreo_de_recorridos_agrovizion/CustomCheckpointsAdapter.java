package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.content.Context;
import android.content.Intent;
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
 * Created by jonnathan on 24/11/17.
 */

public class CustomCheckpointsAdapter extends BaseAdapter {
    String [] result;
    Context context;
    int [] imageId;
    int [] bolitas;
    String usuario;
    private static LayoutInflater inflater=null;
    public CustomCheckpointsAdapter(ListaPuntosCriticos mainActivity, List<String> prgmNameList, int[] prgmImages,int[] bolitas, String usuario) {
        // TODO Auto-generated constructor stub
        result = new String[prgmNameList.size()];
        prgmNameList.toArray(result);
        this.usuario = usuario;
        this.bolitas = bolitas;
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
        CustomCheckpointsAdapter.Holder holder=new CustomCheckpointsAdapter.Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.checkpoint_item_in_list, null);

        for (int k = 0; k < result.length; k++) {

            holder.imgView1 =(ImageView) rowView.findViewById(R.id.imgView1);
            holder.txtView1 =(TextView) rowView.findViewById(R.id.txtView1);
            holder.imgButton1 =(ImageButton) rowView.findViewById(R.id.imgButton1);
            holder.imgButton2 =(ImageButton) rowView.findViewById(R.id.imgButton2);
            holder.imgButton3 =(ImageButton) rowView.findViewById(R.id.imgButton3);
            //holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
            holder.txtView1.setText(result[i]);
            holder.imgView1.setImageResource(bolitas[k]);
            holder.imgButton1.setImageResource(imageId[0]);
            holder.imgButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),ScanNfc.class);
                    intent.putExtra("checkpoint_selected",result[i]);
                    context.startActivity(intent);
                }
            });
            holder.imgButton2.setImageResource(imageId[1]);
            holder.imgButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.imgButton3.setImageResource(imageId[2]);
            holder.imgButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),ComentarioRelevante.class);
                    intent.putExtra("checkpoint_selected",result[i]);
                    intent.putExtra("usuario",usuario);
                    context.startActivity(intent);
                }
            });
        }



        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[i], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}
