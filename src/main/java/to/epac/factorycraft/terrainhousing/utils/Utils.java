package to.epac.factorycraft.terrainhousing.utils;

import org.bukkit.Location;

public class Utils {

    /**
     * Get formatted time display
     *
     * @param remain Time remain
     * @return formatted string
     */
    public static String getTime(long remain) {
        int seconds = (int) (remain / 1000);
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        return days + "d " + hours + "h " + minutes + "m " + sec + "s";
    }

    public static boolean inRegion(Location loc, Location min, Location max) {
        // If minimum's world =/= maximum's world
        if (!min.getWorld().equals(max.getWorld()))
            return false;
        // If location's world =/= minimum's' world (minimum & maximum's world is already same as we checked above)
        if (!loc.getWorld().equals(min.getWorld()))
            return false;

        return inRegion(loc.getX(), loc.getY(), loc.getZ(), min, max);
    }

    public static boolean inRegion(double x, double y, double z, Location min, Location max) {
        return x >= Math.min(min.getX(), max.getX()) && x <= Math.max(min.getX(), max.getX()) &&
                y >= Math.min(min.getY(), max.getY()) && y <= Math.max(min.getY(), max.getY()) &&
                z >= Math.min(min.getZ(), max.getZ()) && z <= Math.max(min.getZ(), max.getZ());
    }
}
