package to.epac.factorycraft.terrainhousing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.bukkit.Location;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;

import to.epac.factorycraft.terrainhousing.TerrainHousing;

public class SchemUtils {

    private static TerrainHousing plugin = TerrainHousing.inst();
    
    /**
     * Paste the specified schematic into Housing
     * 
     * @param id Housing which is going to paste schematic
     * @param name Schematic name that is going to paste, "default" to paste default one
     */
    public static void paste(String id, String name) {
        Location origin = plugin.getTerrainManager().getHousingByName(id).getOrigin();
        if (origin == null) return;

        File file = new File(plugin.getDataFolder(), "schematics" + File.separator + name + ".schem");
        File dir = new File(plugin.getDataFolder(), "schematics");
        if (!dir.isDirectory())
        	dir.mkdirs();

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {

            Clipboard clipboard = reader.read();

            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(
                new BukkitWorld(origin.getWorld()), -1)) {

                Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    // It paste at Max's X Z value
                    // and Min's Y value
                    .to(BlockVector3.at(origin.getX(), origin.getY(), origin.getZ()))
                    // If false, grass will be removed and replaced with air
                    // If true, grass will stay and will not be replaced
                    .ignoreAirBlocks(false)
                    .build();

                try {
                    Operations.complete(operation);
                    editSession.flushSession();

                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            // plugin.getServer().getLogger().severe(ChatColor.RED + name + ".schem not found. Could not place schematic.");
        } catch (Exception e) {
            plugin.getLogger().severe("Unexpected error while pasing schematic.");
            e.printStackTrace();
        }
    }
    /**
     * Save specified Housing into schematic 
     * 
     * @param id Housing needed to save
     * @param name Schematic name that is going to save
     */
    public static void save(String id, String name) {
        Location min = plugin.getTerrainManager().getHousingByName(id).getMin();
        Location max = plugin.getTerrainManager().getHousingByName(id).getMax();
        Location origin = plugin.getTerrainManager().getHousingByName(id).getOrigin();
        if (min == null || max == null || origin == null) return;

        World world = new BukkitWorld(origin.getWorld());
        BlockVector3 vMin = BlockVector3.at(min.getX(), min.getY(), min.getZ());
        BlockVector3 vMax = BlockVector3.at(max.getX(), max.getY(), max.getZ());
        BlockVector3 vOrigin = BlockVector3.at(origin.getX(), origin.getY(), origin.getZ());

        CuboidRegion region = new CuboidRegion(world, vMin, vMax);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, vOrigin);
        forwardExtentCopy.setCopyingEntities(true);

        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        File file = new File(plugin.getDataFolder(), "schematics" + File.separator + name + ".schem");
        File dir = new File(plugin.getDataFolder(), "schematics");
        if (!dir.isDirectory())
        	dir.mkdirs();

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
            editSession.flushSession();

        } catch (FileNotFoundException e) {
            plugin.getLogger().severe(name + ".schem not found. Could not save schematic.");
            e.printStackTrace();
        } catch (Exception e) {
            plugin.getLogger().severe(name + "Unexpected error while saving schematic.");
            e.printStackTrace();
        }
    }
}