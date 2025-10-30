package com.example.climate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddCityActivity extends AppCompatActivity {

    private EditText etCityName;
    private Button btnSaveCity;
    private final String API_KEY = "41690c918bf9385d4f642cf1b7e3122e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        etCityName = findViewById(R.id.etCityName);
        btnSaveCity = findViewById(R.id.btnSaveCity);

        btnSaveCity.setOnClickListener(v -> {
            String city = etCityName.getText().toString().trim();
            if (city.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese una ciudad", Toast.LENGTH_SHORT).show();
            } else {
                obtenerYGuardarClima(city);
            }
        });

        ImageView btnvolver = findViewById(R.id.volverAdd);
        btnvolver.setOnClickListener(v -> {
            Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void obtenerYGuardarClima(String cityName) {
        DBHelper dbHelperCheck = new DBHelper(this);
        if (dbHelperCheck.cityExists(cityName)) {
            Toast.makeText(this, "La ciudad ya está en la lista", Toast.LENGTH_SHORT).show();
            dbHelperCheck.close();
            return;
        }
        dbHelperCheck.close();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName +
                "&appid=" + API_KEY + "&units=metric&lang=es";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        int humidity = main.getInt("humidity");

                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String description = weatherObject.getString("description");

                        long timezoneOffset = response.getLong("timezone");

                        WeatherModel weather = new WeatherModel(
                                0,
                                cityName,
                                temp,
                                humidity,
                                description,
                                timezoneOffset
                        );
                        DBHelper dbHelper = new DBHelper(this);
                        dbHelper.insertWeather(weather);

                        Toast.makeText(this, "Ciudad agregada correctamente", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "No se encontró la ciudad", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }
}
