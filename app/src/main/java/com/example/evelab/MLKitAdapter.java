package com.example.evelab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.evelab.MLKitResult;
import com.example.evelab.R;

import java.util.ArrayList;
import java.util.List;

public class MLKitAdapter extends ArrayAdapter<MLKitResult> {
    List<MLKitResult> items = new ArrayList<>();
    public MLKitAdapter(@NonNull Context context, int resource, @NonNull
    List<MLKitResult> objects) {
        super(context, resource, objects);
        items = objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull
    ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item, parent, false);
        }
        MLKitResult item = items.get(position);
        ImageView icon = convertView.findViewById(R.id.imageViewListItem);
        icon.setImageURI(item.getImageUri());
        TextView textViewItem = convertView.findViewById(R.id.textViewItem);
        textViewItem.setText(item.getReader());
        TextView textViewSubItem =
                convertView.findViewById(R.id.textViewSubItem);
        textViewSubItem.setText(item.getResult().replaceAll("\n", " "));
        return convertView;
    }
}