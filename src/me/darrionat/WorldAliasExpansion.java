package me.darrionat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.manager.LocalExpansionManager;

public class WorldAliasExpansion extends PlaceholderExpansion {
	private static final String ALIAS_CONFIG = "worldAliases.yml";

	private PlaceholderAPIPlugin plugin;

	private File file;
	private FileConfiguration config;

	@Override
	public String getRequiredPlugin() {
		return "PlaceholderAPI";
	}

	/**
	 * Used to check if the expansion is able to register.
	 * 
	 * @return true or false depending on if the required plugin is installed
	 */
	@Override
	public boolean canRegister() {
		if (!Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) {
			return false;
		}
		plugin = (PlaceholderAPIPlugin) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
		return plugin != null;
	}

	@Override
	public String getIdentifier() {
		return "worldAlias";
	}

	@Override
	public String getAuthor() {
		return "Darrionat";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	/**
	 * This method is called when a placeholder is used and matches the set
	 * {@link #getIdentifier() identifier}
	 *
	 * @param offlinePlayer The player to parse placeholders for
	 * @param params        The part after the identifier
	 *                      ({@code %identifier_params%})
	 *
	 * @return returns value or possibly {@code null}
	 */
	@Override
	public String onRequest(OfflinePlayer offlinePlayer, String params) {
		if (offlinePlayer == null || !offlinePlayer.isOnline())
			return null;
		Player p = (Player) offlinePlayer;
		World world = p.getWorld();
		String worldName = world.getName();

		config = getAliasConfig();
		String value = config.getString(worldName);

		if (value != null)
			return value;
		return worldName;
	}

	private FileConfiguration getAliasConfig() {
		if (config == null)
			setupFile();
		return config;
	}

	private void setupFile() {
		LocalExpansionManager localExpansionManager = plugin.getLocalExpansionManager();
		File expansionsFolder = localExpansionManager.getExpansionsFolder();
		file = new File(expansionsFolder, ALIAS_CONFIG);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException exe) {
				System.out.println("Failed to create worldAliases.yml file");
				exe.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		config.set("world", "&bWorld");
		saveConfig();
	}

	private void saveConfig() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}