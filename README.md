# BanWave Plugin

BanWave is a Minecraft plugin designed to manage and execute mass player bans ("banwaves") with ease. The plugin provides features to add, remove, and manage banned players, and to initiate or cancel banwaves.

## Features

- Add players to a banwave list.
- Remove players from the banwave list.
- Execute a banwave to ban all players in the list with a delay between bans.
- Reload configuration and player data without restarting the server.

---

## Commands

| Command                  | Description                                          | Permission      |
| ------------------------ | ---------------------------------------------------- | --------------- |
| `/banwave`               | Displays help information about the plugin.          | `banwave.admin` |
| `/banwave add <name>`    | Adds a player to the banwave list.                   | `banwave.admin` |
| `/banwave remove <name>` | Removes a player from the banwave list.              | `banwave.admin` |
| `/banwave start`         | Starts the banwave and bans all players in the list. | `banwave.admin` |
| `/banwave end`           | Cancels an ongoing banwave.                          | `banwave.admin` |
| `/banwave reload`        | Reloads the configuration and player data.           | `banwave.admin` |

---

## Configuration

The `config.yml` file allows you to customize plugin messages and the ban command used during banwaves. Example:

```yaml
ban_command: "ban %player%"
messages:
  help: "&6BanWave Plugin Commands:"
  add_success: "&aPlayer &e%player% &ahas been added to the banwave."
  remove_success: "&aPlayer &e%player% &ahas been removed from the banwave."
  start: "&cThe banwave has started!"
  end: "&aThe banwave has ended."
  unknown_command: "&cUnknown command."
  ban_detected: "&7Banwave banned &e%player%&7."
  banwave_completed: "&aBanwave completed! Removed %removed% players."
  no_players: "&cNo players in the banwave list."
```

---

## Installation

1. Download the latest release of the plugin from [GitHub Releases](https://github.com/Mekvil/BanWave/releases).
2. Place the `BanWave.jar` file into your server's `plugins` folder.
3. Restart your server to generate the default configuration.
4. Customize the `config.yml` file as needed.
5. Reload via command or restart the server to apply changes.

---

## Building from Source

To build this plugin from source, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/Mekvil/BanWave.git
   cd BanWave
   ```

2. Build the plugin using Maven:

   ```bash
   mvn clean install
   ```

3. The compiled JAR file will be located in the `target` directory.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

