package com.example.mapped.ui.MyPlaces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapped.PlacesTapsAdapter;
import com.example.mapped.R;
import com.example.mapped.TabsAccessorAdapter;
import com.google.android.material.tabs.TabLayout;


public class PlacesFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private PlacesTapsAdapter placesTapsAdapter;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState) {

        mTabLayout = view.findViewById(R.id.placestabs);
        mViewPager = view.findViewById(R.id.placesviewpager);
        placesTapsAdapter = new PlacesTapsAdapter(getFragmentManager());

        mViewPager.setAdapter(placesTapsAdapter);
        // mViewPager.setAdapter(createMessageAdapter());

        mTabLayout.setupWithViewPager(mViewPager);
    }

}