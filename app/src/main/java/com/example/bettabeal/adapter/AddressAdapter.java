package com.example.bettabeal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bettabeal.R;
import com.example.bettabeal.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final Context context;
    private final List<Address> addressList;
    private final OnItemClickListener listener;

    public AddressAdapter(Context context, List<Address> addressList, OnItemClickListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_shipping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        
        Log.d("AddressAdapter", "Binding address: " + address.getName());
        
        holder.tvName.setText(address.getName());
        holder.tvAddress.setText(address.getAddress());
        holder.tvPhone.setText(address.getPhoneNumber());
        
        String district = address.getDistrictName() != null ? address.getDistrictName() : "Unknown District";
        String posCode = address.getPosCode() != null ? address.getPosCode() : "";
        
        holder.tvDistrict.setText(district);
        holder.tvPosCode.setText(posCode);
        
        holder.cbMainAddress.setChecked(address.isMain());
        holder.cbMainAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                if (listener != null) {
                    listener.onMainAddressClick(address, isChecked);
                }
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(address);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvPhone;
        TextView tvDistrict;
        TextView tvPosCode;
        Button btnEdit;
        Button btnDelete;
        CheckBox cbMainAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            tvPosCode = itemView.findViewById(R.id.tvPosCode);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cbMainAddress = itemView.findViewById(R.id.cbMainAddress);
        }
    }

    // Define the OnItemClickListener interface
    public interface OnItemClickListener {
        void onEditClick(Address address);
        void onMainAddressClick(Address address, boolean isChecked);
        void onDeleteClick(Address address);
    }
}
