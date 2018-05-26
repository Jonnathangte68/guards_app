package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ScanNfc extends Activity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    public String usuario;
    public String checkpoint;
    public int drawable;

    public TextView txtSucursalName;
    public TextView txtNombreUsuario;
    public TextView txtCheckpointName;
    public ImageView imgBolita;
    public ImageView imgComment;
    public Button btnRegresarDelScan;

    private FusedLocationProviderClient mFusedLocationClient;
    NfcAdapter nfcAdapter;
    NfcAdapter mNfcAdapter;
    String checkpoint_escaneado = "";

    static Activity scanNfc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_nfc);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("Escanear Punto");

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
            wl.acquire();
        }catch(Exception e) {
            e.printStackTrace();
        }

        scanNfc = this;


        //nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.usuario = extras.get("usuario").toString();
            this.checkpoint = extras.get("checkpoint_selected").toString();
            //this.drawable = (int)extras.get("status");
            /*if(global.estado.equals("verde"))
                this.drawable = R.drawable.green;
            else
                this.drawable = R.drawable.red;*/

        }

        txtSucursalName = (TextView)findViewById(R.id.txtSucursalName);
        txtNombreUsuario = (TextView)findViewById(R.id.txtNombreUsuario);
        txtCheckpointName = (TextView)findViewById(R.id.txtCheckpointName);
        imgBolita = (ImageView) findViewById(R.id.btnStatus);
        imgComment = (ImageView) findViewById(R.id.btnComentar);
        btnRegresarDelScan = (Button)findViewById(R.id.btnRegresarDelScan);

        imgBolita.setImageResource(this.drawable);

        txtNombreUsuario.setText(usuario);
        txtCheckpointName.setText(checkpoint);
        txtSucursalName.setText(usuario);
        btnRegresarDelScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanNfc.this, ListaPuntosCriticos.class);
                intent.putExtra("sesion-usuario",usuario);
                intent.putExtra("sesion","1");
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Boton de Panico", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {

            if (mNfcAdapter == null) {
                Toast.makeText(scanNfc, "Dispositivo sin soporte para funcioalidad NFC", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(scanNfc, "Ha desactivado la configuracion NFC sera reportado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            } else {
                handleIntent(getIntent());
            }
        }catch (Exception er){}

        try {
            ComentarioRelevante.getInstance().finish();
        } catch(Exception e) {e.printStackTrace();}

        //deamonSesionExpireDeactivateRondins();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    // NDEF is not supported by this Tag.
                    NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                    NdefRecord[] records = ndefMessage.getRecords();
                    for (NdefRecord ndefRecord : records) {
                        if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                            try {
                                byte[] payload = ndefRecord.getPayload();
                                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                                int languageCodeLength = payload[0] & 0063;
                                String value_of_cheqpoint = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding).trim();
                                String cqpointtrimmed = checkpoint.toString().trim();
                                if (cqpointtrimmed.equalsIgnoreCase(value_of_cheqpoint)){
                                    seguirAComentar();
                                }else {
                                    //Toast.makeText(scanNfc, "No lo hace!", Toast.LENGTH_SHORT).show();
                                    RegresarALista();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                //Log.e(TAG, "Unsupported Encoding", e);
                            }
                        }
                    }

                }
                //NdefReaderTask hilo= (NdefReaderTask) NdefReaderTask.getSingletonInstance("god",checkpoint, txtNombreUsuario.getText().toString(), this.getApplicationContext()).execute(tag);
                //new NdefReaderTask.getSingletonInstance("god",checkpoint, txtNombreUsuario.getText().toString(), this.getApplicationContext()).execute(tag);
                finish();
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    //new NdefReaderTask(checkpoint, txtNombreUsuario.getText().toString(), this.getApplicationContext()).execute(tag);
                    NdefReaderTask hilo= (NdefReaderTask) NdefReaderTask.getSingletonInstance("god",checkpoint, txtNombreUsuario.getText().toString(), this.getApplicationContext()).execute(tag);
                    finish();
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        try {
            adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
        }
        catch (NullPointerException e) {
            //Toast.makeText(activity, "Escaneado", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(activity,ScanNfc.class);
            intent2.putExtra("checkpoint_selected",this.checkpoint);
            intent2.putExtra("usuario",this.usuario);
            intent2.putExtra("status",this.drawable);
            activity.startActivity(intent2);
        }
    }

    public void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        try {
            adapter.disableForegroundDispatch(activity);
        }
        catch (NullPointerException e) {
            //Toast.makeText(activity, "Escaneado", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(activity,ScanNfc.class);
            intent2.putExtra("checkpoint_selected",this.checkpoint);
            intent2.putExtra("usuario",this.usuario);
            intent2.putExtra("status",this.drawable);
            activity.startActivity(intent2);
        }
        //adapter.disableForegroundDispatch(activity);
    }

    public static ScanNfc getInstance() {
        return (ScanNfc) scanNfc;
    }

    public void seguirAComentar(){
        Intent intent = new Intent(getApplicationContext(), ComentarioRelevante.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("checkpoint_selected", checkpoint);
        intent.putExtra("checkpoint_escaneado_status",1);
        startActivity(intent);
    }

    public void RegresarALista() {
        Toast.makeText(getApplicationContext(), "Error: Esta escaneando un punto equivocado", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ListaPuntosCriticos.class);
        intent.putExtra("sesion-usuario",usuario);
        startActivity(intent);
    }
}