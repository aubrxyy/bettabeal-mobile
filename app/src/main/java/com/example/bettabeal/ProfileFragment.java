package com.example.bettabeal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.bettabeal.model.Customer;
import com.example.bettabeal.model.CustomerResponse;
import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.utils.SessionManager;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "MyAppPrefs";
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private SessionManager sessionManager;
    private FragmentManager.OnBackStackChangedListener backStackListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views dan SessionManager
        initializeViews(view);
        sessionManager = new SessionManager(requireContext());

        // Load user data
        loadUserData();

        // Setup button listeners
        setupButtonListeners(view);

        return view;
    }

    private void loadUserData() {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token.isEmpty() || userId == -1) {
            showToast("Error: Token or User ID is missing");
            return;
        }

        // Tambahkan "Bearer " ke token
        String bearerToken = "Bearer " + token;

        RetrofitClient.getInstance()
            .getApiService()
            .getCustomer(userId, bearerToken)  // Menggunakan bearer token
            .enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, 
                                     Response<CustomerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CustomerResponse customerResponse = response.body();
                        if ("000".equals(customerResponse.getCode())) {  // Periksa kode response
                            Customer customer = customerResponse.getCustomer();
                            updateUI(customer);
                        } else {
                            showToast("Failed: " + customerResponse.getMessage());
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "Unknown error";
                            showToast("Failed to load user data: " + errorBody);
                            Log.e("ProfileFragment", "Error Body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("Failed to load user data");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    Log.e("ProfileFragment", "Network Error: " + t.getMessage());
                    showToast("Network Error: " + t.getMessage());
                }
            });
    }

    private void updateUI(Customer customer) {
        if (customer != null) {
            // Update UI with customer data
            usernameTextView.setText(customer.getFull_name());
            emailTextView.setText(customer.getEmail());

            // Load profile image if exists
            String profileImage = customer.getProfile_image();
            if (profileImage != null && !profileImage.isEmpty()) {
                String fullUrl = "https://api-bettabeal.dgeo.id" + profileImage;
                Glide.with(requireContext())
                    .load(fullUrl)
                    .placeholder(R.drawable.pic_profile_account)
                    .error(R.drawable.pic_profile_account)
                    .into(profileImageView);
            }

            // Save user details to session
            sessionManager.saveUserDetails(
                customer.getFull_name(),
                customer.getBirth_date(),
                customer.getPhone_number(),
                customer.getEmail(),
                customer.getProfile_image()
            );
        }
    }

    private void initializeViews(View view) {
        profileImageView = view.findViewById(R.id.profile_image);
        usernameTextView = view.findViewById(R.id.username_text);
        emailTextView = view.findViewById(R.id.email_text);
    }

    private void setupButtonListeners(View view) {
        view.findViewById(R.id.settings).setOnClickListener(v -> navigateToSettings());
        view.findViewById(R.id.address).setOnClickListener(v -> navigateToShipping());
        view.findViewById(R.id.myorders).setOnClickListener(v -> navigateToMyOrders());
        view.findViewById(R.id.sign_out_btn).setOnClickListener(v -> logoutUser());
    }

   

    // Navigate to Shipping fragment
    private void navigateToShipping() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, new Shipping()); // Replace with ShippingFragment
        transaction.addToBackStack("shipping"); // Tambahkan nama untuk back stack
        transaction.commit();
        hideBottomNavigation(); // Hide Bottom Navigation for Shipping
    }

    // Navigate to Settings fragment
    private void navigateToSettings() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, new SettingsFragment());
        transaction.addToBackStack("settings"); // Tambahkan nama untuk back stack
        transaction.commit();
        hideBottomNavigation(); // Hide Bottom Navigation for Settings
    }

    // Navigate to My Orders fragment
    private void navigateToMyOrders() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, new My_orders()); // Replace with My_orders fragment
        transaction.addToBackStack("orders"); // Tambahkan nama untuk back stack
        transaction.commit();
        hideBottomNavigation(); // Hide Bottom Navigation for My Orders
    }

    // Handle user logout
    private void logoutUser() {
        // Use SessionManager instead of SharedPreferences
        sessionManager.clearSession();

        // Redirect to Sign In screen and clear back stack
        Intent intent = new Intent(getActivity(), Sign_in.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Close current activity
    }

    // Hide the Bottom Navigation
    private void hideBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE); // Hide bottom navigation when needed
            }
        }

    }

    // Show the Bottom Navigation when fragment is resumed
    @Override
    public void onResume() {
        super.onResume();
        showBottomNavigation();
        
        // Tambahkan callback untuk memantau perubahan back stack
        if (getActivity() != null) {
            backStackListener = () -> {
                // Pastikan Fragment masih attached ke Activity
                if (isAdded()) {
                    // Gunakan requireActivity() atau getActivity() yang sudah di-check
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment currentFragment = fragmentManager
                        .findFragmentById(R.id.flFragment);
                    
                    if (currentFragment instanceof HomeFragment ||
                        currentFragment instanceof SearchFragment ||
                        currentFragment instanceof ProfileFragment) {
                        showBottomNavigation();
                    }
                }
            };
            
            requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(backStackListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Hapus listener saat Fragment pause
        if (getActivity() != null && backStackListener != null) {
            getActivity().getSupportFragmentManager()
                .removeOnBackStackChangedListener(backStackListener);
        }
    }

    // Show the Bottom Navigation
    private void showBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                // Cek apakah fragment saat ini adalah fragment utama
                Fragment currentFragment = getParentFragmentManager()
                    .findFragmentById(R.id.flFragment);
                if (currentFragment instanceof HomeFragment ||
                    currentFragment instanceof SearchFragment ||
                    currentFragment instanceof ProfileFragment) {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

