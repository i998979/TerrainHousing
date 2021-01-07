package to.epac.factorycraft.terrainhousing.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.Utils;

public class BreakHandler implements Listener {

    TerrainHousing plugin = TerrainHousing.inst();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        for (Housing housing : plugin.getTerrainManager().getTerrains()) {

            Location min = housing.getMin();
            Location max = housing.getMax();
            if (min == null || max == null) continue;

            if (Utils.inRegion(loc, min, max)) {

                if (housing.getOccupied() == null) {
                    if (!player.hasPermission("TerrainHousing.Admin")) {
                        event.setCancelled(true);
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Please click the sign to claim before you can modify blocks here.");
                    }
                } else {
                    Player occupied = Bukkit.getPlayer(housing.getOccupied());

                    if (!player.equals(occupied)) {
                        event.setCancelled(true);
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "You cannot modify blocks in this area because " +
                                ChatColor.YELLOW + occupied.getName() + ChatColor.RED + " has occupied.");
                    } else {
                        if (FileUtils.getOverrideProtections())
                            if (event.isCancelled())
                                event.setCancelled(false);
                    }
                }

            }
        }
    }
}
