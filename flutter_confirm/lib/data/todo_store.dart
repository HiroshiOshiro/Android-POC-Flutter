import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

import 'todo_item.dart';

/// 保存済み Todo の永続化。SharedPreferences に JSON 配列として保存する（新しい順）。
///
/// キー名（"todo_items"）はネイティブ側の TodoStore.java と揃えてあるが、Android では
/// shared_preferences プラグインが独自の prefs ファイルを使うため、今の時点ではネイティブ側の
/// 保存データとは別物（接続は次のステップで行う）。
class TodoStore {
  static const _itemsKey = 'todo_items';

  Future<List<TodoItem>> loadAll() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_itemsKey) ?? '[]';
    try {
      final array = jsonDecode(raw) as List<dynamic>;
      return array
          .map(
            (e) => TodoItem(
              text: e['text'] as String,
              createdAtMillis: e['createdAt'] as int,
            ),
          )
          .toList();
    } on FormatException {
      return [];
    } on TypeError {
      return [];
    }
  }

  /// 新しい項目を先頭に追加して保存する。
  Future<void> insertFirst(TodoItem item) async {
    final items = await loadAll();
    items.insert(0, item);
    await _saveAll(items);
  }

  /// 指定インデックスの項目を削除して保存する。
  Future<void> removeAt(int index) async {
    final items = await loadAll();
    if (index < 0 || index >= items.length) return;
    items.removeAt(index);
    await _saveAll(items);
  }

  Future<void> _saveAll(List<TodoItem> items) async {
    final prefs = await SharedPreferences.getInstance();
    final array = items
        .map((item) => {'text': item.text, 'createdAt': item.createdAtMillis})
        .toList();
    await prefs.setString(_itemsKey, jsonEncode(array));
  }
}
