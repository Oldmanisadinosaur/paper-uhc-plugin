# Paper UHC Plugin

A comprehensive Ultra Hardcore (UHC) plugin for Paper Minecraft servers (1.21.1+).

## Features

- Player management (join/leave)
- Game state management (start/stop)
- Death tracking
- World border support
- Configurable settings
- Permission-based commands
- Real-time game information

## Building

This project uses Maven. To build:

```bash
mvn clean package
```

The compiled JAR will be located in the `target/` directory.

## Installation

1. Build the plugin using Maven
2. Copy the generated JAR file to your Paper server's `plugins/` directory
3. Restart your server
4. Configure the plugin using `plugins/PaperUHC/config.yml`

## Commands

### Player Commands
- `/uhc join` - Join the UHC game
- `/uhc leave` - Leave the UHC game
- `/uhc info` - View game information

### Admin Commands
- `/uhc start` - Start the game
- `/uhc stop` - Stop the game

## Permissions

- `uhc.join` - Allow players to join UHC games (default: true)
- `uhc.leave` - Allow players to leave UHC games (default: true)
- `uhc.admin` - Admin permission for UHC commands (default: op)
- `uhc.start` - Permission to start UHC games (default: op)

## Configuration

Edit `plugins/PaperUHC/config.yml` to customize:

- `max-players` - Maximum players per game
- `world-border-size` - Size of the world border
- `spawn-radius` - Radius where PvP is disabled
- `pvp-enabled` - Enable/disable PvP
- `heal-amount` - Health restored per golden apple

## Requirements

- Java 21+
- Paper 1.21.1+
- Maven 3.6+

## License

MIT License