package com.example.bettabeal;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.ImageButton;
import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import com.example.bettabeal.adapter.ProductAdapter;
import com.example.bettabeal.model.Product;
import com.example.bettabeal.model.Category;
import com.example.bettabeal.model.ProductResponse;
import com.example.bettabeal.model.CategoryResponse;
import com.example.bettabeal.model.CategoryProductResponse;
import com.example.bettabeal.api.ApiService;
import com.example.bettabeal.utils.GridSpacingItemDecoration;
import java.util.ArrayList;
import java.util.List;
import com.example.bettabeal.fragment.CartFragment;

public class SearchFragment extends Fragment {
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private LinearLayout categoryContainer;
    private Retrofit retrofit;
    private int selectedCategoryId = -1;
    private View rootView;
    private Context context;
    private androidx.appcompat.widget.SearchView searchView;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initRetrofit();
        setupViews();
        setupProductRecyclerView(rootView);
        setupSearchView();
        fetchCategories();
        fetchProducts();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        fetchCategories();
    }

    private void setupProductRecyclerView(View view) {
        rvProducts = view.findViewById(R.id.rvProducts);
        if (rvProducts == null) return;

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(getContext(), true);
        rvProducts.setAdapter(productAdapter);

        int spacingInPixels = dpToPx(8);
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    private void setupViews() {
        categoryContainer = rootView.findViewById(R.id.categoryButtonContainer);
        ImageButton cartButton = rootView.findViewById(R.id.imageButton_cart_search);
        cartButton.setOnClickListener(v -> openCart());
        
        searchView = rootView.findViewById(R.id.searchView);
    }

    private void setupSearchView() {
        if (searchView != null) {
            // Mengatur warna text dan hint
            EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            if (searchEditText != null) {
                searchEditText.setTextColor(Color.BLACK); // Warna untuk text yang diketik
                searchEditText.setHintTextColor(Color.parseColor("#808080")); // Warna abu-abu untuk hint
                searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            // Mengatur warna icon search
            ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            if (searchIcon != null) {
                searchIcon.setColorFilter(Color.parseColor("#808080")); // Warna abu-abu untuk icon
            }

            // Mengatur warna icon close
            ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            if (closeIcon != null) {
                closeIcon.setColorFilter(Color.parseColor("#808080")); // Warna abu-abu untuk icon close
            }

            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterProducts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterProducts(newText);
                    return true;
                }
            });
        }
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-bettabeal.dgeo.id/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void fetchProducts() {
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    if (productResponse.getData() != null && 
                        productResponse.getData().getData() != null) {
                        allProducts = productResponse.getData().getData();
                        productAdapter.setProducts(allProducts);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("SearchFragment", "Error fetching products", t);
            }
        });
    }

    private void fetchCategories() {
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    if (categories != null) {
                        setupCategoryButtons(categories);
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("SearchFragment", "Error fetching categories", t);
            }
        });
    }

    private void setupCategoryButtons(List<Category> categories) {
        if (!isAdded() || categoryContainer == null) {
            return;
        }

        requireActivity().runOnUiThread(() -> {
            categoryContainer.removeAllViews();
            
            Button allButton = createCategoryButton("All", -1, true);
            if (allButton != null) {
                categoryContainer.addView(allButton);
                allButton.setOnClickListener(v -> handleCategorySelection(allButton, null));
            }

            for (Category category : categories) {
                Button button = createCategoryButton(
                    category.getCategoryName(), 
                    category.getCategoryId(),
                    false
                );
                
                if (button != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dpToPx(105),
                        dpToPx(38)
                    );
                    params.setMargins(0, 0, dpToPx(10), 0);
                    button.setLayoutParams(params);
                    
                    button.setOnClickListener(v -> handleCategorySelection(button, category));
                    categoryContainer.addView(button);
                }
            }
        });
    }

    private void handleCategorySelection(Button selectedButton, Category category) {
        resetButtonsAppearance();
        updateButtonAppearance(selectedButton, true);
        
        if (category == null) {
            if (searchView != null) {
                searchView.setQuery("", false);
                searchView.clearFocus();
            }
            fetchProducts();
        } else {
            fetchProductsByCategory(category.getCategoryId());
        }
    }

    private void resetButtonsAppearance() {
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            if (child instanceof Button) {
                updateButtonAppearance((Button) child, false);
            }
        }
    }

    private Button createCategoryButton(String text, int categoryId, boolean isSelected) {
        if (!isAdded()) {
            return null;
        }

        try {
            Context context = getContext();
            if (context == null) {
                return null;
            }

            Button button = new Button(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(105),
                dpToPx(38)
            );
            button.setLayoutParams(params);
            button.setText(text);
            button.setTextSize(12);
            updateButtonAppearance(button, isSelected);
            return button;
        } catch (Exception e) {
            Log.e("SearchFragment", "Error creating button", e);
            return null;
        }
    }

    private void updateButtonAppearance(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.biru_tua)));
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            button.setTextColor(getResources().getColor(R.color.biru_tua));
        }
    }

    private void fetchProductsByCategory(int categoryId) {
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getProductsByCategory(categoryId).enqueue(new Callback<CategoryProductResponse>() {
            @Override
            public void onResponse(Call<CategoryProductResponse> call, Response<CategoryProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryProductResponse categoryResponse = response.body();
                    if (categoryResponse.getData() != null && 
                        categoryResponse.getData().getProducts() != null) {
                        List<Product> products = categoryResponse.getData().getProducts().getData();
                        productAdapter.setProducts(products);
                    } else {
                        productAdapter.setProducts(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryProductResponse> call, Throwable t) {
                Log.e("SearchFragment", "Error fetching products by category", t);
            }
        });
    }

    private int dpToPx(int dp) {
        if (!isAdded()) {
            return dp;
        }
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void openCart() {
        if (getActivity() instanceof Home) {
            CartFragment cartFragment = CartFragment.newInstance(false, true, false);
            ((Home) getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, cartFragment)
                .commit();
            
            ((Home) getActivity()).getBottomNavigationView()
                .setSelectedItemId(R.id.plus_nav);
        }
    }

    private void filterProducts(String query) {
        if (query == null || query.isEmpty()) {
            productAdapter.setProducts(allProducts);
            return;
        }

        // Ubah query menjadi lowercase untuk pencarian yang tidak case-sensitive
        String lowercaseQuery = query.toLowerCase().trim();
        List<Product> filteredList = new ArrayList<>();

        for (Product product : allProducts) {
            // Cek hanya nama produk saja
            String productName = product.getProduct_name().toLowerCase();
            
            // Jika query ditemukan di nama produk
            if (productName.contains(lowercaseQuery)) {
                filteredList.add(product);
            }
        }

        // Update RecyclerView dengan hasil pencarian
        productAdapter.setProducts(filteredList);

        // Opsional: Log untuk debugging
        Log.d("SearchFragment", "Query: " + query + ", Found: " + filteredList.size() + " items");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        rootView = null;
        categoryContainer = null;
    }
}
