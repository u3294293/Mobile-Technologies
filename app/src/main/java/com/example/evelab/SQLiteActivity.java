package com.example.evelab;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SQLiteActivity extends AppCompatActivity {
    Spinner spinner;
    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> adapter;
    MyDbHelper dbHelper;
    String selectedItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sqlite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MyDbHelper(SQLiteActivity.this, "MobileTechResults", null, 1);
        items = dbHelper.readAll();
        items.add(0, "Select an item");
        spinner = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void save (View view) {
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextMark = findViewById(R.id.editTextMark);
        String name = editTextName.getText().toString();
        String mark = editTextMark.getText().toString();
        try {
            Double.parseDouble(mark);
            dbHelper.create(name, mark);
        } catch (Exception e) {
            Toast.makeText(this, "Not a valid mark", Toast.LENGTH_LONG).show();
        }
        items = dbHelper.readAll();
        items.add(0, "Select an item");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                items);
        spinner.setAdapter(adapter);
    }
    public void delete (View view) {
        if (selectedItem.equals("Select an item")) {
            return;
        }
        int from = selectedItem.indexOf(':') + 2;
        int to = selectedItem.indexOf(',');
        String name = selectedItem.substring(from, to);
        dbHelper.delete(name);
        items = dbHelper.readAll();
        items.add(0, "Select an item");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                items);
        spinner.setAdapter(adapter);
    }
    public void update (View view) {
        EditText editTextNewMark = findViewById(R.id.editTextNewMark);
        if (selectedItem.equals("Select an item")) {
            return;
        }
        int from = selectedItem.indexOf(':') + 2;
        int to = selectedItem.indexOf(',');
        String name = selectedItem.substring(from, to);
        String newMark = editTextNewMark.getText().toString();
        try {
            Double.parseDouble(newMark);
            dbHelper.update(name, newMark);
        } catch (Exception e) {
            Toast.makeText(this, "Not a valid mark", Toast.LENGTH_LONG).show();
        }
        items = dbHelper.readAll();
        items.add(0, "Select an item");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                items);
        spinner.setAdapter(adapter);
    }

}