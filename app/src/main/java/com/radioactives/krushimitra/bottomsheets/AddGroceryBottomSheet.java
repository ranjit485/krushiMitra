package com.radioactives.krushimitra.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.interfaces.ApiService;
import com.radioactives.krushimitra.modal.GroceryItem;
import com.radioactives.krushimitra.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroceryBottomSheet extends BottomSheetDialogFragment {

    private Button btnSubmit;
    private EditText etFarmName, etFarmerName, etContact, etCostPerKg;
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
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Set OnClickListener for the Submit button
        btnSubmit.setOnClickListener(v -> {
            String farmName = etFarmName.getText().toString();
            String farmerName = etFarmerName.getText().toString();
            String contact = etContact.getText().toString();
            String costPerKg = etCostPerKg.getText().toString();
            String productName = spinnerProductName.getSelectedItem().toString();

            // Validate input fields
            if ( farmName.isEmpty() || farmerName.isEmpty() || contact.isEmpty() || costPerKg.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create GroceryItem object
            GroceryItem item = new GroceryItem(
                    productName,
                    farmName,
                    contact,
                    costPerKg,
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
        // Initialize Retrofit
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);

        // Make the API call to submit the data
        Call<Void> call = apiService.submitGroceryItem(item);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Notify listener and dismiss the BottomSheet
                    if (listener != null) {
                        listener.onSubmit(item);
                    }
                    dismiss();
                    Toast.makeText(getContext(), "Grocery item submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Submission failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
