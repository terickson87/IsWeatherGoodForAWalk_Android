package com.example.isgoodweatherforawalk;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

// TODO move the location and weather fetches to the MainActivity
// TODO add swipe to refresh
public class MainActivity extends AppCompatActivity {
    private GetLocation m_GetLocation;
    private OpenWeatherApi m_OpenWeatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getAndUseLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        m_GetLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
        createAndCallWeatherApi();
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

    private void showProgressBar() {
        findViewById(R.id.content_main).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_main_linear_layout).setVisibility(View.VISIBLE);
    }

    private void showContent() {
        findViewById(R.id.content_main).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_bar_main_linear_layout).setVisibility(View.GONE);
    }

    private void getAndUseLocation() {
        m_GetLocation = new GetLocation(this);
        if (m_GetLocation.isLocationPermissions()) {
            m_GetLocation.getLocation();
            createAndCallWeatherApi();
        } else {
            m_GetLocation.getPermissions(true);
            // see onRequestPermissionsResult()
        }
    }

    private void createAndCallWeatherApi() {
        m_OpenWeatherApi = new OpenWeatherApi(getApplicationContext(), m_GetLocation.getLatitude(), m_GetLocation.getLongitude());
        ApiCallback apiCallback = new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                m_OpenWeatherApi.parseResponse(response);
                showContent();
            }
        };
        m_OpenWeatherApi.callApi(apiCallback);
    }

    // Getters
    public GetLocation getGetLocation() {
        return m_GetLocation;
    }

    public CurrentWeatherData getCurrentWeatherData() {
        return m_OpenWeatherApi.getCurrentWeatherData();
    }

    public Instant getCurrentNow() {
        return m_OpenWeatherApi.getCurrentDateTime();
    }

    public Integer getTimezoneOffset_s() {
        return  m_OpenWeatherApi.getTimezoneOffset_s();
    }

    public List<MinutelyWeatherData> getMinutelyWeatherData() {
        return m_OpenWeatherApi.getMinutelyWeatherData();
    }

    public List<HourlyWeatherData> getHourlyWeatherData() {
        return m_OpenWeatherApi.getHourlyWeatherData();
    }

    public List<DailyWeatherData> getDailyWeatherData() {
        return m_OpenWeatherApi.getDailyWeatherData();
    }

    public Instant getNextRainTime() {
        return m_OpenWeatherApi.getNextRainTime();
    }
}
