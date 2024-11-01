package com.example.bettabeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the correct layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Mengambil referensi dari ImageButton
        ImageButton btnBack = view.findViewById(R.id.btnbacksettings);

        // Mengatur klik listener untuk tombol kembali
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke fragment sebelumnya (ProfileFragment) di back stack
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();

                    // Menampilkan kembali Bottom Navigation
                    if (getActivity() != null) {
                        View bottomNav = getActivity().findViewById(R.id.bottomnav); // Sesuaikan dengan ID BottomNavigationView
                        if (bottomNav != null) {
                            bottomNav.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sembunyikan Bottom Navigation saat SettingsFragment aktif
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottomnav); // Sesuaikan ID bottom navigation
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }
}
