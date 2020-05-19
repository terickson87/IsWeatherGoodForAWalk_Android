package com.example.isgoodweatherforawalk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment {
    private GetLocation mGetLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_first, container, false);

        getAndUseLocation(fragmentView);

        return  fragmentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

    }

    private void getAndUseLocation(View fragmentView) {
        mGetLocation = new GetLocation(getActivity());
        mGetLocation.getLocation();
        if (mGetLocation.isLocationPermissions()) {
            Double latitude = mGetLocation.getLatitude();
            Double longitude = mGetLocation.getLongitude();
            String cityName = mGetLocation.getCityName();
            String stateName = mGetLocation.getStateName();
            String fullLocationString = "Latitude: " + latitude.toString()
                    + "\nLongitude: " + longitude.toString()
                    + "\nCity: " + cityName
                    + "\nState: " + stateName;
            TextView textView = fragmentView.findViewById(R.id.textview_first);
            textView.setText(fullLocationString);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mGetLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
