package to.epac.factorycraft.terrainhousing.events;

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

import java.util.UUID;

public class SignClickHandler implements Listener {

    TerrainHousing plugin = TerrainHousing.inst();

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Sign sign = (Sign) event.getClickedBlock().getState();
        Location loc = sign.getLocation();

        for (Housing housing : plugin.getTerrainManager().getTerrains()) {
            if (loc.equals(housing.getSign())) {

                // Sign cooldown
                if (housing.hasSignLock()) {
                    return;
                }

                // No one claimed this Housing
                if (housing.getOccupied() == null) {

                    // Lock sign
                    housing.setSignLock(uuid);

                    // Set occupation
                    housing.setOccupied(uuid);

                    // Paste schematic
                    SchemUtils.paste(housing.getId(), uuid.toString());

                    // Update sign
                            /*Sign signblock = (Sign) housing.getSign().getBlock().getState();
                            for (int i = 0; i < 4; i++) {
                                String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignOccupied().get(i));
                                signblock.setLine(i, line);
                            }
                            signblock.update();*/

                    // Update skull
                    if (housing.getSkull() != null && housing.getSkull().getBlock().getState() instanceof Skull)
                        housing.updateSkull(player);

                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "You have claimed this housing, you may build now.");

                    // Start countdown
                    housing.startCd();

                    // Delay 3 seconds
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Remove sign lock
                            housing.removeSignLock();
                        }
                    };
                    runnable.runTaskLater(plugin, 3 * 20);
                } else {
                    Player occupied = Bukkit.getPlayer(housing.getOccupied());

                    // You claimed this Housing
                    if (player.equals(occupied)) {
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Unclaiming housing.");

                        // Stop countdown
                        housing.stopCd();

                        // Lock sign
                        housing.setSignLock(uuid);

                        // Save schematic
                        SchemUtils.save(housing.getId(), uuid.toString());

                        // Paste schematic
                        SchemUtils.paste(housing.getId(), "default");

                        // Remove occupation
                        housing.setOccupied(null);

                        // Update sign
                        for (int i = 0; i < 4; i++) {
                            String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignResetting().get(i));
                            sign.setLine(i, line);
                        }
                        sign.update();

                        // Delay 3 seconds
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                // Remove sign lock
                                housing.removeSignLock();

                                // Update sign
                                for (int i = 0; i < 4; i++) {
                                    String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
                                    sign.setLine(i, line);
                                }
                                sign.update();

                                // Update skull
                                if (housing.getSkull() != null)
                                    housing.updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");

                                player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "The housing has been unclaimed.");
                            }
                        };
                        runnable.runTaskLater(plugin, 3 * 20);
                    }
                    // Someone else claimed this Housing
                    else {
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "This housing is occupied by " +
                                ChatColor.YELLOW + occupied.getName() + ChatColor.GREEN + ".");
                    }
                }
            }
        }
    }
}