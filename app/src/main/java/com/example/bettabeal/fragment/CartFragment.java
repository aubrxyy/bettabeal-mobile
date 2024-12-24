package com.example.bettabeal.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bettabeal.Home;
import com.example.bettabeal.HomeFragment;
import com.example.bettabeal.ProductDetailActivity;
import com.example.bettabeal.R;
import com.example.bettabeal.SearchFragment;
import com.example.bettabeal.adapter.CartAdapter;
import com.example.bettabeal.api.ApiService;
import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.utils.CurrencyFormatter;
import com.example.bettabeal.utils.SessionManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class CartFragment extends Fragment implements CartAdapter.OnCartInteractionListener {
    private static final String ARG_FROM_HOME = "from_home";
    private static final String ARG_FROM_SEARCH = "from_search";
    private static final String ARG_FROM_PRODUCT_DETAIL = "from_product_detail";
    
    public static CartFragment newInstance(boolean fromHome, boolean fromSearch, boolean fromProductDetail) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FROM_HOME, fromHome);
        args.putBoolean(ARG_FROM_SEARCH, fromSearch);
        args.putBoolean(ARG_FROM_PRODUCT_DETAIL, fromProductDetail);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceText;
    private TextView totalItemsText;
    private SessionManager sessionManager;
    private ApiService apiService;
    private TextView cartTotalItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        ImageButton backButton = view.findViewById(R.id.imageButtonbackcart);
        backButton.setOnClickListener(v -> handleBackPress());
        
        initViews(view);
        setupRecyclerView();
        loadCartItems();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceText = view.findViewById(R.id.textView4);
        totalItemsText = view.findViewById(R.id.total_items);
        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getInstance().getApiService();

        cartTotalItems = view.findViewById(R.id.cart_total_items);

        View btnCheckout = view.findViewById(R.id.btn_delivery);
        btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() == 0) {
                Toast.makeText(requireContext(), 
                    "Your cart is empty", 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            CheckoutFragment checkoutFragment = new CheckoutFragment();
            
            // Tentukan container ID berdasarkan activity
            int containerId;
            if (getActivity() instanceof ProductDetailActivity) {
                containerId = R.id.fragment_container;
            } else {
                containerId = R.id.flFragment;
            }

            // Gunakan container ID yang sesuai
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, checkoutFragment)
                .addToBackStack(null)
                .commit();
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(requireContext(), this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadCartItems() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        Log.d("CartFragment", "Using token: " + authHeader);

        apiService.getCart(authHeader).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse.CartData cartData = response.body().getData();
                    cartAdapter.setItems(cartData.getItems());
                    updateCartUI(cartData);
                } else {
                    Log.e("CartFragment", "Error response: " + response.code());
                    try {
                        Log.e("CartFragment", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(requireContext(), "Failed to load cart: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("CartFragment", "Network error: " + t.getMessage());
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartUI(CartResponse.CartData cartData) {
        totalPriceText.setText(CurrencyFormatter.formatRupiah(cartData.getSubtotal()));
        cartTotalItems.setText("(" + cartData.getTotal_items() + ")");
    }

    @Override
    public void onIncreaseQuantity(int cartItemId) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        String authHeader = "Bearer " + token;

        CartResponse.CartItem item = cartAdapter.getItemById(cartItemId);
        if (item == null) return;
        
        // Hitung quantity baru (quantity saat ini + 1)
        int newQuantity = item.getQuantity() + 1;
        
        // Panggil API dengan quantity baru
        apiService.updateCartItemQuantity(authHeader, cartItemId, newQuantity)
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            loadCartItems(); // Refresh cart
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("CartFragment", "Error Body: " + errorBody);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(requireContext(),
                                    "Stock tidak cukup",  // Pesan diubah
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDecreaseQuantity(int cartItemId) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        // Dapatkan item dari adapter
        CartResponse.CartItem item = cartAdapter.getItemById(cartItemId);
        if (item == null) return;
        
        // Jika quantity adalah 1, mungkin Anda ingin menghapus item
        if (item.getQuantity() <= 1) {
            // Implementasi penghapusan item jika diperlukan
            return;
        }
        
        // Hitung quantity baru (quantity saat ini - 1)
        int newQuantity = item.getQuantity() - 1;
        
        // Panggil API dengan quantity baru
        apiService.updateCartItemQuantity(authHeader, cartItemId, newQuantity)
            .enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    if (response.isSuccessful()) {
                        loadCartItems(); // Refresh cart
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e("CartFragment", "Error Body: " + errorBody);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(requireContext(), 
                            "Failed to update quantity: " + response.code(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    Log.e("CartFragment", "Network Error: " + t.getMessage());
                    Toast.makeText(requireContext(), 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onDeleteItem(int cartItemId) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        apiService.deleteCartItem(authHeader, cartItemId)
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            loadCartItems(); // Refresh cart
                            Toast.makeText(requireContext(),
                                    "Item removed from cart",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Failed to remove item",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void navigateToCheckout() {
        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new CheckoutFragment())
            .addToBackStack(null)  // Agar bisa kembali ke fragment sebelumnya
            .commit();
    }

    
    private void handleBackPress() {
        if (getArguments() != null) {
            boolean fromProductDetail = getArguments().getBoolean(ARG_FROM_PRODUCT_DETAIL, false);
            boolean fromSearch = getArguments().getBoolean(ARG_FROM_SEARCH, false);
            boolean fromHome = getArguments().getBoolean(ARG_FROM_HOME, false);

            if (fromProductDetail && getActivity() instanceof ProductDetailActivity) {
                // Kembali ke ProductDetail
                ((ProductDetailActivity) getActivity()).showMainContent();
            } else if (getActivity() instanceof Home) {
                if (fromSearch) {
                    // Kembali ke SearchFragment dengan container ID yang benar
                    SearchFragment searchFragment = new SearchFragment();
                    ((Home) getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, searchFragment) // Menggunakan flFragment bukan fragment_container
                        .commit();
                    
                    ((Home) getActivity()).getBottomNavigationView()
                        .setSelectedItemId(R.id.search_nav);
                } else if (fromHome) {
                    // Kembali ke HomeFragment
                    HomeFragment homeFragment = new HomeFragment();
                    ((Home) getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, homeFragment) // Menggunakan flFragment
                        .commit();
                    
                    ((Home) getActivity()).getBottomNavigationView()
                        .setSelectedItemId(R.id.home_nav);
                }
            }
        }
    }
} 


