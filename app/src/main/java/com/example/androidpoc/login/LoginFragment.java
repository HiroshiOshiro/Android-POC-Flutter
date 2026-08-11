package com.example.androidpoc.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;

// TODO: Stage 2 でここにログインフォームを実装する（今は土台の確認用）。
public class LoginFragment extends Fragment {

    public LoginFragment() {
        super(R.layout.fragment_placeholder);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.placeholder_text)).setText(R.string.tab_login);
    }
}
