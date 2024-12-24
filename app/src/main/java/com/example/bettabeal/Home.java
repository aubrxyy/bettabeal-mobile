package com.example.bettabeal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final HomeFragment homeFragment = new HomeFragment();
    private final SearchFragment searchFragment = new SearchFragment();
    private final ArticleFragment articleFragment= new ArticleFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    // Membuat Map untuk menyimpan itemId dan fragment yang sesuai
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set null untuk itemIconTint agar warna asli icon drawable digunakan
        bottomNavigationView.setItemIconTintList(null);

        // Mengisi Map dengan pasangan itemId dan fragment yang terkait
        fragmentMap.put(R.id.home_nav, homeFragment);
        fragmentMap.put(R.id.search_nav, searchFragment);
        fragmentMap.put(R.id.article_nav, articleFragment);
        fragmentMap.put(R.id.prof_nav, profileFragment);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        // Handle window insets untuk padding layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Tambahkan getter untuk bottomNavigationView
    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}
