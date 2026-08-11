package com.example.androidpoc.common;

import androidx.fragment.app.Fragment;

import com.example.androidpoc.music.MusicListFragment;

public class MusicTabFragment extends TabHostFragment {
    @Override
    protected Fragment createRootFragment() {
        return new MusicListFragment();
    }
}
