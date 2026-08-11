package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;

// TODO: Stage 4 でここに Todo 一覧 + 入力 + 確認①②/完了フローの起点を実装する（今は土台の確認用）。
public class TodoListFragment extends Fragment {

    public TodoListFragment() {
        super(R.layout.fragment_placeholder);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.placeholder_text)).setText(R.string.tab_todo);
    }
}
