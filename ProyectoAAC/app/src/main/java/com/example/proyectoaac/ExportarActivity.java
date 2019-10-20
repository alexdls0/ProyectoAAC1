package com.example.proyectoaac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportarActivity extends AppCompatActivity {

    private Button btExportar;
    private TextView tvRecordatorio;
    private EditText etNombreArchivo;
    private RadioGroup rgLocation;
    private List<Contacto> contactos;

    private static final String ULTIMO_FICHERO = "ultimoFichero";
    private static final String ULTIMO_TIPO = "tipoFichero";
    private static final String FICHEROS = "ficheros";
    private static final String TAG = ExportarActivity.class.getName();

    private static final int NADA = -1;
    private static final int INTERNO = 0;
    private static final int  PRIVADO = 1;
    private int tipo = NADA;
    private String nombreFichero;
    private String ficheros;
    private String formato = "";

    private static final int PERMISO_LEER = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar);

        initComponents();
    }

    private void checkPermissions(String permiso, int titulo, int mensaje, AfterPermissionCheck apc){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                explain(R.string.tituloEscrituraContactos, R.string.mensajeExplicacionEscrituraContactos, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_LEER);
            }
        } else {
            apc.doTheJob();
        }
    }

    private void explain(int title, int message, final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(ExportarActivity.this, new String[]{permission}, PERMISO_LEER);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_LEER){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                escribirArchivo();
            } else {
                finish();
            }
        }
    }

    private void initComponents() {
        btExportar = findViewById(R.id.btExportar);
        tvRecordatorio = findViewById(R.id.tvRecordatorio);
        rgLocation = findViewById(R.id.rgLocation);
        etNombreArchivo = findViewById(R.id.etNombreArchivo);
        readSettings();
        contactos = getListaContactos();

        btExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirArchivo();
            }
        });
    }

    private boolean isCorrect() {
        if(etNombreArchivo.getText().toString().trim().isEmpty() || etNombreArchivo.getText().toString().trim().length() < 4){
            return false;
        } else {
            nombreFichero = corregirTitulo(etNombreArchivo.getText().toString().trim());
            tipo = ExportarActivity.getCheckedType(rgLocation.getCheckedRadioButtonId());
            return !(nombreFichero.isEmpty() || tipo == NADA);
        }

    }

    private void escribirArchivo() {
        if(isCorrect()){
            File f=new File( getPath(ExportarActivity.this, tipo),nombreFichero);
            try {
                FileWriter fw = new FileWriter(f);
                fw.write(contactos.toString());
                fw.flush();
                fw.close();
                Toast.makeText(this, R.string.correctly, Toast.LENGTH_LONG).show();
                etNombreArchivo.setText("");
                cambiarPreferences();
                readSettings();
                SharedPreferences sharedPref = getSharedPreferences("exportar", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(FICHEROS, ficheros);
                editor.commit();
            }catch(IOException e){
                Log.v(TAG,e.getMessage());
            }
        } else{
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }

    }

    private static int getCheckedType(int item) {
        int tipo = NADA;

        switch (item){
            case R.id.rbInterno:
                tipo = INTERNO;
                break;
            case R.id.rbPrivado:
                tipo = PRIVADO;
                break;
        }

        return tipo;
    }

    private static File getPath(Context context, int tipo){
        File file = null;
        switch(tipo){
            case INTERNO:
                file = context.getFilesDir();
                break;
            case PRIVADO:
                file = context.getExternalFilesDir(null);
                break;
        }
        return file;
    }

    public List<Contacto> getListaContactos(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            contacto.setTelefonos(getListaTelefonos(contacto.getId()));
            lista.add(contacto);
        }
        return lista;
    }

    public List<String> getListaTelefonos(long id){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id+""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while(cursor.moveToNext()){
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }

    public String corregirTitulo(String titulo){
        String extension = ".txt";

        if(!formato.equalsIgnoreCase("not found")){
            extension = formato;

            if(extension.substring(0,1).equalsIgnoreCase(".")){
                extension = formato;
            }
            else {
                extension = "." + formato;
            }
        }

        if(!titulo.substring(titulo.length()-4,titulo.length()).equalsIgnoreCase(extension)){
            titulo += extension;
        }

        return titulo;
    }

    private void readSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        formato = sharedPreferences.getString("tituloArchivoPreference", "not found");
        boolean localizacion = sharedPreferences.getBoolean("location", false);
        boolean esInterno = sharedPreferences.getBoolean("locationArchivoPreference", false);
        boolean recordatorio = sharedPreferences.getBoolean("rememberArchivoPreference", false);

        sharedPreferences = ExportarActivity.this.getSharedPreferences("exportar",Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString(ULTIMO_FICHERO, "");
        int posicion = sharedPreferences.getInt(ULTIMO_TIPO, -1);

        ficheros = sharedPreferences.getString(FICHEROS,"");
        if(!nombre.equalsIgnoreCase("") || posicion != -1){
            ficheros += "-"+nombre+"#"+posicion;
        }

        if(localizacion){
            if(esInterno){
                tipo = 0;
            } else {
                tipo = 1;
            }
        }


        if(recordatorio){
            if(posicion == 0){
                tvRecordatorio.setText("File: "+nombre+" Location: Internal");
            }
            if(posicion == 1){
                tvRecordatorio.setText("File: "+nombre+" Location: Private");
            }
            if(posicion == -1){
                tvRecordatorio.setText("File: Not found Location: Unknown");
            }
        }

        if(tipo == 0 || tipo == 1){
            selectRadio(tipo);
        }
    }

    public void cambiarPreferences(){
        SharedPreferences sharedPref = getSharedPreferences("exportar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ULTIMO_FICHERO, nombreFichero);
        editor.putInt(ULTIMO_TIPO, tipo);
        editor.commit();
    }

    private void selectRadio(int tipo) {
        switch (tipo){
            case INTERNO:
                RadioButton rbInt = ExportarActivity.this.findViewById(R.id.rbInterno);
                rbInt.setChecked(true);
                break;
            case PRIVADO:
                RadioButton rbPri = ExportarActivity.this.findViewById(R.id.rbPrivado);
                rbPri.setChecked(true);
                break;
        }
    }

}
