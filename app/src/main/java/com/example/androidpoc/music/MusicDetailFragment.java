package com.example.androidpoc.music;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.androidpoc.R;
import com.example.androidpoc.model.MusicTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Music 詳細画面。600x600 アートワーク、曲名/アーティスト、メタデータ1行、
 * 30秒プレビュー再生/停止（画面を離れたら自動停止）、外部ブラウザで開くボタン。
 */
public class MusicDetailFragment extends Fragment {

    private static final String ARG_TRACK = "track";

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Button previewButton;

    public MusicDetailFragment() {
        super(R.layout.fragment_music_detail);
    }

    public static MusicDetailFragment newInstance(MusicTrack track) {
        MusicDetailFragment fragment = new MusicDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRACK, track);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MusicTrack track = (MusicTrack) requireArguments().getSerializable(ARG_TRACK);

        Toolbar toolbar = view.findViewById(R.id.music_detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        ImageView artwork = view.findViewById(R.id.detail_artwork);
        Glide.with(artwork).load(track.getArtworkUrlLarge()).into(artwork);

        TextView nameView = view.findViewById(R.id.detail_track_name);
        nameView.setText(track.trackName != null ? track.trackName : getString(R.string.music_title_unknown));

        TextView artistView = view.findViewById(R.id.detail_artist_name);
        artistView.setText(track.artistName);

        TextView metadataView = view.findViewById(R.id.detail_metadata);
        metadataView.setText(buildMetadataLine(track));

        previewButton = view.findViewById(R.id.detail_preview_button);
        if (track.previewUrl != null && !track.previewUrl.isEmpty()) {
            previewButton.setVisibility(View.VISIBLE);
            updatePreviewButtonLabel();
            previewButton.setOnClickListener(v -> togglePreview(track.previewUrl));
        }

        Button openButton = view.findViewById(R.id.detail_open_button);
        if (track.trackViewUrl != null && !track.trackViewUrl.isEmpty()) {
            openButton.setVisibility(View.VISIBLE);
            openButton.setOnClickListener(v ->
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(track.trackViewUrl))));
        }
    }

    private String buildMetadataLine(MusicTrack track) {
        List<String> parts = new ArrayList<>();
        if (track.collectionName != null && !track.collectionName.isEmpty()) parts.add(track.collectionName);
        if (track.primaryGenreName != null && !track.primaryGenreName.isEmpty()) parts.add(track.primaryGenreName);
        String date = track.getReleaseDateText();
        if (date != null) parts.add(date);
        String duration = track.getDurationText();
        if (duration != null) parts.add(duration);
        return String.join(" ・ ", parts);
    }

    private void togglePreview(String previewUrl) {
        if (isPlaying) {
            stopPreview();
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(previewUrl);
            mediaPlayer.setOnCompletionListener(mp -> stopPreview());
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
            isPlaying = true;
            updatePreviewButtonLabel();
        } catch (IOException e) {
            stopPreview();
        }
    }

    private void stopPreview() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        if (previewButton != null) {
            updatePreviewButtonLabel();
        }
    }

    private void updatePreviewButtonLabel() {
        previewButton.setText(isPlaying ? R.string.music_preview_stop : R.string.music_preview_play);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPreview();
    }
}
