package to.epac.factorycraft.terrainhousing.events;

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

import java.util.UUID;

public class QuitHandler implements Listener {

    TerrainHousing plugin = TerrainHousing.inst();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        for (Housing housing : plugin.getTerrainManager().getTerrains()) {
            if (housing.getOccupied() == null) continue;

            if (housing.getOccupied().equals(uuid)) {

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
                Sign signBlock = (Sign) housing.getSign().getBlock().getState();
                for (int i = 0; i < 4; i++) {
                    String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignResetting().get(i));
                    signBlock.setLine(i, line);
                }
                signBlock.update();

                // Delay 3 seconds
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Remove sign lock
                        housing.removeSignLock();

                        // Update sign
                        for (int i = 0; i < 4; i++) {
                            String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
                            signBlock.setLine(i, line);
                        }
                        signBlock.update();

                        // Update skull
                        if (housing.getSkull() != null)
                            housing.updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
                    }
                };
                runnable.runTaskLater(plugin, 3 * 20);
            }
        }
    }
}
