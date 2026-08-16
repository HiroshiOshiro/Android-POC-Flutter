import 'fake_todo_remote.dart';
import 'todo_item.dart';
import 'todo_store.dart';

/// Todo の一覧取得・削除（ローカルのみ）と、保存（疑似リモート成功後にローカル永続化）を担う。
class TodoRepository {
  final TodoStore _todoStore;
  final FakeTodoRemote _remote;

  TodoRepository({TodoStore? todoStore, FakeTodoRemote? remote})
    : _todoStore = todoStore ?? TodoStore(),
      _remote = remote ?? FakeTodoRemote();

  Future<List<TodoItem>> loadAll() => _todoStore.loadAll();

  /// 指定インデックスを削除する。ローカルのみで、通信は行わない（iOS/ネイティブ版と同じ）。
  Future<void> delete(int index) => _todoStore.removeAt(index);

  /// 疑似リモートへ送信し、成功した場合だけローカルへ保存する（失敗時は保存しない）。
  Future<void> submit(String text) async {
    await _remote.submit(text);
    await _todoStore.insertFirst(
      TodoItem(text: text, createdAtMillis: DateTime.now().millisecondsSinceEpoch),
    );
  }
}
