package com.example.androidpoc.music;

import com.example.androidpoc.R;

/** どの通信段で失敗したかを表すカテゴリ（iOS 版の TransportFailure に相当）。 */
public enum MusicError {
    OFFLINE(R.string.music_error_offline),
    NETWORK(R.string.music_error_network),
    SERVER(R.string.music_error_server),
    DECODING(R.string.music_error_decoding),
    UNKNOWN(R.string.music_error_unknown);

    public final int messageResId;

    MusicError(int messageResId) {
        this.messageResId = messageResId;
    }

    public MusicException toException() {
        return new MusicException(this);
    }

    public static class MusicException extends Exception {
        public final MusicError error;

        MusicException(MusicError error) {
            super(error.name());
            this.error = error;
        }
    }
}
