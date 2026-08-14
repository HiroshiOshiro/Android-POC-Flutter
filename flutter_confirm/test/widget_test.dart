import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_confirm/main.dart';

void main() {
  testWidgets('Confirm1Screen renders the toolbar, caption and next button', (
    WidgetTester tester,
  ) async {
    await tester.pumpWidget(const ConfirmApp());
    await tester.pump();

    expect(find.text('確認①'), findsOneWidget);
    expect(find.text('入力内容を確認してください'), findsOneWidget);
    expect(find.text('次へ'), findsOneWidget);
  });
}
