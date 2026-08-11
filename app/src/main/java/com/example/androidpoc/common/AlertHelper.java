package com.example.androidpoc.common;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.example.androidpoc.R;

/**
 * 「エラー」タイトル＋メッセージ＋OKのみ、という共通のエラー表示
 * （iOS 版で全画面が使う共通アラートパターンに相当）。
 */
public final class AlertHelper {
    private AlertHelper() {}

    public static void showError(Context context, @StringRes int messageResId) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(messageResId)
                .setPositiveButton(R.string.common_ok, null)
                .show();
    }
}
