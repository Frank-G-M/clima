package com.example.climate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteCityActivity extends AppCompatActivity {
    private EditText editTextCityName;
    private Button buttonDeleteCity;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_city);

        editTextCityName = findViewById(R.id.editTextCityName);
        buttonDeleteCity = findViewById(R.id.buttonDeleteCity);
        dbHelper = new DBHelper(this);

        buttonDeleteCity.setOnClickListener(v -> {
            String cityName = editTextCityName.getText().toString().trim();

            if (cityName.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean deleted = dbHelper.deleteCity(cityName);
            if (deleted) {
                Toast.makeText(this, "Ciudad eliminada correctamente", Toast.LENGTH_SHORT).show();
                editTextCityName.setText("");
            } else {
                Toast.makeText(this, "No se encontró la ciudad", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView btnvolver = findViewById(R.id.volverDelete);
        btnvolver.setOnClickListener(v -> {
            Intent intent = new Intent(DeleteCityActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
