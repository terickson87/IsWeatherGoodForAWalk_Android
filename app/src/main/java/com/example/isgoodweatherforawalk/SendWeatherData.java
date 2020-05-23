package com.example.isgoodweatherforawalk;

import java.util.List;

public interface SendWeatherData {
    public void sendCurrentWeatherData(CurrentWeatherData currentWeatherData);
    public void sendMinutelyWeatherData(List<MinutelyWeatherData> minutelyWeatherData);
    public void sendHourlyWeatherData(List<HourlyWeatherData> hourlyWeatherData);
    public void sendDailyWeatherData(List<DailyWeatherData> dailyWeatherData);
}
