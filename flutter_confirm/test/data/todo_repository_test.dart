import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:flutter_confirm/data/fake_todo_remote.dart';
import 'package:flutter_confirm/data/todo_repository.dart';

void main() {
  setUp(() {
    SharedPreferences.setMockInitialValues({});
  });

  group('TodoRepository', () {
    test('loadAll returns an empty list initially', () async {
      final repo = TodoRepository();
      expect(await repo.loadAll(), isEmpty);
    });

    test('submit persists successful items with newest first', () async {
      final repo = TodoRepository();
      await repo.submit('first');
      await repo.submit('second');

      final items = await repo.loadAll();
      expect(items.map((e) => e.text).toList(), ['second', 'first']);
    });

    test('submit with "fail" throws and does not persist', () async {
      final repo = TodoRepository();
      await expectLater(
        () => repo.submit('fail'),
        throwsA(isA<TodoSubmitException>()),
      );
      expect(await repo.loadAll(), isEmpty);
    });

    test('the fail trigger is case/whitespace-insensitive', () async {
      final repo = TodoRepository();
      await expectLater(
        () => repo.submit('  FAIL  '),
        throwsA(isA<TodoSubmitException>()),
      );
      expect(await repo.loadAll(), isEmpty);
    });

    test('delete removes the item at the given index', () async {
      final repo = TodoRepository();
      await repo.submit('first');
      await repo.submit('second');
      // Stored newest-first: [second, first].
      await repo.delete(1);

      final items = await repo.loadAll();
      expect(items.map((e) => e.text).toList(), ['second']);
    });

    test('delete with an out-of-range index is a no-op', () async {
      final repo = TodoRepository();
      await repo.submit('only');
      await repo.delete(5);
      await repo.delete(-1);

      final items = await repo.loadAll();
      expect(items.map((e) => e.text).toList(), ['only']);
    });
  });

  group('FakeTodoRemote', () {
    test('waits roughly 1 second before resolving for normal text', () async {
      final remote = FakeTodoRemote();
      final stopwatch = Stopwatch()..start();
      await remote.submit('todo');
      stopwatch.stop();
      expect(stopwatch.elapsedMilliseconds, greaterThanOrEqualTo(950));
    });

    test('throws TodoSubmitException for "fail" (case/whitespace-insensitive)', () async {
      final remote = FakeTodoRemote();
      await expectLater(
        () => remote.submit('  Fail '),
        throwsA(isA<TodoSubmitException>()),
      );
    });
  });
}
