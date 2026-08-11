package com.example.androidpoc.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidpoc.R;
import com.example.androidpoc.model.MusicTrack;

import java.util.ArrayList;
import java.util.List;

public class MusicTrackAdapter extends RecyclerView.Adapter<MusicTrackAdapter.ViewHolder> {

    public interface OnTrackClickListener {
        void onTrackClick(MusicTrack track);
    }

    private final List<MusicTrack> tracks = new ArrayList<>();
    private final OnTrackClickListener listener;

    public MusicTrackAdapter(OnTrackClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<MusicTrack> newTracks) {
        tracks.clear();
        tracks.addAll(newTracks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicTrack track = tracks.get(position);
        holder.name.setText(track.trackName);
        holder.artist.setText(track.artistName);
        Glide.with(holder.artwork).load(track.artworkUrl100).into(holder.artwork);
        holder.itemView.setOnClickListener(v -> listener.onTrackClick(track));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView artwork;
        final TextView name;
        final TextView artist;

        ViewHolder(View itemView) {
            super(itemView);
            artwork = itemView.findViewById(R.id.track_artwork);
            name = itemView.findViewById(R.id.track_name);
            artist = itemView.findViewById(R.id.track_artist);
        }
    }
}
