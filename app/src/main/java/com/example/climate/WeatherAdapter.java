package com.example.climate;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<WeatherModel> list;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    public WeatherAdapter(List<WeatherModel> list) {
        this.list = list;

        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(updateTimeRunnable);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity, tvTemp, textViewTime;
        ImageView imgWeather;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imgWeather = itemView.findViewById(R.id.imgWeather);
        }
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel weather = list.get(position);

        holder.tvCity.setText(weather.getCity());
        holder.tvTemp.setText(String.format(Locale.getDefault(), "%.1f°C", weather.getTemperature()));

        long timezoneOffset = weather.getTimezone();
        long currentTimeMillis = System.currentTimeMillis();
        long localTimeMillis = currentTimeMillis + (timezoneOffset * 1000);

        Date date = new Date(localTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String localTime = sdf.format(date);

        holder.textViewTime.setText("Hora local: " + localTime);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable);
    }
}
