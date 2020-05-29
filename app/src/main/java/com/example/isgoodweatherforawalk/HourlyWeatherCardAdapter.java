package com.example.isgoodweatherforawalk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherCardAdapter extends RecyclerView.Adapter<HourlyWeatherCardAdapter.HourlyWeatherCardHolder> {
    private ArrayList<HourlyWeatherData> m_HourlyWeatherDataList;
    private MainActivity m_MainActivity;

    public HourlyWeatherCardAdapter(MainActivity mainActivity, List<HourlyWeatherData> hourlyWeatherData) {
        m_MainActivity = mainActivity;
        m_HourlyWeatherDataList = new ArrayList<HourlyWeatherData>(hourlyWeatherData);
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public HourlyWeatherCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_weather_card, parent, false);
        return new HourlyWeatherCardHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the ViewHolder#itemView to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use ViewHolder#getAdapterPosition() which will
     * have the updated adapter position.
     * <p>
     * Override onBindViewHolder(ViewHolder, int, List) instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherCardHolder holder, int position) {
        HourlyWeatherData hourlyWeatherData = m_HourlyWeatherDataList.get(position);
        String stringToSet;

        Instant now = m_MainActivity.getCurrentNow();
        Integer timeZoneOffset_s = m_MainActivity.getTimezoneOffset_s();
        TimeCalculation timeCalculation = new TimeCalculation(now, hourlyWeatherData.m_Time, timeZoneOffset_s, TimeCalculation.mc_DateTimeFormatterPattern_TimeAndDate);
        stringToSet = timeCalculation.getFullTimeString();
        holder.m_HourlyWeatherCardDateView.setText(stringToSet);

        stringToSet = hourlyWeatherData.m_Temperature.toString() + "°F"
                + " / " + hourlyWeatherData.m_TemperatureFeelsLike.toString() + "°F";
        holder.m_HourlyWeatherCardTemperatureView.setText(stringToSet);

        stringToSet = hourlyWeatherData.m_PercentCloudy.toString() + "%";
        holder.m_HourlyWeatherCardCloudyView.setText(stringToSet);

        stringToSet = hourlyWeatherData.m_PrecipitationVolume.toString() + "mm";
        holder.m_HourlyWeatherCardRainView.setText(stringToSet);

        stringToSet = "Yes"; // TODO add the calculation if it is good to walk or not test
        holder.m_HourlyWeatherCardIsGoodWalkView.setText(stringToSet);

        stringToSet = hourlyWeatherData.m_WeatherObj.m_MainDescriptionShort;
        holder.m_HourlyWeatherCardDescriptionView.setText(stringToSet);

        // Set icon
        String iconUrl = hourlyWeatherData.m_WeatherObj.getIconUrl();
        Context context = holder.m_HourlyWeatherCardIconView.getContext();
        Picasso.with(context).load(iconUrl).into(holder.m_HourlyWeatherCardIconView);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return m_HourlyWeatherDataList.size();
    }

    public class HourlyWeatherCardHolder extends RecyclerView.ViewHolder {
        public CardView m_HourlyWeatherCardView;
        public ImageView m_HourlyWeatherCardIconView;
        public TextView m_HourlyWeatherCardDateView;
        public TextView m_HourlyWeatherCardTemperatureView;
        public TextView m_HourlyWeatherCardCloudyView;
        public TextView m_HourlyWeatherCardRainView;
        public TextView m_HourlyWeatherCardIsGoodWalkView;
        public TextView m_HourlyWeatherCardDescriptionView;

        public HourlyWeatherCardHolder(@NonNull View itemView) {
            super(itemView);
            m_HourlyWeatherCardView = itemView.findViewById(R.id.hourly_weather_card);
            m_HourlyWeatherCardIconView = itemView.findViewById(R.id.hourly_weather_card_icon);
            m_HourlyWeatherCardDateView = itemView.findViewById(R.id.hourly_weather_card_date_value);
            m_HourlyWeatherCardTemperatureView = itemView.findViewById(R.id.hourly_weather_card_temperature_value);
            m_HourlyWeatherCardCloudyView = itemView.findViewById(R.id.hourly_weather_card_cloudy_value);
            m_HourlyWeatherCardRainView = itemView.findViewById(R.id.hourly_weather_card_rain_value);
            m_HourlyWeatherCardIsGoodWalkView = itemView.findViewById(R.id.hourly_weather_card_good_walk_value);
            m_HourlyWeatherCardDescriptionView = itemView.findViewById(R.id.hourly_weather_card_description_value);
        }
    }
}
