package to.epac.factorycraft.terrainhousing.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.SchemUtils;

public class SignClickHandler implements Listener {

    TerrainHousing plugin = TerrainHousing.inst();

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                Sign sign = (Sign) event.getClickedBlock().getState();
                Location loc = sign.getLocation();
                
                for (Housing th: plugin.getTerrainManager().getTerrains()) {
                    if (loc.equals(th.getSign())) {

                        // Sign cooldown
                        if (th.hasSignLock()) {
                            return;
                        }

                        // No one claimed this Housing
                        if (th.getOccupied() == null) {

                            // Lock sign
                            th.setSignLock(uuid);

                            // Set occupation
                            th.setOccupied(uuid);

                            // Paste schematic
                            SchemUtils.paste(th.getId(), uuid.toString());

                            // Update sign
                            /*Sign signblock = (Sign) th.getSign().getBlock().getState();
                            for (int i = 0; i < 4; i++) {
                                String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignOccupied().get(i));
                                signblock.setLine(i, line);
                            }
                            signblock.update();*/

                            // Update skull
                            if (th.getSkull() != null && th.getSkull().getBlock().getState() instanceof Skull)
                            	th.updateSkull(player);
                            
                            player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "You have claimed this Housing, you may build now.");

                            // Start countdown
                            th.startCd();
                            
                            // Delay 3 seconds
                            BukkitRunnable runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    // Remove sign lock
                                    th.removeSignLock();
                                }
                            };
                            runnable.runTaskLater(plugin, 3 * 20);
                        } else {
                            Player occupied = Bukkit.getPlayer(th.getOccupied());

                            // You claimed this Housing
                            if (player.equals(occupied)) {
                            	player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Unclaiming Housing.");
                            	
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
                                        if (th.getSkull() != null && th.getSkull().getBlock().getState() instanceof Skull)
                                        	th.updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
                                        
                                        player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Unclaimed.");
                                    }
                                };
                                runnable.runTaskLater(plugin, 3 * 20);
                            }
                            // Someone else claimed this Housing
                            else {
                                try {
                                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN +
                                        "This Housing is occupied by " + ChatColor.YELLOW +
                                        occupied.getName() + ChatColor.GREEN + ".");
                                } catch (Exception e) {
                                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN +
                                        "This Housing is occupied.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}