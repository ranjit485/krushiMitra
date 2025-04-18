package com.radioactives.krushimitra.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.services.TTSManager;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TTSManager ttsManager;

    private FloatingActionButton myLocationButton;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        myLocationButton = view.findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(view1 -> zoomToUserLocation());
        ttsManager = new TTSManager(requireContext());


        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sangli = new LatLng(16.8524, 74.5815);
        LatLng sampleLocation = new LatLng(16.854, 74.588);
        mMap.addMarker(new MarkerOptions().position(sangli).title("Sangli - Your Place!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sangli, 15f));

        enableMyLocation();
//        drawSamplePolyline();
        addMorePins();
        drawPolylineTo(sangli);

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    private void drawSamplePolyline() {
        LatLng sangli = new LatLng(16.8524, 74.5815);
        LatLng miraj = new LatLng(16.8244, 74.7421);
        mMap.addPolyline(new PolylineOptions()
                .add(sangli, miraj)
                .width(8)
                .color(ContextCompat.getColor(requireContext(), R.color.md_theme_primaryContainer_highContrast)));
    }

    private void addMorePins() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(16.854, 74.588)).title("Pin 1"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(16.860, 74.600)).title("Pin 2"));
    }
    private void drawPolylineTo(LatLng destination) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

            mMap.addPolyline(new PolylineOptions()
                    .add(currentLocation, destination)
                    .width(8)
                    .color(ContextCompat.getColor(requireContext(), R.color.md_theme_scrim_mediumContrast))
            );

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f));

            // Optional: remove listener after one-time polyline
            mMap.setOnMyLocationChangeListener(null);
        });
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(location -> {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));
            mMap.setOnMyLocationChangeListener(null); // Remove listener after first zoom
        });
    }
    private void calculateAndSpeakDistance(LatLng startPoint, LatLng endPoint) {
        Location location1 = new Location("");
        location1.setLatitude(startPoint.latitude);
        location1.setLongitude(startPoint.longitude);

        Location location2 = new Location("");
        location2.setLatitude(endPoint.latitude);
        location2.setLongitude(endPoint.longitude);

        float distance = location1.distanceTo(location2); // Distance in meters
//        float distanceInKm = distance / 1000f;  // Convert meters to kilometers
//        String distanceMessage = "The distance is " + distanceInKm + " kilometers.";

        String distanceMessage = "The distance is " + distance + " meters.";
        ttsManager.speak(distanceMessage);  // Speak the distance
    }

    @Override
    public void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }


}
