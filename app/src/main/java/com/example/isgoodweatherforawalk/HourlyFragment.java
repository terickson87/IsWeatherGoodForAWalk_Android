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
    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private ArrayList<HourlyWeatherData> m_HourlyWeatherDataList;

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
        m_Activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_hourly, container, false);

        // Set up the RecyclerView
        m_RecyclerView = fragmentView.findViewById(R.id.hourly_recyclerview);
        m_RecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        m_RecyclerView.setLayoutManager(layoutManager);

        // Get the data
        m_HourlyWeatherDataList = new ArrayList<HourlyWeatherData>(m_Activity.getHourlyWeatherData());

        // specify an adapter
        RecyclerView.Adapter adapter = new HourlyWeatherCardAdapter(m_Activity, m_HourlyWeatherDataList);
        m_RecyclerView.setAdapter(adapter);

        return  fragmentView;
    }
}
