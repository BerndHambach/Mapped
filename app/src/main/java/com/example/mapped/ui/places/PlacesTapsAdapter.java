package com.example.mapped.ui.places;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mapped.ui.places.favorites.FavoritesFragment;
import com.example.mapped.ui.places.myplaces.MyPlacesFragment;

public class PlacesTapsAdapter extends FragmentPagerAdapter {


    public PlacesTapsAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                MyPlacesFragment myplacesFragment = new MyPlacesFragment();
                return myplacesFragment;
            case 1:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                return favoritesFragment;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Places";
            case 1:
                return "Favorites";

            default:
                return null;
        }
    }
}

