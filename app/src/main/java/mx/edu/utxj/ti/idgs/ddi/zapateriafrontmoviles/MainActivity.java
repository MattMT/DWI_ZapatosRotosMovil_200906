package mx.edu.utxj.ti.idgs.ddi.zapateriafrontmoviles;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import mx.edu.utxj.ti.idgs.ddi.zapateriafrontmoviles.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {

    private EditText etNumeroControl;
    private EditText etTipo;
    private EditText etMarca;
    private EditText etTalla;
    private EditText etPrecioCompra;
    private EditText etPrecioVenta;
    private EditText etExistencias;

    private Button btnBuscar;
    private Button btnGuardar;
    private Button btnEliminar;
    private Button btnActualizar;

    private ListView lvCalzados;

    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String url= "http://192.168.1.72:3330/";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumeroControl=findViewById(R.id.etNumeroControl);
        etTipo=findViewById(R.id.etTipo);
        etMarca=findViewById(R.id.etMarca);
        etTalla=findViewById(R.id.etTalla);
        etPrecioCompra=findViewById(R.id.etPrecioCompra);
        etPrecioVenta=findViewById(R.id.etPrecioVenta);
        etExistencias=findViewById(R.id.etExistencias);

        btnBuscar=findViewById(R.id.btnBuscar);
        btnGuardar=findViewById(R.id.btnGuardar);
        btnEliminar=findViewById(R.id.btnEliminar);
        btnActualizar=findViewById(R.id.btnActualizar);

        lvCalzados=findViewById(R.id.lvCalzados);

        colaPeticiones= Volley.newRequestQueue(this);
        listarCalzados();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion = new JsonObjectRequest(
                        Request.Method.GET,
                        url + etNumeroControl.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Clazado no existente", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etTipo.setText(response.getString("tipo"));
                                        etMarca.setText(response.getString("marca"));
                                        etTalla.setText(String.valueOf(response.getInt("talla")));
                                        etPrecioCompra.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioVenta.setText(String.valueOf(response.getInt("precioventa")));
                                        etExistencias.setText(String.valueOf(response.getInt("existencia")));
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject calzado = new JSONObject();
                try {
                    calzado.put("numerocontrol",etNumeroControl.getText().toString());
                    calzado.put("tipo",etTipo.getText().toString());
                    calzado.put("marca",etMarca.getText().toString());
                    calzado.put("talla",Float.parseFloat(etTalla.getText().toString()));
                    calzado.put("preciocompra",Float.parseFloat(etPrecioCompra.getText().toString()));
                    calzado.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                    calzado.put("existencia",Float.parseFloat(etExistencias.getText().toString()));

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url + "insert",
                        calzado,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Calzado insertado")) {
                                        Toast.makeText(MainActivity.this, "Se inserto el calzado exitosamente", Toast.LENGTH_SHORT).show();
                                        etNumeroControl.setText("");
                                        etTipo.setText("");
                                        etMarca.setText("");
                                        etTalla.setText("");
                                        etPrecioCompra.setText("");
                                        etPrecioVenta.setText("");
                                        etExistencias.setText("");
                                        adapter.clear();
                                        lvCalzados.setAdapter(adapter);
                                        listarCalzados();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNumeroControl.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Seleccione un articulo", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url + "borrar/" + etNumeroControl.getText().toString(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Calzado eliminado")) {
                                            Toast.makeText(MainActivity.this, "Se ha eliminado el calzado", Toast.LENGTH_SHORT).show();
                                            etNumeroControl.setText("");
                                            etTipo.setText("");
                                            etMarca.setText("");
                                            etTalla.setText("");
                                            etPrecioCompra.setText("");
                                            etPrecioVenta.setText("");
                                            etExistencias.setText("");
                                            adapter.clear();
                                            lvCalzados.setAdapter(adapter);
                                            listarCalzados();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "El calzado no está dentro del catalogo", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(jsonObjectRequest);
                }
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNumeroControl.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Seleccione un articulo", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject calzados = new JSONObject();
                    try {
                        calzados.put("numerocontrol", etNumeroControl.getText().toString());
                        if (!etTipo.getText().toString().isEmpty()) {
                            calzados.put("tipo", etTipo.getText().toString());
                        }
                        if (!etMarca.getText().toString().isEmpty()){
                            calzados.put("marca",etMarca.getText().toString());
                        }
                        if (!etTalla.getText().toString().isEmpty()){
                            calzados.put("talla",Float.parseFloat(etTalla.getText().toString()));
                        }
                        if (!etPrecioCompra.getText().toString().isEmpty()){
                            calzados.put("preciocompra",Float.parseFloat(etPrecioCompra.getText().toString()));
                        }
                        if (!etPrecioVenta.getText().toString().isEmpty()){
                            calzados.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                        }
                        if (!etExistencias.getText().toString().isEmpty()){
                            calzados.put("existencia",Float.parseFloat(etExistencias.getText().toString()));
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    JsonObjectRequest actualizar = new JsonObjectRequest(
                            Request.Method.PUT,
                            url + "actualizar/" + etNumeroControl.getText().toString(),
                            calzados,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Calzado actualizado")) {
                                            Toast.makeText(MainActivity.this, "Se ha actulizado el articulo", Toast.LENGTH_SHORT).show();
                                            etNumeroControl.setText("");
                                            etTipo.setText("");
                                            etMarca.setText("");
                                            etTalla.setText("");
                                            etPrecioCompra.setText("");
                                            etPrecioVenta.setText("");
                                            etExistencias.setText("");
                                            adapter.clear();
                                            lvCalzados.setAdapter(adapter);
                                            listarCalzados();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "No se encontró el articulo", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(actualizar);
                }
            }
        });

    }

    protected void listarCalzados(){
        jsonArrayRequest=new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try{
                                String numerocontrol=response.getJSONObject(i).getString("numerocontrol");
                                String tipo=response.getJSONObject(i).getString("tipo");
                                String marca=response.getJSONObject(i).getString("marca");
                                origenDatos.add(numerocontrol+" - "+tipo+" - "+marca);
                            }catch (JSONException e){

                            }
                        }
                        adapter=new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, origenDatos);
                        lvCalzados.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }

}