package com.radioactives.krushimitra.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.adapters.GroceryAdapter;
import com.radioactives.krushimitra.bottomsheets.AddGroceryBottomSheet;
import com.radioactives.krushimitra.interfaces.GroceryApiService;
import com.radioactives.krushimitra.modal.GroceryItem;
import com.radioactives.krushimitra.utils.GroceryApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketFragment extends Fragment {

    private RecyclerView recyclerView;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search For Market");
        }

        spinner = view.findViewById(R.id.spinner_select_plant);
        recyclerView = view.findViewById(R.id.product_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                loadGroceryItems(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab_add_grocery);

        fab.setOnClickListener(v -> {
            AddGroceryBottomSheet bottomSheet = new AddGroceryBottomSheet();

            bottomSheet.setGrocerySubmitListener(item -> {
                // Handle submitted item (optional)
                // e.g., update UI or notify adapter
            });

            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }

    private void loadGroceryItems(String itemName) {
        GroceryApiService api = GroceryApiClient.getClient().create(GroceryApiService.class);
        Call<List<GroceryItem>> call = api.getItemsByProduct(itemName);

        call.enqueue(new Callback<List<GroceryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroceryItem>> call, @NonNull Response<List<GroceryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroceryAdapter adapter = new GroceryAdapter(requireContext(), response.body(), item -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", item.getName());
                        bundle.putString("farmName", item.getFarmName());
                        bundle.putString("contact", item.getContact());
                        bundle.putDouble("lat", item.getLatitude());
                        bundle.putDouble("lng", item.getLongitude());

                        MapFragment mapFragment = new MapFragment();
                        mapFragment.setArguments(bundle);

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, mapFragment)
                                .addToBackStack(null)
                                .commit();
                    });

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroceryItem>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load items: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
