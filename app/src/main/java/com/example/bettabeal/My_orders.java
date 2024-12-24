package com.example.bettabeal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.bettabeal.adapter.OrdersAdapter;
import com.example.bettabeal.model.OrdersResponse;
import com.example.bettabeal.utils.SessionManager;
import com.example.bettabeal.api.RetrofitClient;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import org.json.JSONObject;
import android.util.Log;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class My_orders extends Fragment {
    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private SessionManager sessionManager;
    private List<OrdersResponse.Order> originalOrders;

    public My_orders() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        // Inisialisasi
        sessionManager = new SessionManager(requireContext());
        initViews(view);
        loadOrders();

        // Referensi untuk tombol back
        ImageButton btnbackmyorder = view.findViewById(R.id.btnbackmyorder);

        // Set OnClickListener untuk tombol back
        btnbackmyorder.setOnClickListener(v -> navigateToProfile());

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrdersAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Tambahkan item decoration untuk spacing antar item
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, 
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = spacingInPixels;
            }
        });
    }

    private void loadOrders() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApiService()
            .getOrders("Bearer " + token, 1)
            .enqueue(new Callback<OrdersResponse>() {
                @Override
                public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OrdersResponse ordersResponse = response.body();
                        if (ordersResponse.getData() != null && 
                            ordersResponse.getData().getOrders() != null) {
                            
                            // Debug log untuk setiap order
                            for (OrdersResponse.Order order : ordersResponse.getData().getOrders()) {
                                if (!order.getItems().isEmpty()) {
                                    OrdersResponse.OrderItem firstItem = order.getItems().get(0);
                                    Log.d("API Response", "Order ID: " + order.getOrderId());
                                    Log.d("API Response", "Product: " + firstItem.getProductName());
                                    Log.d("API Response", "Image URL: " + firstItem.getImageUrl());
                                }
                            }
                            
                            originalOrders = ordersResponse.getData().getOrders();
                            adapter.setOrders(originalOrders);
                        } else {
                            Toast.makeText(requireContext(), 
                                "No orders found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(errorBody);
                                String message = jObjError.getString("message");
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), 
                                "Failed to load orders", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrdersResponse> call, Throwable t) {
                    Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Network error: " + t.getMessage());
                }
            });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoading() {
        // Implement loading indicator if needed
    }

    private void hideLoading() {
        // Hide loading indicator if needed
    }

    // Fungsi untuk pindah ke ProfileFragment
    private void navigateToProfile() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, new ProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        showBottomNavigation();
    }

    // Menampilkan kembali Bottom Navigation
    private void showBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }

    private void filterOrders(String shippingStatus) {
        if (originalOrders == null) return;
        
        List<OrdersResponse.Order> filteredList;
        if (shippingStatus.equals("all")) {
            filteredList = new ArrayList<>(originalOrders);
        } else {
            filteredList = originalOrders.stream()
                .filter(order -> shippingStatus.equalsIgnoreCase(order.getShippingStatus()))
                .collect(Collectors.toList());
        }
        
        adapter.setOrders(filteredList);
    }
}
