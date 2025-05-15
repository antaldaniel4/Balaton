package com.example.balaton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GPSActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private FusedLocationProviderClient locationClient;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private DistanceAdapter adapter;
    private List<PlaceWithDistance> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        recyclerView = findViewById(R.id.recycler_distances);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DistanceAdapter(places);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocationAndCalculateDistances();
        }
    }

    private void getCurrentLocationAndCalculateDistances() {
        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchPlacesAndCalculateDistances(location);
                    } else {
                        Toast.makeText(this, "Nem sikerült lekérni a helyzetet.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchPlacesAndCalculateDistances(Location currentLocation) {
        db.collection("gps_adatok")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    places.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        double lat = doc.getDouble("latitude");
                        double lon = doc.getDouble("longitude");

                        float[] results = new float[1];
                        Location.distanceBetween(
                                currentLocation.getLatitude(),
                                currentLocation.getLongitude(),
                                lat,
                                lon,
                                results
                        );

                        places.add(new PlaceWithDistance(name, results[0]));
                    }

                    // Rendezés növekvő távolság szerint
                    Collections.sort(places, Comparator.comparingDouble(p -> p.distanceMeters));
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndCalculateDistances();
        } else {
            Toast.makeText(this, "A helyhozzáférés szükséges.", Toast.LENGTH_LONG).show();
        }
    }

    static class PlaceWithDistance {
        String name;
        float distanceMeters;

        PlaceWithDistance(String name, float distanceMeters) {
            this.name = name;
            this.distanceMeters = distanceMeters;
        }
    }
}
