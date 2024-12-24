package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
// Pastikan import R yang benar
import com.example.bettabeal.R;  // Sesuaikan dengan package name Anda
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bettabeal.api.ApiService;
import com.example.bettabeal.model.ProductDetailResponse;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.model.AddToCartRequest;
import com.example.bettabeal.utils.SessionManager;
import com.example.bettabeal.fragment.CartFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import android.content.SharedPreferences;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageButton backButton, cartButton;
    private TextView productName, productPrice, productDescription, categoryName, stockText;
    private Button addToCartButton;
    private LinearLayout imageContainer;
    private Retrofit retrofit;
    private long currentProductId = -1;
    private SessionManager sessionManager;
    private View mainContent;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sessionManager = new SessionManager(this);

        mainContent = findViewById(R.id.main_content);
        fragmentContainer = findViewById(R.id.fragment_container);

        initViews();
        initRetrofit();
        setupListeners();

        long productId = getIntent().getLongExtra("product_id", -1);
        if (productId != -1) {
            fetchProductDetail(productId);
        }
    }

    private void initViews() {
        // Header
        backButton = findViewById(R.id.backButton);
        cartButton = findViewById(R.id.cartButton);
        
        // Product Details
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        categoryName = findViewById(R.id.categoryName);
        stockText = findViewById(R.id.stockText);
        addToCartButton = findViewById(R.id.addToCartButton);
        
        // Image container
        imageContainer = findViewById(R.id.imageContainer);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        
        cartButton.setOnClickListener(v -> openCart());
        
        addToCartButton.setOnClickListener(v -> {
            if (currentProductId != -1) {
                addToCart(currentProductId);
            }
        });
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body());
                
                String token = sessionManager.getToken();
                if (!token.isEmpty()) {
                    String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
                    Log.d("Retrofit", "Using auth header: " + authHeader);
                    requestBuilder.header("Authorization", authHeader);
                }
                
                Request request = requestBuilder.build();
                return chain.proceed(request);
            })
            .build();

        retrofit = new Retrofit.Builder()
            .baseUrl("https://api-bettabeal.dgeo.id/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    private String getUserToken() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getString("user_token", "");
    }

    private void fetchProductDetail(long productId) {
        currentProductId = productId;
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getProductDetail(productId).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetailResponse.ProductDetail product = response.body().getData();
                    updateUI(product);
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, 
                    "Error loading product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(ProductDetailResponse.ProductDetail product) {
        if (product != null) {
            productName.setText(product.getProduct_name());
            productPrice.setText(String.format("Rp %.0f", product.getPrice()));
            productDescription.setText(product.getDescription());
            
            if (product.getCategory() != null) {
                categoryName.setText("Category: " + product.getCategory().getCategory_name());
            }
            
            stockText.setText("Stock: " + product.getStock_quantity());

            // Clear existing images
            imageContainer.removeAllViews();

            // Add main image
            if (product.getMain_image() != null) {
                addImageToScroll(product.getMain_image().getImage_url());
            }

            // Add additional images if any
            if (product.getAdditional_images() != null) {
                for (ProductDetailResponse.ProductImage image : product.getAdditional_images()) {
                    addImageToScroll(image.getImage_url());
                }
            }
        }
    }

    private void addImageToScroll(String imageUrl) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.weight = 1;
        imageView.setLayoutParams(params);
        imageView.setPadding(15, 15, 15, 15);
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.product_contoh)
            .error(R.drawable.product_contoh)
            .into(imageView);

        imageContainer.addView(imageView);
    }

    private void addToCart(long productId) {
        String token = sessionManager.getToken();
        Log.d("AddToCart", "Raw token: " + token);
        
        if (token.isEmpty()) {
            Log.e("AddToCart", "Token is empty, redirecting to login");
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Sign_in.class));
            finish();
            return;
        }

        // Show loading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding to cart...");
        progressDialog.show();

        AddToCartRequest request = new AddToCartRequest(productId);
        
        // Pastikan format Bearer token benar
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        Log.d("AddToCart", "Authorization header: " + authHeader);

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.addToCart(authHeader, request)
            .enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    progressDialog.dismiss();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        CartResponse cartResponse = response.body();
                        String message = cartResponse.getMessage();
                        if (message == null || message.isEmpty()) {
                            message = "Successfully added to cart";
                        }
                        Log.d("AddToCart", "Success response: " + message);
                        Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("AddToCart", "Error response: " + errorBody);
                            Log.e("AddToCart", "Response code: " + response.code());
                            Toast.makeText(ProductDetailActivity.this, 
                                "Failed to add to cart: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("AddToCart", "Error parsing error response", e);
                            Toast.makeText(ProductDetailActivity.this, 
                                "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("AddToCart", "Network error", t);
                    Toast.makeText(ProductDetailActivity.this, 
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    // Optional: Method untuk update badge cart
    private void updateCartBadge() {
        // Implementasi update badge cart jika ada
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragment_container);
            
        if (currentFragment instanceof CartFragment) {
            showMainContent();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void openCart() {
        if (mainContent != null) mainContent.setVisibility(View.GONE);
        if (fragmentContainer != null) fragmentContainer.setVisibility(View.VISIBLE);
        
        CartFragment cartFragment = CartFragment.newInstance(false, false, true);
        
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, cartFragment)
            .addToBackStack(null)
            .commit();
    }

    // Method baru untuk menampilkan konten utama
    public void showMainContent() {
        if (mainContent != null) mainContent.setVisibility(View.VISIBLE);
        if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
        
        // Pop fragment dari back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }
} 