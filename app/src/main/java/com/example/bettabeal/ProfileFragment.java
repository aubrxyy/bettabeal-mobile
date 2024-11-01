package com.example.bettabeal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Mengambil referensi dari ImageButton
        ImageButton imgbtn = view.findViewById(R.id.btnsettings);

        // Mengatur klik listener untuk ImageButton
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Mengganti ProfileFragment dengan SettingsFragment pada container utama
                transaction.replace(R.id.flFragment, new SettingsFragment());
                transaction.addToBackStack(null); // Tambahkan ke back stack agar bisa kembali
                transaction.commit();

                // Menyembunyikan Bottom Navigation
                if (getActivity() != null) {
                    View bottomNav = getActivity().findViewById(R.id.bottomnav); // Sesuaikan ID bottom navigation
                    if (bottomNav != null) {
                        bottomNav.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Menampilkan kembali Bottom Navigation saat kembali ke ProfileFragment
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.main); // Sesuaikan ID bottom navigation
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }
}
