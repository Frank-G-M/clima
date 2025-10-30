package com.example.climate;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class WeatherService {
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=es";
    private final String apiKey;
    private final RequestQueue queue;

    public interface WeatherCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public WeatherService(Context context, String apiKey) {
        this.apiKey = apiKey;
        this.queue = Volley.newRequestQueue(context);
    }

    public void getWeather(String city, WeatherCallback callback) {
        String url = String.format(API_URL, city, apiKey);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );
        queue.add(request);
    }
}
