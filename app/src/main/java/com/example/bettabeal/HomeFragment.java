package com.example.bettabeal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;


import com.example.bettabeal.adapter.HomeArticleAdapter;
import com.example.bettabeal.adapter.HomeProductAdapter;
import com.example.bettabeal.api.ApiService;

import com.example.bettabeal.model.Article;
import com.example.bettabeal.model.ArticleItem;
import com.example.bettabeal.model.ArticleResponse;
import com.example.bettabeal.model.CategoryProductResponse;
import com.example.bettabeal.model.Product;
import com.example.bettabeal.model.ProductResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import android.graphics.Rect;
import com.example.bettabeal.fragment.CartFragment;


public class HomeFragment extends Fragment {

    private HomeProductAdapter homeProductAdapter;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ImageButton btnProducts;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        setupProductRecyclerView(view);
        setupArticleRecyclerView(view);
        return view;
    }

    private void initViews(View view) {
        btnProducts = view.findViewById(R.id.imagebtn_products);

        btnProducts.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, searchFragment)
                    .addToBackStack(null)
                    .commit();
        });

        ImageButton btnCart = view.findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                CartFragment cartFragment = CartFragment.newInstance(true, false, false);
                ((Home) getActivity()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, cartFragment)
                    .commit();
            }
        });

        ImageButton btnArticle = view.findViewById(R.id.imagebtn_article);
        btnArticle.setOnClickListener(v -> {
            ArticleFragment articleFragment = new ArticleFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, articleFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupProductRecyclerView(View view) {
        try {
            recyclerView = view.findViewById(R.id.rvProducts);
            if (recyclerView == null) {
                Log.e("HomeFragment", "rvProducts is null");
                return;
            }

            homeProductAdapter = new HomeProductAdapter(getContext());
            recyclerView.setAdapter(homeProductAdapter);
            
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), 
                LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));

            setupRetrofit();
            fetchProducts();

        } catch (Exception e) {
            Log.e("HomeFragment", "Error in setupProductRecyclerView", e);
            e.printStackTrace();
        }
    }

    private void setupRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-bettabeal.dgeo.id")
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
                        List<Product> products = productResponse.getData().getData();
                        homeProductAdapter.setProducts(products);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching products", t);
            }
        });
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
                        homeProductAdapter.setProducts(products);
                    } else {
                        homeProductAdapter.setProducts(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryProductResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching products by category", t);
            }
        });
    }


    private void setupArticleRecyclerView(View view) {
        try {
            RecyclerView rvArticles = view.findViewById(R.id.rvArticles);
            if (rvArticles == null) {
                Log.e("HomeFragment", "rvArticles is null");
                return;
            }

            // Buat adapter dengan click listener
            HomeArticleAdapter homearticleAdapter = new HomeArticleAdapter(getContext(), article -> {
                // Navigate to detail when article is clicked
                ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(article);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, detailFragment)
                        .addToBackStack(null)
                        .commit();
            });
            
            rvArticles.setAdapter(homearticleAdapter);

            // Set layout manager dengan vertical orientation
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvArticles.setLayoutManager(layoutManager);

            // Enable nested scrolling
            rvArticles.setNestedScrollingEnabled(true);

            // Fetch articles
            ApiService apiService = retrofit.create(ApiService.class);
            apiService.getArticles().enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Article> allArticles = new ArrayList<>();
                        if (response.body().getArticles() != null) {
                            for (ArticleItem item : response.body().getArticles()) {
                                allArticles.add(new Article(
                                        item.getArticleId(),
                                        item.getTitle(),
                                        item.getContent(),
                                        item.getImage(),
                                        item.getCreatedAt()
                                ));
                            }

                            // Urutkan artikel berdasarkan tanggal terbaru
                            Collections.sort(allArticles, (a1, a2) -> {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                    Date date1 = sdf.parse(a1.getCreatedAt());
                                    Date date2 = sdf.parse(a2.getCreatedAt());
                                    return date2.compareTo(date1); // Descending order (terbaru dulu)
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            });

                            // Ambil 5 artikel terbaru
                            final List<Article> latestArticles;
                            if (allArticles.size() > 5) {
                                latestArticles = allArticles.subList(0, 5);
                            } else {
                                latestArticles = allArticles;
                            }

                            // Update UI di main thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    homearticleAdapter.setArticles(latestArticles);
                                });
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArticleResponse> call, Throwable t) {
                    Log.e("HomeFragment", "Error fetching articles", t);
                }
            });
        } catch (Exception e) {
            Log.e("HomeFragment", "Error in setupArticleRecyclerView", e);
            e.printStackTrace();
        }
    }

    // Helper method untuk konversi dp ke pixel
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Class untuk mengatur spacing grid
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    // Class untuk spacing horizontal
    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public HorizontalSpaceItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, 
            RecyclerView.State state) {
            outRect.right = spacing;
        }
    }

    private void openCart() {
        if (getActivity() instanceof Home) {
            // Buat instance CartFragment dengan fromHome = true
            CartFragment cartFragment = CartFragment.newInstance(true, false, false);
            
            ((Home) getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, cartFragment)
                .commit();
            
            ((Home) getActivity()).getBottomNavigationView()
                .setSelectedItemId(R.id.plus_nav);
        }
    }
}