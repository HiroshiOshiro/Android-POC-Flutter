package com.example.androidpoc.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidpoc.R;

/**
 * タブ1つ分の入れ物。自分専用の child FragmentManager を持つことで、
 * BottomNavigationView でタブを切り替えても、そのタブで表示中だった画面
 * （バックスタックを含む）がそのまま残る。
 */
public abstract class TabHostFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getChildFragmentManager().findFragmentById(R.id.tab_content) == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.tab_content, createRootFragment())
                    .commit();
        }
    }

    /** このタブの起点となる画面（一覧画面など）を生成する。 */
    protected abstract Fragment createRootFragment();
}
