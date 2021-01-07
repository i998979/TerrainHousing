package to.epac.factorycraft.terrainhousing.utils;

import org.bukkit.ChatColor;
import to.epac.factorycraft.terrainhousing.TerrainHousing;

import java.util.List;

public class FileUtils {

    /**
     * Get plugin's prefix
     *
     * @return Plugin prefix
     */
    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', TerrainHousing.inst().getConfig().getString("TerrainHousing.Prefix"));
    }


    /**
     * Get whether TerrainHousing can override protection plugins or not
     *
     * @return true/false
     */
    public static boolean getOverrideProtections() {
        return TerrainHousing.inst().getConfig().getBoolean("TerrainHousing.OverrideProtections");
    }

    /**
     * Set TerrainHousing override protection plugins
     *
     * @param override Override protections or not
     */
    public static void setOverrideProtections(boolean override) {
        TerrainHousing.inst().getConfig().set("TerrainHousing.OverrideProtections", override);
        TerrainHousing.inst().saveConfig();
    }


    /**
     * Get sign text of Housing available
     *
     * @return String list of sign text
     */
    public static List<String> getSignAvailable() {
        return TerrainHousing.inst().getConfig().getStringList("TerrainHousing.SignText.Available");
    }

    /**
     * Get sign text of Housing occupied
     *
     * @return String list of sign text
     */
    public static List<String> getSignOccupied() {
        return TerrainHousing.inst().getConfig().getStringList("TerrainHousing.SignText.Occupied");
    }

    /**
     * Get sign text of Housing resetting
     *
     * @return String list of sign text
     */
    public static List<String> getSignResetting() {
        return TerrainHousing.inst().getConfig().getStringList("TerrainHousing.SignText.Resetting");
    }


}
