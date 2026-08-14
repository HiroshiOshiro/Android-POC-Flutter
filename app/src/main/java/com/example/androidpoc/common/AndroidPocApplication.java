package com.example.androidpoc.common;

import android.app.Application;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

/** Todo 確認①②画面（flutter_confirm/）用の FlutterEngine を起動時に事前ウォームアップしてキャッシュする。 */
public class AndroidPocApplication extends Application {

    public static final String CONFIRM_ENGINE_ID = "confirm_engine";

    @Override
    public void onCreate() {
        super.onCreate();
        FlutterEngine flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        FlutterEngineCache.getInstance().put(CONFIRM_ENGINE_ID, flutterEngine);
    }
}
