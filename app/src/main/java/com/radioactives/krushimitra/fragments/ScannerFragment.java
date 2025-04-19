package com.radioactives.krushimitra.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.services.TTSManager;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerFragment extends Fragment {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private TTSManager ttsManager;

    private static final int MODEL_INPUT_SIZE = 224; // Change to match your model's input size
    private final String[] labels = {"Red Rot", " Rust", "Healthy", "yellow","Mosaic"}; // Change accordingly

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        previewView = view.findViewById(R.id.previewView);
        FloatingActionButton captureButton = view.findViewById(R.id.captureButton);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Scan Your Plant.");
        }

        captureButton.setOnClickListener(v -> takePhoto());
        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();
        ttsManager = new TTSManager(getContext());
        return view;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(requireContext().getExternalFilesDir(null), "photo_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(requireContext(), "Photo saved", Toast.LENGTH_SHORT).show();
                runInference(photoFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
                Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void runInference(File photoFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, true);

        try {
            // Load model
            Interpreter tflite = new Interpreter(loadModelFile("plant_disease_model.tflite"));

            // Preprocess image
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(resized);

            // ImageProcessor for resizing and normalization
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0, 255)) // Normalizing pixel values to [0, 1]
                    .build();
            tensorImage = imageProcessor.process(tensorImage);

            // Get output shape dynamically
            int[] outputShape = tflite.getOutputTensor(0).shape(); // e.g., [1, 5]
            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32);

            // Run inference
            tflite.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

            // Get output array and find the max prediction
            float[] output = outputBuffer.getFloatArray();
            int maxIdx = 0;
            float maxProb = output[0];
            for (int i = 1; i < output.length; i++) {
                if (output[i] > maxProb) {
                    maxProb = output[i];
                    maxIdx = i;
                }
            }

            // Get prediction label
            String prediction = labels[maxIdx];

            // Show Toast with prediction
            Toast.makeText(requireContext(), "Prediction: " + prediction, Toast.LENGTH_LONG).show();

            // Switch to result fragment and pass the image path
            ResultFragment resultFragment = ResultFragment.newInstance(prediction, maxProb, photoFile.getAbsolutePath());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, resultFragment)  // Replace with the result fragment
                    .addToBackStack(null)  // Optionally add to the back stack to allow back navigation
                    .commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private MappedByteBuffer loadModelFile(String modelName) throws IOException {
        FileInputStream inputStream = new FileInputStream(requireContext().getAssets().openFd(modelName).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = requireContext().getAssets().openFd(modelName).getStartOffset();
        long declaredLength = requireContext().getAssets().openFd(modelName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
