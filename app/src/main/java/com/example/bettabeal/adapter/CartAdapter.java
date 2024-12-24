package com.example.bettabeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bettabeal.R;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.utils.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartResponse.CartItem> cartItems = new ArrayList<>();
    private final Context context;
    private final OnCartInteractionListener listener;

    public interface OnCartInteractionListener {
        void onIncreaseQuantity(int cartItemId);
        void onDecreaseQuantity(int cartItemId);
        void onDeleteItem(int cartItemId);
    }

    public CartAdapter(Context context, OnCartInteractionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<CartResponse.CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage, deleteBtn;
        private final TextView productName, productPrice, quantityText;
        private final TextView decreaseBtn, increaseBtn;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            decreaseBtn = itemView.findViewById(R.id.decreaseBtn);
            increaseBtn = itemView.findViewById(R.id.increaseBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

        void bind(CartResponse.CartItem item) {
            productName.setText(item.getProduct().getName());
            productPrice.setText(CurrencyFormatter.formatRupiah(
                Double.parseDouble(item.getProduct().getPrice())
            ));
            quantityText.setText(String.valueOf(item.getQuantity()));

            // Load image using Glide
            Glide.with(context)
                .load(item.getProduct().getImage_url())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(productImage);

            // Setup buttons
            increaseBtn.setOnClickListener(v -> 
                listener.onIncreaseQuantity(item.getCart_item_id())
            );

            decreaseBtn.setOnClickListener(v -> 
                listener.onDecreaseQuantity(item.getCart_item_id())
            );

            // Set quantity and handle visibility
            int quantity = item.getQuantity();
            quantityText.setText(String.valueOf(quantity));
            
            if (quantity == 1) {
                decreaseBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                decreaseBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.GONE);
            }

            // Setup delete button
            deleteBtn.setOnClickListener(v -> 
                listener.onDeleteItem(item.getCart_item_id())
            );
        }
    }

    public CartResponse.CartItem getItemById(int cartItemId) {
        for (CartResponse.CartItem item : cartItems) {
            if (item.getCart_item_id() == cartItemId) {
                return item;
            }
        }
        return null;
    }
} 