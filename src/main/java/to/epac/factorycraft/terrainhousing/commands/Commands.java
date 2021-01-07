package to.epac.factorycraft.terrainhousing.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import to.epac.factorycraft.terrainhousing.TerrainHousing;
import to.epac.factorycraft.terrainhousing.terrains.Housing;
import to.epac.factorycraft.terrainhousing.utils.FileUtils;
import to.epac.factorycraft.terrainhousing.utils.SchemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Commands implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Help page
        if (args.length == 0) {
            HelpPage(sender);
            return false;
        }
        // No permission
        if (!sender.hasPermission("TerrainHousing.Admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
            return false;
        }
        // Help page
        if (args[0].equalsIgnoreCase("help")) {
            HelpPage(sender);
        }
        // Reload
        else if (args[0].equalsIgnoreCase("reload")) {
            TerrainHousing.inst().reloadConfig();
            TerrainHousing.inst().getTerrainManager().load();
            sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Configuration reloaded.");
        }
        // OverrideProtection
        else if (args[0].equalsIgnoreCase("overrideprotections")) {
            FileUtils.setOverrideProtections(!FileUtils.getOverrideProtections());

            sender.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Override Protections is now " +
                    (FileUtils.getOverrideProtections() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                    + ChatColor.YELLOW + ".");
        }
        // Save
        else if (args[0].equalsIgnoreCase("save")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing id you've entered does not exist.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a player name.");
                return false;
            }

            try {
                Player target = Bukkit.getPlayer(args[2]);

                SchemUtils.save(args[1], target.getUniqueId().toString());
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Housing " + ChatColor.YELLOW +
                        args[1] + ChatColor.DARK_GREEN + " saved to " + ChatColor.YELLOW +
                        args[2] + ChatColor.GREEN + ".");

            } catch (Exception e) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Cannot find player named " +
                        ChatColor.YELLOW + args[2] + ChatColor.RED + ".");
                return false;
            }
        }
        // Load
        else if (args[0].equalsIgnoreCase("load")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "housing id you've entered does not exist.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a player name.");
                return false;
            }

            try {
                Player target = Bukkit.getPlayer(args[2]);

                SchemUtils.paste(args[1], target.getUniqueId().toString());
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Player " + ChatColor.YELLOW +
                        args[2] + ChatColor.GREEN + "'s housing pasted at " + ChatColor.YELLOW +
                        args[1] + ChatColor.GREEN + ".");

            } catch (Exception e) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Cannot find player named " +
                        ChatColor.YELLOW + args[2] + ChatColor.RED + ".");
                return false;
            }
        }
        // Reset
        else if (args[0].equalsIgnoreCase("reset")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing id you've entered does not exist.");
                return false;
            }

            SchemUtils.paste(args[1], "default");
            sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Pasted default housing schematic at " +
                    ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing id you've entered does not exist.");
                return false;
            }
            Housing housing = TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]);
            TerrainHousing.inst().getTerrainManager().unclaim(housing);
            TerrainHousing.inst().getTerrainManager().delete(args[1]);
            sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Unclaimed and deleted housing " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
        }
        // Idle
        else if (args[0].equalsIgnoreCase("idle")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter an integer (in ticks).");
                return false;
            }

            try {
                long idle = Long.parseLong(args[2]);

                try {
                    TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setIdle(idle);
                    sender.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Housing edit session will expire after " + ChatColor.GREEN + idle + ChatColor.YELLOW + " ticks.");
                } catch (NullPointerException e) {
                    sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing named \"" + args[1] + " does not exist.");
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a valid integer (in ticks).");
                return false;
            }
        }
        // Player related commands
        else {
            // Not player
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                return false;
            }

            Player player = (Player) sender;
            Location loc = player.getLocation();
            Location accr = loc.clone();
            accr.setX(accr.getBlockX());
            accr.setY(accr.getBlockY());
            accr.setZ(accr.getBlockZ());
            accr.setPitch(0);
            accr.setYaw(0);


            if (args[0].equalsIgnoreCase("create")) {
                try {
                    Region region = WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelection(BukkitAdapter.adapt(loc.getWorld()));
                    BlockVector3 vMin = region.getMinimumPoint();
                    BlockVector3 vMax = region.getMaximumPoint();
                    Location min = new Location(loc.getWorld(), vMin.getBlockX(), vMin.getBlockY(), vMin.getBlockZ());
                    Location max = new Location(loc.getWorld(), vMax.getBlockX(), vMax.getBlockY(), vMax.getBlockZ());

                    Location origin = min.clone();
                    BlockFace facing = player.getFacing();
                    if (facing == BlockFace.EAST) origin.setYaw(270);
                    if (facing == BlockFace.SOUTH) origin.setYaw(0);
                    if (facing == BlockFace.WEST) origin.setYaw(90);
                    if (facing == BlockFace.NORTH) origin.setYaw(180);


                    Housing housing = new Housing(args[1]);
                    housing.setOrigin(origin);
                    housing.setMin(min);
                    housing.setMax(max);
                    TerrainHousing.inst().getTerrainManager().addTerrain(housing);
                    TerrainHousing.inst().getTerrainManager().save();

                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Housing named \"" + args[1] + "\" created."
                            + " Please set the Claiming Sign by typing \"/th sign " + args[1] + "\""
                            + " and Display Skull by typing \"/th skull " + args[1] + "\".");

                    if (args.length >= 3 && args[2].equalsIgnoreCase("true")) {
                        SchemUtils.save(args[1], "default");
                        player.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Default schematic has been saved. The old one (if exist) has been overwritten.");
                    }

                } catch (IncompleteRegionException e) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please complete WorldEdit region selection by picking 2 corners.");
                    return false;
                } catch (Exception e) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "The housing creation cannot be completed. Please check server console.");
                    e.printStackTrace();
                    return false;
                }
            }
            // Sign
            else if (args[0].equalsIgnoreCase("sign")) {
                if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                    return false;
                }

                try {
                    TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setSign(accr);
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Display sign location set.");
                } catch (NullPointerException e) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing named \"" + args[1] + " does not exist.");
                    return false;
                }
            }
            // Skull
            else if (args[0].equalsIgnoreCase("skull")) {
                if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a housing id.");
                    return false;
                }

                try {
                    TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setSkull(accr);
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Display skull location set.");
                } catch (NullPointerException e) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing named \"" + args[1] + " does not exist.");
                    return false;
                }
            }
            // Wrong argument
            else {
                HelpPage(sender);
                return false;
            }
        }
        return true;
    }

    private static final List<String> COMMANDS = Arrays.asList("Reload", "OverrideProtections", "Save",
            "Load", "Reset", "Delete", "Create", "Idle", "Sign", "Skull");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.addAll(COMMANDS);
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("Save")) {
                if (args.length == 2) {
                    for (Housing th : TerrainHousing.inst().getTerrainManager().getTerrains()) {
                        commands.add(th.getId());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
                } else if (args.length == 3) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        commands.add(player.getName());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
                }
            }
            if (args[0].equalsIgnoreCase("Load")) {
                if (args.length == 2) {
                    for (Housing housing : TerrainHousing.inst().getTerrainManager().getTerrains()) {
                        commands.add(housing.getId());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
                } else if (args.length == 3) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        commands.add(player.getName());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
                }
            }
            if (args[0].equalsIgnoreCase("Reset") || args[0].equalsIgnoreCase("Idle") ||
                    args[0].equalsIgnoreCase("Delete") || args[0].equalsIgnoreCase("Sign") ||
                    args[0].equalsIgnoreCase("Skull")) {
                if (args.length == 2) {
                    for (Housing housing : TerrainHousing.inst().getTerrainManager().getTerrains()) {
                        commands.add(housing.getId());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    public void HelpPage(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7-------------------" + FileUtils.getPrefix() + "&7-------------------"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Main command: &e/TerrainHousing, /th, /trh"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c<>: Required &d[]: Optional"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Reload&b: &3Reload configuration."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th OverrideProtections&b: " +
                "&3Let players build even if region protection plugins are active. &3&l*Those messages still sent to players, " +
                "just they can bypass the restriction*&3."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Save &c<Id> <Player>&b: &3Force save housing into player's data."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Load &c<Id> <Player>&b: &3Force load housing from player's data."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Reset &c<Id>&b: &3Reset housing into default state."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Delete &c<Id>&b: &3Unclaim housing and delete it from config."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Create &c<Id> &d[Overwrite]&b: &3Create housing based on your WorldEdit corner selection," +
                " player can only build between 2 selected corners. If overwrite is \"true\", the selected area will be saved as default schematic."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Idle &c<Id> <ticks>&b: &3Set how long will housing edit session expires."));

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Sign &c<Id>&b: &3Set Sign Display location."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Skull &c<Id>&b: &3Set Head Display location."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7-------------------" + FileUtils.getPrefix() + "&7-------------------"));
    }
}