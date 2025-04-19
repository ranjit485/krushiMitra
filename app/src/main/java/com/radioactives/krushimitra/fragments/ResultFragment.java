package com.radioactives.krushimitra.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.radioactives.krushimitra.R;

public class ResultFragment extends Fragment {

    private static final String ARG_PREDICTION = "prediction";
    private static final String ARG_PROBABILITY = "probability";
    private static final String ARG_IMAGE_PATH = "image_path";

    public static ResultFragment newInstance(String prediction, float probability, String imagePath) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREDICTION, prediction);
        args.putFloat(ARG_PROBABILITY, probability);
        args.putString(ARG_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView predictionTextView = view.findViewById(R.id.predictionTextView);
        TextView probabilityTextView = view.findViewById(R.id.probabilityTextView);
        ImageView imageView = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            String prediction = getArguments().getString(ARG_PREDICTION);
            float probability = getArguments().getFloat(ARG_PROBABILITY);
            String imagePath = getArguments().getString(ARG_IMAGE_PATH);

            // Set the prediction and probability text
            predictionTextView.setText("Prediction: " + prediction);
            probabilityTextView.setText("Probability: " + probability);

            // Load the captured image
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }
}
