package to.epac.factorycraft.terrainhousing.terrains;

import java.util.UUID;

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
import to.epac.factorycraft.terrainhousing.utils.Utils;

public class Housing {

    private TerrainHousing plugin = TerrainHousing.inst();

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

    /*public Housing(String id, long idle, BlockFace facing, Location min, Location max, Location origin, Location sign, Location skull) {
    	this.id = id;
    	this.idle = idle;
    	this.facing = facing;
    	this.min = min;
    	this.max = max;
    	this.origin = origin;
    	this.sign = sign;
    	this.skull = skull;
    }*/






    public void startCd() {
        this.expire = Utils.getTimeInt() + idle / 20;

        this.countdown = new BukkitRunnable() {
            @Override
            public void run() {
                long now = Utils.getTimeInt();
                long remain = expire - now;

                String sec = String.format("%02d", remain % 60);
                String min = String.format("%02d", remain % 3600 / 60);
                String hr = String.format("%02d", remain % 86400 / 3600);

                Sign signblock = (Sign) getSign().getBlock().getState();

                if (now <= expire) {
                    if (Integer.parseInt(min) <= 0 && Integer.parseInt(min) <= 0 && Integer.parseInt(sec) <= 10) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(occupied);
                        if (offPlayer.isOnline()) {
                            Player player = (Player) offPlayer;
                            player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing session will expire in "
                            + Integer.parseInt(sec) + " second" + (sec.equals("01") ? "" : "s") + "!");
                        }
                    }
                    
                    if (signblock instanceof Sign) {
	                    for (int i = 0; i < 4; i++) {
	                        String line = ChatColor.translateAlternateColorCodes('&',
	                            FileUtils.getSignOccupied().get(i)
	                            .replaceAll("%player%", Bukkit.getOfflinePlayer(occupied).getName())
	                            .replaceAll("%timer%", (hr.equals("0") ? hr + ":" : "") + min + ":" + sec));
	                        signblock.setLine(i, line);
	                    }
	                    signblock.update();
                    }
                }
                else {
                    stopCd();

                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(occupied);
                    if (offPlayer.isOnline()) {
                        Player player = (Player) offPlayer;
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing session expired. Unclaiming.");
                    }

                    // Lock sign
                    setSignLock(occupied);

                    // Save schematic
                    SchemUtils.save(id, occupied.toString());

                    // Paste schematic
                    SchemUtils.paste(id, "default");

                    // Remove occupation
                    setOccupied(null);

                    // Update sign
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
                            removeSignLock();

                            // Update sign
                            if (signblock instanceof Sign) {
	                            for (int i = 0; i < 4; i++) {
	                                String line = ChatColor.translateAlternateColorCodes('&', FileUtils.getSignAvailable().get(i));
	                                signblock.setLine(i, line);
	                            }
	                            signblock.update();
                            }

                            // Update skull
                            if (getSkull() != null && getSkull().getBlock().getState() instanceof Skull)
                            	updateSkull("8667ba71-b85a-4004-af54-457a9734eed7");
                        }
                    };
                    runnable.runTaskLater(plugin, 3 * 20);
                }
            }
        };

        countdown.runTaskTimer(plugin, 0, 20);
    }
    public void stopCd() {
    	if (countdown != null)
    		if (!countdown.isCancelled())
    			countdown.cancel();
    }
    
    
    
    
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
        plugin.getTerrainManager().save();
    }






    public long getIdle() {
        return idle > 0 ? idle : 300;
    }
    public void setIdle(long idle) {
        this.idle = idle;
        plugin.getTerrainManager().save();
    }






    public Location getMin() {
        return min;
    }
    public void setMin(Location min) {
        this.min = min;
        plugin.getTerrainManager().save();
    }






    public Location getMax() {
        return max;
    }
    public void setMax(Location max) {
        this.max = max;
        plugin.getTerrainManager().save();
    }






    public Location getOrigin() {
        return origin;
    }
    public void setOrigin(Location origin) {
        this.origin = origin;
        plugin.getTerrainManager().save();
    }






    public Location getSign() {
        return sign;
    }
    public void setSign(Location sign) {
        this.sign = sign;
        plugin.getTerrainManager().save();
    }






    /**
     * Lock sign so that no one can use it
     * @param uuid Who lock the sign
     */
    public void setSignLock(UUID uuid) {
        getSign().getBlock().getState().setMetadata("TerrainHousing:OccupiedBy",
            new FixedMetadataValue(TerrainHousing.inst(), uuid));
    }
    public void setSignLock() {
        getSign().getBlock().getState().setMetadata("TerrainHousing:OccupiedBy",
            new FixedMetadataValue(TerrainHousing.inst(), Utils.getTimeInt()));
    }
    public void removeSignLock() {
        getSign().getBlock().getState().removeMetadata("TerrainHousing:OccupiedBy", TerrainHousing.inst());
    }
    public boolean hasSignLock() {
        return getSign().getBlock().getState().hasMetadata("TerrainHousing:OccupiedBy");
    }






    public Location getSkull() {
        return skull;
    }
    public void setSkull(Location skull) {
        this.skull = skull;
    }
    public void updateSkull(String uid) {
        updateSkull(UUID.fromString(uid));
    }
    public void updateSkull(UUID uuid) {
        updateSkull(Bukkit.getOfflinePlayer(uuid));
    }
    public void updateSkull(OfflinePlayer player) {
        Skull skull = (Skull) getSkull().getBlock().getState();
        skull.setOwningPlayer(player);
        skull.update();
    }






    public UUID getOccupied() {
        return occupied;
    }
    public void setOccupied(UUID occupied) {
        this.occupied = occupied;
    }
}