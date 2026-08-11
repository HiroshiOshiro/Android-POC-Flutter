import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_confirm/main.dart';

void main() {
  testWidgets('Confirm1Screen shows a loading indicator until native text arrives', (
    WidgetTester tester,
  ) async {
    await tester.pumpWidget(const ConfirmApp());
    await tester.pump();

    expect(find.text('確認①'), findsOneWidget);
    expect(find.byType(CircularProgressIndicator), findsOneWidget);
  });
}
