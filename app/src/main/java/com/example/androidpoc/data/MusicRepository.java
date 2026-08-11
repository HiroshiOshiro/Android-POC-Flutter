package com.example.androidpoc.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.example.androidpoc.model.MusicTrack;
import com.example.androidpoc.music.MusicError;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 楽曲検索を担うリポジトリ。通信前に接続チェックを行い（オフラインを他の失敗より先に判定）、
 * 各段の失敗を {@link MusicError} に変換する。
 */
public class MusicRepository {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Context appContext;
    private final ItunesMusicRemote remote = new ItunesMusicRemote();

    public MusicRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void search(String term, Callback<List<MusicTrack>> callback) {
        EXECUTOR.execute(() -> {
            if (!isNetworkReachable()) {
                postError(callback, MusicError.OFFLINE);
                return;
            }
            try {
                List<MusicTrack> tracks = remote.search(term);
                mainHandler.post(() -> callback.onSuccess(tracks));
            } catch (ItunesMusicRemote.HttpStatusException e) {
                postError(callback, MusicError.SERVER);
            } catch (org.json.JSONException e) {
                postError(callback, MusicError.DECODING);
            } catch (IOException e) {
                postError(callback, MusicError.NETWORK);
            } catch (RuntimeException e) {
                postError(callback, MusicError.UNKNOWN);
            }
        });
    }

    private boolean isNetworkReachable() {
        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void postError(Callback<List<MusicTrack>> callback, MusicError error) {
        mainHandler.post(() -> callback.onError(error.toException()));
    }
}
