package nl.rubend.autoloadunloadworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static org.bukkit.event.EventPriority.MONITOR;

public final class AutoLoadUnloadWorld extends JavaPlugin implements Listener {
    private YamlConfiguration config;
    @Override public void onEnable() {
        this.saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    private void unloadWorld(World world) {
        String name=world.getName();
        if(world.getPlayers().size()>0) return;
        if(!Objects.requireNonNull(config.getList("worlds")).contains(name)) return;
        Bukkit.unloadWorld(name, Objects.requireNonNull(config.getList("save")).contains(name));
    }
    @EventHandler(priority=MONITOR) private void onChangedWorld(PlayerChangedWorldEvent event) {
        unloadWorld(event.getFrom());

    }
    @EventHandler private void onLogout(PlayerQuitEvent event) {
        World world=event.getPlayer().getLocation().getWorld();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> unloadWorld(world), 0L);
    }
}
