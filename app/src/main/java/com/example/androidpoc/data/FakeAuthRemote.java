package com.example.androidpoc.data;

import java.util.Locale;
import java.util.UUID;

/**
 * 疑似リモート API。実通信は行わず、~1秒後に成功/失敗を返す
 * （iOS 版の FakeAuthRemoteDataSource と同じ挙動）。同期・ブロッキング呼び出しで、
 * バックグラウンドスレッドからの利用を前提とする。
 */
public class FakeAuthRemote {

    /** メールアドレスに "fail" を含む場合は失敗させる（動作確認用の仕込み。iOS 版と同じ）。 */
    public String login(String email, String hashedPassword) throws AuthException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (email.toLowerCase(Locale.ROOT).contains("fail")) {
            throw new AuthException("invalid_credentials");
        }
        return "user-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
