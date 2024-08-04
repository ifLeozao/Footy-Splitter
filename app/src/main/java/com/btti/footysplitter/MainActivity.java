package com.btti.footysplitter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btti.teamfutsorteio.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editTextPlayers;
    private CheckBox checkBoxDistributeFirst12;
    private Spinner spinnerTeamSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPlayers = findViewById(R.id.editTextPlayers);
        checkBoxDistributeFirst12 = findViewById(R.id.checkBoxDistributeFirst12);
        spinnerTeamSize = findViewById(R.id.spinnerTeamSize);
        Button buttonGenerateTeams = findViewById(R.id.buttonGenerateTeams);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.team_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeamSize.setAdapter(adapter);

        buttonGenerateTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String players = editTextPlayers.getText().toString().trim();
                if (players.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter at least one player", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean distributeFirst12 = checkBoxDistributeFirst12.isChecked();
                int teamSize = Integer.parseInt(spinnerTeamSize.getSelectedItem().toString());
                Intent intent = new Intent(MainActivity.this, TeamsActivity.class);
                intent.putExtra("playerList", players);
                intent.putExtra("distributeFirst12", distributeFirst12);
                intent.putExtra("teamSize", teamSize);
                startActivity(intent);
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
}
