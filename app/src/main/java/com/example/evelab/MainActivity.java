package com.example.evelab;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.evelab.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("message");
        TextView textView = findViewById(R.id.textViewOutput);
        textView.setText(msg);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child("example").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uploadedText = snapshot.getValue().toString();
                TextView textView = findViewById(R.id.textViewOutput);
                textView.setText(uploadedText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("UI and Events");
        dbref2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // The snapshot will go through all children in the current reference (UI and Events)
                String uploadedText = snapshot.getValue().toString();
                TextView textView = findViewById(R.id.textViewOutput);
                textView.append("\n" + uploadedText); // get all values
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void displayMessage(View view) {
        TextView textView = findViewById(R.id.textViewOutput);
        EditText editText = findViewById(R.id.editTextInput);
        textView.setText(editText.getText());

        Toast.makeText(this, "OK button clicked.", Toast.LENGTH_LONG).show();

        String inputText = editText.getText().toString();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("UI and Events");
        String childname = dbref.push().getKey();
        dbref.child(childname).setValue(inputText);

    }
}