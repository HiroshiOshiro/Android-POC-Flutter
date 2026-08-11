package com.example.androidpoc.todo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.androidpoc.R;
import com.example.androidpoc.common.AndroidPocApplication;
import com.example.androidpoc.data.Callback;
import com.example.androidpoc.data.TodoRepository;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * Todo の確認①②画面を Flutter（flutter_confirm/）でホストするラッパー。
 * ネイティブの Confirm1Fragment/Confirm2Fragment を1つに統合し、確認①⇄②の画面内遷移は
 * Dart 側の Navigator に任せる。データ層（TodoRepository）はここから既存実装をそのまま呼ぶ。
 */
public class ConfirmFlutterFragment extends FlutterFragment {

    private static final String CHANNEL = "com.example.androidpoc/confirm";
    private static final String ARG_TEXT = "text";

    private MethodChannel channel;

    public static ConfirmFlutterFragment newInstance(String text) {
        ConfirmFlutterFragment fragment = new FlutterFragment.CachedEngineFragmentBuilder(
                ConfirmFlutterFragment.class, AndroidPocApplication.CONFIRM_ENGINE_ID)
                .build();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        channel = new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL);
        channel.setMethodCallHandler(this::onMethodCall);
        // エンジンは Application 起動時に事前ウォームアップされ、Dart の main()/確認①の初期表示は
        // その時点で一度だけ走る（この確認画面がまだ存在しない時点）。そのため、確認対象のテキストは
        // Dart 側からの起動時プルではなく、アタッチのたびにこちらからプッシュする。
        channel.invokeMethod("setInitialText", requireArguments().getString(ARG_TEXT));
    }

    @Override
    public void onDetach() {
        // キャッシュ済みエンジンは次の確認画面表示でも使い回されるため、このフラグメントが
        // 破棄された後にコールバックが古い状態を参照して呼ばれないようにハンドラを外す。
        if (channel != null) {
            channel.setMethodCallHandler(null);
        }
        super.onDetach();
    }

    private void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "submitTodo": {
                String text = call.arguments();
                new TodoRepository(requireContext()).submit(text, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (!isAdded()) return;
                        result.success(true);
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!isAdded()) return;
                        result.success(false);
                    }
                });
                break;
            }

            case "onSavedSuccessfully":
                if (isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tab_content, new CompletionFragment())
                            .commit();
                }
                result.success(null);
                break;

            case "exitToList":
                if (isAdded()) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tab_content, new TodoListFragment())
                            .commit();
                }
                result.success(null);
                break;

            default:
                result.notImplemented();
        }
    }
}
