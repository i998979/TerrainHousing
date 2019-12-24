package to.epac.factorycraft.terrainhousing.terrains;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.SchemUtils;

public class TerrainManager {

    private TerrainHousing plugin;

    private ConcurrentHashMap <String, Housing> thList;

    private ConcurrentHashMap <String, Sign> signList;

    public TerrainManager(TerrainHousing plugin) {
        this.plugin = plugin;
        this.thList = new ConcurrentHashMap<>();

        this.signList = new ConcurrentHashMap<>();

    }





    public void unclaimAll() {
        for (Housing th: thList.values()) {
            if (th.getOccupied() != null) {
                // Stop countdown
                th.stopCd();

                th.removeSignLock();

                // Save schematic
                SchemUtils.save(th.getId(), th.getOccupied().toString());

                // Paste schematic
                SchemUtils.paste(th.getId(), "default");

                // Remove occupation
                th.setOccupied(null);
                
                
                // Update sign
                Sign signblock = (Sign) th.getSign().getBlock().getState();
                if (signblock instanceof Sign) {
	                for (int i = 0; i < 4; i++) {
	                    String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
	                    signblock.setLine(i, line);
	                }
	                signblock.update();
                }

                // Update skull
                th.updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
            }
        }
    }





    public void load() {
        thList.clear();

        FileConfiguration conf = plugin.getConfig();
        for (String id: conf.getConfigurationSection("TerrainHousing.Location").getKeys(false)) {
            try {
                long idle = conf.getLong("TerrainHousing.Location." + id + ".Idle");
                Location min = (Location) conf.get("TerrainHousing.Location." + id + ".Minimum");
                Location max = (Location) conf.get("TerrainHousing.Location." + id + ".Maximum");
                Location origin = (Location) conf.get("TerrainHousing.Location." + id + ".Origin");
                Location sign = (Location) conf.get("TerrainHousing.Location." + id + ".Sign");
                Location skull = (Location) conf.get("TerrainHousing.Location." + id + ".Skull");

                Housing th = new Housing(id, idle, min, max, origin, sign, skull);
                thList.put(id, th);

            } catch (Exception e) {
                plugin.getLogger().warning("Error loading Housing " + id + ".");
                e.printStackTrace();
                continue;
            }
        }
    }

    public void save() {
        FileConfiguration conf = plugin.getConfig();

        for (Housing th: thList.values()) {
            conf.set("TerrainHousing.Location." + th.getId() + ".Idle", th.getIdle());
            conf.set("TerrainHousing.Location." + th.getId() + ".Minimum", th.getMin());
            conf.set("TerrainHousing.Location." + th.getId() + ".Maximum", th.getMax());
            conf.set("TerrainHousing.Location." + th.getId() + ".Origin", th.getOrigin());
            conf.set("TerrainHousing.Location." + th.getId() + ".Sign", th.getSign());
            conf.set("TerrainHousing.Location." + th.getId() + ".Skull", th.getSkull());
        }
        plugin.saveConfig();
    }

    public Housing create(String id) {
    	Housing th = new Housing(id);
    	addTerrain(th);
    	return th;
    }





    public Collection<Housing> getTerrains() {
        return thList.values();
    }
    public void addTerrain(Housing terrain) {
        thList.put(terrain.getId(), terrain);
    }
    public void removeTerrain(Housing terrain) {
        removeTerrain(terrain.getId());
    }
    public void removeTerrain(String id) {
        thList.remove(id);
    }






    public Housing getHousingByName(String id) {
        return thList.get(id);
    }
}