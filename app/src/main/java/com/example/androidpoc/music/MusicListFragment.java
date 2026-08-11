package com.example.androidpoc.music;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpoc.R;
import com.example.androidpoc.common.AlertHelper;
import com.example.androidpoc.data.Callback;
import com.example.androidpoc.data.MusicRepository;
import com.example.androidpoc.model.MusicTrack;

import java.util.Collections;
import java.util.List;

/**
 * Music 一覧画面（Controller）。検索バー（初期値 "J-POP" で初回自動検索）＋一覧。
 * タップで詳細画面を同じタブの子 FragmentManager に積む。
 */
public class MusicListFragment extends Fragment {

    private static final String DEFAULT_TERM = "J-POP";

    private MusicRepository musicRepository;
    private MusicTrackAdapter adapter;

    private EditText searchField;
    private RecyclerView listView;
    private View progressView;
    private TextView emptyView;

    private boolean hasSearched = false;

    public MusicListFragment() {
        super(R.layout.fragment_music_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicRepository = new MusicRepository(requireContext());

        Toolbar toolbar = view.findViewById(R.id.music_toolbar);
        toolbar.setTitle(R.string.music_title);
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        searchField = view.findViewById(R.id.music_search_field);
        searchField.setText(DEFAULT_TERM);
        searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                search();
                return true;
            }
            return false;
        });

        listView = view.findViewById(R.id.music_list);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MusicTrackAdapter(this::onTrackClick);
        listView.setAdapter(adapter);

        progressView = view.findViewById(R.id.music_progress);
        emptyView = view.findViewById(R.id.music_empty);

        if (!hasSearched) {
            search();
        }
    }

    private void onTrackClick(MusicTrack track) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.tab_content, MusicDetailFragment.newInstance(track))
                .addToBackStack(null)
                .commit();
    }

    private void search() {
        String term = searchField.getText().toString().trim();
        if (term.isEmpty()) {
            return;
        }
        setLoading(true);
        musicRepository.search(term, new Callback<List<MusicTrack>>() {
            @Override
            public void onSuccess(List<MusicTrack> tracks) {
                if (!isAdded()) return;
                setLoading(false);
                hasSearched = true;
                adapter.submitList(tracks);
                updateEmptyState(tracks.isEmpty());
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                setLoading(false);
                hasSearched = true;
                adapter.submitList(Collections.emptyList());
                updateEmptyState(false);
                MusicError error = (e instanceof MusicError.MusicException)
                        ? ((MusicError.MusicException) e).error
                        : MusicError.UNKNOWN;
                AlertHelper.showError(requireContext(), error.messageResId);
            }
        });
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        listView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
