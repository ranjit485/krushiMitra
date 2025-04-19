package com.radioactives.krushimitra.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.interfaces.GroceryApiService;
import com.radioactives.krushimitra.modal.GroceryItem;
import com.radioactives.krushimitra.utils.GroceryApiClient;
import com.radioactives.krushimitra.utils.RetrofitClient;
import com.radioactives.krushimitra.utils.RetrofitClientForGrocery;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroceryBottomSheet extends BottomSheetDialogFragment {
    private ProgressBar progressBar;

    private Button btnSubmit;
    private EditText etFarmName, etFarmerName, etContact, etCostPerKg,cityName;
    private Spinner spinnerProductName;

    public interface GrocerySubmitListener {
        void onSubmit(GroceryItem item);
    }

    private GrocerySubmitListener listener;

    public void setGrocerySubmitListener(GrocerySubmitListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_grocery, container, false);

        // Initialize the UI components
        etFarmName = view.findViewById(R.id.et_farm_name);
        etFarmerName = view.findViewById(R.id.et_farmer_name);
        etContact = view.findViewById(R.id.et_contact);
        etCostPerKg = view.findViewById(R.id.et_cost_per_kg);
        spinnerProductName = view.findViewById(R.id.spinner_product_name);
        cityName = view.findViewById(R.id.et_city_name);


        btnSubmit = view.findViewById(R.id.btn_submit);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set OnClickListener for the Submit button
        btnSubmit.setOnClickListener(v -> {
            String farmName = etFarmName.getText().toString();
            String farmerName = etFarmerName.getText().toString();
            String contact = etContact.getText().toString();
            String costPerKg = etCostPerKg.getText().toString();
            String cityNameLocal = cityName.getText().toString();
            String productName = spinnerProductName.getSelectedItem().toString();

            // Validate input fields
            if ( cityNameLocal.isEmpty() || farmerName.isEmpty() || contact.isEmpty() || costPerKg.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create GroceryItem object
            GroceryItem item = new GroceryItem(
                    productName,
                    cityNameLocal,
                    farmName,
                    "₹"+costPerKg+"/Kg",
                    0.0, // Example latitude
                    0.0, // Example longitude
                    farmerName,
                    contact
            );

            // API call to submit grocery item
            submitGroceryItem(item);
        });

        return view;
    }
    private void submitGroceryItem(GroceryItem item) {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        GroceryApiService apiService = RetrofitClientForGrocery.getGroceryApiService();

        apiService.addItem(item).enqueue(new Callback<GroceryItem>() {
            @Override
            public void onResponse(@NonNull Call<GroceryItem> call, @NonNull Response<GroceryItem> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Item added successfully!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onSubmit(response.body());
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GroceryItem> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
