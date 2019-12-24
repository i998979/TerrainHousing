package to.epac.factorycraft.terrainhousing.utils;

import java.util.Date;

import org.bukkit.Location;

public class Utils {
	/**
	 * Get current system time in millisecond
	 * @return now in millisecond
	 */
	public static long getTimeInt() {
		java.util.Date now = new Date();
		long i = now.getTime();
		
		return i / 1000;
	}
	/**
	 * Get formatted time display
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
	
	public static boolean locContains(int x, int y, int z, Location min, Location max) {
        return x >= Math.min(min.getX(), max.getX()) && x <= Math.max(min.getX(), max.getX()) &&
        		y >= Math.min(min.getY(), max.getY()) && y <= Math.max(min.getY(), max.getY()) &&
        		z >= Math.min(min.getZ(), max.getZ()) && z <= Math.max(min.getZ(), max.getZ());
	}
}
