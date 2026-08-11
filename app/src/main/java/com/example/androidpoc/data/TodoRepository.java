package com.example.androidpoc.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.androidpoc.model.TodoItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Todo の一覧取得・削除（ローカルのみ）と、保存（疑似リモート成功後にローカル永続化）を担う。
 */
public class TodoRepository {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final TodoStore todoStore;
    private final FakeTodoRemote remote = new FakeTodoRemote();

    public TodoRepository(Context context) {
        this.todoStore = new TodoStore(context);
    }

    public void loadAll(Callback<List<TodoItem>> callback) {
        EXECUTOR.execute(() -> {
            List<TodoItem> items = todoStore.loadAll();
            mainHandler.post(() -> callback.onSuccess(items));
        });
    }

    /** 指定インデックスを削除する。ローカルのみで、通信は行わない（iOS 版と同じ）。 */
    public void delete(int index, Callback<Void> callback) {
        EXECUTOR.execute(() -> {
            todoStore.removeAt(index);
            mainHandler.post(() -> callback.onSuccess(null));
        });
    }

    /** 疑似リモートへ送信し、成功した場合だけローカルへ保存する（失敗時は保存しない）。 */
    public void submit(String text, Callback<Void> callback) {
        EXECUTOR.execute(() -> {
            try {
                remote.submit(text);
            } catch (FakeTodoRemote.TodoSubmitException e) {
                mainHandler.post(() -> callback.onError(e));
                return;
            }
            todoStore.insertFirst(new TodoItem(text, System.currentTimeMillis()));
            mainHandler.post(() -> callback.onSuccess(null));
        });
    }
}
