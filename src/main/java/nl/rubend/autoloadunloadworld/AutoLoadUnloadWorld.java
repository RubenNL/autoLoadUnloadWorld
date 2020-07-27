package nl.rubend.autoloadunloadworld;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AutoLoadUnloadWorld extends JavaPlugin implements Listener {
	@Override public void onEnable() {
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	private void unloadWorld(World world) {
		if(world.getPlayers().size()>0) return;
		String name=world.getName();
		reloadConfig();
		if(!Objects.requireNonNull(getConfig().getList("worlds")).contains(name)) return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this,()->{
			if(Bukkit.getWorld(name)==null) return;
			if(Bukkit.getWorld(name).getPlayers().size()>0) return;
			Bukkit.unloadWorld(name, Objects.requireNonNull(getConfig().getList("save")).contains(name));
		},20L*15);
	}
	@EventHandler(priority=EventPriority.MONITOR) private void onChangedWorld(PlayerChangedWorldEvent event) {
		unloadWorld(event.getFrom());

	}
	@EventHandler private void onLogout(PlayerQuitEvent event) {
		World world=event.getPlayer().getLocation().getWorld();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> unloadWorld(world));
	}
	private boolean loadingWorld=false;
	@EventHandler private void onPortalClick(PlayerInteractEvent event) {
		if(loadingWorld) return;
		if(!event.hasBlock()) return;
		Material portalMaterial= Objects.requireNonNull(event.getClickedBlock()).getType();
		if(!(portalMaterial==Material.NETHER_PORTAL || portalMaterial==Material.END_PORTAL)) return;
		String worldName=event.getPlayer().getWorld().getName();
		String targetWorld=worldName.split("_")[0];
		if(worldName.contains("_")) event.getPlayer().sendMessage("Going to the overworld!");
		else if(portalMaterial==Material.NETHER_PORTAL) targetWorld+="_nether";
		else if(portalMaterial==Material.END_PORTAL) targetWorld+="_the_end";
		event.getPlayer().sendMessage("Going to "+targetWorld+"!");
		if(!((MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager().hasUnloadedWorld(targetWorld,true)) {
			event.getPlayer().sendMessage("World is not a MV world, not going to it!");
			return;
		}
		if(Bukkit.getWorld(targetWorld)==null) {
			event.getPlayer().sendMessage("Loading world "+targetWorld+"...");
			loadingWorld=true;
			((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager().loadWorld(targetWorld);
			loadingWorld=false;
			event.getPlayer().sendMessage("World "+targetWorld+" loaded!");
		} else event.getPlayer().sendMessage("World is already loaded, not loading it again.");
		event.getPlayer().sendMessage("you can hop in the portal. you have 15 seconds to go to the other world, before it gets unloaded.");
		unloadWorld(Bukkit.getWorld(targetWorld));
	}
}
