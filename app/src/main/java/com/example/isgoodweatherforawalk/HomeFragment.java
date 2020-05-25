package com.example.isgoodweatherforawalk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View m_FragmentView;
    private GetLocation m_GetLocation;
    private OpenWeatherApi m_OpenWeatherApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_FragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        getAndUseLocation(m_FragmentView);

        return m_FragmentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

    }

    private void getAndUseLocation(View fragmentView) {
        m_GetLocation = new GetLocation(getActivity());
        m_GetLocation.getLocation();
        if (m_GetLocation.isLocationPermissions()) {
            Double latitude = m_GetLocation.getLatitude();
            Double longitude = m_GetLocation.getLongitude();
            String cityName = m_GetLocation.getCityName();
            String stateName = m_GetLocation.getStateName();
            String fullLocationString = "Latitude: " + latitude.toString()
                    + "\nLongitude: " + longitude.toString()
                    + "\nCity: " + cityName
                    + "\nState: " + stateName;
            TextView textView = fragmentView.findViewById(R.id.textview_first);
            textView.setText(fullLocationString);
            createAndCallWeatherApi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        m_GetLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
        createAndCallWeatherApi();
    }

    private void createAndCallWeatherApi() {
        m_OpenWeatherApi = new OpenWeatherApi(getContext(), m_GetLocation.getLatitude(), m_GetLocation.getLongitude());
        ApiCallback apiCallback = new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                m_OpenWeatherApi.parseResponse(response);
                SendWeatherData sendWeatherData = (SendWeatherData) getActivity();
                if (sendWeatherData != null) {
                    sendWeatherData.sendCurrentWeatherData(m_OpenWeatherApi.getCurrentWeatherData());
                    sendWeatherData.sendMinutelyWeatherData(m_OpenWeatherApi.getMinutelyWeatherData());
                    sendWeatherData.sendHourlyWeatherData(m_OpenWeatherApi.getHourlyWeatherData());
                    sendWeatherData.sendDailyWeatherData(m_OpenWeatherApi.getDailyWeatherData());
                }
                setWeatherValues();
            }
        };
        m_OpenWeatherApi.callApi(apiCallback);
    }

    private void setWeatherValues() {
        CurrentWeatherData currentWeatherData = m_OpenWeatherApi.getCurrentWeatherData();

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
        Integer timeZoneOffset_s = m_OpenWeatherApi.getTimezoneOffset_s();

        Instant sunrise = currentWeatherData.m_SunriseTime;
        TextView homefragSunriseValue = m_FragmentView.findViewById(R.id.homefrag_sunrise_value);
        setTimeValue(homefragSunriseValue, now, sunrise, timeZoneOffset_s);

        Instant sunset = currentWeatherData.m_SunsetTime;
        TextView homefragSunsetValue = m_FragmentView.findViewById(R.id.homefrag_sunset_value);
        setTimeValue(homefragSunsetValue, now, sunset, timeZoneOffset_s);

        Instant forecastTime = currentWeatherData.m_Time;
        TextView homefragForcastTimeValue = m_FragmentView.findViewById(R.id.homefrag_forcast_time_value);
        setTimeValue(homefragForcastTimeValue, now, forecastTime, timeZoneOffset_s);

        makeMinutelyRainChart();
    }

    private void makeMinutelyRainChart() {
        // Get the minutely weather data
        LineDataSet dataSet = buildLineGraphDataset(m_OpenWeatherApi.getMinutelyWeatherData());

        // Set chart styling
        dataSet.setColor(R.attr.colorPrimaryDark);
        dataSet.setCircleColor(R.attr.colorPrimary);
        dataSet.setCircleHoleColor(R.attr.colorPrimaryDark);
        dataSet.setValueTextSize(8);
        dataSet.setValueTextColor(R.attr.colorPrimaryDark);

        // Get the line chart
        LineChart lineChart = m_FragmentView.findViewById(R.id.homefrag_minutely_rain_chart);

        // Set legend and axis lines
        lineChart.getLegend().setTextSize(16);
        lineChart.getLegend().setFormSize(16);
        lineChart.getLegend().setDrawInside(false);
        lineChart.getLegend().setXEntrySpace(5);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);

        // Set chart data and redraw.
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private LineDataSet buildLineGraphDataset(List<MinutelyWeatherData> minutelyWeatherDataList) {
        List<Entry> entries = new ArrayList<>();
        for (int iMinute = 0; iMinute < minutelyWeatherDataList.size(); iMinute++) {
            MinutelyWeatherData minutelyWeatherData = minutelyWeatherDataList.get(iMinute);
            Instant time = minutelyWeatherData.m_Time;
            int diff_sec = (int) (m_OpenWeatherApi.getCurrentWeatherData().m_Time.getEpochSecond() - time.getEpochSecond());
            if (diff_sec >= 0) {
                int diff_min = diff_sec/60;
                float PrecipitationVolume = (float) (double) minutelyWeatherData.m_PrecipitationVolume;
                entries.add(new Entry(diff_min, PrecipitationVolume));
            }
        }
        return new LineDataSet(entries, "Minutely Rain");
    }

    private void setTimeValue(TextView textView, Instant now, Instant other, Integer timeZoneOffset_s) {
        TimeCalculation timeCalculation = new TimeCalculation(now, other, timeZoneOffset_s, "hh:mm aa");
        textView.setText(timeCalculation.getFullTimeString());
    }
}
