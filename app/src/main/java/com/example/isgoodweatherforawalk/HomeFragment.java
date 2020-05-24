package com.example.isgoodweatherforawalk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class HomeFragment extends Fragment {
    private View m_FragmentView;
    private GetLocation m_GetLocation;
    private ApiCallback m_ApiCallback;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        m_GetLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
        createAndCallWeatherApi();
    }

    private void createAndCallWeatherApi() {
        m_OpenWeatherApi = new OpenWeatherApi(getContext(), m_GetLocation.getLatitude(), m_GetLocation.getLongitude());
        m_ApiCallback = new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                m_OpenWeatherApi.parseResponse(response);
                SendWeatherData sendWeatherData = (SendWeatherData) getActivity();
                sendWeatherData.sendCurrentWeatherData(m_OpenWeatherApi.getCurrentWeatherData());
                sendWeatherData.sendMinutelyWeatherData(m_OpenWeatherApi.getMinutelyWeatherData());
                sendWeatherData.sendHourlyWeatherData(m_OpenWeatherApi.getHourlyWeatherData());
                sendWeatherData.sendDailyWeatherData(m_OpenWeatherApi.getDailyWeatherData());
                setWeatherValues();
            }
        };
        m_OpenWeatherApi.callApi(m_ApiCallback);
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
    }

    private void makeMinutelyRainChart() {

    }

    private void setTimeValue(TextView textView, Instant now, Instant other, Integer timeZoneOffset_s) {
        // Get the clock time string, e.g. 06:00 pm
        ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutesSeconds(0, 0, timeZoneOffset_s);
        ZoneId zone = ZoneId.ofOffset("UTC", zoneOffset);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(other, zone);
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern("hh:mm aa"));

        // Get the time difference string, e.g. 6 hr ago
        Long diff = now.getEpochSecond() - other.getEpochSecond();
        Long diffVal;
        String diffLabel;
        if (diff/3600.0 > 1) {
            diffVal = diff/3600;
            diffLabel = "hr";
        } else if (diff/60.0 > 1) {
            diffVal = diff/60;
            diffLabel = "min";
        } else {
            diffVal = diff;
            diffLabel = "s";
        }
        String diffString;
        if (diff > 0) { // now is after other, ie other happened first
            diffString = diffVal + " " + diffLabel + " " + "ago";
        } else { // other is after now, ie now happened first
            diffString = "in"+ " " + diffVal + " " + diffLabel;
        }

        // Build and set the full string
        String fullTimeString = localDateTimeString + " (" + diffString + ")";
        textView.setText(fullTimeString);
    }
}
