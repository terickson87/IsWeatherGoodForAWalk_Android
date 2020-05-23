package com.example.isgoodweatherforawalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONObject;

public class HomeFragment extends Fragment {
    private GetLocation m_GetLocation;
    private ApiCallback m_ApiCallback;
    private OpenWeatherApi m_OpenWeatherApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_home, container, false);

        getAndUseLocation(fragmentView);


        return  fragmentView;
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
                Activity activity = getActivity();
                SendWeatherData sendWeatherData = (SendWeatherData) activity;
                sendWeatherData.sendCurrentWeatherData(m_OpenWeatherApi.getCurrentWeatherData());
                sendWeatherData.sendMinutelyWeatherData(m_OpenWeatherApi.getMinutelyWeatherData());
                sendWeatherData.sendHourlyWeatherData(m_OpenWeatherApi.getHourlyWeatherData());
                sendWeatherData.sendDailyWeatherData(m_OpenWeatherApi.getDailyWeatherData());
            }
        };
        m_OpenWeatherApi.callApi(m_ApiCallback);
    }
}
