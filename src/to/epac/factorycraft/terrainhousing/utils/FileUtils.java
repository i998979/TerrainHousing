package to.epac.factorycraft.terrainhousing.utils;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import to.epac.factorycraft.terrainhousing.TerrainHousing;

public class FileUtils {
	
	private static Plugin plugin = TerrainHousing.inst();	
	
	/**
	 * Get plugin's prefix
	 * @return Plugin prefix
	 */
	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("TerrainHousing.Prefix"));
	}
	
	
	
	
	
	
	/**
	 * Get whether TerrainHousing can override protection plugins or not
	 * @return true/false
	 */
	public static boolean getOverrideProtections() {
		return plugin.getConfig().getBoolean("TerrainHousing.OverrideProtections");
	}
	/**
	 * Set TerrainHousing override protection plugins
	 * @param override
	 */
	public static void setOverrideProtections(boolean override) {
		plugin.getConfig().set("TerrainHousing.OverrideProtections", override);
		plugin.saveConfig();
	}
	
	
	
	
	
	
	/**
	 * Get sign text of Housing available
	 * @return String list of sign text
	 */
	public static List<String> getSignAvailable() {
		return plugin.getConfig().getStringList("TerrainHousing.SignText.Available");
	}
	/**
	 * Get sign text of Housing occupied
	 * @return String list of sign text
	 */
	public static List<String> getSignOccupied() {
		return plugin.getConfig().getStringList("TerrainHousing.SignText.Occupied");
	}
	/**
	 * Get sign text of Housing resetting
	 * @return String list of sign text
	 */
	public static List<String> getSignResetting() {
		return plugin.getConfig().getStringList("TerrainHousing.SignText.Resetting");
	}
	
	
}
