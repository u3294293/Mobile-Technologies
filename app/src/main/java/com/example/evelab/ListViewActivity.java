package com.example.evelab;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    String reader = "No reader available";
    String result = "No result available";
    String filename;
    Uri uri = Uri.EMPTY;
    List<MLKitResult> mlKitResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reader = extras.getString("reader");
            result = extras.getString("result");
            filename = extras.getString("filename");
        }

        try {
            if (filename != null) {
                uri = loadImageFromGallery(filename + ".png");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mlKitResults.add(new MLKitResult(reader, result, uri));

        MLKitAdapter adapter = new MLKitAdapter(this, R.layout.list_item, mlKitResults);
        ListView listView = findViewById(R.id.listView);

        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MLKitResult res = mlKitResults.get(position);
                    Intent intent = new Intent(view.getContext(), MLKitActivity.class);
                    intent.putExtra("reader", res.getReader());
                    intent.putExtra("result", res.getResult());
                    intent.putExtra("uri", res.getImageUri().toString());
                    startActivity(intent);
                }
            });
        }
    }

    private Uri loadImageFromGallery(String filename) throws IOException {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = {filename};
        try (Cursor cursor = getContentResolver().query(uri, projection,
                selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(idColumn);
                Uri imageUri = ContentUris.withAppendedId(uri, imageId);
                return imageUri;
            }
        }
        return null;
    }
}