package com.example.bettabeal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.Customer;
import com.example.bettabeal.model.CustomerResponse;
import com.example.bettabeal.utils.SessionManager;

public class SettingsFragment extends Fragment {

    private TextView usernameTextView, dateOfBirthTextView, phoneNumberTextView, emailTextView;
    private ImageView profileImageView;
    private SessionManager sessionManager;
    private static final String BASE_URL = "https://api-bettabeal.dgeo.id/api/";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        ImageButton btnBack = view.findViewById(R.id.btnbacksettings);
        Button btnEditSet = view.findViewById(R.id.btneditset);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        dateOfBirthTextView = view.findViewById(R.id.dateOfBirthTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        profileImageView = view.findViewById(R.id.ivProfileImage);

        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());

        // Hide BottomNavigationView
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }

        // Fetch biodata when the fragment is created
        fetchBiodata();

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Edit settings button click listener
        btnEditSet.setOnClickListener(v -> {
            Editsettings editSettingsFragment = new Editsettings();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, editSettingsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    private void fetchBiodata() {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token.isEmpty() || userId == -1) {
            showToast("Error: Token or User ID is missing");
            return;
        }

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        RetrofitClient.getInstance()
            .getApiService()
            .getCustomer(userId, authHeader)
            .enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, 
                                     Response<CustomerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CustomerResponse customerResponse = response.body();
                        if ("000".equals(customerResponse.getCode())) {
                            handleCustomerData(customerResponse.getCustomer());
                        } else {
                            showToast("Error: Failed to fetch data");
                        }
                    } else {
                        try {
                            JSONObject errorBody = new JSONObject(response.errorBody().string());
                            showToast(errorBody.optString("message", 
                                    "Error: Failed to fetch data"));
                        } catch (Exception e) {
                            showToast("Error: Failed to fetch data");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    showToast("Error: Unable to connect");
                }
            });
    }

    private void handleCustomerData(Customer customer) {
        // Display data in TextViews
        usernameTextView.setText(customer.getFull_name());
        dateOfBirthTextView.setText(customer.getBirth_date());
        phoneNumberTextView.setText(customer.getPhone_number());
        emailTextView.setText(customer.getEmail());

        // Handle profile image
        String profileImage = customer.getProfile_image();
        if (profileImage != null && !profileImage.isEmpty()) {
            String baseUrl = "https://api-bettabeal.dgeo.id";
            String fullProfileImageUrl = baseUrl + profileImage;

            Glide.with(this)
                .load(fullProfileImageUrl)
                .into(profileImageView);
        }

        // Save to SessionManager
        sessionManager.saveUserDetails(
            customer.getFull_name(),
            customer.getBirth_date(),
            customer.getPhone_number(),
            customer.getEmail(),
            customer.getProfile_image() != null ? 
                "https://api-bettabeal.dgeo.id" + customer.getProfile_image() : null
        );
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
