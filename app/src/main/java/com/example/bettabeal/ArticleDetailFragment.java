package com.example.bettabeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import com.example.bettabeal.model.Article;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleDetailFragment extends Fragment {
    private static final String ARG_ARTICLE = "article";
    private Article article;

    public static ArticleDetailFragment newInstance(Article article) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = getArguments().getParcelable(ARG_ARTICLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);

        initViews(view);
        displayArticle(view);

        return view;
    }

    private void initViews(View view) {
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void displayArticle(View view) {
        if (article == null) return;

        ImageView imvArticle = view.findViewById(R.id.imvArticle);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvContent = view.findViewById(R.id.tvContent);

        // Set title
        tvTitle.setText(article.getTitle());

        // Set content
        tvContent.setText(article.getContent());

        // Format and set date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            Date date = inputFormat.parse(article.getCreatedAt());
            tvDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            tvDate.setText(article.getCreatedAt());
        }

        // Load image
        if (article.getImage() != null && !article.getImage().isEmpty()) {
            String imageUrl = "https://api-bettabeal.dgeo.id" + article.getImage();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(imvArticle);
        } else {
            imvArticle.setImageResource(R.drawable.placeholder_image);
        }
    }
} 