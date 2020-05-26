package com.example.isgoodweatherforawalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocation implements LocationListener {
    public static final int mc_REQUEST_LOCATION_PERMISSION = 1;
    public static final int mc_MAX_LOCATION_RESULTS = 1;
    public static final String TAG = "GetLocation";

    private Activity m_Activity;
    private Double m_Latitude;
    private Double m_Longitude;
    private String m_CityName;
    private String m_StateName;
    private String m_CountryCode;
    public LocationManager m_LocationManager;

    // ***** Constructor *****
    public GetLocation(Activity activity) {
        m_Activity = activity;
        m_LocationManager = (LocationManager) m_Activity.getSystemService(Context.LOCATION_SERVICE);
    }

    // ***** Main functionality *****
    public void getLocation() {
        getPermissions(true);
    }

    private boolean isCoarseLocationPermission() {
        return ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isLocationPermissions() {
        return  isCoarseLocationPermission() && isFineLocationPermission();
    }

    public void getPermissions(boolean getLocation) {
        if (!isCoarseLocationPermission() || !isFineLocationPermission()) {
            System.out.println(TAG + " - GetPermissions: Location Permissions Not Yet Granted.");
            ActivityCompat.requestPermissions(m_Activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, mc_REQUEST_LOCATION_PERMISSION);
        } else {
            System.out.println(TAG + " - GetPermissions: Location Permissions Granted.");
            if (getLocation) {
                getAndHandleGoodLocation();
            }
        }
    }

    private void getAndHandleGoodLocation() {
        // This check must be local to prevent lint errors
        if (ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissions(true);
            return;
        }

        Location location = m_LocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            System.out.println(TAG + "getAndHandleGoodLocation - Non Null Location");
            handleGoodLocation(location);
        } else {
            System.out.println(TAG + "getAndHandleGoodLocation - Null Location");
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(m_LocationManager.getBestProvider(criteria, true)).toString();
            m_LocationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    private void handleGoodLocation(Location location) {
        m_Latitude = location.getLatitude();
        m_Longitude = location.getLongitude();
        System.out.println(TAG + " handleGoodLocation() - " + "lat: " + m_Latitude + ", long: " + m_Longitude);
        Address address = getAddress(m_Activity, m_Latitude, m_Longitude);
        m_CityName = getCityName(address);
        System.out.println("City Name: " + m_CityName);
        m_StateName = getStateName(address);
        System.out.println("State Name: " + m_StateName);
        m_CountryCode = getCountryCode(address);
        System.out.println("Country Code: " + m_CountryCode);
        SendLocationData sendLocationData = (SendLocationData) m_Activity;
        sendLocationData.sendLocation(this);
    }

    // ***** Utility Methods *****
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mc_REQUEST_LOCATION_PERMISSION) {
            if(grantResults.length==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("onRequestPermissionsResult: Permission granted");
                getAndHandleGoodLocation();
            }
        } else {
            System.out.println(TAG + "onRequestPermissionsResult(): requestCode (" + requestCode + ") not equal to " + mc_REQUEST_LOCATION_PERMISSION);
        }
    }

    public static Address getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, mc_MAX_LOCATION_RESULTS);
            return addresses.get(0);
        } catch (IOException e) {
            System.out.println("Error getting Address: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String getCityName(Address address) {
        return address.getLocality();
    }

    public static String getStateName(Address address){
        return address.getAdminArea();
    }

    public static String getCountryCode(Address address) {
        return address.getCountryCode();
    }

    // ***** Setters *****
    public void setLatitude(Double latitude) {
        this.m_Latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.m_Longitude = longitude;
    }

    public void setCityName(String cityName) {
        this.m_CityName = cityName;
    }

    public void setStateName(String stateName) {
        this.m_StateName = stateName;
    }

    // ***** Getters *****
    public Double getLatitude() {
        return m_Latitude;
    }

    public Double getLongitude() {
        return m_Longitude;
    }

    public String getCityName() {
        return m_CityName;
    }

    public String getStateName() {
        return m_StateName;
    }

    public String getCountryCode() {
        return m_CountryCode;
    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        // remove location callback:
        m_LocationManager.removeUpdates(this);

        // Handle good location
        System.out.println(TAG + "onLocationChange(): Location Received");
        handleGoodLocation(location);
    }

    /**
     * This callback will never be invoked and providers can be considers as always in the
     * @link LocationProvider#AVAILABLE state.
     *
     * @param provider
     * @param status
     * @param extras
     * @deprecated This callback will never be invoked.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }
}
