package com.example.androidpoc.common;

import androidx.fragment.app.Fragment;

import com.example.androidpoc.todo.TodoListFragment;

public class TodoTabFragment extends TabHostFragment {
    @Override
    protected Fragment createRootFragment() {
        return new TodoListFragment();
    }
}
