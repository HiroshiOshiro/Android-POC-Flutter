package com.example.androidpoc.data;

import com.example.androidpoc.model.MusicTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * iTunes Search API のリモートデータソース。サードパーティ通信ライブラリは使わず
 * {@link HttpURLConnection} を直接使う（iOS 版が URLSession を直接使うのに合わせる）。
 * 同期・ブロッキング呼び出しで、バックグラウンドスレッドからの利用を前提とする。
 */
public class ItunesMusicRemote {

    public List<MusicTrack> search(String term) throws IOException, HttpStatusException, JSONException {
        String encodedTerm = URLEncoder.encode(term, StandardCharsets.UTF_8.name());
        String urlString = "https://itunes.apple.com/search?term=" + encodedTerm
                + "&country=JP&lang=ja_jp&media=music&entity=song&limit=50";
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new HttpStatusException(status);
            }

            String body = readStream(connection.getInputStream());
            return parseTracks(body);
        } finally {
            connection.disconnect();
        }
    }

    private static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static List<MusicTrack> parseTracks(String body) throws JSONException {
        JSONObject root = new JSONObject(body);
        JSONArray results = root.getJSONArray("results");
        List<MusicTrack> tracks = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            if (!item.has("trackId")) {
                continue; // 曲以外（アルバム等）は除外
            }
            tracks.add(new MusicTrack(
                    item.optInt("trackId"),
                    item.optString("trackName", null),
                    item.optString("artistName", null),
                    item.optString("collectionName", null),
                    item.optString("primaryGenreName", null),
                    item.optString("artworkUrl100", null),
                    item.optString("previewUrl", null),
                    item.optString("trackViewUrl", null),
                    item.optString("releaseDate", null),
                    item.optInt("trackTimeMillis", 0)
            ));
        }
        return tracks;
    }

    public static class HttpStatusException extends Exception {
        public final int statusCode;

        public HttpStatusException(int statusCode) {
            super("HTTP " + statusCode);
            this.statusCode = statusCode;
        }
    }
}
