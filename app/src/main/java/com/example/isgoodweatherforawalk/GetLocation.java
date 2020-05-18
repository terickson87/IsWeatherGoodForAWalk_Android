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

import java.util.List;
import java.util.Locale;

public class GetLocation implements LocationListener {
    public static final int mc_REQUEST_LOCATION_PERMISSION = 1;
    public static final int mc_MAX_LOCATION_RESULTS = 1;
    public static final String TAG = "GetLocation";

    private Activity mActivity;
    private Double mLatitude;
    private Double mLongitude;
    private String mCityName;
    private String mStateName;
    public LocationManager mLocationManager;

    // ***** Constructor *****
    public GetLocation(Activity activity) {
        mActivity = activity;
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    // ***** Main functionality *****
    public void getLocation() {
        getPermissions(true);
        setLocations();
    }

    private boolean isCoarseLocationPermission() {
        return ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getPermissions(boolean getLocation) {
        if (!isCoarseLocationPermission() || !isFineLocationPermission()) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, mc_REQUEST_LOCATION_PERMISSION);
        } else {
            if (getLocation) {
                getAndHandleGoodLocation();
            }
        }
    }

    private void getAndHandleGoodLocation() {
        // This check must be local to prevent lint errors
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissions(true);
            return;
        }

        Location location = mLocationManager.getLastKnownLocation(mLocationManager.GPS_PROVIDER);

        if (location != null) {
            System.out.println(TAG + "getPermissions - Non Null Location");
            handleGoodLocation(location);
        } else {
            System.out.println(TAG + "getPermissions - Null Location");
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(mLocationManager.getBestProvider(criteria, true)).toString();
            mLocationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    private void handleGoodLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        System.out.println(TAG + " handleGoodLocation() - " + "lat: " + mLatitude + ", long: " + mLongitude);
        mCityName = getCityName(mActivity, mLatitude, mLongitude);
        System.out.println("City Name: " + mCityName);
        mStateName = getStateName(mActivity, mLatitude, mLongitude);
        System.out.println("State Name: " + mStateName);
    }

    private void setLocations() {

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

    public static String getCityName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, mc_MAX_LOCATION_RESULTS);
            return addresses.get(0).getLocality();
        }
        catch(Exception e){
            System.out.println("Error getting cityname: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public static String getStateName(Context context, double latitude, double longitude){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, mc_MAX_LOCATION_RESULTS);
            return addresses.get(0).getAdminArea();
        }
        catch(Exception e){
            System.out.println("Error getting statename: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    // ***** Setters *****
    public void setLatitude(Double latitude) {
        this.mLatitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.mLongitude = longitude;
    }

    public void setCityName(String cityName) {
        this.mCityName = cityName;
    }

    public void setStateName(String stateName) {
        this.mStateName = stateName;
    }

    // ***** Getters *****
    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getStateName() {
        return mStateName;
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
        mLocationManager.removeUpdates(this);

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
