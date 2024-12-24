package com.example.bettabeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.bettabeal.R;
import com.example.bettabeal.model.Article;

import java.util.ArrayList;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeArticleAdapter extends RecyclerView.Adapter<HomeArticleAdapter.ArticleViewHolder> {
    private List<Article> articles = new ArrayList<>();
    private Context context;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public HomeArticleAdapter(Context context, OnArticleClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_home_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ImageView articleImage;
        private TextView articleTitle;
        private TextView tvDescription;
        private TextView tvDate;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.imvArticle);
            articleTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);

            // Set click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onArticleClick(articles.get(position));
                }
            });
        }

        void bind(Article article) {
            // Set title
            articleTitle.setText(article.getTitle());

            // Set description
            if (article.getContent() != null) {
                String content = article.getContent();
                if (content.length() > 100) {
                    content = content.substring(0, 97) + "...";
                }
                tvDescription.setText(content);
            }

            // Set date
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
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(articleImage);
            } else {
                articleImage.setImageResource(R.drawable.placeholder_image);
            }
        }
    }
} 