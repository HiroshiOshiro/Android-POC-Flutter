package com.example.androidpoc.model;

import java.io.Serializable;
import java.util.Locale;

public class MusicTrack implements Serializable {
    public final int id;
    public final String trackName;
    public final String artistName;
    public final String collectionName;
    public final String primaryGenreName;
    public final String artworkUrl100;
    public final String previewUrl;
    public final String trackViewUrl;
    public final String releaseDate;
    public final int trackTimeMillis;

    public MusicTrack(int id, String trackName, String artistName, String collectionName,
                       String primaryGenreName, String artworkUrl100, String previewUrl,
                       String trackViewUrl, String releaseDate, int trackTimeMillis) {
        this.id = id;
        this.trackName = trackName;
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.primaryGenreName = primaryGenreName;
        this.artworkUrl100 = artworkUrl100;
        this.previewUrl = previewUrl;
        this.trackViewUrl = trackViewUrl;
        this.releaseDate = releaseDate;
        this.trackTimeMillis = trackTimeMillis;
    }

    /** 100x100 のサムネイル URL を 600x600 に置換する（無ければ null）。 */
    public String getArtworkUrlLarge() {
        if (artworkUrl100 == null) return null;
        return artworkUrl100.replace("100x100", "600x600");
    }

    /** ISO8601 の先頭10文字（yyyy-MM-dd）だけを使う。 */
    public String getReleaseDateText() {
        if (releaseDate == null || releaseDate.length() < 10) return null;
        return releaseDate.substring(0, 10);
    }

    /** m:ss 形式。長さ不明（0）なら null。 */
    public String getDurationText() {
        if (trackTimeMillis <= 0) return null;
        int totalSeconds = trackTimeMillis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
    }
}
