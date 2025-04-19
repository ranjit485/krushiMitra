package com.radioactives.krushimitra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.modal.GroceryItem;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    public interface OnGroceryClickListener {
        void onGroceryClick(GroceryItem item);
    }

    private List<GroceryItem> items;
    private Context context;
    private OnGroceryClickListener listener;

    public GroceryAdapter(Context context, List<GroceryItem> items, OnGroceryClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.location.setText(item.getLocation());
        holder.farmName.setText(item.getFarmName());
        holder.cost.setText(item.getCostPerKg());

        holder.itemView.setOnClickListener(v -> listener.onGroceryClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, cost, farmName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.grocery_name);
            location = itemView.findViewById(R.id.farm_location);
            cost = itemView.findViewById(R.id.cost_per_kg);
            farmName = itemView.findViewById(R.id.farm_name);
        }
    }
}
