package com.example.androidpoc.login;

import com.example.androidpoc.R;

/**
 * ログイン処理のどの段階で失敗したかを表すカテゴリ（iOS 版の AuthError に相当）。
 * 表示文言は共通の OK のみアラートで出す。
 */
public enum LoginError {
    VALIDATION(R.string.login_error_validation),
    ENCRYPTION(R.string.login_error_encryption),
    NETWORK(R.string.login_error_network),
    PERSISTENCE(R.string.login_error_persistence),
    UNKNOWN(R.string.login_error_unknown);

    public final int messageResId;

    LoginError(int messageResId) {
        this.messageResId = messageResId;
    }

    public LoginException toException() {
        return new LoginException(this);
    }

    /** {@link com.example.androidpoc.data.Callback#onError} で運ぶための薄いラッパー。 */
    public static class LoginException extends Exception {
        public final LoginError error;

        LoginException(LoginError error) {
            super(error.name());
            this.error = error;
        }
    }
}
