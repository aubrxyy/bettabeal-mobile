package com.example.bettabeal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bettabeal.adapter.ArticleListAdapter;
import com.example.bettabeal.api.ApiService;
import com.example.bettabeal.model.Article;
import com.example.bettabeal.model.ArticleItem;
import com.example.bettabeal.model.ArticleResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArticleListAdapter adapter;
    private Retrofit retrofit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        
        setupRetrofit();
        initViews(view);
        setupRecyclerView(view);
        fetchArticles();
        
        return view;
    }

    private void setupRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-bettabeal.dgeo.id")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initViews(View view) {
        ImageButton btnNotification = view.findViewById(R.id.btnNotification);
        ImageButton btnCart = view.findViewById(R.id.imageButton);

        btnNotification.setOnClickListener(v -> {
            // Handle notification click
            // Tambahkan navigasi ke NotificationFragment jika ada
        });

        btnCart.setOnClickListener(v -> {
            // Handle cart click
            // Tambahkan navigasi ke CartFragment jika ada
        });
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rvArticles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ArticleListAdapter(getContext(), article -> {
            // Navigate to article detail
            ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(article);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
    }

    private void fetchArticles() {
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getArticles().enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = new ArrayList<>();
                    if (response.body().getArticles() != null) {
                        for (ArticleItem item : response.body().getArticles()) {
                            articles.add(new Article(
                                item.getArticleId(),
                                item.getTitle(),
                                item.getContent(),
                                item.getImage(),
                                item.getCreatedAt()
                            ));
                        }
                        
                        // Sort by date
                        Collections.sort(articles, (a1, a2) -> {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                Date date1 = sdf.parse(a1.getCreatedAt());
                                Date date2 = sdf.parse(a2.getCreatedAt());
                                return date2.compareTo(date1);
                            } catch (ParseException e) {
                                Log.e("ArticleFragment", "Date parsing error", e);
                                return 0;
                            }
                        });
                        
                        // Update UI on main thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                adapter.setArticles(articles);
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Log.e("ArticleFragment", "Error fetching articles", t);
            }
        });
    }
} 