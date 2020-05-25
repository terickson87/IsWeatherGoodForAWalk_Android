package com.example.isgoodweatherforawalk;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class WeatherObj {
    String m_Description;
    String m_MainDescriptionShort;
    private String m_IconUrl;
    private static final String mc_IconUrlStart = "https://openweathermap.org/img/wn/";
    private static final String mc_IconUrlEnd = "@4x.png";

    void setIcon(String icon) {
        m_IconUrl = mc_IconUrlStart + icon + mc_IconUrlEnd;
    }

    void setIconUrl(String iconUrl) {
        m_IconUrl = iconUrl;
    }

    String getIconUrl() {
        return m_IconUrl;
    }

}

class DailyTemp {
    double m_Day;
    double m_Min;
    double m_Max;
    double m_Night;
    double m_Evening;
    double m_Morning;
}

class DailyTempFeelsLike {
    double m_Day;
    double m_Night;
    double m_Evening;
    double m_Morning;
}

class CurrentWeatherData {
    Instant m_Time;
    Instant m_SunsetTime;
    Instant m_SunriseTime;
    Double m_Temperature;
    Double m_TemperatureFeelsLike;
    Integer m_PercentCloudy;
    Double m_WindSpeed;
    Double m_WindGust;
    WeatherObj m_WeatherObj;
}

class MinutelyWeatherData {
    Instant m_Time;
    Double m_PrecipitationVolume; // mm
}

class HourlyWeatherData {
    Instant m_Time;
    Double m_Temperature;
    Double m_TemperatureFeelsLike;
    Integer m_PercentCloudy;
    Double m_WindSpeed;
    WeatherObj m_WeatherObj;

}

class DailyWeatherData {
    Instant m_Time;
    Instant m_SunsetTime;
    Instant m_SunriseTime;
    DailyTemp m_DailyTemp;
    DailyTempFeelsLike m_DailyTempFeelsLike;
    Integer m_PercentCloudy;
    Double m_WindSpeed;
    WeatherObj m_WeatherObj;
    Double m_PrecipitationVolume; // mm
    Double m_SnowVolume; // mm
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
    private Instant m_CurrentDate;
    private long m_CurrentUnixUtcTime;

    // Url props
    private String m_ApiUrl = mc_OneCallApiUrl;
    private int m_NumberOfApiParams = 0;

    // Response JSON props
    private JSONObject m_ResponseJson;
    private JSONObject m_CurrentJson;
    private JSONArray m_MinutelyJson;
    private JSONArray m_HourlyJson;
    private JSONArray m_DailyJson;

    // Response Data props
    private String m_TimezoneString;
    private Integer m_TimezoneOffset_s; // Shift in seconds from UTC
    private CurrentWeatherData m_CurrentWeatherData;
    private List<MinutelyWeatherData> m_MinutelyWeatherData;
    private List<HourlyWeatherData> m_HourlyWeatherData;
    private List<DailyWeatherData> m_DailyWeatherData;

    // Getters
    public CurrentWeatherData getCurrentWeatherData() {
        return m_CurrentWeatherData;
    }

    public List<MinutelyWeatherData> getMinutelyWeatherData() {
        return m_MinutelyWeatherData;
    }

    public List<HourlyWeatherData> getHourlyWeatherData() {
        return m_HourlyWeatherData;
    }

    public List<DailyWeatherData> getDailyWeatherData() {
        return m_DailyWeatherData;
    }

    public String getTimezoneString() {
        return m_TimezoneString;
    }

    public Integer getTimezoneOffset_s() {
        return m_TimezoneOffset_s;
    }

    // Constructor
    public OpenWeatherApi(Context context, double latitude, double longitude) {
        m_Context = context;
        m_Latitude = latitude;
        m_Longitude = longitude;
        m_CurrentDate = Instant.now();
        m_CurrentUnixUtcTime = Calendar.getInstance().getTimeInMillis();
    }

    // Public Methods
    public void callApi(final ApiCallback apiCallback) {

        // Build Url
        addParamToUrl(mc_LatTag + m_Latitude.toString());
        addParamToUrl(mc_LongTag + m_Longitude.toString());
        addParamToUrl(mc_UnitsTag + mc_UnitsImperial);
        addParamToUrl(mc_ApiTag + mc_ApiKey);
        System.out.println(TAG + " - callApi Called. ApiUrl = \"" + m_ApiUrl + "\"");

        // Build request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, m_ApiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        apiCallback.onSuccess(response);
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
        System.out.println(TAG + " - parseResponseJson Called.");
        String currentTag = "current";
        String minutelyTag = "minutely";
        String hourlyTag = "hourly";
        String dailyTag = "daily";

        try {
            m_TimezoneString = m_ResponseJson.getString("timezone");
            m_TimezoneOffset_s = m_ResponseJson.getInt("timezone_offset");
            m_CurrentJson = m_ResponseJson.getJSONObject(currentTag);
            m_MinutelyJson = m_ResponseJson.getJSONArray(minutelyTag);
            m_HourlyJson = m_ResponseJson.getJSONArray(hourlyTag);
            m_DailyJson = m_ResponseJson.getJSONArray(dailyTag);
            m_TimezoneString = m_ResponseJson.getString("timezone");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseResponse(JSONObject response) {
        System.out.println(TAG + " - parseResponse Called.");
        m_ResponseJson = response;
        parseResponseJson();
        parseCurrent();
        parseMinutely();
        parseHourly();
        parseDaily();
    }

    private WeatherObj parseWeatherJson(JSONObject parentObjOfWeatherJsonArr) {
        System.out.println(TAG + " - parseWeatherJson Called.");
        WeatherObj weatherObj = new WeatherObj();
        JSONArray weatherJsonArr;
        try {
            weatherJsonArr = parentObjOfWeatherJsonArr.getJSONArray("weather");
            JSONObject weatherJsonObj = weatherJsonArr.getJSONObject(0);
            weatherObj.m_Description = weatherJsonObj.getString("description");
            weatherObj.m_MainDescriptionShort = weatherJsonObj.getString("main");
            weatherObj.setIcon(weatherJsonObj.getString("icon"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherObj;
    }

    private void parseCurrent() {
        System.out.println(TAG + " - ParseCurrent Called.");
        m_CurrentWeatherData = new CurrentWeatherData();
        try {
            m_CurrentWeatherData.m_Time = Instant.ofEpochSecond(m_CurrentJson.getLong("dt"));
            m_CurrentWeatherData.m_SunriseTime = Instant.ofEpochSecond(m_CurrentJson.getLong("sunrise"));
            m_CurrentWeatherData.m_SunsetTime = Instant.ofEpochSecond(m_CurrentJson.getLong("sunset"));
            m_CurrentWeatherData.m_Temperature = m_CurrentJson.getDouble("temp");
            m_CurrentWeatherData.m_TemperatureFeelsLike = m_CurrentJson.getDouble("feels_like");
            m_CurrentWeatherData.m_PercentCloudy = m_CurrentJson.getInt("clouds");
            m_CurrentWeatherData.m_WindSpeed = m_CurrentJson.getDouble("wind_speed");
            m_CurrentWeatherData.m_WindGust = m_CurrentJson.getDouble("wind_gust");
            m_CurrentWeatherData.m_WeatherObj = parseWeatherJson(m_CurrentJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseMinutely() {
        System.out.println(TAG + " - parseMinutely Called.");
        m_MinutelyWeatherData = new LinkedList<>();
        try {
            for (int iMinute = 0; iMinute < m_MinutelyJson.length(); iMinute++) {
                JSONObject minuteJson = m_MinutelyJson.getJSONObject(iMinute);
                MinutelyWeatherData minutelyWeatherData = new MinutelyWeatherData();

                minutelyWeatherData.m_Time = Instant.ofEpochSecond(minuteJson.getLong("dt"));
                minutelyWeatherData.m_PrecipitationVolume = minuteJson.getDouble("precipitation");

                m_MinutelyWeatherData.add(minutelyWeatherData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseHourly() {
        System.out.println(TAG + " - parseHourly Called.");
        m_HourlyWeatherData = new LinkedList<>();
        try {
            for (int iHour = 0; iHour < m_HourlyJson.length(); iHour++) {
                JSONObject hourJson = m_HourlyJson.getJSONObject(iHour);
                HourlyWeatherData hourlyWeatherData = new HourlyWeatherData();

                hourlyWeatherData.m_Time = Instant.ofEpochSecond(hourJson.getLong("dt"));
                hourlyWeatherData.m_Temperature = hourJson.getDouble("temp");
                hourlyWeatherData.m_TemperatureFeelsLike = hourJson.getDouble("feels_like");
                hourlyWeatherData.m_PercentCloudy = hourJson.getInt("clouds");
                hourlyWeatherData.m_WindSpeed = hourJson.getDouble("wind_speed");
                hourlyWeatherData.m_WeatherObj = parseWeatherJson(hourJson);

                m_HourlyWeatherData.add(hourlyWeatherData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseDaily() {
        System.out.println(TAG + " - parseDaily Called.");
        m_DailyWeatherData = new LinkedList<>();
        for (int iDay = 0; iDay < m_DailyJson.length(); iDay++) {
            DailyWeatherData dailyWeatherData = new DailyWeatherData();
            try {
                JSONObject dayJson = m_DailyJson.getJSONObject(iDay);
                dailyWeatherData.m_Time = Instant.ofEpochSecond(dayJson.getLong("dt"));
                dailyWeatherData.m_SunriseTime = Instant.ofEpochSecond(dayJson.getLong("sunrise"));
                dailyWeatherData.m_SunsetTime = Instant.ofEpochSecond(dayJson.getLong("sunset"));
                dailyWeatherData.m_DailyTemp = parseDailyTemp(dayJson);
                dailyWeatherData.m_DailyTempFeelsLike = parseDailyTempFeelsLike(dayJson);
                dailyWeatherData.m_WindSpeed = dayJson.getDouble("wind_speed");
                dailyWeatherData.m_WeatherObj = parseWeatherJson(dayJson);
                dailyWeatherData.m_PercentCloudy = dayJson.getInt("clouds");
                try {
                    dailyWeatherData.m_PrecipitationVolume = dayJson.getDouble("rain");
                } catch(JSONException e) {
                    dailyWeatherData.m_PrecipitationVolume = 0.0;
                }
                try {
                    dailyWeatherData.m_SnowVolume = dayJson.getDouble("snow");
                } catch(JSONException e) {
                    dailyWeatherData.m_SnowVolume = 0.0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            m_DailyWeatherData.add(dailyWeatherData);
            }

    }

    private DailyTemp parseDailyTemp(JSONObject parentObjOfDailyTempJsonObj) {
        DailyTemp dailyTemp = new DailyTemp();
        try {
            JSONObject dailyTempJsonObj = parentObjOfDailyTempJsonObj.getJSONObject("temp");
            dailyTemp.m_Day = dailyTempJsonObj.getDouble("day");
            dailyTemp.m_Night = dailyTempJsonObj.getDouble("night");
            dailyTemp.m_Evening = dailyTempJsonObj.getDouble("eve");
            dailyTemp.m_Morning = dailyTempJsonObj.getDouble("morn");
            dailyTemp.m_Max = dailyTempJsonObj.getDouble("min");
            dailyTemp.m_Min = dailyTempJsonObj.getDouble("max");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyTemp;
    }

    private DailyTempFeelsLike parseDailyTempFeelsLike(JSONObject parentObjOfDailyTempFeelsLikeJsonObj) {
        DailyTempFeelsLike dailyTempFeelsLike = new DailyTempFeelsLike();
        try {
            JSONObject dailyTempFeelsLikeJsonObj = parentObjOfDailyTempFeelsLikeJsonObj.getJSONObject("feels_like");
            dailyTempFeelsLike.m_Day = dailyTempFeelsLikeJsonObj.getDouble("day");
            dailyTempFeelsLike.m_Night = dailyTempFeelsLikeJsonObj.getDouble("night");
            dailyTempFeelsLike.m_Evening = dailyTempFeelsLikeJsonObj.getDouble("eve");
            dailyTempFeelsLike.m_Morning = dailyTempFeelsLikeJsonObj.getDouble("morn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyTempFeelsLike;
    }

    private Date unixSecondsToDate(long unixSeconds) {
        return new Date(unixSeconds*1000L);
    }
}
