package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bettabeal.model.AddressRequest;
import com.example.bettabeal.model.Address;
import com.example.bettabeal.model.AddressListResponse;
import com.example.bettabeal.model.DistrictResponse;

import com.example.bettabeal.utils.SessionManager;
import com.example.bettabeal.model.AddressRequest;
import com.example.bettabeal.model.PoscodeResponse;
import com.example.bettabeal.model.AddressResponse;
import com.example.bettabeal.model.District;
import com.example.bettabeal.model.DistrictResponse;
import com.example.bettabeal.model.Poscode;
import com.example.bettabeal.model.PoscodeResponse;
import com.example.bettabeal.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class EditShipping extends Fragment {
    private EditText etNama, etAddress, etPhoneNumber;
    private AutoCompleteTextView acDistrict, acPoscode;
    private Button btnUpdate;
    private ImageButton btnBackShip;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private List<District> districts;
    private List<Poscode> poscodes;
    private int addressId = -1;
    private Address currentAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressId = getArguments().getInt("address_id", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_shipping, container, false);
        
        initializeViews(view);
        loadDistricts();
        if (addressId != -1) {
            loadAddressDetails();
        } else {
            showToast("Invalid address ID");
            requireActivity().onBackPressed();
        }
        
        return view;
    }

    private void initializeViews(View view) {
        etNama = view.findViewById(R.id.etnama);
        etAddress = view.findViewById(R.id.etAddress);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        acDistrict = view.findViewById(R.id.acDistrict);
        acPoscode = view.findViewById(R.id.acPoscode);
        btnUpdate = view.findViewById(R.id.btnUpdateAddress);
        btnBackShip = view.findViewById(R.id.btnbackship);
        
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        
        sessionManager = new SessionManager(requireContext());

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnBackShip.setOnClickListener(v -> requireActivity().onBackPressed());
        
        btnUpdate.setOnClickListener(v -> validateAndUpdateAddress());
        
        acDistrict.setOnItemClickListener((parent, view, position, id) -> {
            District selectedDistrict = (District) parent.getItemAtPosition(position);
            loadPoscodes(selectedDistrict.getDistrictId());
        });
    }

    private void loadAddressDetails() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        RetrofitClient.getInstance()
            .getApiService()
            .getAddress("Bearer " + token, addressId)
            .enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        currentAddress = response.body().getData();
                        populateFields();
                    } else {
                        showToast("Failed to load address details");
                        requireActivity().onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + t.getMessage());
                    requireActivity().onBackPressed();
                }
            });
    }

    private void populateFields() {
        if (currentAddress != null) {
            etNama.setText(currentAddress.getName());
            etAddress.setText(currentAddress.getAddress());
            etPhoneNumber.setText(currentAddress.getPhoneNumber());
            
            // Set district after districts are loaded
            if (districts != null) {
                for (District district : districts) {
                    if (district.getDistrictId() == currentAddress.getDistrictId()) {
                        acDistrict.setText(district.getDistrictName(), false);
                        loadPoscodes(district.getDistrictId());
                        break;
                    }
                }
            }
        }
    }

    private void loadDistricts() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        RetrofitClient.getInstance()
            .getApiService()
            .getDistricts("Bearer " + token)
            .enqueue(new Callback<DistrictResponse>() {
                @Override
                public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        districts = response.body().getData();
                        setupDistrictAdapter();
                        if (currentAddress != null) {
                            populateFields();
                        }
                    } else {
                        showToast("Failed to load districts");
                    }
                }

                @Override
                public void onFailure(Call<DistrictResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + t.getMessage());
                }
            });
    }

    private void loadPoscodes(int districtId) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        RetrofitClient.getInstance()
            .getApiService()
                .getPoscodesByDistrict("Bearer " + token, districtId)
            .enqueue(new Callback<PoscodeResponse>() {
                @Override
                public void onResponse(Call<PoscodeResponse> call, Response<PoscodeResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        poscodes = response.body().getData();
                        setupPoscodeAdapter();
                        
                        // Set poscode if editing
                        if (currentAddress != null) {
                            for (Poscode poscode : poscodes) {
                                if (poscode.getPoscodeId() == currentAddress.getPoscodeId()) {
                                    acPoscode.setText(poscode.getCode(), false);
                                    break;
                                }
                            }
                        }
                    } else {
                        showToast("Failed to load poscodes");
                    }
                }

                @Override
                public void onFailure(Call<PoscodeResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + t.getMessage());
                }
            });
    }

    private void setupDistrictAdapter() {
        ArrayAdapter<District> adapter = new ArrayAdapter<District>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            districts
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                }
                TextView textView = (TextView) convertView;
                District district = getItem(position);
                if (district != null) {
                    textView.setText(district.getDistrictName());
                }
                return convertView;
            }
        };
        acDistrict.setAdapter(adapter);
    }

    private void setupPoscodeAdapter() {
        ArrayAdapter<Poscode> adapter = new ArrayAdapter<Poscode>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            poscodes
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                }
                TextView textView = (TextView) convertView;
                Poscode poscode = getItem(position);
                if (poscode != null) {
                    textView.setText(poscode.getCode());
                }
                return convertView;
            }
        };
        acPoscode.setAdapter(adapter);
    }

    private void validateAndUpdateAddress() {
        String name = etNama.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() 
            || acDistrict.getText().toString().isEmpty() 
            || acPoscode.getText().toString().isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        District selectedDistrict = null;
        for (District district : districts) {
            if (district.getDistrictName().equals(acDistrict.getText().toString())) {
                selectedDistrict = district;
                break;
            }
        }

        Poscode selectedPoscode = null;
        for (Poscode poscode : poscodes) {
            if (poscode.getCode().equals(acPoscode.getText().toString())) {
                selectedPoscode = poscode;
                break;
            }
        }

        if (selectedDistrict == null || selectedPoscode == null) {
            showToast("Please select valid district and poscode");
            return;
        }

        updateAddress(name, address, selectedDistrict.getDistrictId(), 
                     selectedPoscode.getPoscodeId(), phoneNumber);
    }

    private void updateAddress(String name, String address, int districtId, 
                             int poscodeId, String phoneNumber) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        AddressRequest request = new AddressRequest(
            name, address, districtId, poscodeId, phoneNumber,
            currentAddress != null ? (currentAddress.isMain() ? 1 : 0) : 0
        );

        RetrofitClient.getInstance()
            .getApiService()
            .updateAddress("Bearer " + token, addressId, request)
            .enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        AddressResponse addressResponse = response.body();
                        if (addressResponse.isSuccess()) {
                            showToast("Address updated successfully");
                            requireActivity().onBackPressed();
                        } else {
                            showToast("Failed to update address");
                        }
                    } else {
                        showToast("Failed to update address");
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + t.getMessage());
                }
            });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}



