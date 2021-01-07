package to.epac.factorycraft.terrainhousing.utils;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.SpongeSchematicReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;

public class SchemUtils {

    /**
     * Paste the specified schematic into Housing
     *
     * @param id   Housing which is going to paste schematic
     * @param name Schematic name that is going to paste, "default" to paste default one
     */
    public static void paste(String id, String name) {

        Location origin = TerrainHousing.inst().getTerrainManager().getHousingByName(id).getOrigin();
        if (origin == null) return;

        File file = new File(TerrainHousing.inst().getDataFolder(), "schematics" + File.separator + name + ".schem");

        // Load schematic
        Clipboard clipboard = null;
        try {
            BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file));
            NBTInputStream nbtStream = new NBTInputStream(new BufferedInputStream(new GZIPInputStream(buffered)));

            clipboard = (new SpongeSchematicReader(nbtStream)).read();

        } catch (Exception e) {
            TerrainHousing.inst().getLogger().severe("Error loading schematic " + name + ".schem.");
        }

        // Paste schematic in world
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(origin.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    // It paste at Max's X Z value
                    // and Min's Y value
                    .to(BlockVector3.at(origin.getX(), origin.getY(), origin.getZ()))
                    // If false, grass will be removed and replaced with air
                    // If true, grass will stay and will not be replaced
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            TerrainHousing.inst().getLogger().severe("Error pasting schematic " + name + ".schem.");
        }
    }

    /**
     * Save specified Housing into schematic
     *
     * @param id   Housing needed to save
     * @param name Schematic name that is going to save
     */
    public static void save(String id, String name) {
        Housing housing = TerrainHousing.inst().getTerrainManager().getHousingByName(id);
        Location min = housing.getMin();
        Location max = housing.getMax();
        Location origin = housing.getOrigin();
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

        File file = new File(TerrainHousing.inst().getDataFolder(), "schematics" + File.separator + name + ".schem");
        File dir = new File(TerrainHousing.inst().getDataFolder(), "schematics");
        if (!dir.isDirectory())
            dir.mkdirs();

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);

        } catch (Exception e) {
            TerrainHousing.inst().getLogger().severe(name + "Error saving schematic " + name + ".schem in disk.");
        }
    }
}