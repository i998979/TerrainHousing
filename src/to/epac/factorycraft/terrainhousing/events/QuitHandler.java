package to.epac.factorycraft.terrainhousing.events;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.SchemUtils;

public class QuitHandler implements Listener {
	
	TerrainHousing plugin = TerrainHousing.inst();
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		for (Housing th: plugin.getTerrainManager().getTerrains()) {
			if (th.getOccupied() == null) continue;
			
			if (th.getOccupied().equals(uuid)) {
				
				// Stop countdown
                th.stopCd();
            	
                // Lock sign
                th.setSignLock(uuid);

                // Save schematic
                SchemUtils.save(th.getId(), uuid.toString());

                // Paste schematic
                SchemUtils.paste(th.getId(), "default");
                
                // Remove occupation
                th.setOccupied(null);

                // Update sign
                Sign signblock = (Sign) th.getSign().getBlock().getState();
	                if (signblock instanceof Sign) {
	                for (int i = 0; i < 4; i++) {
	                    String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignResetting().get(i));
	                    signblock.setLine(i, line);
	                }
	                signblock.update();
                }

                // Delay 3 seconds
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Remove sign lock
                        th.removeSignLock();

                        // Update sign
                        if (signblock instanceof Sign) {
	                        for (int i = 0; i < 4; i++) {
	                            String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
	                            signblock.setLine(i, line);
	                        }
	                        signblock.update();
                        }

                        // Update skull
                        if (th.getSkull().getBlock() != null)
                        	th.updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
                    }
                };
                runnable.runTaskLater(plugin, 3 * 20);
			}
		}
	}
}
