package com.example.androidpoc.todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpoc.R;
import com.example.androidpoc.common.AlertHelper;
import com.example.androidpoc.data.Callback;
import com.example.androidpoc.data.TodoRepository;
import com.example.androidpoc.model.TodoItem;

import java.util.List;

/**
 * Todo 一覧・入力画面（Controller）。保存ボタンで確認①へ進む。
 * 確認②の保存成功→完了→「入力に戻る」でここまでバックスタックが戻るたびに
 * {@link #onResume()} が呼ばれるので、そこで一覧の再取得と入力欄クリアを行う
 * （iOS 版の viewWillAppear / resetInput に相当）。
 */
public class TodoListFragment extends Fragment {

    private TodoRepository todoRepository;
    private TodoAdapter adapter;
    private EditText inputField;

    public TodoListFragment() {
        super(R.layout.fragment_todo_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todoRepository = new TodoRepository(requireContext());

        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.tab_todo);
        view.findViewById(R.id.toolbar_back).setVisibility(View.GONE);

        inputField = view.findViewById(R.id.todo_input);
        Button saveButton = view.findViewById(R.id.todo_save_button);
        saveButton.setOnClickListener(v -> onSaveTapped());

        RecyclerView listView = view.findViewById(R.id.todo_list);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TodoAdapter();
        listView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                   @NonNull RecyclerView.ViewHolder viewHolder,
                                   @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteAt(viewHolder.getBindingAdapterPosition());
            }
        }).attachToRecyclerView(listView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // タブの hide/show では呼ばれず、確認/完了フローから戻ってきた時と
        // 初回表示時にのみ呼ばれる。iOS 版と同じく、戻ってきたら入力欄を空にして一覧を再取得する。
        inputField.setText("");
        reload();
    }

    private void reload() {
        todoRepository.loadAll(new Callback<List<TodoItem>>() {
            @Override
            public void onSuccess(List<TodoItem> items) {
                if (!isAdded()) return;
                adapter.submitList(items);
            }

            @Override
            public void onError(Exception e) {
                // ローカル読み込みのみなので実質発生しない。
            }
        });
    }

    private void deleteAt(int position) {
        todoRepository.delete(position, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                reload();
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                reload();
            }
        });
    }

    private void onSaveTapped() {
        String text = inputField.getText().toString().trim();
        if (text.isEmpty()) {
            AlertHelper.showError(requireContext(), R.string.todo_input_empty_message);
            return;
        }
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.tab_content, ConfirmFlutterFragment.newInstance(text))
                .addToBackStack("confirm1")
                .commit();
    }
}
