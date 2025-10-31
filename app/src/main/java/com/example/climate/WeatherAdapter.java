package com.example.climate;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<WeatherModel> list;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

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

        int weatherIcon = getWeatherIcon(weather.getDescription());
        holder.imgWeather.setImageResource(weatherIcon);

        long timezoneOffset = weather.getTimezone();

        TimeZone cityTimeZone = TimeZone.getTimeZone("GMT");
        cityTimeZone.setRawOffset((int) (timezoneOffset * 1000));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(cityTimeZone);

        String localTime = sdf.format(new Date());

        holder.textViewTime.setText("Hora local: " + localTime);

        // Manejar clicks en el item para pronóstico
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(adapterPosition);
                    }
                }
            }
        });

        // Botón para eliminar ciudad
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    String cityName = list.get(adapterPosition).getCity();
                    showDeleteDialog(holder.itemView.getContext(), cityName, adapterPosition);
                    return true;
                }
                return false;
            }
        });
    }

    private void showDeleteDialog(Context context, String cityName, int position) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Eliminar Ciudad")
                .setMessage("¿Estás seguro de que quieres eliminar " + cityName + "?")
                .setPositiveButton("Eliminar", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        deleteCity(context, cityName, position);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void deleteCity(Context context, String cityName, int position) {
        DBHelper dbHelper = new DBHelper(context);
        boolean deleted = dbHelper.deleteCity(cityName);

        if (deleted) {
            list.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, cityName + " eliminada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
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
        } else if (desc.contains("drizzle") || desc.contains("llovizna")) {
            return R.drawable.ic_rain;
        } else if (desc.contains("thunderstorm")) {
            return R.drawable.ic_storm;
        } else if (desc.contains("fog")) {
            return R.drawable.ic_mist;
        } else {
            return R.drawable.ic_default;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable);
    }
}