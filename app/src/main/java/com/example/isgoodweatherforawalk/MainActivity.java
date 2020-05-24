package com.example.isgoodweatherforawalk;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SendWeatherData, SendLocationData {
    private GetLocation m_GetLocation;
    private CurrentWeatherData m_CurrentWeatherData;
    private List<MinutelyWeatherData> m_MinutelyWeatherData;
    private List<HourlyWeatherData> m_HourlyWeatherData;
    private List<DailyWeatherData> m_DailyWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        m_GetLocation = new GetLocation(this);
        m_GetLocation.getPermissions(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        m_GetLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO add other fragments and add bundles to pass the weather and location data between fragments.
        if (id == R.id.action_location) {
            // Get the permissions if requested
            m_GetLocation.getPermissions(false);
            Snackbar.make(findViewById(R.id.main_activity_coordinator_layout), R.string.location_permissions_snackbar, Snackbar.LENGTH_SHORT);
            return true;

        } else if (id == R.id.menu_refresh) {
            // Refresh the current fragment (Does Not work)
            // TODO add swipe to refresh

        } else if (id == R.id.menu_units) {
            // Pop up unit selection dialog
            // Set toast for units set
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendCurrentWeatherData(CurrentWeatherData currentWeatherData) {
        m_CurrentWeatherData = currentWeatherData;
    }

    @Override
    public void sendMinutelyWeatherData(List<MinutelyWeatherData> minutelyWeatherData) {
        m_MinutelyWeatherData = minutelyWeatherData;
    }

    @Override
    public void sendHourlyWeatherData(List<HourlyWeatherData> hourlyWeatherData) {
        m_HourlyWeatherData = hourlyWeatherData;
    }

    @Override
    public void sendDailyWeatherData(List<DailyWeatherData> dailyWeatherData) {
        m_DailyWeatherData = dailyWeatherData;
    }

    @Override
    public void sendLocation(GetLocation getLocation) {
        m_GetLocation = getLocation;
    }

    public GetLocation getGetLocation() {
        return m_GetLocation;
    }

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
}
