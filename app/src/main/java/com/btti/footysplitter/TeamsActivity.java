package com.btti.footysplitter;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btti.teamfutsorteio.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {
    private LinearLayout teamsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        teamsContainer = findViewById(R.id.teamsContainer);
        Button buttonRegenerateTeams = findViewById(R.id.buttonRegenerateTeams);
        Button buttonBackToInput = findViewById(R.id.buttonBackToInput);

        String playerList = getIntent().getStringExtra("playerList");
        boolean distributeFirst12 = getIntent().getBooleanExtra("distributeFirst12", false);
        int teamSize = getIntent().getIntExtra("teamSize", 6);
        generateTeams(playerList, distributeFirst12, teamSize);

        buttonRegenerateTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateTeams(playerList, distributeFirst12, teamSize);
                Toast.makeText(TeamsActivity.this, "Teams regenerated", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBackToInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView developerName = findViewById(R.id.developerName);
        developerName.setText(getString(R.string.developer_name));

        TextView appVersion = findViewById(R.id.appVersion);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText(getString(R.string.app_version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void generateTeams(String playerList, boolean distributeFirst12, int teamSize) {
        teamsContainer.removeAllViews();

        List<String> players = new ArrayList<>();
        Collections.addAll(players, playerList.split("\\n"));

        List<List<String>> teams = new ArrayList<>();

        if (distributeFirst12) {
            List<String> first12Players = players.subList(0, Math.min(players.size(), 12));
            Collections.shuffle(first12Players);
            List<String> remainingPlayers = players.subList(Math.min(players.size(), 12), players.size());

            List<String> team1 = new ArrayList<>();
            List<String> team2 = new ArrayList<>();

            for (int i = 0; i < first12Players.size(); i++) {
                if (i % 2 == 0) {
                    team1.add(first12Players.get(i));
                } else {
                    team2.add(first12Players.get(i));
                }
            }

            teams.add(team1);
            teams.add(team2);

            Collections.shuffle(remainingPlayers);

            while (remainingPlayers.size() > 0) {
                List<String> team = new ArrayList<>();
                for (int i = 0; i < teamSize && remainingPlayers.size() > 0; i++) {
                    team.add(remainingPlayers.remove(0));
                }
                teams.add(team);
            }
        } else {
            Collections.shuffle(players);

            while (players.size() > 0) {
                List<String> team = new ArrayList<>();
                for (int i = 0; i < teamSize && players.size() > 0; i++) {
                    team.add(players.remove(0));
                }
                teams.add(team);
            }
        }

        // Obter as cores de fundo e texto dos times
        int teamBackgroundColor = getResources().getColor(R.color.team_background);
        int teamTextColor = getResources().getColor(R.color.team_text);

        for (int i = 0; i < teams.size(); i++) {
            LinearLayout teamLayout = new LinearLayout(this);
            teamLayout.setOrientation(LinearLayout.VERTICAL);
            teamLayout.setBackgroundColor(teamBackgroundColor);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 16);
            teamLayout.setLayoutParams(layoutParams);

            TextView teamHeader = new TextView(this);
            teamHeader.setText("Time " + (i + 1) + " (" + teams.get(i).size() + " jogadores):");
            teamHeader.setTextColor(teamTextColor);
            teamLayout.addView(teamHeader);

            for (String player : teams.get(i)) {
                TextView playerView = new TextView(this);
                playerView.setText(player);
                playerView.setTextColor(teamTextColor);
                teamLayout.addView(playerView);
            }

            teamsContainer.addView(teamLayout);
        }
    }
}