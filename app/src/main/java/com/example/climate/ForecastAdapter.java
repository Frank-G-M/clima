package com.example.climate;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder>{
    private List<ForecastModel> forecastList;

    public ForecastAdapter(List<ForecastModel> forecastList) {
        this.forecastList = forecastList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemp;
        ImageView imgWeather;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            imgWeather = itemView.findViewById(R.id.imgWeather);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        Log.d("ForecastAdapter", "ViewHolder creado con layout item_forecast");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (forecastList.isEmpty()) {
            Log.d("ForecastAdapter", "Lista de pronósticos vacía en onBindViewHolder");
            return;
        }

        ForecastModel forecast = forecastList.get(position);

        Log.d("ForecastAdapter", "Mostrando pronóstico: " + forecast.getTime() + " - " + forecast.getTemperature() + "°C");

        holder.tvTime.setText(forecast.getTime());
        holder.tvTemp.setText(String.format(Locale.getDefault(), "%.0f°C", forecast.getTemperature()));

        int weatherIcon = getWeatherIcon(forecast.getDescription());
        holder.imgWeather.setImageResource(weatherIcon);
    }

    private int getWeatherIcon(String description) {
        if (description == null) return R.drawable.ic_default;

        String desc = description.toLowerCase();

        if (desc.contains("clear") || desc.contains("despejado") || desc.contains("soleado")) {
            return R.drawable.ic_clear;
        } else if (desc.contains("cloud") || desc.contains("nublado") || desc.contains("nubes")) {
            return R.drawable.ic_cloudy;
        } else if (desc.contains("rain") || desc.contains("lluvia") || desc.contains("lluvioso")) {
            return R.drawable.ic_rain;
        } else if (desc.contains("snow") || desc.contains("nieve") || desc.contains("nevando")) {
            return R.drawable.ic_snow;
        } else if (desc.contains("storm") || desc.contains("tormenta") || desc.contains("truenos")) {
            return R.drawable.ic_storm;
        } else if (desc.contains("mist") || desc.contains("neblina") || desc.contains("bruma") || desc.contains("niebla")) {
            return R.drawable.ic_mist;
        } else {
            return R.drawable.ic_default;
        }
    }

    @Override
    public int getItemCount() {
        int count = forecastList.size();
        Log.d("ForecastAdapter", "getItemCount: " + count);
        return count;
    }

    public void updateData(List<ForecastModel> newForecastList) {
        Log.d("ForecastAdapter", "updateData llamado con: " + newForecastList.size() + " elementos");

        // ✅ LIMPIAR LA LISTA ACTUAL Y AGREGAR LOS NUEVOS DATOS
        this.forecastList.clear();
        if (newForecastList != null) {
            this.forecastList.addAll(newForecastList);
        }

        Log.d("ForecastAdapter", "Después de updateData, itemCount: " + getItemCount());
        notifyDataSetChanged();
    }
}