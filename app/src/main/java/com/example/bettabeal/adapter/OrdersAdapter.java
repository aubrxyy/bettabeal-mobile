package com.example.bettabeal.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bettabeal.R;
import com.example.bettabeal.model.OrdersResponse;
import com.example.bettabeal.utils.ShippingStatusHelper;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private List<OrdersResponse.Order> orders = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public OrdersAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrdersResponse.Order order = orders.get(position);
        if (!order.getItems().isEmpty()) {
            OrdersResponse.OrderItem firstItem = order.getItems().get(0);
            
            // Load image
            loadImage(firstItem.getImageUrl(), holder.productImage);

            // Set product details
            holder.productName.setText(firstItem.getProductName());
            holder.quantity.setText("Quantity: " + firstItem.getQuantity());
            
            // Set status with appropriate color
            String status = order.getShippingStatus();
            holder.status.setText(status);
            
            int statusColor;
            switch (status.toLowerCase()) {
                case "delivered":
                    statusColor = ContextCompat.getColor(context, R.color.status_delivered);
                    break;
                case "shipped":
                    statusColor = ContextCompat.getColor(context, R.color.status_shipped);
                    break;
                case "cancelled":
                    statusColor = ContextCompat.getColor(context, R.color.status_cancelled);
                    break;
                default:
                    statusColor = ContextCompat.getColor(context, R.color.status_pending);
                    break;
            }
            
            GradientDrawable statusBackground = (GradientDrawable) holder.status.getBackground();
            statusBackground.setColor(statusColor);

            // Handle expand button visibility and functionality
            if (order.getItems().size() > 1) {
                holder.btnExpand.setVisibility(View.VISIBLE);
                setupExpandableContent(holder, order);
            } else {
                holder.btnExpand.setVisibility(View.GONE);
                holder.additionalItemsContainer.setVisibility(View.GONE);
            }
        }
    }

    private void setupExpandableContent(OrderViewHolder holder, OrdersResponse.Order order) {
        // Initialize the expand button state
        boolean isExpanded = holder.additionalItemsContainer.getVisibility() == View.VISIBLE;
        holder.btnExpand.setRotation(isExpanded ? 180 : 0);
        
        // Load additional items if already expanded
        if (isExpanded) {
            loadAdditionalItems(holder.additionalItemsContainer, order.getItems());
        }

        holder.btnExpand.setOnClickListener(v -> {
            boolean expanding = holder.additionalItemsContainer.getVisibility() != View.VISIBLE;
            holder.additionalItemsContainer.setVisibility(expanding ? View.VISIBLE : View.GONE);
            holder.btnExpand.animate().rotation(expanding ? 180 : 0).setDuration(200).start();
            
            if (expanding) {
                loadAdditionalItems(holder.additionalItemsContainer, order.getItems());
            }
        });
    }

    private void loadAdditionalItems(LinearLayout container, List<OrdersResponse.OrderItem> items) {
        container.removeAllViews();
        
        for (int i = 1; i < items.size(); i++) {
            OrdersResponse.OrderItem item = items.get(i);
            View itemView = inflater.inflate(R.layout.item_additional_product, container, false);
            
            ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
            TextView tvName = itemView.findViewById(R.id.tvName);
            TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
            
            loadImage(item.getImageUrl(), imgProduct);
            tvName.setText(item.getProductName());
            tvQuantity.setText("Quantity : " + item.getQuantity());

            container.addView(itemView);
        }
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Fix double URL issue
            final String finalImageUrl;
            if (imageUrl.contains("http://api-bettabeal.dgeo.id/storage/")) {
                finalImageUrl = imageUrl.replace(
                    "https://api-bettabeal.dgeo.id/storage/http://api-bettabeal.dgeo.id/storage/",
                    "https://api-bettabeal.dgeo.id/storage/"
                );
            } else {
                finalImageUrl = imageUrl;
            }
            
            Log.d("ImageLoading", "Original URL: " + imageUrl);
            Log.d("ImageLoading", "Fixed URL: " + finalImageUrl);

            Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.default_product_image)
                .error(R.drawable.default_product_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ImageLoading", "Failed to load image: " + finalImageUrl);
                        if (e != null) {
                            Log.e("ImageLoading", "Error details:", e);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("ImageLoading", "Successfully loaded image: " + finalImageUrl);
                        return false;
                    }
                })
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.default_product_image);
            Log.w("ImageLoading", "Empty image URL provided");
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, quantity, status;
        ImageButton btnExpand;
        LinearLayout additionalItemsContainer;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            status = itemView.findViewById(R.id.status);
            btnExpand = itemView.findViewById(R.id.btnExpand);
            additionalItemsContainer = itemView.findViewById(R.id.additionalItemsContainer);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_orders, parent, false);
        return new OrderViewHolder(view);
    }

    public void setOrders(List<OrdersResponse.Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }
} 