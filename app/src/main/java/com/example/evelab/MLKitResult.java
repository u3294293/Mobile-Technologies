package com.example.evelab;

import android.net.Uri;


public class MLKitResult {
    private String reader;
    private String result;
    private Uri imageUri;

    public MLKitResult(String reader, String result, Uri imageUri) {
        this.reader = reader;
        this.result = result;
        this.imageUri = imageUri;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
