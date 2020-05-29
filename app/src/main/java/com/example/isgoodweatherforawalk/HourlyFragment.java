package com.example.isgoodweatherforawalk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HourlyFragment extends Fragment {
    private MainActivity m_Activity;

    public HourlyFragment() {
        // Required empty public constructor
    }


    public static HourlyFragment newInstance() {
        HourlyFragment fragment = new HourlyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        m_Activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_hourly, container, false);

        // Set up the RecyclerView
        RecyclerView recyclerView = fragmentView.findViewById(R.id.hourly_recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Get the data
        List<HourlyWeatherData> hourlyWeatherDataList = m_Activity.getHourlyWeatherData();

        // specify an adapter
        RecyclerView.Adapter adapter = new HourlyWeatherCardAdapter(m_Activity, hourlyWeatherDataList);
        recyclerView.setAdapter(adapter);

        return  fragmentView;
    }
}
