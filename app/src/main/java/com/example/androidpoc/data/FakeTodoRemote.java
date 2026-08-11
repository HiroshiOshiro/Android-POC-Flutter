package com.example.androidpoc.data;

import java.util.Locale;

/**
 * 疑似リモート送信。実通信は行わず、~1秒後に成功/失敗を返す。
 * 入力が "fail" の場合は失敗させる（動作確認用の仕込み。iOS 版と同じ）。
 */
public class FakeTodoRemote {

    public void submit(String text) throws TodoSubmitException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (text.trim().toLowerCase(Locale.ROOT).equals("fail")) {
            throw new TodoSubmitException("submit_failed");
        }
    }

    public static class TodoSubmitException extends Exception {
        public TodoSubmitException(String message) {
            super(message);
        }
    }
}
