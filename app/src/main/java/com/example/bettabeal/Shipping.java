package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.bettabeal.utils.SessionManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.bettabeal.adapter.AddressAdapter;
import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.Address;
import com.example.bettabeal.model.AddressListResponse;
import com.example.bettabeal.model.AddressResponse;
import com.example.bettabeal.model.District;
import com.example.bettabeal.model.DistrictResponse;
import com.example.bettabeal.model.Poscode;
import com.example.bettabeal.model.PoscodeResponse;
import com.example.bettabeal.model.BaseResponse;
import com.example.bettabeal.model.MainAddressResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Collections;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Shipping extends Fragment implements AddressAdapter.OnItemClickListener {
    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private ProgressDialog progressDialog;
    private FloatingActionButton btnAdd;
    private ImageButton btnBackShipAddress;
    private SessionManager sessionManager;
    private Map<Integer, String> districtNames = new HashMap<>();
    private Map<Integer, String> posCodes = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi SessionManager
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shipping, container, false);

        initViews(view);
        setupRecyclerView(view);
        loadAddresses();

        return view;
    }

    private void initViews(View view) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");

        btnAdd = view.findViewById(R.id.btnadd);
        btnBackShipAddress = view.findViewById(R.id.btnbackshipadress);

        btnAdd.setOnClickListener(v -> {
            AddShipping addShippingFragment = new AddShipping();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, addShippingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnBackShipAddress.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    private void setupRecyclerView(View view) {
        rvAddresses = view.findViewById(R.id.rvAddresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(requireContext(), addressList, this);
        rvAddresses.setAdapter(addressAdapter);
    }

    private void loadAddresses() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        
        // Load districts first
        RetrofitClient.getInstance()
            .getApiService()
            .getDistricts("Bearer " + token)
            .enqueue(new Callback<DistrictResponse>() {
                @Override
                public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<District> districts = response.body().getData();
                        districtNames.clear(); // Clear existing data
                        posCodes.clear();
                        
                        for (District district : districts) {
                            districtNames.put(district.getDistrictId(), district.getDistrictName());
                            loadPoscodes(token, district.getDistrictId());
                        }
                        
                        // Load addresses after districts loaded
                        loadAddressesData(token);
                    } else {
                        progressDialog.dismiss();
                        showToast("Failed to load districts");
                    }
                }

                @Override
                public void onFailure(Call<DistrictResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Error: " + t.getMessage());
                }
            });
    }

    private void loadPoscodes(String token, int districtId) {
        RetrofitClient.getInstance()
            .getApiService()
            .getPoscodesByDistrict("Bearer " + token, districtId)
            .enqueue(new Callback<PoscodeResponse>() {
                @Override
                public void onResponse(Call<PoscodeResponse> call, Response<PoscodeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Poscode> poscodes = response.body().getData();
                        for (Poscode poscode : poscodes) {
                            posCodes.put(poscode.getPoscodeId(), poscode.getCode());
                        }
                    }
                }

                @Override
                public void onFailure(Call<PoscodeResponse> call, Throwable t) {
                    Log.e("Shipping", "Failed to load poscodes: " + t.getMessage());
                }
            });
    }

    private void loadAddressesData(String token) {
        RetrofitClient.getInstance()
            .getApiService()
            .getAddresses("Bearer " + token)
            .enqueue(new Callback<AddressListResponse>() {
                @Override
                public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        AddressListResponse listResponse = response.body();
                        if (listResponse.isSuccess()) {
                            List<Address> addresses = listResponse.getData();
                            addressList.clear(); // Clear existing data
                            
                            if (addresses != null && !addresses.isEmpty()) {
                                // Sort addresses by created_at (newest first)
                                Collections.sort(addresses, (a1, a2) -> {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                        Date date1 = sdf.parse(a1.getCreatedAt());
                                        Date date2 = sdf.parse(a2.getCreatedAt());
                                        return date2.compareTo(date1);
                                    } catch (ParseException e) {
                                        return 0;
                                    }
                                });
                                
                                for (Address address : addresses) {
                                    String districtName = districtNames.get(address.getDistrictId());
                                    String posCode = posCodes.get(address.getPoscodeId());
                                    address.setDistrictName(districtName);
                                    address.setPosCode(posCode);
                                    addressList.add(address);
                                }
                            }
                            addressAdapter.notifyDataSetChanged();
                        } else {
                            showToast("Failed to load addresses");
                        }
                    }
                }

                @Override
                public void onFailure(Call<AddressListResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Error: " + t.getMessage());
                }
            });
    }

    private String getToken() {
        String token = sessionManager.getToken();
        return token.isEmpty() ? null : token;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateMainAddress(Address address, boolean isChecked) {
        String token = getToken();
        if (token == null) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        progressDialog.setMessage("Updating main address...");

        RetrofitClient.getInstance()
            .getApiService()
            .setMainAddress("Bearer " + token, address.getAddressId())
            .enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    progressDialog.dismiss();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        AddressResponse addressResponse = response.body();
                        if (addressResponse.isSuccess()) {
                            // Update local data dengan data dari response
                            Address updatedAddress = addressResponse.getData();
                            address.setMain(updatedAddress.isMain());
                            
                            // Update semua alamat lain menjadi non-main jika alamat ini menjadi main
                            if (updatedAddress.isMain()) {
                                for (Address otherAddress : addressList) {
                                    if (!otherAddress.getAddressId().equals(address.getAddressId())) {
                                        otherAddress.setMain(false);
                                    }
                                }
                            }
                            
                            addressAdapter.notifyDataSetChanged();
                            showToast(addressResponse.getMessage());
                        } else {
                            // Rollback checkbox
                            address.setMain(!isChecked);
                            addressAdapter.notifyDataSetChanged();
                            showToast(addressResponse.getMessage());
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("MainAddress", "Error Body: " + errorBody);
                            // Rollback checkbox
                            address.setMain(!isChecked);
                            addressAdapter.notifyDataSetChanged();
                            showToast("Failed to update main address");
                        } catch (IOException e) {
                            // Rollback checkbox
                            address.setMain(!isChecked);
                            addressAdapter.notifyDataSetChanged();
                            showToast("Failed to update main address");
                        }
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("MainAddress", "Error: " + t.getMessage());
                    // Rollback checkbox
                    address.setMain(!isChecked);
                    addressAdapter.notifyDataSetChanged();
                    showToast("Network error: " + t.getMessage());
                }
            });
    }

    @Override
    public void onEditClick(Address address) {
        AddShipping addShippingFragment = new AddShipping();
        Bundle bundle = new Bundle();
        bundle.putString("address_id", String.valueOf(address.getAddressId()));
        addShippingFragment.setArguments(bundle);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.flFragment, addShippingFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMainAddressClick(Address address, boolean isChecked) {
        updateMainAddress(address, isChecked);
    }

    @Override
    public void onDeleteClick(Address address) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Address")
            .setMessage("Are you sure you want to delete this address?")
            .setPositiveButton("Delete", (dialog, which) -> {
                deleteAddress(address);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteAddress(Address address) {
        progressDialog.show();
        String token = getToken();

        RetrofitClient.getInstance()
            .getApiService()
            .deleteAddress("Bearer " + token, address.getAddressId())
            .enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    progressDialog.dismiss();
                    
                    // Debug log untuk melihat response
                    Log.d("DeleteAddress", "Response Code: " + response.code());
                    if (response.body() != null) {
                        Log.d("DeleteAddress", "Response Body: " + response.body().getMessage());
                    }
                    
                    // Cek HTTP status code 200 atau 204 (success)
                    if (response.isSuccessful()) {
                        // Hapus dari list dan update RecyclerView
                        int position = addressList.indexOf(address);
                        if (position != -1) {
                            addressList.remove(position);
                            addressAdapter.notifyItemRemoved(position);
                            // Notify range untuk memastikan semua item terupdate dengan benar
                            addressAdapter.notifyItemRangeChanged(position, addressList.size());
                        }
                        showToast("Address deleted successfully");
                        
                        // Refresh list setelah delete
                        loadAddresses();
                    } else {
                        try {
                            // Jika ada error message dari server
                            String errorBody = response.errorBody().string();
                            Log.e("DeleteAddress", "Error Body: " + errorBody);
                            showToast("Failed to delete address: " + errorBody);
                        } catch (IOException e) {
                            showToast("Failed to delete address");
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("DeleteAddress", "Error: " + t.getMessage());
                    showToast("Error: " + t.getMessage());
                }
            });
    }

    // Tambahkan method untuk refresh list
    private void refreshList() {
        if (addressList != null) {
            addressList.clear();
            addressAdapter.notifyDataSetChanged();
            loadAddresses();
        }
    }
}
