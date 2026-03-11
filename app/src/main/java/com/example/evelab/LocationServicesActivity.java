package com.example.evelab;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class LocationServicesActivity extends AppCompatActivity {
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    private double latitude;
    public double longitude;
    public String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_services);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermissions();
        createLocationServicesClient();
        createLocationRequestLocationCallback();
    }

    public boolean requestPermissions() {
        int REQUEST_PERMISSION = 3000;
        String permissions[] = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean grantFinePermission =
                ContextCompat.checkSelfPermission(this, permissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
        boolean grantCoarsePermission =
                ContextCompat.checkSelfPermission(this, permissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;
        if (!grantFinePermission && !grantCoarsePermission) {
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_PERMISSION);
        } else if (!grantFinePermission) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[0]},
                    REQUEST_PERMISSION);
        } else if (!grantCoarsePermission) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[1]},
                    REQUEST_PERMISSION);
        }
        return grantFinePermission && grantCoarsePermission;
    }

    public void createLocationServicesClient() {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(LocationServicesActivity.this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                startLocationUpdates();
                            }
                        });
    }
    public void createLocationRequestLocationCallback() {
        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000).build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();

                TextView tvlat = findViewById(R.id.textViewLatitude);
                TextView tvlng = findViewById(R.id.textViewLongitude);
                tvlat.setText("Latitude: " + latitude);
                tvlng.setText("Longitude: " + longitude);

                showLocationAddress();
            }
        };
    }
    public void showLocationAddress() {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            geocoder.getFromLocation(latitude, longitude, 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> addresses) {
                    if (addresses != null && !addresses.isEmpty()) {
                        address = "Address: ";
                        Address addr = addresses.get(0);
                        for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++) {
                            address += addr.getAddressLine(i) + "\n";
                        }
                    } else {
                        address = "Address: Unknown";
                    }

                    // Only update the address field here
                    TextView tvaddr = findViewById(R.id.textViewAddress);
                    tvaddr.setText(address);
                }
            });
        } catch (Exception e) {
            TextView tvaddr = findViewById(R.id.textViewAddress);
            tvaddr.setText("Address: Service not available");
            e.printStackTrace();
        }
    }
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
}