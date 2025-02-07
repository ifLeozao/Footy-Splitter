package com.btti.footysplitter;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    private EditText editTextPlayers;
    private CheckBox checkBoxDistributeFirst;
    private Spinner spinnerTeamSize;

    private static final String PREFS_NAME = "FootySplitterPrefs";
    private static final String KEY_PLAYERS_LIST = "players_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPlayers = findViewById(R.id.editTextPlayers);
        checkBoxDistributeFirst = findViewById(R.id.checkBoxDistributeFirst12);
        spinnerTeamSize = findViewById(R.id.spinnerTeamSize);
        Button buttonGenerateTeams = findViewById(R.id.buttonGenerateTeams);

        loadPlayersList();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.team_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeamSize.setAdapter(adapter);

        buttonGenerateTeams.setOnClickListener(v -> {
            String players = editTextPlayers.getText().toString().trim();
            if (players.isEmpty()) {
                Toast.makeText(MainActivity.this, "Digite pelo menos um jogador!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean distributeFirst = checkBoxDistributeFirst.isChecked();
            int teamSize = getSelectedTeamSize();

            if (teamSize != 4 && teamSize != 5 && teamSize != 6) {
                Toast.makeText(MainActivity.this, "Selecione um tamanho válido para o time!", Toast.LENGTH_SHORT).show();
                return;
            }

            savePlayersList(players);

            Intent intent = new Intent(MainActivity.this, TeamsActivity.class);
            intent.putExtra("playerList", players);
            intent.putExtra("distributeFirst", distributeFirst);
            intent.putExtra("teamSize", teamSize);
            startActivity(intent);
        });

        TextView developerName = findViewById(R.id.developerName);
        developerName.setText(getString(R.string.developer_name));

        TextView appVersion = findViewById(R.id.appVersion);
        setAppVersion(appVersion);
    }

    /**
     * Obtém o tamanho do time do Spinner e trata possíveis erros.
     */
    private int getSelectedTeamSize() {
        try {
            String selectedValue = spinnerTeamSize.getSelectedItem().toString();
            return Integer.parseInt(selectedValue);
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    /**
     * Obtém a versão do aplicativo e exibe no TextView correspondente.
     */
    private void setAppVersion(TextView appVersion) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText(getString(R.string.app_version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            appVersion.setText("Versão não encontrada");
            e.printStackTrace();
        }
    }

    /**
     * Salva a lista de jogadores digitada no EditText para manter após sair do app.
     */
    private void savePlayersList(String players) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAYERS_LIST, players);
        editor.apply();
    }

    /**
     * Carrega a lista de jogadores salva anteriormente e define no EditText.
     */
    private void loadPlayersList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedPlayers = sharedPreferences.getString(KEY_PLAYERS_LIST, "");
        editTextPlayers.setText(savedPlayers);
    }
}
