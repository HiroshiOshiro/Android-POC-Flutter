package com.example.androidpoc.data;

/** 非同期処理の結果を受け取る共通インターフェース。呼び出し元（UIスレッド）で受け取る。 */
public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception error);
}
