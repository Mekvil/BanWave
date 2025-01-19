package me.mekvil.Banwave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Banwave extends JavaPlugin {
    private PlayerManager playerManager;
    private Map<String, String> messages;
    private boolean isBanwaveRunning = false;
    private List<Integer> taskIds = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("BanWave has been Enabled!");
        saveDefaultConfig();
        playerManager = new PlayerManager();
        loadMessages();
        this.getCommand("banwave").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("BanWave has been Disabled!");
    }

    private void loadMessages() {
        messages = new HashMap<>();
        messages.put("help", getMessageOrDefault("messages.help", "&7Available commands:"));
        messages.put("add_success", getMessageOrDefault("messages.add_success", "&aPlayer %player% has been added to the banwave."));
        messages.put("remove_success", getMessageOrDefault("messages.remove_success", "&aPlayer %player% has been removed from the banwave."));
        messages.put("start", getMessageOrDefault("messages.start", "&eBanwave has started."));
        messages.put("end", getMessageOrDefault("messages.end", "&eBanwave has ended."));
        messages.put("unknown_command", getMessageOrDefault("messages.unknown_command", "&cUnknown command. Type /banwave for help."));
        messages.put("ban_detected", getMessageOrDefault("messages.ban_detected", "&7Banwave banned &e%player%&7."));
        messages.put("banwave_completed", getMessageOrDefault("messages.banwave_completed", "&aBanwave completed. &7Removed %removed% players."));
        messages.put("add_command", getMessageOrDefault("messages.add_command", "&e/banwave add <player> - Add player to banwave"));
        messages.put("remove_command", getMessageOrDefault("messages.remove_command", "&e/banwave remove <player> - Remove player from banwave"));
        messages.put("start_command", getMessageOrDefault("messages.start_command", "&e/banwave start - Start the banwave"));
        messages.put("end_command", getMessageOrDefault("messages.end_command", "&e/banwave end - End the banwave"));
        messages.put("no_players", getMessageOrDefault("messages.no_players", "&cNo players to ban in the banwave."));
    }

    private String getMessageOrDefault(String path, String defaultMessage) {
        String message = getConfig().getString(path);
        if (message == null) {
            return ChatColor.translateAlternateColorCodes('&', defaultMessage);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void startBanwave(CommandSender sender) {
        try {
            Set<String> bannedPlayers = playerManager.getBannedPlayers();
            String banCommand = getConfig().getString("ban_command");

            if (banCommand == null) {
                sender.sendMessage(ChatColor.RED + "Ban command is not defined in config.yml.");
                return;
            }

            if (bannedPlayers.isEmpty()) {
                sender.sendMessage(messages.get("no_players"));
                return;
            }

            Bukkit.broadcastMessage(messages.get("start"));

            isBanwaveRunning = true;
            int delay = 0;
            final int[] count = {0};

            for (String player : bannedPlayers) {
                String commandToExecute = banCommand.replace("%player%", player);
                int taskId = Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (isBanwaveRunning) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                        count[0]++;
                        String banMessage = messages.get("ban_detected")
                                .replace("%count%", String.valueOf(count[0]))
                                .replace("%player%", player);
                        Bukkit.broadcastMessage(banMessage);
                        playerManager.removePlayer(player);
                    }
                }, delay).getTaskId();
                taskIds.add(taskId);
                delay += 20;
            }

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (isBanwaveRunning) {
                    Bukkit.broadcastMessage(messages.get("banwave_completed").replace("%removed%", String.valueOf(count[0])));
                }
                isBanwaveRunning = false;
            }, delay);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while starting the banwave: " + e.getMessage());
            Bukkit.getLogger().severe("Error during banwave: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("banwave")) {
            if (!sender.hasPermission("banwave.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(messages.get("help"));
                sender.sendMessage(messages.get("add_command"));
                sender.sendMessage(messages.get("remove_command"));
                sender.sendMessage(messages.get("start_command"));
                sender.sendMessage(messages.get("end_command"));
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage("Please specify a player to add.");
                    } else {
                        String playerToAdd = args[1];
                        playerManager.addPlayer(playerToAdd);
                        sender.sendMessage(messages.get("add_success").replace("%player%", playerToAdd));
                    }
                    break;
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage("Please specify a player to remove.");
                    } else {
                        String playerToRemove = args[1];
                        playerManager.removePlayer(playerToRemove);
                        sender.sendMessage(messages.get("remove_success").replace("%player%", playerToRemove));
                    }
                    break;
                case "start":
                    startBanwave(sender);
                    break;
                case "end":
                    if (!isBanwaveRunning) {
                        sender.sendMessage(ChatColor.RED + "Banwave is not currently running.");
                    } else {
                        isBanwaveRunning = false;
                        for (int taskId : taskIds) {
                            Bukkit.getScheduler().cancelTask(taskId);
                        }
                        taskIds.clear();
                        sender.sendMessage(ChatColor.YELLOW + "Banwave has been ended.");
                    }
                    break;
                case "reload":
                    reloadConfig();
                    loadMessages();
                    playerManager.reloadPlayers();
                    sender.sendMessage(ChatColor.GREEN + "Banwave configuration and players reloaded.");
                    break;
                default:
                    sender.sendMessage(messages.get("unknown_command"));
                    break;
            }
            return true;
        }
        return false;
    }
}
