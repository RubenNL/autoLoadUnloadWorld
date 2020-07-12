package nl.rubend.autoloadunloadworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class AutoLoadUnloadWorld extends JavaPlugin implements Listener {
    private YamlConfiguration config;
    @Override public void onEnable() {
        this.saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    @EventHandler private void onChangedWorld(PlayerChangedWorldEvent event) {
        World world=event.getFrom();
        String name=world.getName();
        if(world.getPlayers().size()>0) return;
        if(!Objects.requireNonNull(config.getList("worlds")).contains(name)) return;
        Bukkit.unloadWorld(name, Objects.requireNonNull(config.getList("save")).contains(name));
    }
}
