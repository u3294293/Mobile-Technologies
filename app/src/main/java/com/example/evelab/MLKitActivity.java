package com.example.evelab;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

public class MLKitActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private ImageView imageView;
    private TextView textViewOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mlkit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageViewMLKit);
        textViewOutput = findViewById(R.id.textViewMLKit);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("uri")) {
            String savedResult = extras.getString("result");
            textViewOutput.setText(savedResult);
            String uriString = extras.getString("uri");
            if (uriString != null) {
                Uri savedUri = Uri.parse(uriString);
                imageView.setImageURI(savedUri);

                imageFileUri = savedUri;
            }
        }
    }
    private boolean checkPermission() {
        String permission = android.Manifest.permission.CAMERA;
        boolean grantCamera = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
        if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permission},
                    REQUEST_PERMISSION);
        }
        return grantCamera;
    }

    public void openCamera(View view) {
        if (!checkPermission()) return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }

    public void loadImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                if (result.getData() != null && result.getData().getData() != null) {
                                    imageFileUri = result.getData().getData();
                                }
                                imageView.setImageURI(imageFileUri);
                                textViewOutput.setText("");
                                try {
                                    InputImage image = InputImage.fromFilePath(getBaseContext(), imageFileUri);
                                    processImageFromBarcodeReader(image);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

    public void processImageFromBarcodeReader(InputImage image) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        textViewOutput.append(Html.fromHtml("<font color='navy'><b>Detected barcode:</b></font><br>", Html.FROM_HTML_MODE_LEGACY));
                        String res = "";
                        for (Barcode barcode : barcodes) {
                            res = barcode.getRawValue();
                            textViewOutput.append(res + "\n");
                        }
                        if (res.length() < 2) {
                            textViewOutput.append(" Barcode not found.\n");
                        }
                    }
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processImageFromContentReader(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    if (labels.isEmpty()) {
                        textViewOutput.append("Nothing found in the image\n");
                        return;
                    }
                    textViewOutput.append(Html.fromHtml("<font color='navy'><b>Detected image content:</b></font><br>", Html.FROM_HTML_MODE_LEGACY));
                    int counter = 1;
                    for (ImageLabel label : labels) {
                        textViewOutput.append(" " + counter + ". " + label.getText() +
                                " (" + String.format("%.1f", label.getConfidence() * 100.0f) + "% confidence)\n");
                        counter++;
                    }
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processImageFromTextReader(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    textViewOutput.append(Html.fromHtml("<font color='navy'><b>Detected text:</b></font><br>", Html.FROM_HTML_MODE_LEGACY));
                    String result = visionText.getText();
                    if (result.length() > 1) textViewOutput.append(" " + result + "\n");
                    else textViewOutput.append(" No text found.\n");
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void openListView(View view) {
// Get image from the current imageFileUri
        Bitmap bitmap = getBitmapFromUri(imageFileUri);
// Create a unique filename from the current date time
        String currentDateTime = LocalDateTime.now().toString();
        String imageFilename = currentDateTime.replaceAll("\\D+", "");
// Save the image to gallery with the unique filename
        saveImageToGallery(bitmap, imageFilename, MLKitActivity.this);
// open List View activity
        Intent intent = new Intent(MLKitActivity.this, ListViewActivity.class);
        intent.putExtra("reader", "Barcode Reader");
        intent.putExtra("result", textViewOutput.getText().toString());
        intent.putExtra("filename", imageFilename);
        startActivity(intent);
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ImageDecoder.Source source =
                    ImageDecoder.createSource(getContentResolver(), uri);
            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
            return bitmap;
        } catch (IOException e) {
            Log.e("URI_TO_BITMAP", "Failed to load image", e);
            return null;
        }
    }
    private void saveImageToGallery(Bitmap bitmap, String fileName, Context
            context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES);
        Uri imageUri =
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream =
                    context.getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("SAVE_GALLERY", "Image saved to gallery: " +
                    imageUri.toString());
        } catch (IOException e) {
            Log.e("SAVE_GALLERY", "Error saving image", e);
        }
    }
}