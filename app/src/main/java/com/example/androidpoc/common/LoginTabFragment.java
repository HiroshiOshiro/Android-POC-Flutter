package com.example.androidpoc.common;

import androidx.fragment.app.Fragment;

import com.example.androidpoc.login.LoginFragment;

public class LoginTabFragment extends TabHostFragment {
    @Override
    protected Fragment createRootFragment() {
        return new LoginFragment();
    }
}
