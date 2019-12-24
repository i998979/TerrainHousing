package to.epac.factorycraft.terrainhousing.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;

public class InteractHandler implements Listener {
	
	TerrainHousing plugin = TerrainHousing.inst();
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (block == null) return;
		
		Location loc = block.getLocation();
		
        for (Housing th: plugin.getTerrainManager().getTerrains()) {

            Location min = th.getMin();
            Location max = th.getMax();
            if (min == null || max == null) continue;

            if (loc.getX() >= Math.min(min.getX(), max.getX()) &&
                loc.getY() >= Math.min(min.getY(), max.getY()) &&
                loc.getZ() >= Math.min(min.getZ(), max.getZ())) {

                if (loc.getX() <= Math.max(min.getX(), max.getX()) &&
                    loc.getY() <= Math.max(min.getY(), max.getY()) &&
                    loc.getZ() <= Math.max(min.getZ(), max.getZ())) {

                    if (th.getOccupied() == null) {
                        if (!player.hasPermission("TerrainHousing.Admin")) {
                            event.setCancelled(true);
                            player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW +
                                "Click the sign to claim before you can modify blocks here.");
                        }
                    }
                    else {
                        Player occupied = Bukkit.getPlayer(th.getOccupied());
                        
                        if (!player.equals(occupied)) {
                            event.setCancelled(true);
                            player.sendMessage(FileUtils.getPrefix() + ChatColor.RED +
                                "You cannot modify blocks in this area because " + ChatColor.YELLOW +
                                occupied.getName() + ChatColor.RED + " is occupied.");
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
}
