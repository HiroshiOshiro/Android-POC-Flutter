package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;

/** 完了画面。固定メッセージ＋「入力に戻る」で一覧へ戻る。 */
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
        // popBackStack は使わない。一覧表示から確認画面への遷移は addToBackStack していないため
        // （完了画面への非バックスタック replace と混在すると、一覧フラグメントと完了フラグメントが
        // 同じコンテナに二重に残る不具合になる）、常に新しい一覧フラグメントへ明示的に replace する。
        backButton.setOnClickListener(v -> getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.tab_content, new TodoListFragment())
                .commit());
    }
}
