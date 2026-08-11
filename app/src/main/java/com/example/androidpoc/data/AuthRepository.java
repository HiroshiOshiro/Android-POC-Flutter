package com.example.androidpoc.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.androidpoc.login.LoginError;
import com.example.androidpoc.model.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ログインの一連の流れ（ハッシュ化 → 疑似リモート → 永続化）を束ね、
 * 各段の失敗を {@link LoginError}（どこで失敗したか）に変換する。
 * 呼び出しはバックグラウンドで実行し、結果は必ずメインスレッドへ post する。
 */
public class AuthRepository {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AuthStore authStore;
    private final FakeAuthRemote remote = new FakeAuthRemote();

    public AuthRepository(Context context) {
        this.authStore = new AuthStore(context);
    }

    public void login(String email, String password, Callback<Session> callback) {
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            callback.onError(LoginError.VALIDATION.toException());
            return;
        }

        EXECUTOR.execute(() -> {
            String hashed;
            try {
                hashed = PasswordHasher.sha256Hex(password);
            } catch (RuntimeException e) {
                postError(callback, LoginError.ENCRYPTION);
                return;
            }

            String userId;
            try {
                userId = remote.login(trimmedEmail, hashed);
            } catch (FakeAuthRemote.AuthException e) {
                postError(callback, LoginError.NETWORK);
                return;
            }

            try {
                authStore.saveEmail(trimmedEmail);
                authStore.saveUserId(userId);
            } catch (RuntimeException e) {
                postError(callback, LoginError.PERSISTENCE);
                return;
            }

            Session session = new Session(trimmedEmail, userId);
            mainHandler.post(() -> callback.onSuccess(session));
        });
    }

    /** 保存済みメール＋userID が両方あればセッションを復元する（無ければ null＝未ログイン扱い）。 */
    public void loadSession(Callback<Session> callback) {
        EXECUTOR.execute(() -> {
            String email = authStore.loadEmail();
            String userId = authStore.loadUserId();
            Session session = (email != null && !email.isEmpty() && userId != null)
                    ? new Session(email, userId)
                    : null;
            mainHandler.post(() -> callback.onSuccess(session));
        });
    }

    private void postError(Callback<Session> callback, LoginError error) {
        mainHandler.post(() -> callback.onError(error.toException()));
    }
}
