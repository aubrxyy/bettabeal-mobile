package com.example.bettabeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bettabeal.R;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.utils.CurrencyFormatter;

import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {
    private final Context context;
    private final List<CartResponse.CartItem> items;

    public CheckoutItemAdapter(Context context, List<CartResponse.CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartResponse.CartItem item = items.get(position);
        
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvQuantity.setText(String.format("%d pcs", item.getQuantity()));
        holder.tvPrice.setText(CurrencyFormatter.formatRupiah(
            Double.parseDouble(item.getProduct().getPrice())
        ));

        Glide.with(context)
            .load(item.getProduct().getImage_url())
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvQuantity, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}