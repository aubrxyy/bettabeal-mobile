package com.example.bettabeal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bettabeal.ProductDetailActivity;
import com.example.bettabeal.R;
import com.example.bettabeal.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private final Context context;

    public ProductAdapter(Context context, boolean isGrid) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_search_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void setProducts(List<Product> newProducts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProductDiffCallback(products, newProducts));
        products.clear();
        products.addAll(newProducts);
        diffResult.dispatchUpdatesTo(this);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private View ratingContainer;
        private TextView tvRating;
        private TextView tvDot;
        private TextView tvSales;

        ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            ratingContainer = itemView.findViewById(R.id.ratingContainer);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDot = itemView.findViewById(R.id.tvDot);
            tvSales = itemView.findViewById(R.id.tvSales);
        }

        void bind(Product product) {
            if (product == null) return;

            productName.setText(product.getProduct_name());
            productPrice.setText(String.format("Rp %.0f", product.getPrice()));

            boolean hasRating = product.getAverage_rating() > 0;
            boolean hasSales = product.getTotal_sales() > 0;

            ratingContainer.setVisibility(hasRating ? View.VISIBLE : View.GONE);
            tvDot.setVisibility(hasRating && hasSales ? View.VISIBLE : View.GONE);
            tvSales.setVisibility(hasSales ? View.VISIBLE : View.GONE);

            if (hasRating) {
                tvRating.setText(String.format("%.1f", product.getAverage_rating()));
            }
            if (hasSales) {
                tvSales.setText(String.format("%d+ terjual", product.getTotal_sales()));
            }

            final String finalImageUrl = getProductImageUrl(product);

            if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
                Glide.with(context)
                    .load(finalImageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                  Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ProductAdapter", "Failed to load image: " + finalImageUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                     Target<Drawable> target, DataSource dataSource,
                                                     boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.default_image);
                Log.w("ProductAdapter", "No image URL for: " + product.getProduct_name());
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                context.startActivity(intent);
            });
        }

        private String getProductImageUrl(Product product) {
            if (product.getMain_image() != null) {
                return product.getMain_image().getImage_url();
            } else if (product.getImages() != null && !product.getImages().isEmpty()) {
                return product.getImages().get(0).getImage_url();
            }
            return null;
        }
    }

    private static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Product> oldList;
        private final List<Product> newList;

        ProductDiffCallback(List<Product> oldList, List<Product> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getProduct_id() == 
                   newList.get(newItemPosition).getProduct_id();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Product oldProduct = oldList.get(oldItemPosition);
            Product newProduct = newList.get(newItemPosition);
            
            return oldProduct.getProduct_name().equals(newProduct.getProduct_name()) &&
                   oldProduct.getPrice() == newProduct.getPrice() &&
                   oldProduct.getDescription().equals(newProduct.getDescription());
        }
    }
} 