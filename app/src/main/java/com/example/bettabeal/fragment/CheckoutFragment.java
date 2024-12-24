package com.example.bettabeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bettabeal.R;
import com.example.bettabeal.Shipping;
import com.example.bettabeal.adapter.CheckoutItemAdapter;
import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.Address;
import com.example.bettabeal.model.AddressListResponse;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.model.OrderResponse;
import com.example.bettabeal.utils.CurrencyFormatter;
import com.example.bettabeal.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutFragment extends Fragment {
    private SessionManager sessionManager;
    private TextView tvName, tvAddress, tvPhone, tvTotal;
    private RecyclerView rvCheckoutItems;
    private CheckoutItemAdapter checkoutAdapter;
    private Long selectedAddressId = -1L;
    private WebView webView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_checkout, container, false);
        
        sessionManager = new SessionManager(requireContext());
        initViews(view);
        setupWebView();
        loadMainAddress();
        loadCartItems();
        
        return view;
    }

    private void initViews(View view) {
        webView = view.findViewById(R.id.webView);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvTotal = view.findViewById(R.id.tvTotal);
        rvCheckoutItems = view.findViewById(R.id.rvCheckoutItems);
        
        view.findViewById(R.id.btnChange).setOnClickListener(v -> {
            if (getActivity() != null) {
                int containerId = ((ViewGroup) getActivity().findViewById(android.R.id.content))
                    .getChildAt(0)
                    .getId();
                
                getParentFragmentManager().beginTransaction()
                    .replace(containerId, new Shipping())
                    .addToBackStack(null)
                    .commit();
            }
        });

        view.findViewById(R.id.btnBackCheckout).setOnClickListener(v -> {
            if (webView.getVisibility() == View.VISIBLE) {
                showCheckoutLayout();
            } else {
                getParentFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            if (selectedAddressId == -1L) {
                Toast.makeText(requireContext(), 
                    "Please select delivery address", 
                    Toast.LENGTH_SHORT).show();
                return;
            }
            createOrder();
        });

        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("payment_success")) {
                    showCheckoutLayout();
                    navigateToOrderHistory();
                    return true;
                } else if (url.contains("payment_failed")) {
                    showCheckoutLayout();
                    Toast.makeText(requireContext(), 
                        "Payment failed", 
                        Toast.LENGTH_LONG).show();
                    return true;
                } else if (url.contains("payment_pending")) {
                    showCheckoutLayout();
                    Toast.makeText(requireContext(), 
                        "Payment pending", 
                        Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void createOrder() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("address_id", selectedAddressId);

        RetrofitClient.getInstance().getApiService()
            .createOrder("Bearer " + token, requestBody)
            .enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OrderResponse orderResponse = response.body();
                        if ("success".equals(orderResponse.getStatus())) {
                            String paymentUrl = orderResponse.getData().getPaymentUrl();
                            showPaymentWebView(paymentUrl);
                        } else {
                            Toast.makeText(requireContext(),
                                orderResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showPaymentWebView(String url) {
        for (int i = 0; i < ((ViewGroup) requireView()).getChildCount(); i++) {
            View child = ((ViewGroup) requireView()).getChildAt(i);
            if (child != webView) {
                child.setVisibility(View.GONE);
            }
        }
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private void showCheckoutLayout() {
        for (int i = 0; i < ((ViewGroup) requireView()).getChildCount(); i++) {
            View child = ((ViewGroup) requireView()).getChildAt(i);
            if (child != webView) {
                child.setVisibility(View.VISIBLE);
            }
        }
        webView.setVisibility(View.GONE);
    }

    private void navigateToOrderHistory() {
        requireActivity().getSupportFragmentManager().popBackStack(null, 
            FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // Navigate to order history
    }

    private void loadMainAddress() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        String authHeader = "Bearer " + token;
        RetrofitClient.getInstance().getApiService()
                .getAddresses(authHeader)
                .enqueue(new Callback<AddressListResponse>() {
                    @Override
                    public void onResponse(Call<AddressListResponse> call,
                                           Response<AddressListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Address> addresses = response.body().getData();
                            for (Address address : addresses) {
                                if (address.isMain()) {
                                    selectedAddressId = address.getAddressId();
                                    tvName.setText(address.getName());
                                    tvAddress.setText(address.getAddress());
                                    tvPhone.setText(address.getPhoneNumber());
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressListResponse> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                     "Failed to load address", 
                                     Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCartItems() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) return;

        String authHeader = "Bearer " + token;
        RetrofitClient.getInstance().getApiService()
                .getCart(authHeader)
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, 
                                         Response<CartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartResponse.CartData cartData = response.body().getData();
                            checkoutAdapter = new CheckoutItemAdapter(requireContext(), 
                                                                    cartData.getItems());
                            rvCheckoutItems.setAdapter(checkoutAdapter);
                            tvTotal.setText(CurrencyFormatter.formatRupiah(cartData.getSubtotal()));
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), 
                                     "Failed to load cart items", 
                                     Toast.LENGTH_SHORT).show();
                    }
                });
    }
}