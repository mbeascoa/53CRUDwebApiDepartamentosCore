package com.example.a53crudwebapidepartamentoscore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;


import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Buscarporid extends AppCompatActivity {

      private TextView resultado;
    private EditText numdep;
    private Button botonRegresar, btnBuscarPorId;
    private static final String TAG= MainActivity.class.getSimpleName();

    //01 metodo inicial al Crearse la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscarporid);

        this.numdep = (EditText) findViewById(R.id.et_numero_buscar);
        this.botonRegresar=(Button) findViewById(R.id.btn_regresar_buscar);
        this.btnBuscarPorId=(Button) findViewById(R.id.btn_buscarporid);
        this.resultado=(TextView)findViewById(R.id.tv_ResultadoBusqueda);

        //gestionamos la accion del boton Buscar por departamento
        this.btnBuscarPorId.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                String idedept = numdep.getText().toString();
                Log.i(TAG, "onCreate Buscamos por Departamento  : " + idedept);
                leerservicio(idedept);
            }
        });

    }

    //02
    public void cerrarVentana(View view) {
        finish();
    }

    //03
    public void leerservicio(String idedept) {
        try {
            String urlbase = "https://webapidepartamentos20210716114825.azurewebsites.net/api/Departamentos/";
            String url = urlbase + idedept;
            Log.i(TAG, "La url de acceso a los servicios web Restfull es : "+url);
            new Buscarporid.HttpAsyncTask().execute(url);

        } catch (Exception e){
            //manage exceptions
            System.out.println(e.toString());
            System.out.println("Error leyendo del Web Api RestFull");

        }
    }

    //04
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return recuperarContenido(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getBaseContext(), "Datos recibidos!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject objetojson = new JSONObject(result);
                    Departamentos departamento = new Departamentos();
                    departamento = convertirJsonObjectDepartamentos(objetojson);
                    resultado.setText(departamento.getNombre() + "  ,  " + departamento.getLocalidad());


                } catch (JSONException e) {
                    System.out.println(e.toString());
                    System.out.println("onPostExecute");
                }
            } else {
                resultado.setText( "Este departamento no existe , vuelve a consultar el listado de departamentos o bien buscarlo nuevamente");
                }

        }
    }

    //05
    public String recuperarContenido (String url) {
        HttpClient httpclient = new DefaultHttpClient();
        String resultado = null;
        HttpGet httpget = new HttpGet(url);
        HttpResponse respuesta = null;
        InputStream stream = null;
        try {
            respuesta = httpclient.execute(httpget);
            HttpEntity entity = respuesta.getEntity();
            if (entity != null ){
                stream= entity.getContent();
                resultado = convertirInputToString(stream);
            }
        } catch (Exception e){
            System.out.println( e.toString());
            System.out.println(" en método recuperancontenido(url");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        System.out.println("Se captur{o lo siguiente " + resultado);
        return resultado;
    }

    //06
    private String convertirInputToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line= "";
        String resultado = "";
        while ((line = bufferedReader.readLine()) != null)
            resultado += line;
        inputStream.close();
        return resultado;
    }

    //07
    public Departamentos convertirJsonObjectDepartamentos(JSONObject jsonObject) throws JSONException {
        Departamentos dep = new Departamentos();
        String num, nom, loc;
        num = jsonObject.optString("numero").toString();
        nom = jsonObject.optString ("nombre").toString();
        loc= jsonObject.optString("localidad").toString();
        dep.setNumero(num);
        dep.setNombre(nom);
        dep.setLocalidad(loc);

        return dep;
    }   // final convertirJsonObjectDepartamentos

    // 08 método  para infrar el menu superior

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navegacion, menu);
        return true;
    }

    //09 método para gestionar el item seleccionado

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        Intent accion;

        if (id== R.id.item_consultar)
        {
            Intent i = new Intent(this, Listado_registros.class);
            startActivity(i);

        }else if (id== R.id.item_alta_registro) {
            Intent i = new Intent(this, Alta_registro.class);
            startActivity(i);
        } else if( id== R.id.item_signup){
            Intent i = new Intent(this, SignUp_Registro.class);
            startActivity(i);
        }else if (id== R.id.item_navegar){

            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, Buscarporid.class);
            startActivity(accion);
        }
        return true;
    }

}
