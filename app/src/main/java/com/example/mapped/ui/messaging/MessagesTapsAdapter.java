package com.example.mapped.ui.messaging;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mapped.ui.messaging.MessagesTapFragment;

public class MessagesTapsAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 3;
    public MessagesTapsAdapter (@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull @Override public Fragment createFragment(int position) {
        return MessagesTapFragment.newInstance(position);
    }
    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
