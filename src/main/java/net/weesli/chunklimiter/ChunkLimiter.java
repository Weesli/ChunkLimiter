package net.weesli.chunklimiter;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ChunkLimiter extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this,this);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        getCommand("ChunkLimiter").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        Chunk chunk = e.getBlock().getChunk();

        if (getConfig().get("options.block-items." + e.getBlock().getType().name()) == null) {
            return;
        }

        if(e.getPlayer().hasPermission("chunklimiter.bypass")){
            return;
        }

        int size = 0;

        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX | 15;
        int maxY = chunk.getWorld().getMaxHeight();
        int maxZ = minZ | 15;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = 0; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Material material = e.getBlock().getWorld().getBlockAt(x,y,z).getType();
                    if (e.getBlock().getType() == material){
                        size++;
                    }
                }
            }
        }

        if (size > getConfig().getInt("options.block-items." + e.getBlock().getType().name())){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.max-limit")));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRunning by &bWeesli"));
        }else  if (args[0].equalsIgnoreCase("reload")){
            if (sender.isOp()){
                reloadConfig();
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "System file is reloaded.");
            }
        }

        return false;
    }
}
