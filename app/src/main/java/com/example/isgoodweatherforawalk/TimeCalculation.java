package com.example.isgoodweatherforawalk;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeCalculation {
    // Static Constants
    public static final int SEC_IN_MIN = 60;
    public static final int MIN_IN_HR = 60;
    public static final int SEC_IN_HR = SEC_IN_MIN*MIN_IN_HR;

    // Member Variables
    private Instant m_Now;
    private Instant m_Other;
    private Long m_Diff_s;
    private Integer m_TimezoneOffset_s = 0;
    private ZoneOffset m_ZoneOffset = ZoneOffset.ofHoursMinutesSeconds(0, 0, m_TimezoneOffset_s);
    private ZoneId m_ZoneId = ZoneId.ofOffset("UTC", m_ZoneOffset);
    private LocalDateTime m_LocalDateTime;
    private String m_DateTimeFormatterPattern = mc_DateTimeFormatterPattern_JustTime;
    public static final String mc_DateTimeFormatterPattern_JustTime = "h:mm a";
    public static final String mc_DateTimeFormatterPattern_TimeAndDate = "E, MMM dd, h:mm a";
    private DateTimeFormatter m_DateTimeFormatter = DateTimeFormatter.ofPattern(m_DateTimeFormatterPattern);
    private String m_LocalDateTimeString;
    private String m_DiffString;
    private String m_FullTimeString;

    // Constructors
    public TimeCalculation(Instant now, Instant other) {
        m_Now = now;
        m_Other = other;
        buildAllStrings();
    }

    public TimeCalculation(Instant now, Instant other, Integer timezoneOffset_s) {
        m_Now = now;
        m_Other = other;
        m_TimezoneOffset_s = timezoneOffset_s;
        buildAllStrings();
    }

    public TimeCalculation(Instant now, Instant other, Integer timezoneOffset_s, String dateTimeFormatterPattern) {
        m_Now = now;
        m_Other = other;
        m_TimezoneOffset_s = timezoneOffset_s;
        m_DateTimeFormatterPattern = dateTimeFormatterPattern;
        buildAllStrings();
    }

    public TimeCalculation(Instant now, Instant other, String dateTimeFormatterPattern) {
        m_Now = now;
        m_Other = other;
        m_DateTimeFormatterPattern = dateTimeFormatterPattern;
        buildAllStrings();
    }

    // Public Getters
    public Instant getNow() {
        return m_Now;
    }

    public Instant getOther() {
        return m_Other;
    }

    public Integer getTimezoneOffset_s() {
        return m_TimezoneOffset_s;
    }

    public ZoneOffset getZoneOffset() {
        return m_ZoneOffset;
    }

    public ZoneId getZoneId() {
        return m_ZoneId;
    }

    public LocalDateTime getLocalDateTime() {
        return m_LocalDateTime;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return m_DateTimeFormatter;
    }

    public String getLocalDateTimeString() {
        return m_LocalDateTimeString;
    }

    public Long getDiff_s() {
        return m_Diff_s;
    }

    public String getDiffString() {
        return m_DiffString;
    }

    public String getFullTimeString() {
        return m_FullTimeString;
    }

    // Public Setters
    public void setNow(Instant now) {
        m_Now = now;
        buildAllStrings();
    }

    public void setOther(Instant other) {
        m_Other = other;
        buildAllStrings();
    }

    public void setDateTimePattern(String dateTimePattern) {
        m_DateTimeFormatterPattern = dateTimePattern;
        buildAllStrings();
    }

    public void setTimezoneOffset_s(Integer timezoneOffset_s) {
        m_TimezoneOffset_s = timezoneOffset_s;
        buildAllStrings();
    }

    public void setFormatJustTime() {
        m_DateTimeFormatterPattern = mc_DateTimeFormatterPattern_JustTime;
        buildAllStrings();
    }

    public void setFormatTimeAndDate() {
        m_DateTimeFormatterPattern = mc_DateTimeFormatterPattern_TimeAndDate;
        buildAllStrings();
    }

    // Calculation Methods
    private void buildAllStrings() {
        buildLocalDateTimeString();
        buildDiffString();
        buildFullTimeString();
    }

    private void buildLocalDateTimeString() {
        // Get the clock time string, e.g. 06:00 pm
        int hoursOffset = getHours(m_TimezoneOffset_s);
        int minutesOffset = getMinutes(m_TimezoneOffset_s);
        int secondsOffset = getSeconds(m_TimezoneOffset_s);
        m_ZoneOffset = ZoneOffset.ofHoursMinutesSeconds(hoursOffset, minutesOffset, secondsOffset);
        m_ZoneId = ZoneId.ofOffset("UTC", m_ZoneOffset);
        m_LocalDateTime = LocalDateTime.ofInstant(m_Other, m_ZoneId);
        m_LocalDateTimeString = m_LocalDateTime.format(m_DateTimeFormatter);
    }

    private void buildDiffString() {
        // Get the time difference string, e.g. 6 hr ago
        m_Diff_s = m_Now.getEpochSecond() - m_Other.getEpochSecond();
        long diffVal;
        String diffLabel;
        diffVal = getHours(m_Diff_s);
        if (Math.abs(diffVal) > 0) {
            diffLabel = "hr";
        } else {
            diffVal = getMinutes(m_Diff_s);
            if (Math.abs(diffVal) > 0) {
                diffLabel = "min";
            } else {
                diffVal = getSeconds(m_Diff_s);
                diffLabel = "s";
            }
        }

        if (m_Diff_s > 0) { // now is after other, ie other happened first
            m_DiffString = diffVal + " " + diffLabel + " " + "ago";
        } else { // other is after now, ie now happened first
            m_DiffString = "in"+ " " + -diffVal + " " + diffLabel;
        }
    }

    private int getHours(int seconds) {
        return seconds/SEC_IN_HR;
    }

    private int getMinutes(int seconds) {
        int hours = getHours(seconds);
        seconds -= hours*SEC_IN_HR;
        return seconds/SEC_IN_MIN;
    }

    private int getSeconds(int seconds) {
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds);
        return seconds - hours*SEC_IN_HR - minutes*MIN_IN_HR;
    }

    private long getHours(long seconds) {
        return seconds/SEC_IN_HR;
    }

    private long getMinutes(long seconds) {
        long hours = getHours(seconds);
        seconds -= hours*SEC_IN_HR;
        return seconds/SEC_IN_MIN;
    }

    private long getSeconds(long seconds) {
        long hours = getHours(seconds);
        long minutes = getMinutes(seconds);
        return seconds - hours*SEC_IN_HR - minutes*MIN_IN_HR;
    }

    private void buildFullTimeString() {
        m_FullTimeString = m_LocalDateTimeString + " (" + m_DiffString + ")";
    }
}
