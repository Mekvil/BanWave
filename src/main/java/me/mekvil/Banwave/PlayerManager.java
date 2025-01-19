package me.mekvil.Banwave;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class PlayerManager {
    private static final String FILE_PATH = "plugins/BanWave/players.json";
    private final Set<String> bannedPlayers;
    private final Gson gson;

    public PlayerManager() {
        gson = new Gson();
        bannedPlayers = new HashSet<>();
        loadPlayers();
    }

    public void addPlayer(String playerName) {
        bannedPlayers.add(playerName);
        savePlayers();
    }

    public void removePlayer(String playerName) {
        bannedPlayers.remove(playerName);
        savePlayers();
    }

    public Set<String> getBannedPlayers() {
        return bannedPlayers;
    }

    public void reloadPlayers() {
        bannedPlayers.clear();
        loadPlayers();
    }

    private void loadPlayers() {
        try {
            File dir = new File("plugins/BanWave");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            try (Reader reader = new FileReader(file)) {
                Set<String> players = gson.fromJson(reader, new TypeToken<Set<String>>() {}.getType());
                if (players != null) {
                    bannedPlayers.addAll(players);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load banned players: " + e.getMessage());
        }
    }

    public void savePlayers() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(bannedPlayers, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save banned players: " + e.getMessage());
        }
    }
}
