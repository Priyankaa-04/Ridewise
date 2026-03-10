package com.example.ridewise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.ridewise.model.RideOption;
import com.example.ridewise.ui.RideAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private List<RideOption> rideList;
    private RideAdapter rideAdapter;
    private MaterialCardView bottomSheet;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;


    private AutoCompleteTextView etPickup, etDestination;
    private Button btnCalculate;

    private static final int AUTOCOMPLETE_REQUEST_PICKUP = 1001;
    private static final int AUTOCOMPLETE_REQUEST_DEST = 1002;

    private Place pickupPlace, destinationPlace;

    // store primitive lat/lng values to avoid type conflicts
    private double pickupLat = Double.NaN, pickupLng = Double.NaN;
    private double destLat = Double.NaN, destLng = Double.NaN;

    private final String API_KEY = "AIzaSyDLG_Cp-4ZHO7oH58ivXbiJO4vTssnE1gA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Views
        etPickup = findViewById(R.id.etPickup);
        etDestination = findViewById(R.id.etDestination);
        btnCalculate = findViewById(R.id.btnCalculate);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(200); // initial peek height
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        recyclerView = bottomSheet.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ride list
        rideList = new ArrayList<>();
        addDefaultRides();

        // Adapter with click handling
        rideAdapter = new RideAdapter(rideList, this::openBookingLink);
        recyclerView.setAdapter(rideAdapter);

        // Autocomplete triggers
        etPickup.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_REQUEST_PICKUP));
        etDestination.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_REQUEST_DEST));

        // Calculate fare button
        btnCalculate.setOnClickListener(v -> {
            if (Double.isNaN(pickupLat) || Double.isNaN(destLat)) {
                Toast.makeText(this, "Select both pickup and destination", Toast.LENGTH_SHORT).show();
            } else {
                fetchDistanceMatrix();
            }
        });
    }

    private void addDefaultRides() {
        rideList.clear();
        rideList.add(new RideOption("Ola", "🚖 Cab", "-", 0));
        rideList.add(new RideOption("Ola", "🛺 Auto", "-", 0));
        rideList.add(new RideOption("Ola", "🛵 Bike", "-", 0));

        rideList.add(new RideOption("Uber", "🚗 Cab", "-", 0));
        rideList.add(new RideOption("Uber", "🛺 Auto", "-", 0));
        rideList.add(new RideOption("Uber", "🛵 Bike", "-", 0));

        rideList.add(new RideOption("Rapido", "🛵 Bike", "-", 0));
        rideList.add(new RideOption("Rapido", "🛺 Auto", "-", 0));

        rideList.add(new RideOption("Namma Yatri", "🛺 Auto", "-", 0));
        rideList.add(new RideOption("Namma Yatri", "🚖 Cab", "-", 0));
    }

    private void openAutocomplete(int requestCode) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (place != null) {
                // Save the Place object
                if (requestCode == AUTOCOMPLETE_REQUEST_PICKUP) {
                    pickupPlace = place;
                    etPickup.setText(place.getName());
                    // Convert Places LatLng to Maps LatLng and store primitives
                    LatLng pLatLng = place.getLatLng();
                    if (pLatLng != null) {
                        pickupLat = pLatLng.latitude;
                        pickupLng = pLatLng.longitude;
                        if (mMap != null) {
                            LatLng mapLatLng = new LatLng(pickupLat, pickupLng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLatLng, 14));
                        }
                    }
                } else if (requestCode == AUTOCOMPLETE_REQUEST_DEST) {
                    destinationPlace = place;
                    etDestination.setText(place.getName());
                    LatLng dLatLng = place.getLatLng();
                    if (dLatLng != null) {
                        destLat = dLatLng.latitude;
                        destLng = dLatLng.longitude;
                        if (mMap != null) {
                            LatLng mapLatLng = new LatLng(destLat, destLng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLatLng, 14));
                        }
                    }
                }
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            String msg = (status != null && status.getStatusMessage() != null) ? status.getStatusMessage() : "Unknown error";
            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDistanceMatrix() {
        try {
            // Use the primitive doubles (no type mismatch)
            String origin = pickupLat + "," + pickupLng;
            String destination = destLat + "," + destLng;
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric"
                    + "&origins=" + URLEncoder.encode(origin, "UTF-8")
                    + "&destinations=" + URLEncoder.encode(destination, "UTF-8")
                    + "&key=" + API_KEY;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        if (response.body() != null) {
                            String jsonString = response.body().string();
                            JSONObject json = new JSONObject(jsonString);
                            JSONObject element = json.getJSONArray("rows").getJSONObject(0)
                                    .getJSONArray("elements").getJSONObject(0);

                            double distanceKm = element.getJSONObject("distance").getDouble("value") / 1000.0;
                            double durationMin = element.getJSONObject("duration").getDouble("value") / 60.0;

                            runOnUiThread(() -> updateRideOptions(distanceKm, durationMin));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error encoding locations", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRideOptions(double distanceKm, double durationMin) {
        rideList.clear();

        // Ola
        rideList.add(new RideOption("Ola", "🚖 Cab", Math.round(durationMin) + " min", Math.round(distanceKm * 12)));
        rideList.add(new RideOption("Ola", "🛺 Auto", Math.round(durationMin) + " min", Math.round(distanceKm * 9)));
        rideList.add(new RideOption("Ola", "🛵 Bike", Math.round(durationMin) + " min", Math.round(distanceKm * 6)));

        // Uber
        rideList.add(new RideOption("Uber", "🚗 Cab", Math.round(durationMin) + " min", Math.round(distanceKm * 11.5)));
        rideList.add(new RideOption("Uber", "🛺 Auto", Math.round(durationMin) + " min", Math.round(distanceKm * 8.5)));
        rideList.add(new RideOption("Uber", "🛵 Bike", Math.round(durationMin) + " min", Math.round(distanceKm * 6.5)));

        // Rapido
        rideList.add(new RideOption("Rapido", "🛵 Bike", Math.round(durationMin) + " min", Math.round(distanceKm * 6)));
        rideList.add(new RideOption("Rapido", "🛺 Auto", Math.round(durationMin) + " min", Math.round(distanceKm * 8)));

        // Namma Yatri
        rideList.add(new RideOption("Namma Yatri", "🛺 Auto", Math.round(durationMin) + " min", Math.round(distanceKm * 8.5)));
        rideList.add(new RideOption("Namma Yatri", "🚖 Cab", Math.round(durationMin) + " min", Math.round(distanceKm * 10)));

        rideAdapter.notifyDataSetChanged();
        // Smoothly expand the bottom sheet to show results
        bottomSheet.postDelayed(() ->
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 300);


    }

    private void openBookingLink(RideOption ride) {
        if (Double.isNaN(pickupLat) || Double.isNaN(destLat)) {
            Toast.makeText(this, "Please select pickup and destination first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url;

        switch (ride.getProvider().toLowerCase()) {
            case "ola":
                // Ola deep link with coordinates
                url = "ola://app/launch?pickup_lat=" + pickupLat +
                        "&pickup_lng=" + pickupLng +
                        "&drop_lat=" + destLat +
                        "&drop_lng=" + destLng;
                break;

            case "uber":
                // Uber deep link with coordinates
                url = "uber://?action=setPickup" +
                        "&pickup[latitude]=" + pickupLat +
                        "&pickup[longitude]=" + pickupLng +
                        "&dropoff[latitude]=" + destLat +
                        "&dropoff[longitude]=" + destLng;
                break;

            case "rapido":
                url = "https://rapido.bike/";
                break;

            case "namma yatri":
                url = "https://nammayatri.in/";
                break;

            default:
                Toast.makeText(this, "No booking link available", Toast.LENGTH_SHORT).show();
                return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage(getPackageNameForProvider(ride.getProvider()));
            startActivity(intent);
        } catch (Exception e) {
            // fallback to web if app not installed
            String fallback = ride.getProvider().equalsIgnoreCase("ola")
                    ? "https://book.olacabs.com/"
                    : "https://m.uber.com/";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fallback)));
        }
    }

    // Helper to set correct package name
    private String getPackageNameForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "ola":
                return "com.olacabs.customer";
            case "uber":
                return "com.ubercab";
            default:
                return null;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bangalore = new LatLng(12.9716, 77.5946);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 12f));
    }
}
