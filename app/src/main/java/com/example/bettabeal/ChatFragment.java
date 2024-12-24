package com.example.bettabeal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.FragmentTransaction;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        // Find the back button by ID
        ImageButton backButton = view.findViewById(R.id.imageButton);

        // Set an OnClickListener to handle button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace current fragment with HomeFragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, new HomeFragment()); // Use R.id.flFragment as the container ID
                transaction.addToBackStack(null); // Optionally add to back stack if you want to navigate back
                transaction.commit();

                // Ensure Bottom Navigation is shown when going back to HomeFragment
                showBottomNavigation();
            }
        });

        // Hide bottom navigation when entering this fragment
        hideBottomNavigation();

        return view;
    }

    // Hide the Bottom Navigation
    private void hideBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE); // Hide bottom navigation
            }
        }
    }

    // Show the Bottom Navigation
    private void showBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE); // Show bottom navigation
            }
        }
    }
}
