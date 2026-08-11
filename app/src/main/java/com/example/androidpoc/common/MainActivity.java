package com.example.androidpoc.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidpoc.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * NiA/iOS 版の UITabBarController に相当する、タブ切替の親 Activity。
 * 各タブは {@link TabHostFragment} を hide/show するだけで、破棄・再生成しない
 * （＝タブを切り替えても各タブの表示中の画面がそのまま残る）。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_TODO = "tab_todo";
    private static final String TAG_MUSIC = "tab_music";
    private static final String TAG_LOGIN = "tab_login";

    private Fragment todoTab;
    private Fragment musicTab;
    private Fragment loginTab;
    private Fragment activeTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean firstCreation = savedInstanceState == null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        todoTab = fragmentManager.findFragmentByTag(TAG_TODO);
        if (todoTab == null) {
            todoTab = new TodoTabFragment();
            transaction.add(R.id.tab_container, todoTab, TAG_TODO);
        }
        musicTab = fragmentManager.findFragmentByTag(TAG_MUSIC);
        if (musicTab == null) {
            musicTab = new MusicTabFragment();
            transaction.add(R.id.tab_container, musicTab, TAG_MUSIC);
        }
        loginTab = fragmentManager.findFragmentByTag(TAG_LOGIN);
        if (loginTab == null) {
            loginTab = new LoginTabFragment();
            transaction.add(R.id.tab_container, loginTab, TAG_LOGIN);
        }
        transaction.commitNow();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        // menu/bottom_nav_menu.xml は XML の app:menu ではなくここで読み込む
        // （サードパーティ AAR のカスタム属性(app:)を使わない構成にするため）。
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);

        if (firstCreation) {
            hideAllExcept(todoTab);
            activeTab = todoTab;
            bottomNav.setSelectedItemId(R.id.nav_todo);
        } else {
            // 各タブの hidden 状態は FragmentManager が自動で保存/復元するので、
            // 回転などの再生成後はそこから「どのタブが表示中だったか」を読み取る。
            if (!musicTab.isHidden()) {
                activeTab = musicTab;
                bottomNav.setSelectedItemId(R.id.nav_music);
            } else if (!loginTab.isHidden()) {
                activeTab = loginTab;
                bottomNav.setSelectedItemId(R.id.nav_login);
            } else {
                activeTab = todoTab;
                bottomNav.setSelectedItemId(R.id.nav_todo);
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_todo) {
                showTab(todoTab);
                return true;
            } else if (id == R.id.nav_music) {
                showTab(musicTab);
                return true;
            } else if (id == R.id.nav_login) {
                showTab(loginTab);
                return true;
            }
            return false;
        });
    }

    private void hideAllExcept(Fragment visible) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (todoTab != visible) transaction.hide(todoTab);
        if (musicTab != visible) transaction.hide(musicTab);
        if (loginTab != visible) transaction.hide(loginTab);
        transaction.commitNow();
    }

    private void showTab(Fragment target) {
        if (activeTab == target) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeTab);
        transaction.show(target);
        transaction.commit();
        activeTab = target;
    }
}
