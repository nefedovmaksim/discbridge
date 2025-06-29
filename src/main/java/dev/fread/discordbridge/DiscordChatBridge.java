package dev.fread.discordbridge;

import dev.fread.discordbridge.command.DChatCommand;
import dev.fread.discordbridge.config.ConfigManager;
import dev.fread.discordbridge.discord.DiscordBot;
import dev.fread.discordbridge.listener.ChatListener;
import dev.fread.discordbridge.listener.JoinQuitListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordChatBridge extends JavaPlugin {

    private ConfigManager configManager;
    private DiscordBot    discordBot;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.load();

        try {
            discordBot = new DiscordBot(this,
                    getConfig().getString("discord.token"),
                    getConfig().getString("discord.channelId"));
            getLogger().info("Discord-бот запущен.");
        } catch (Exception ex) {
            getLogger().severe("Не удалось инициализировать Discord-бот: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this),      this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this),  this);

        getCommand("dchat").setExecutor(new DChatCommand(this));
        getCommand("dchat").setTabCompleter((s, c, l, a) -> java.util.Collections.singletonList("reload"));

        getLogger().info(ChatColor.GREEN + "DiscordChatBridge активирован!");
    }

    @Override
    public void onDisable() {
        if (discordBot != null) discordBot.shutdown();
        getLogger().info("DiscordChatBridge выключен.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public DiscordBot    getDiscordBot()    { return discordBot;    }
}
