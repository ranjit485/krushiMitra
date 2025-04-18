package com.radioactives.krushimitra.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.radioactives.krushimitra.R;

import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        FloatingActionButton captureButton = view.findViewById(R.id.captureButton);


        if (getActivity() != null) {
            // This line changes the title of the MaterialToolbar from the Activity
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search For Market");
        }

        return view;    }
}