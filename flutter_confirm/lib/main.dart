import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const _channel = MethodChannel('com.example.androidpoc/confirm');

const _teal = Color(0xFF0D7D87);
const _white = Colors.white;

void main() {
  runApp(const ConfirmApp());
}

class ConfirmApp extends StatelessWidget {
  const ConfirmApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(primaryColor: _teal),
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

class Confirm1Screen extends StatefulWidget {
  const Confirm1Screen({super.key});

  @override
  State<Confirm1Screen> createState() => _Confirm1ScreenState();
}

class _Confirm1ScreenState extends State<Confirm1Screen> {
  String? _text;

  @override
  void initState() {
    super.initState();
    _loadInitialText();
  }

  Future<void> _loadInitialText() async {
    String text;
    try {
      text = await _channel.invokeMethod<String>('getInitialText') ?? '';
    } on PlatformException {
      text = '';
    } on MissingPluginException {
      text = '';
    }
    if (!mounted) return;
    setState(() => _text = text);
  }

  void _exitToList() {
    _channel.invokeMethod('exitToList');
  }

  void _goToConfirm2() {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => Confirm2Screen(text: _text!)),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_text == null) {
      return const Scaffold(
        body: SafeArea(
          child: Column(
            children: [
              _ConfirmToolbar(title: '確認①'),
              Expanded(child: Center(child: CircularProgressIndicator())),
            ],
          ),
        ),
      );
    }
    return _ConfirmScaffold(
      toolbarTitle: '確認①',
      onBack: _exitToList,
      caption: '入力内容を確認してください',
      text: _text!,
      actionLabel: '次へ',
      onAction: _goToConfirm2,
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
