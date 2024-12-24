package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.AddressRequest;
import com.example.bettabeal.model.AddressResponse;
import com.example.bettabeal.model.District;
import com.example.bettabeal.model.DistrictResponse;
import com.example.bettabeal.model.Poscode;
import com.example.bettabeal.model.PoscodeResponse;
import com.example.bettabeal.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShipping extends Fragment {

    private EditText etNama, etAddress, etPhoneNumber;
    private AutoCompleteTextView acDistrict, acPoscode;
    private Button btnSave;
    private ImageButton btnBackShip;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    private List<District> districts = new ArrayList<>();
    private List<Poscode> poscodes = new ArrayList<>();
    private District selectedDistrict;
    private Poscode selectedPoscode;
    private Long addressId;

    public AddShipping() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shipping, container, false);
        initializeViews(view);
        setupListeners();
        loadDistricts();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String addressIdStr = getArguments().getString("address_id");
            if (addressIdStr != null) {
                addressId = Long.parseLong(addressIdStr);
            }
        }
    }

    private void initializeViews(View view) {
        etNama = view.findViewById(R.id.etnama);
        etAddress = view.findViewById(R.id.etAddress);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        acDistrict = view.findViewById(R.id.acDistrict);
        acPoscode = view.findViewById(R.id.acPoscode);
        btnSave = view.findViewById(R.id.saveaddress);
        btnBackShip = view.findViewById(R.id.btnbackship);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(requireContext());
    }

    private void setupListeners() {
        btnBackShip.setOnClickListener(v -> requireActivity().onBackPressed());
        btnSave.setOnClickListener(v -> validateAndSaveAddress());

        acDistrict.setOnItemClickListener((parent, view, position, id) -> {
            selectedDistrict = (District) parent.getItemAtPosition(position);
            if (selectedDistrict != null) {
                acPoscode.setText("");
                selectedPoscode = null;
                loadPoscodes(selectedDistrict.getDistrictId());
            }
        });

        acDistrict.setOnClickListener(v -> {
            if (!acDistrict.isPopupShowing()) {
                acDistrict.showDropDown();
            }
        });

        acPoscode.setOnClickListener(v -> {
            if (!acPoscode.isPopupShowing() && selectedDistrict != null) {
                acPoscode.showDropDown();
            }
        });

        acPoscode.setOnItemClickListener((parent, view, position, id) -> {
            selectedPoscode = (Poscode) parent.getItemAtPosition(position);
        });
    }

    private void loadDistricts() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        Log.d("API_DEBUG", "Loading districts with token: " + token);
        progressDialog.show();
        progressDialog.setMessage("Loading districts...");

        RetrofitClient.getInstance().getApiService()
            .getDistricts("Bearer " + token)
            .enqueue(new Callback<DistrictResponse>() {
                @Override
                public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                    progressDialog.dismiss();
                    
                    Log.d("API_DEBUG", "Response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        DistrictResponse districtResponse = response.body();
                        if (districtResponse.isSuccess() && districtResponse.getData() != null 
                            && !districtResponse.getData().isEmpty()) {
                            districts = districtResponse.getData();
                            Log.d("API_DEBUG", "Districts loaded successfully. Count: " + districts.size());
                            setupDistrictAdapter();
                        } else {
                            Log.e("API_DEBUG", "Failed to load districts: " + districtResponse.getCode());
                            showToast("Failed to load districts");
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "Unknown error";
                            Log.e("API_DEBUG", "Error response: " + errorBody);
                            showToast("Failed to load districts");
                        } catch (Exception e) {
                            Log.e("API_DEBUG", "Error reading error body: " + e.getMessage());
                            showToast("Error loading districts");
                        }
                    }
                }

                @Override
                public void onFailure(Call<DistrictResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("API_DEBUG", "Network error: " + t.getMessage());
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
        RetrofitClient.getInstance().getApiService()
            .getPoscodesByDistrict("Bearer " + token, districtId)
            .enqueue(new Callback<PoscodeResponse>() {
                @Override
                public void onResponse(Call<PoscodeResponse> call, Response<PoscodeResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        PoscodeResponse poscodeResponse = response.body();
                        if (poscodeResponse.isSuccess() && poscodeResponse.getData() != null 
                            && !poscodeResponse.getData().isEmpty()) {
                            poscodes = poscodeResponse.getData();
                            setupPoscodeAdapter();
                        } else {
                            showToast("Failed to load postal codes");
                        }
                    } else {
                        showToast("Failed to load postal codes");
                    }
                }

                @Override
                public void onFailure(Call<PoscodeResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                }
            });
    }

    private void setupDistrictAdapter() {
        if (getContext() == null) return;
        
        if (districts != null && !districts.isEmpty()) {
            Log.d("ADAPTER_DEBUG", "Setting up adapter with " + districts.size() + " districts");
            ArrayAdapter<District> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_item,
                districts
            );
            acDistrict.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            acDistrict.setHint("Select District");
        } else {
            Log.e("ADAPTER_DEBUG", "No districts available for adapter");
            showToast("No districts available");
        }
    }

    private void setupPoscodeAdapter() {
        if (getContext() == null) return;
        
        if (poscodes != null && !poscodes.isEmpty()) {
            ArrayAdapter<Poscode> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_item,
                poscodes
            );
            acPoscode.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            acPoscode.setHint("Select Postal Code");
        } else {
            showToast("No postal codes available for selected district");
        }
    }

    private void validateAndSaveAddress() {
        String name = etNama.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() 
            || selectedDistrict == null || selectedPoscode == null) {
            showToast("Please fill all fields");
            return;
        }

        AddressRequest request = new AddressRequest(
            name,
            address,
            selectedDistrict.getDistrictId(),
            selectedPoscode.getPoscodeId(),
            phoneNumber,
            0  // is_main default to 0
        );

        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Please login first");
            return;
        }

        progressDialog.show();
        RetrofitClient.getInstance().getApiService()
            .createAddress("Bearer " + token, request)
            .enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        AddressResponse addressResponse = response.body();
                        if (addressResponse.isSuccess()) {
                            showToast(addressResponse.getMessage() != null ? 
                                addressResponse.getMessage() : "Address saved successfully");
                            requireActivity().onBackPressed();
                        } else {
                            showToast(addressResponse.getMessage() != null ? 
                                addressResponse.getMessage() : "Failed to save address");
                        }
                    } else {
                        showToast("Failed to save address");
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                }
            });
    }

    private void showToast(String message) {
        if (message == null || message.isEmpty()) {
            message = "An error occurred";  // Default message jika null atau kosong
        }
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
