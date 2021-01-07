package to.epac.factorycraft.terrainhousing.terrains;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.SchemUtils;

import java.util.UUID;

public class Housing {

    private String id;
    private long idle;

    private Location min;
    private Location max;
    private Location origin;

    private Location sign;
    private Location skull;

    private UUID occupied;

    private BukkitRunnable countdown;
    private long expire;

    public Housing(String id) {
        this.id = id;
        this.idle = 6000;
    }

    public Housing(String id, long idle, Location min, Location max, Location origin, Location sign, Location skull) {
        this.id = id;
        this.idle = idle;
        this.min = min;
        this.max = max;
        this.origin = origin;
        this.sign = sign;
        this.skull = skull;
    }


    public void startCd() {
        this.expire = (System.currentTimeMillis() / 1000) + (idle / 20);

        this.countdown = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis() / 1000;
                long remain = expire - now;

                String sec = String.format("%02d", remain % 60);
                String min = String.format("%02d", remain % 3600 / 60);
                String hr =  String.format("%02d", remain % 86400 / 3600);

                Sign signBlock = (Sign) sign.getBlock().getState();

                if (now <= expire) {
                    if (Integer.parseInt(hr) <= 0 && Integer.parseInt(min) <= 0 && Integer.parseInt(sec) <= 10) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(occupied);
                        if (offPlayer.isOnline())
                            ((Player) offPlayer).sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing session will expire in "
                                    + sec + " second" + (Integer.parseInt(sec) == 1 ? "" : "s") + "!");
                    }

                    for (int i = 0; i < 4; i++) {
                        String line = ChatColor.translateAlternateColorCodes('&',
                                FileUtils.getSignOccupied().get(i)
                                        .replaceAll("%player%", Bukkit.getOfflinePlayer(occupied).getName())
                                        .replaceAll("%timer%", (Integer.parseInt(hr) == 0 ? hr + ":" : "") + min + ":" + sec));
                        signBlock.setLine(i, line);
                    }
                    signBlock.update();
                } else {
                    stopCd();

                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(occupied);
                    if (offPlayer.isOnline())
                        ((Player) offPlayer).sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing session has expired. Unclaiming.");

                    // Lock sign
                    setSignLock(occupied);

                    // Save schematic
                    SchemUtils.save(id, occupied.toString());

                    // Paste schematic
                    SchemUtils.paste(id, "default");

                    // Remove occupation
                    setOccupied(null);

                    // Update sign
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
                            removeSignLock();

                            // Update sign
                            for (int i = 0; i < 4; i++) {
                                String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
                                signBlock.setLine(i, line);
                            }
                            signBlock.update();

                            // Update skull
                            if (skull != null && skull.getBlock().getState() instanceof Skull)
                                updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
                        }
                    };
                    runnable.runTaskLater(TerrainHousing.inst(), 3 * 20);
                }
            }
        };
        countdown.runTaskTimer(TerrainHousing.inst(), 0, 20);
    }

    public void stopCd() {
        if (countdown != null && !countdown.isCancelled())
            countdown.cancel();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public long getIdle() {
        return idle > 0 ? idle : 6000;
    }

    public void setIdle(long idle) {
        this.idle = idle;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public Location getMin() {
        return min;
    }

    public void setMin(Location min) {
        this.min = min;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public Location getMax() {
        return max;
    }

    public void setMax(Location max) {
        this.max = max;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public Location getSign() {
        return sign;
    }

    public void setSign(Location sign) {
        this.sign = sign;
        TerrainHousing.inst().getTerrainManager().save();
    }


    public void setSignLock(UUID uuid) {
        if (sign == null) return;

        sign.getBlock().getState().setMetadata("TerrainHousing:OccupiedBy",
                new FixedMetadataValue(TerrainHousing.inst(), uuid));
    }

    public void setSignLock() {
        if (sign == null) return;

        sign.getBlock().getState().setMetadata("TerrainHousing:OccupiedBy",
                new FixedMetadataValue(TerrainHousing.inst(), System.currentTimeMillis() / 1000));
    }

    public void removeSignLock() {
        if (sign == null) return;

        sign.getBlock().getState().removeMetadata("TerrainHousing:OccupiedBy", TerrainHousing.inst());
    }

    public boolean hasSignLock() {
        if (sign == null) return false;

        return sign.getBlock().getState().hasMetadata("TerrainHousing:OccupiedBy");
    }


    public Location getSkull() {
        return skull;
    }

    public void setSkull(Location skull) {
        this.skull = skull;
        TerrainHousing.inst().getTerrainManager().save();
    }

    public void updateSkull(String uuid) {
        updateSkull(UUID.fromString(uuid));
    }

    public void updateSkull(UUID uuid) {
        updateSkull(Bukkit.getOfflinePlayer(uuid));
    }

    public void updateSkull(OfflinePlayer player) {
        if (skull == null) return;

        Skull skull0 = (Skull) skull.getBlock().getState();
        skull0.setOwningPlayer(player);
        skull0.update();
    }


    public UUID getOccupied() {
        return occupied;
    }

    public void setOccupied(UUID uuid) {
        this.occupied = uuid;
    }
}