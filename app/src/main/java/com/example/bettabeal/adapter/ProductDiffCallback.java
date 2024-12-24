package com.example.bettabeal.adapter;

import androidx.recyclerview.widget.DiffUtil;
import com.example.bettabeal.model.Product;
import java.util.List;
import java.util.Objects;

public class ProductDiffCallback extends DiffUtil.Callback {
    private final List<Product> oldList;
    private final List<Product> newList;

    public ProductDiffCallback(List<Product> oldList, List<Product> newList) {
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
