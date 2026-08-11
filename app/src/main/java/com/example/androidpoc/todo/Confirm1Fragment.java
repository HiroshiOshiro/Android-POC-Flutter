package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;

/** 確認①: 入力内容の読み取り専用確認。「次へ」で確認②へ、戻るボタンで一覧へ。 */
public class Confirm1Fragment extends Fragment {

    private static final String ARG_TEXT = "text";

    public Confirm1Fragment() {
        super(R.layout.fragment_confirm);
    }

    public static Confirm1Fragment newInstance(String text) {
        Confirm1Fragment fragment = new Confirm1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String text = requireArguments().getString(ARG_TEXT);

        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.confirm1_title);
        View back = view.findViewById(R.id.toolbar_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        ((TextView) view.findViewById(R.id.confirm_caption)).setText(R.string.confirm_caption1);
        ((TextView) view.findViewById(R.id.confirm_text)).setText(text);
        view.findViewById(R.id.confirm_progress).setVisibility(View.GONE);

        Button actionButton = view.findViewById(R.id.confirm_action_button);
        actionButton.setText(R.string.confirm1_next);
        actionButton.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tab_content, Confirm2Fragment.newInstance(text))
                        .addToBackStack("confirm2")
                        .commit());
    }
}
