package com.example.budimadani;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class About extends Fragment {

    private TextView tvGithubUrl;

    public About() {
        // Required empty public constructor for Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Connect this Java Fragment to about.xml
        View view = inflater.inflate(R.layout.about, container, false);

        // Link GitHub URL TextView from XML
        tvGithubUrl = view.findViewById(R.id.tvGithubUrl);

        // Make the GitHub URL look like a real clickable link
        tvGithubUrl.setPaintFlags(tvGithubUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Open GitHub URL in web browser when clicked
        tvGithubUrl.setOnClickListener(v -> openGitHubRepository());

        return view;
    }

    private void openGitHubRepository() {
        // Get the text directly from the TextView to avoid any missing resource R.string issues
        String githubUrl = tvGithubUrl.getText().toString();

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(),
                    "No browser application found.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
