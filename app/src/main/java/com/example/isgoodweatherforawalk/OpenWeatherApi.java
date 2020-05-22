package com.example.isgoodweatherforawalk;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

class CurrentWeatherData {

}

class MinutelyWeatherData {

}

class HourlyWeatherData {

}

class DailyWeatherData {

}

public class OpenWeatherApi {
//    https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={part}&units={units}&appid={YOUR API KEY}
//    lat, lon geographical coordinates (latitude, longitude)
//    API key unique API key (you can always find it on the account page, on the "API key" tab)
//    part (optional parameter) by using this parameter you can exclude some parts of weather data from the API response. The value parts should be a comma-delimited list (without spaces). Available values:
//    current
//    minutely
//    hourly
//    daily

    // Static Props
    private static final String TAG = "OpenWeatherApi";
    private static final String mc_ApiKey =  "84f0c5edd23b278b44921947bfe13289";
    private static final String mc_OneCallApiUrl = "https://api.openweathermap.org/data/2.5/onecall?";
    private static final String mc_ApiTag = "appid=";
    private static final String mc_LatTag = "lat=";
    private static final String mc_LongTag = "lon=";
    private static final String mc_UnitsTag = "units=";
    private static final String mc_UnitsImperial = "imperial"; // Fahrenheit,
    private static final String mc_UnitsMetric = "metric"; // Celsius,

    // Constructor props
    private Context m_Context; // Use getContext() or getActivity().getApplicationContext()
    private Double m_Latitude;
    private Double m_Longitude;
    private Date m_CurrentDate;
    private long m_CurrentUnixUtcTime;

    // Url props
    private String m_ApiUrl = mc_OneCallApiUrl;
    private int m_NumberOfApiParams = 0;

    // Response JSON props
    private JSONObject m_ResponseJson;
    private JSONObject m_CurrentJson;
    private JSONObject m_MinutelyJson;
    private JSONObject m_HourlyJson;
    private JSONObject m_DailyJson;

    // Response Data props
    private CurrentWeatherData currentWeatherData;
    private MinutelyWeatherData minutelyWeatherData;
    private HourlyWeatherData hourlyWeatherData;
    private DailyWeatherData dailyWeatherData;

    // Constructor
    public OpenWeatherApi(Context context, double latitude, double longitude) {
        m_Context = context;
        m_Latitude = latitude;
        m_Longitude = longitude;
        m_CurrentDate = Calendar.getInstance().getTime();
        m_CurrentUnixUtcTime = Calendar.getInstance().getTimeInMillis();
    }

    // Public Methods
    public void callApi() {
        // Build Url
        addParamToUrl(mc_LatTag + m_Latitude.toString());
        addParamToUrl(mc_LongTag + m_Longitude.toString());
        addParamToUrl(mc_UnitsTag + mc_UnitsImperial);
        addParamToUrl(mc_ApiTag + mc_ApiKey);

        // Build request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, m_ApiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(TAG + "callApi: There was an error with Volley's response.");
                    }
                });

        // Add to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(m_Context);
        requestQueue.add(jsonObjectRequest);
    }

    private void addParamToUrl(String param) {
        if (m_NumberOfApiParams < 1) {
            m_ApiUrl += param;
        } else {
            m_ApiUrl += "&" + param;
        }
        m_NumberOfApiParams++;
    }

    private void parseResponseJson() {
        String currentTag = "current";
        String minutelyTag = "minutely";
        String hourlyTag = "hourly";
        String dailyTag = "daily";

        try {
            m_CurrentJson = m_ResponseJson.getJSONObject(currentTag);
            m_MinutelyJson = m_ResponseJson.getJSONObject(minutelyTag);
            m_HourlyJson = m_ResponseJson.getJSONObject(hourlyTag);
            m_DailyJson = m_ResponseJson.getJSONObject(dailyTag);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResponse(JSONObject response) {
        m_ResponseJson = response;
        parseResponseJson();
    }

    private Date unixSecondsToDate(long unixSeconds) {
        return new Date(unixSeconds*1000L);
    }
}
