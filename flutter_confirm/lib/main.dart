import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const _channel = MethodChannel('com.example.androidpoc/confirm');

const _teal = Color(0xFF0D7D87);
const _white = Colors.white;

final _navigatorKey = GlobalKey<NavigatorState>();

// エンジンは Application 起動時に事前ウォームアップされ、main() はその1回しか実行されない
// （FlutterFragment は毎回同じ実行中エンジン/Dart 状態にアタッチするだけ）。そのため確認①の
// 表示内容は Dart 側からの起動時プルではなく、ネイティブ側が確認画面表示のたびに
// setInitialText で能動的にプッシュする方式にしている。
final ValueNotifier<String> _initialText = ValueNotifier<String>('');

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  _channel.setMethodCallHandler(_handleNativeCall);
  runApp(const ConfirmApp());
}

Future<void> _handleNativeCall(MethodCall call) async {
  if (call.method == 'setInitialText') {
    _initialText.value = call.arguments as String? ?? '';
    _navigatorKey.currentState?.popUntil((route) => route.isFirst);
  }
}

class ConfirmApp extends StatelessWidget {
  const ConfirmApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: _navigatorKey,
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primaryColor: _teal,
        scaffoldBackgroundColor: _white,
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: _teal,
            foregroundColor: _white,
          ),
        ),
        progressIndicatorTheme: const ProgressIndicatorThemeData(color: _teal),
      ),
      home: const Confirm1Screen(),
    );
  }
}

class _ConfirmToolbar extends StatelessWidget {
  final String title;
  final VoidCallback? onBack;

  const _ConfirmToolbar({required this.title, this.onBack});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 56,
      color: _teal,
      child: Stack(
        alignment: Alignment.center,
        children: [
          Text(
            title,
            style: const TextStyle(
              color: _white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          if (onBack != null)
            Positioned(
              left: 4,
              child: IconButton(
                icon: const Icon(Icons.arrow_back, color: _white),
                onPressed: onBack,
              ),
            ),
        ],
      ),
    );
  }
}

class _ConfirmScaffold extends StatelessWidget {
  final String toolbarTitle;
  final VoidCallback? onBack;
  final String caption;
  final String text;
  final String actionLabel;
  final VoidCallback? onAction;
  final bool isSubmitting;

  const _ConfirmScaffold({
    required this.toolbarTitle,
    required this.onBack,
    required this.caption,
    required this.text,
    required this.actionLabel,
    required this.onAction,
    this.isSubmitting = false,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _ConfirmToolbar(title: toolbarTitle, onBack: onBack),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Text(
                      'こんにちは',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.grey),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'タスクを管理しましょう',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    const Spacer(),
                    Text(
                      caption,
                      textAlign: TextAlign.center,
                      style: const TextStyle(color: Colors.grey),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      text,
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 24),
                    Center(
                      child: ElevatedButton(
                        onPressed: isSubmitting ? null : onAction,
                        child: Text(actionLabel),
                      ),
                    ),
                    if (isSubmitting)
                      const Padding(
                        padding: EdgeInsets.only(top: 8),
                        child: Center(
                          child: SizedBox(
                            width: 24,
                            height: 24,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          ),
                        ),
                      ),
                    const Spacer(),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class Confirm1Screen extends StatelessWidget {
  const Confirm1Screen({super.key});

  void _exitToList() {
    _channel.invokeMethod('exitToList');
  }

  void _goToConfirm2(BuildContext context, String text) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => Confirm2Screen(text: text)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<String>(
      valueListenable: _initialText,
      builder: (context, text, _) => _ConfirmScaffold(
        toolbarTitle: '確認①',
        onBack: _exitToList,
        caption: '入力内容を確認してください',
        text: text,
        actionLabel: '次へ',
        onAction: () => _goToConfirm2(context, text),
      ),
    );
  }
}

class Confirm2Screen extends StatefulWidget {
  final String text;

  const Confirm2Screen({super.key, required this.text});

  @override
  State<Confirm2Screen> createState() => _Confirm2ScreenState();
}

class _Confirm2ScreenState extends State<Confirm2Screen> {
  bool _isSubmitting = false;

  Future<void> _onSaveTapped() async {
    if (_isSubmitting) return;
    setState(() => _isSubmitting = true);
    bool success;
    try {
      success = await _channel.invokeMethod<bool>('submitTodo', widget.text) ?? false;
    } on PlatformException {
      success = false;
    } on MissingPluginException {
      success = false;
    }
    if (!mounted) return;
    setState(() => _isSubmitting = false);
    if (success) {
      _channel.invokeMethod('onSavedSuccessfully');
    } else {
      _showError();
    }
  }

  void _showError() {
    showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('エラー'),
        content: const Text('保存に失敗しました'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return _ConfirmScaffold(
      toolbarTitle: '確認②',
      onBack: () => Navigator.of(context).pop(),
      caption: 'この内容で保存します',
      text: widget.text,
      actionLabel: '保存',
      onAction: _onSaveTapped,
      isSubmitting: _isSubmitting,
    );
  }
}
