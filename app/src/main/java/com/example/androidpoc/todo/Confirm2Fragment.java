package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;
import com.example.androidpoc.common.AlertHelper;
import com.example.androidpoc.data.Callback;
import com.example.androidpoc.data.TodoRepository;

/**
 * 確認②: 同内容の確認＋保存。保存ボタンで疑似リモート送信（~1秒、入力が "fail" なら失敗）。
 * 成功で完了画面へ（バックスタックには積まない）、失敗はこの画面のまま共通アラートで表示。
 */
public class Confirm2Fragment extends Fragment {

    private static final String ARG_TEXT = "text";

    private TodoRepository todoRepository;
    private Button actionButton;
    private View progressView;
    private boolean isSubmitting = false;

    public Confirm2Fragment() {
        super(R.layout.fragment_confirm);
    }

    public static Confirm2Fragment newInstance(String text) {
        Confirm2Fragment fragment = new Confirm2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todoRepository = new TodoRepository(requireContext());
        String text = requireArguments().getString(ARG_TEXT);

        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.confirm2_title);
        View back = view.findViewById(R.id.toolbar_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        ((TextView) view.findViewById(R.id.confirm_caption)).setText(R.string.confirm_caption2);
        ((TextView) view.findViewById(R.id.confirm_text)).setText(text);

        progressView = view.findViewById(R.id.confirm_progress);
        actionButton = view.findViewById(R.id.confirm_action_button);
        actionButton.setText(R.string.confirm2_save);
        actionButton.setOnClickListener(v -> onSaveTapped(text));
    }

    private void onSaveTapped(String text) {
        if (isSubmitting) return;
        setSubmitting(true);
        todoRepository.submit(text, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                setSubmitting(false);
                // 完了画面はバックスタックに積まない（「戻る」は confirm1/confirm2 をまとめて
                // ポップして一覧へ戻る想定のため）。
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tab_content, new CompletionFragment())
                        .commit();
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                setSubmitting(false);
                AlertHelper.showError(requireContext(), R.string.confirm_error_submit_failed);
            }
        });
    }

    private void setSubmitting(boolean submitting) {
        isSubmitting = submitting;
        progressView.setVisibility(submitting ? View.VISIBLE : View.GONE);
        actionButton.setEnabled(!submitting);
    }
}
