package to.epac.factorycraft.terrainhousing.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        // Reload
        if (args[0].equalsIgnoreCase("help")) {
        	HelpPage(sender);
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            TerrainHousing.inst().reloadConfig();
            TerrainHousing.inst().getTerrainManager().load();
            sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Configuration reloaded.");
        }
        // OverrideProtection
        else if (args[0].equalsIgnoreCase("overrideprotections")) {
            if (FileUtils.getOverrideProtections()) {
                FileUtils.setOverrideProtections(false);

                sender.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Override Protections is now " +
                    ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
            } else {
                FileUtils.setOverrideProtections(true);

                sender.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Override Protections is now " +
                    ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
            }
        }
        // Save
        else if (args[0].equalsIgnoreCase("save")) {
            if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing Id you've entered does not exist.");
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
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing Id you've entered does not exist.");
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
                    args[2] + ChatColor.GREEN + "'s Housing pasted at " + ChatColor.YELLOW +
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
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                return false;
            }
            if (TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]) == null) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Housing Id you've entered does not exist.");
                return false;
            }

            SchemUtils.paste(args[1], "default");
            sender.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Pasted default Housing schematic at " +
                ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
        }
        // Idle
        else if (args[0].equalsIgnoreCase("idle")) {
        	if (args.length == 1) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter an integer (in tick).");
                return false;
            }
            
            try {
            	long idle = Long.parseLong(args[2]);
                
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setIdle(idle);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		th.setIdle(idle);
            	} finally {
            		sender.sendMessage(FileUtils.getPrefix() + ChatColor.YELLOW + "Housing edit session will expire after " + ChatColor.GREEN + idle + ChatColor.YELLOW + " ticks.");
            	}
            } catch (NumberFormatException e) {
                sender.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a valid integer (in tick).");
                return false;
            }
        }
        else {
        	// Not player
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                return false;
            }

            Player player = (Player) sender;
            Location loc = player.getLocation();
            Location accr = loc;
            accr.setX(accr.getBlockX());
            accr.setY(accr.getBlockY());
            accr.setZ(accr.getBlockZ());
            accr.setPitch(0);
            accr.setYaw(0);
            
            // Minimum
            if (args[0].equalsIgnoreCase("min")) {
            	if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                    return false;
                }
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setMin(accr);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		th.setMin(accr);
            	} finally {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Minimum location set.");
            	}
            }
            // Maximum
            else if (args[0].equalsIgnoreCase("max")) {
            	if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                    return false;
                }
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setMax(accr);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		th.setMax(accr);
            	} finally {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Maximum location set.");
            	}
            }
            // Origin
            else if (args[0].equalsIgnoreCase("origin")) {
            	if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                    return false;
                }
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setOrigin(accr);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		BlockFace facing = player.getFacing();
            		if (facing == BlockFace.EAST ) accr.setYaw(270);
            		if (facing == BlockFace.SOUTH) accr.setYaw(0);
            		if (facing == BlockFace.WEST ) accr.setYaw(90);
            		if (facing == BlockFace.NORTH) accr.setYaw(180);
            			
            		th.setOrigin(accr);
            	} finally {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Origin set.");
            	}
            }
            // Sign
            else if (args[0].equalsIgnoreCase("sign")) {
            	if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                    return false;
                }
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setSign(accr);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		th.setSign(accr);
            	} finally {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Sign location set.");
            	}
            }
            // Skull
            else if (args[0].equalsIgnoreCase("skull")) {
            	if (args.length == 1) {
                    player.sendMessage(FileUtils.getPrefix() + ChatColor.RED + "Please enter a Housing Id.");
                    return false;
                }
            	try {
            		TerrainHousing.inst().getTerrainManager().getHousingByName(args[1]).setSkull(accr);
            	} catch (NullPointerException e) {
            		Housing th = new Housing(args[1]);
            		TerrainHousing.inst().getTerrainManager().addTerrain(th);
            		th.setSkull(accr);
            	} finally {
            		player.sendMessage(FileUtils.getPrefix() + ChatColor.GREEN + "Skull location set.");
            	}
            }
            // Wrong argument
            else {
                HelpPage(sender);
                return false;
            }
        }
        return false;
    }

    private static final List<String> COMMANDS = Arrays.asList("Reload", "OverrideProtections", "Save",
    		"Load", "Reset", "Idle", "Min", "Max", "Origin", "Sign", "Skull");
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    	List<String> completions = new ArrayList<>();
    	List<String> commands = new ArrayList<>();
    	
    	if (args.length == 1) {
    		commands.addAll(COMMANDS);
    		StringUtil.copyPartialMatches(args[0], commands, completions);
    	}
    	else if (args.length >= 2) {
    		if (args[0].equalsIgnoreCase("Save")) {
    			if (args.length == 2) {
    				for (Housing th : TerrainHousing.inst().getTerrainManager().getTerrains()) {
        				commands.add(th.getId());
            		}
            		StringUtil.copyPartialMatches(args[1], commands, completions);
    			}
    			else if (args.length == 3) {
            		for (Player player : Bukkit.getOnlinePlayers()) {
            			commands.add(player.getName());
            		}
            		StringUtil.copyPartialMatches(args[1], commands, completions);
            	}
        	}
    		if (args[0].equalsIgnoreCase("Load")) {
    			if (args.length == 2) {
    				for (Housing th : TerrainHousing.inst().getTerrainManager().getTerrains()) {
        				commands.add(th.getId());
            		}
            		StringUtil.copyPartialMatches(args[1], commands, completions);
    			}
    			else if (args.length == 3) {
            		for (Player player : Bukkit.getOnlinePlayers()) {
            			commands.add(player.getName());
            		}
            		StringUtil.copyPartialMatches(args[1], commands, completions);
            	}
        	}
    		if (args[0].equalsIgnoreCase("Reset") || args[0].equalsIgnoreCase("Idle") ||
    				args[0].equalsIgnoreCase("Min") || args[0].equalsIgnoreCase("Max") ||
    				args[0].equalsIgnoreCase("Origin") || args[0].equalsIgnoreCase("Sign") ||
    				args[0].equalsIgnoreCase("Skull")) {
    			if (args.length == 2) {
    				for (Housing th : TerrainHousing.inst().getTerrainManager().getTerrains()) {
        				commands.add(th.getId());
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
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Save &c<Id> <Player>&b: &3Force save Housing into player's data."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Load &c<Id> <Player>&b: &3Force load Housing from player's data."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Reset &c<Id>&b: &3Reset Housing into default state."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Idle &c<Id> <ticks>&b: &3Set how long will Housing edit session expires."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Min &c<Id>&b: &3Set the minimum point of the Housing," +
            " player can only build between Min and Max point."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Max &c<Id>&b: &3Set the maximum point of the Housing," +
            " player can only build between Min and Max point."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Origin &c<Id>&b: &3Set the origin of the Housing," +
            " Housing data will be placed base on the origin."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Sign &c<Id>&b: &3Set Sign Display location."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/th Skull &c<Id>&b: &3Set Head Dispaly location."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7-------------------" + FileUtils.getPrefix() + "&7-------------------"));
    }
}