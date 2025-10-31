package com.example.climate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerViewForecast;
    private WeatherAdapter adapter;
    private ForecastAdapter forecastAdapter;
    private List<WeatherModel> weatherList = new ArrayList<>();
    private List<ForecastModel> forecastList = new ArrayList<>();
    private DBHelper dbHelper;
    private TextView tvForecastTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        // Configurar RecyclerView principal (vertical - ciudades)
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(weatherList);
        recyclerView.setAdapter(adapter);

        // Configurar RecyclerView de pronósticos (horizontal)
        recyclerViewForecast = findViewById(R.id.recyclerViewForecast);
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        forecastAdapter = new ForecastAdapter(forecastList);
        recyclerViewForecast.setAdapter(forecastAdapter);

        tvForecastTitle = findViewById(R.id.tvForecastTitle);

        // Cargar datos iniciales
        loadWeatherData();

        // Configurar click en items para mostrar pronóstico
        adapter.setOnItemClickListener(new WeatherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                WeatherModel selectedCity = weatherList.get(position);
                loadForecastData(selectedCity.getCity());
            }
        });

        // Botón para agregar ciudad
        FloatingActionButton fabAddCity = findViewById(R.id.fabAddCity);
        fabAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeatherData();
        // Ocultar pronósticos cuando vuelvas a la actividad
        hideForecastSection();
    }

    private void loadWeatherData() {
        weatherList.clear();
        weatherList.addAll(dbHelper.getAllWeather());
        adapter.notifyDataSetChanged();
    }

    private void loadForecastData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName +
                "&appid=41690c918bf9385d4f642cf1b7e3122e&units=metric&lang=es";

        Log.d("ForecastDebug", "Cargando pronóstico para: " + cityName);
        Log.d("ForecastDebug", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("ForecastDebug", "Respuesta recibida: " + response.toString());
                            processForecastData(response, cityName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al cargar pronóstico", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        Log.e("ForecastDebug", "Error Volley: " + error.toString());
                        Toast.makeText(MainActivity.this, "Error al cargar pronóstico", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    private void processForecastData(JSONObject response, String cityName) throws JSONException {
        //CREAR NUEVA LISTA en lugar de limpiar la existente
        List<ForecastModel> newForecastList = new ArrayList<>();

        JSONArray list = response.getJSONArray("list");
        Log.d("ForecastDebug", "Número de elementos en pronóstico API: " + list.length());

        // Tomar solo los próximos 8 pronósticos
        int count = Math.min(8, list.length());
        for (int i = 0; i < count; i++) {
            JSONObject item = list.getJSONObject(i);

            long timestamp = item.getLong("dt") * 1000;
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));

            JSONObject main = item.getJSONObject("main");
            double temp = main.getDouble("temp");

            JSONArray weatherArray = item.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");

            Log.d("ForecastDebug", "Pronóstico " + i + ": " + time + " - " + temp + "°C - " + description);

            ForecastModel forecast = new ForecastModel(time, temp, description, "");
            newForecastList.add(forecast);
        }

        Log.d("ForecastDebug", "Total de pronósticos procesados: " + newForecastList.size());

        // PASAR LA NUEVA LISTA AL ADAPTER
        final List<ForecastModel> finalList = newForecastList;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ACTUALIZAR CON LA NUEVA LISTA
                forecastAdapter.updateData(finalList);

                Log.d("ForecastDebug", "Adapter item count después de update: " + forecastAdapter.getItemCount());

                // MOSTRAR LA SECCIÓN
                showForecastSection(cityName);
            }
        });
    }

    private void showForecastSection(String cityName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvForecastTitle.setVisibility(View.VISIBLE);
                tvForecastTitle.setText("Pronóstico: " + cityName);
                recyclerViewForecast.setVisibility(View.VISIBLE);

                // VERIFICAR QUE SE HAYA HECHO VISIBLE
                Log.d("ForecastDebug", "Title visible: " + (tvForecastTitle.getVisibility() == View.VISIBLE));
                Log.d("ForecastDebug", "RecyclerView visible: " + (recyclerViewForecast.getVisibility() == View.VISIBLE));
                Log.d("ForecastDebug", "Adapter tiene datos: " + forecastAdapter.getItemCount());
            }
        });
    }

    private void hideForecastSection() {
        tvForecastTitle.setVisibility(View.GONE);
        recyclerViewForecast.setVisibility(View.GONE);
    }
}