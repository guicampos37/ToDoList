package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private DatabaseReference databaseReference;
    private EditText taskInput;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Task itemToRemove = taskList.get(position);
                deleteItem(itemToRemove);
            }
        });
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        taskInput = findViewById(R.id.taskInput);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDesc = taskInput.getText().toString().trim();
                if (!taskDesc.isEmpty()) {
                    String id = databaseReference.push().getKey();
                    Task newTask = new Task(id, taskDesc);
                    databaseReference.child(id).setValue(newTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Tarefa Adicionada", Toast.LENGTH_SHORT).show();
                                taskInput.setText("");
                                loadTasks(); // Atualiza a lista
                            } else {
                                Toast.makeText(MainActivity.this, "Falha ao adicionar a tarefa", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Campo tarefa não pode estar vazio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadTasks();
    }

    private void deleteItem(Task task) {
        // Implemente a lógica de remoção aqui
        databaseReference.child(task.getTaskId()).removeValue();
    }

    private void loadTasks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    if (task != null) {
                        task.setTaskId(postSnapshot.getKey());
                    }
                    taskList.add(task);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Falha ao carregar tarefas.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
