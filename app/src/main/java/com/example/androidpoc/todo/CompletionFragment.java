package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.androidpoc.R;

/** 完了画面。固定メッセージ＋「入力に戻る」で confirm1/confirm2 をまとめて戻し一覧へ。 */
public class CompletionFragment extends Fragment {

    public CompletionFragment() {
        super(R.layout.fragment_completion);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.completion_title);
        view.findViewById(R.id.toolbar_back).setVisibility(View.GONE);

        Button backButton = view.findViewById(R.id.completion_back_button);
        backButton.setOnClickListener(v -> getParentFragmentManager()
                .popBackStack("confirm1", FragmentManager.POP_BACK_STACK_INCLUSIVE));
    }
}
