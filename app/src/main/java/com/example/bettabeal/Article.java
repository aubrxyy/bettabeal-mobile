package com.example.bettabeal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.FragmentTransaction;

import com.example.bettabeal.fragment.CartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Article extends Fragment {

    public Article() {
        // Konstruktor kosong yang diperlukan
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk fragment Article
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        // Temukan ImageButton yang akan digunakan untuk berpindah ke Cart
        ImageButton navigateToCartButton = view.findViewById(R.id.imageButton); // Pastikan ID button ini sesuai

        // Set OnClickListener untuk tombol imageButton
        navigateToCartButton.setOnClickListener(v -> {
            // Ganti fragment dengan CartFragment
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, new CartFragment()); // Ganti dengan Cart Fragment
            transaction.addToBackStack(null);
            transaction.commit();

            // Sembunyikan BottomNavigationView saat ke CartFragment
            hideBottomNavigation();
        });

        return view;
    }

    // Fungsi untuk menyembunyikan BottomNavigationView saat berpindah ke CartFragment
    private void hideBottomNavigation() {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE); // Sembunyikan Bottom Navigation
            }
        }
    }
}
