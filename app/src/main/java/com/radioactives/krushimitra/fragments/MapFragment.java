package com.radioactives.krushimitra.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Map");
        }

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
        enableMyLocation();

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String farmName = getArguments().getString("farmName");
            String contact = getArguments().getString("contact");
            double lat = getArguments().getDouble("lat");
            double lng = getArguments().getDouble("lng");

            LatLng itemLocation = new LatLng(lat, lng);
            String title = name + " (" + farmName + ")";
            String snippet = "Contact: " + contact;

            mMap.addMarker(new MarkerOptions()
                    .position(itemLocation)
                    .title(title)
                    .snippet(snippet));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itemLocation, 15f));

            drawPolylineTo(itemLocation);
        } else {
            LatLng sangli = new LatLng(16.8524, 74.5815);
            mMap.addMarker(new MarkerOptions().position(sangli).title("Sangli - Your Place!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sangli, 15f));
        }
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

    private void drawPolylineTo(LatLng destination) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
            mMap.addPolyline(new PolylineOptions()
                    .add(currentLocation, destination)
                    .width(8)
                    .color(ContextCompat.getColor(requireContext(), R.color.md_theme_scrim_mediumContrast)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f));
            mMap.setOnMyLocationChangeListener(null); // Remove listener after first use

            calculateAndSpeakDistance(currentLocation, destination);
        });
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));
            mMap.setOnMyLocationChangeListener(null);
        });
    }

    private void calculateAndSpeakDistance(LatLng startPoint, LatLng endPoint) {
        Location location1 = new Location("");
        location1.setLatitude(startPoint.latitude);
        location1.setLongitude(startPoint.longitude);

        Location location2 = new Location("");
        location2.setLatitude(endPoint.latitude);
        location2.setLongitude(endPoint.longitude);

        float distance = location1.distanceTo(location2); // meters
        String distanceMessage = "The distance is " + (int) distance + " meters.";
        ttsManager.speak(distanceMessage);
    }

    @Override
    public void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}
