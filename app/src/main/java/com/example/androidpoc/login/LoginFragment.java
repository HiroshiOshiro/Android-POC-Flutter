package com.example.androidpoc.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;
import com.example.androidpoc.common.AlertHelper;
import com.example.androidpoc.data.AuthRepository;
import com.example.androidpoc.data.Callback;
import com.example.androidpoc.model.Session;

/**
 * ログイン画面（Controller）。iOS 版の LoginView/LoginViewModel に相当する一連の挙動を持つ:
 * 入力検証→送信→エラー表示、画面表示時のセッション復元、利用規約の折りたたみ。
 */
public class LoginFragment extends Fragment {

    private AuthRepository authRepository;

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private ProgressBar progressBar;
    private LinearLayout savedInfoSection;
    private TextView savedEmailText;
    private TextView savedUserIdText;

    private boolean isLoading = false;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());

        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.login_title);
        view.findViewById(R.id.toolbar_back).setVisibility(View.GONE);

        emailField = view.findViewById(R.id.login_email);
        passwordField = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        progressBar = view.findViewById(R.id.login_progress);
        savedInfoSection = view.findViewById(R.id.login_saved_info);
        savedEmailText = view.findViewById(R.id.login_saved_email);
        savedUserIdText = view.findViewById(R.id.login_saved_user_id);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSubmitEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);
        updateSubmitEnabled();

        loginButton.setOnClickListener(v -> onLoginTapped());

        View termsHeader = view.findViewById(R.id.login_terms_header);
        TextView termsBody = view.findViewById(R.id.login_terms_body);
        ImageView termsChevron = view.findViewById(R.id.login_terms_chevron);
        termsHeader.setOnClickListener(v -> {
            boolean expanded = termsBody.getVisibility() == View.VISIBLE;
            termsBody.setVisibility(expanded ? View.GONE : View.VISIBLE);
            termsChevron.setRotation(expanded ? 0f : 180f);
        });

        restoreSession();
    }

    private void updateSubmitEnabled() {
        boolean canSubmit = !isLoading
                && emailField.getText().length() > 0
                && passwordField.getText().length() > 0;
        loginButton.setEnabled(canSubmit);
        loginButton.setAlpha(canSubmit ? 1f : 0.5f);
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        updateSubmitEnabled();
    }

    private void onLoginTapped() {
        setLoading(true);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        authRepository.login(email, password, new Callback<Session>() {
            @Override
            public void onSuccess(Session session) {
                if (!isAdded()) return;
                setLoading(false);
                // パスワードは保持しない（iOS 版と同じ）。
                passwordField.setText("");
                showSession(session);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                setLoading(false);
                LoginError error = (e instanceof LoginError.LoginException)
                        ? ((LoginError.LoginException) e).error
                        : LoginError.UNKNOWN;
                AlertHelper.showError(requireContext(), error.messageResId);
            }
        });
    }

    private void restoreSession() {
        authRepository.loadSession(new Callback<Session>() {
            @Override
            public void onSuccess(Session session) {
                if (!isAdded() || session == null) return;
                showSession(session);
            }

            @Override
            public void onError(Exception e) {
                // 復元失敗（Keystore読み取り不可等）は「未ログイン」として扱う（iOS 版と同じ）。
            }
        });
    }

    private void showSession(Session session) {
        savedInfoSection.setVisibility(View.VISIBLE);
        savedEmailText.setText(getString(R.string.login_saved_email, session.email));
        savedUserIdText.setText(getString(R.string.login_saved_user_id, session.userId));
    }
}
