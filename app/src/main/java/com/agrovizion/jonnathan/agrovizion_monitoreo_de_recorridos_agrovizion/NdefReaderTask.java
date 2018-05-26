package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Background task for reading the data. Do not block the UI thread while reading.
 *
 * @author Ralf Wondratschek
 *
 */
class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    String checkpoint;
    String usuario;
    Context ctx;
    String myname="";
    private static NdefReaderTask iamgod;

    private NdefReaderTask(String nombre,String c, String usuario, Context context) {
        super();
        this.checkpoint = c;
        this.ctx = context;
        this.usuario = usuario;
        this.myname=nombre;


    }

    public static NdefReaderTask getSingletonInstance(String nombre,String c, String usuario, Context context) {
        try {
            if (iamgod == null) {
                iamgod = new NdefReaderTask(nombre, c, usuario, context);
            } else {
                iamgod = null;
                iamgod = new NdefReaderTask(nombre, c, usuario, context);
                System.out.println("No se puede crear el objeto " + nombre + " porque ya existe un objeto de la clase SoyUnico");
            }

            return iamgod;
        }catch(Exception er){
            System.out.println("error-------------->"+er.getMessage().toString());
            return null;
        }
    }

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    //Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            //mTextView.setText("Read content: " + result);
            Log.d("Leido", "Leidooooooooooooooooooooooooooooooo");
            System.out.println("Leidoooo");
            System.out.println(result);
            Activity temporal = new Activity();
            if(result.trim().equalsIgnoreCase(checkpoint.trim())){
                /*Intent intent = new Intent(activity.getApplicationContext(),ComentarioRelevante.class);

                temporal.startActivity(intent);*/
                /*if(ComentarioRelevante.getInstance()){}
                else {
                    ComentarioRelevante.getInstance().finish();
                }*/
                Intent intent = new Intent(ctx, ComentarioRelevante.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d(toString(), "intent = " + intent.toString());
                intent.putExtra("usuario",usuario);
                intent.putExtra("checkpoint_selected", checkpoint);
                intent.putExtra("checkpoint_escaneado_status",1);
                ctx.startActivity(intent);
            }
            else {
                Toast.makeText(ctx, "Error: Esta escaneando un punto equivocado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ctx, ListaPuntosCriticos.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d(toString(), "intent = " + intent.toString());
                intent.putExtra("sesion-usuario",usuario);
                intent.putExtra("sesion", 1);
                ctx.startActivity(intent);
                ScanNfc.getInstance().finish();
            }
        }
    }
}