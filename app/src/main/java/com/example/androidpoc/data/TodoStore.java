package com.example.androidpoc.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidpoc.model.TodoItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** 保存済み Todo の永続化。SharedPreferences に JSON 配列として保存する（新しい順）。 */
public class TodoStore {
    private static final String PREFS = "todo_prefs";
    private static final String ITEMS_KEY = "todo_items";

    private final SharedPreferences prefs;

    public TodoStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<TodoItem> loadAll() {
        List<TodoItem> items = new ArrayList<>();
        String raw = prefs.getString(ITEMS_KEY, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                items.add(new TodoItem(obj.getString("text"), obj.getLong("createdAt")));
            }
        } catch (JSONException e) {
            return new ArrayList<>();
        }
        return items;
    }

    /** 新しい項目を先頭に追加して保存する。 */
    public void insertFirst(TodoItem item) {
        List<TodoItem> items = loadAll();
        items.add(0, item);
        saveAll(items);
    }

    /** 指定インデックスの項目を削除して保存する。 */
    public void removeAt(int index) {
        List<TodoItem> items = loadAll();
        if (index < 0 || index >= items.size()) return;
        items.remove(index);
        saveAll(items);
    }

    private void saveAll(List<TodoItem> items) {
        JSONArray array = new JSONArray();
        for (TodoItem item : items) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("text", item.text);
                obj.put("createdAt", item.createdAtMillis);
            } catch (JSONException ignored) {
                // text/createdAt は常に有効な値なので実質発生しない。
            }
            array.put(obj);
        }
        prefs.edit().putString(ITEMS_KEY, array.toString()).apply();
    }
}
