package com.example.isgoodweatherforawalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private MainActivity m_Activity;
    private View m_FragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_FragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        return m_FragmentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.homefrag_button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        view.findViewById(R.id.homefrag_button_main_activity_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MainActivity2.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        hideChart();
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to Activity#onStart() Activity.onStart of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    public void setLocationValues() {
        GetLocation getLocation = m_Activity.getGetLocation();
        Double latitude = getLocation.getLatitude();
        Double longitude = getLocation.getLongitude();
        String cityName = getLocation.getCityName();
        String stateName = getLocation.getStateName();
        String countryCode = getLocation.getCountryCode();
        String fullLocationString = String.format(Locale.getDefault(), "(%.2f,%.2f) - %s, %s, %s", latitude, longitude, cityName, stateName, countryCode);
        TextView textView = m_FragmentView.findViewById(R.id.homefrag_title);
        textView.setText(fullLocationString);
    }

    public void setWeatherValues() {
        CurrentWeatherData currentWeatherData = m_Activity.getCurrentWeatherData();

        TextView homefragTemperatureValue = m_FragmentView.findViewById(R.id.homefrag_temperature_value);
        String temperatureString = currentWeatherData.m_Temperature.toString() + " °F";
        homefragTemperatureValue.setText(temperatureString);

        TextView homefragFeelsLikeValue = m_FragmentView.findViewById(R.id.homefrag_feels_like_value);
        String feelsLikeString = currentWeatherData.m_TemperatureFeelsLike.toString() + " °F";
        homefragFeelsLikeValue.setText(feelsLikeString);

        TextView homefragPercentCloudyValue = m_FragmentView.findViewById(R.id.homefrag_percent_cloudy_value);
        String percentCloudString = currentWeatherData.m_PercentCloudy.toString();
        homefragPercentCloudyValue.setText(percentCloudString);

        Instant now = Instant.now();
        Integer timeZoneOffset_s = m_Activity.getTimezoneOffset_s();

        Instant sunrise = currentWeatherData.m_SunriseTime;
        TextView homefragSunriseValue = m_FragmentView.findViewById(R.id.homefrag_sunrise_value);
        setTimeValue(homefragSunriseValue, now, sunrise, timeZoneOffset_s);

        Instant sunset = currentWeatherData.m_SunsetTime;
        TextView homefragSunsetValue = m_FragmentView.findViewById(R.id.homefrag_sunset_value);
        setTimeValue(homefragSunsetValue, now, sunset, timeZoneOffset_s);

        Instant forecastTime = currentWeatherData.m_Time;
        TextView homefragForcastTimeValue = m_FragmentView.findViewById(R.id.homefrag_forcast_time_value);
        setTimeValue(homefragForcastTimeValue, now, forecastTime, timeZoneOffset_s);

        Instant nextRainTime = m_Activity.getNextRainTime();
        TextView homefragNextRainTimeValue = m_FragmentView.findViewById(R.id.homefrag_next_rain_value);
        setTimeValue(homefragNextRainTimeValue, now, nextRainTime, timeZoneOffset_s);

        // Set the icon
        String iconUrl = currentWeatherData.m_WeatherObj.getIconUrl();
        ImageView iconImageView = m_FragmentView.findViewById(R.id.homefrag_weather_icon);
        Picasso.with(requireActivity().getApplicationContext()).load(iconUrl).into(iconImageView);

        // TODO if it isn't going to rain in the next hour, don't display the chart and display a message instead.
        //  Also Have it have the X-Axis turn off too. Make a method to turn on/off both.
        //  Have an if statement here to run the turn off, and have a call at the end of makeMinutelyRainChart() to turn it back on.
        makeMinutelyRainChart();
    }

    private void hideChart() {
        m_FragmentView.findViewById(R.id.homefrag_minutely_rain_chart).setVisibility(View.GONE);
        m_FragmentView.findViewById(R.id.homefrag_chart_x_axis_label).setVisibility(View.GONE);
        m_FragmentView.findViewById(R.id.homefrag_chart_no_rain_text).setVisibility(View.VISIBLE);
    }

    private void showChart() {
        m_FragmentView.findViewById(R.id.homefrag_minutely_rain_chart).setVisibility(View.VISIBLE);
        m_FragmentView.findViewById(R.id.homefrag_chart_x_axis_label).setVisibility(View.VISIBLE);
        m_FragmentView.findViewById(R.id.homefrag_chart_no_rain_text).setVisibility(View.GONE);
    }

    private void makeMinutelyRainChart() {
        hideChart();

        // Get the line chart
        LineChart lineChart = m_FragmentView.findViewById(R.id.homefrag_minutely_rain_chart);

        // Set chart styling
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(1f); // dp
        lineChart.setBorderColor(Color.LTGRAY);
        lineChart.setDrawMarkers(false);

        // Set Axis Styling
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        lineChart.getAxisRight().setEnabled(false);

        // Set legend
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float sp = 14;
        float px = sp*displayMetrics.scaledDensity;
        float dp = px/displayMetrics.density;
        legend.setTextSize(dp); // dp
        legend.setDrawInside(false);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(dp);

        // Set the Axis Labels
        // The yLabel is set with the legend
        TextView xAxisLabel = m_FragmentView.findViewById(R.id.homefrag_chart_x_axis_label);
        xAxisLabel.setText(R.string.homefrag_chart_x_axis_label);

        // Get the minutely weather data
        LineDataSet dataSet = buildLineGraphDataset(m_Activity.getMinutelyWeatherData());

        // Set dataset styling
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setColor(R.attr.colorPrimaryDark);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        // Set chart data and redraw.
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.invalidate();

        Instant now = m_Activity.getCurrentNow();
        Instant nextRain = m_Activity.getNextRainTime();
        int diff_sec = (int) (now.getEpochSecond() - nextRain.getEpochSecond());
        if (diff_sec < TimeCalculation.SEC_IN_HR) {
            // next rain is in less than an hour and should therefore be in the minutely rain data
            showChart();
        } else {
            hideChart();
        }
    }

    private LineDataSet buildLineGraphDataset(List<MinutelyWeatherData> minutelyWeatherDataList) {
        List<Entry> entries = new ArrayList<>();
        for (int iMinute = 0; iMinute < minutelyWeatherDataList.size(); iMinute++) {
            MinutelyWeatherData minutelyWeatherData = minutelyWeatherDataList.get(iMinute);
            Instant time = minutelyWeatherData.m_Time;
            int diff_sec = (int) (time.getEpochSecond() - m_Activity.getCurrentWeatherData().m_Time.getEpochSecond());
            if (diff_sec >= 0) {
                int diff_min = diff_sec/TimeCalculation.SEC_IN_MIN;
                float PrecipitationVolume = (float) (double) minutelyWeatherData.m_PrecipitationVolume;
                entries.add(new Entry(diff_min, PrecipitationVolume));
            }
        }
        String yLabel = getResources().getString(R.string.homefrag_chart_y_axis_label);
        return new LineDataSet(entries, yLabel);
    }

    private void setTimeValue(TextView textView, Instant now, Instant other, Integer timeZoneOffset_s) {
        TimeCalculation timeCalculation = new TimeCalculation(now, other, timeZoneOffset_s, TimeCalculation.mc_DateTimeFormatterPattern_JustTime);
        textView.setText(timeCalculation.getFullTimeString());
    }
}
