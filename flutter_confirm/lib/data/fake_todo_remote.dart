/// 疑似リモート送信。実通信は行わず、~1秒後に成功/失敗を返す。
/// 入力が "fail" の場合は失敗させる（動作確認用の仕込み。iOS 版・ネイティブ版と同じ）。
class FakeTodoRemote {
  Future<void> submit(String text) async {
    await Future<void>.delayed(const Duration(seconds: 1));
    if (text.trim().toLowerCase() == 'fail') {
      throw const TodoSubmitException('submit_failed');
    }
  }
}

class TodoSubmitException implements Exception {
  final String message;

  const TodoSubmitException(this.message);

  @override
  String toString() => 'TodoSubmitException: $message';
}
