package to.epac.factorycraft.terrainhousing;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import to.epac.factorycraft.terrainhousing.commands.Commands;
import to.epac.factorycraft.terrainhousing.events.*;
import to.epac.factorycraft.terrainhousing.metrics.Metrics;
import to.epac.factorycraft.terrainhousing.terrains.TerrainManager;

import java.io.File;

public class TerrainHousing extends JavaPlugin {

    private static TerrainHousing inst;

    private TerrainManager terrainManager;

    public static File configFile;

    public void onEnable() {

        inst = this;

        Metrics metrics = new Metrics(this, 6099);

        terrainManager = new TerrainManager(this);
        // Load everything from config
        // -> Load everything from TerrainManager
        // -> In TerrainManager, loop all things in the config
        //    and add to HashMap
        terrainManager.load();

        File file = new File(inst.getDataFolder(), "schematics");
        if (!file.isDirectory())
            file.mkdirs();


        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Configuration not found. Generating the default one.");

            getConfig().options().copyDefaults(true);
            saveConfig();
        }


        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BreakHandler(), this);
        pm.registerEvents(new InteractHandler(), this);
        pm.registerEvents(new PlaceHandler(), this);
        pm.registerEvents(new QuitHandler(), this);
        pm.registerEvents(new SignClickHandler(), this);

        getCommand("TerrainHousing").setExecutor(new Commands());
    }

    public void onDisable() {
        // Loop through HashMap
        // -> Save their data into config
        terrainManager.save();
        // Unclaim all Housing and reset to default
        terrainManager.unclaimAll();
    }

    public static TerrainHousing inst() {
        return inst;
    }

    public TerrainManager getTerrainManager() {
        return this.terrainManager;
    }
}