package es.capitanpuerka.marriage.controller;

import org.bukkit.entity.*;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.database.DatabaseUtils;
import es.capitanpuerka.marriage.player.MarryPlayer;

import org.bukkit.*;
import javax.annotation.*;
import java.util.*;

public class PlayerController
{
    private final Map<Player, MarryPlayer> playerRegistry;
    private static PlayerController instance;
    
    private PlayerController() {
        this.playerRegistry = new HashMap<Player, MarryPlayer>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.register(player);
        }
    }
    
    public MarryPlayer register(@Nonnull final Player bukkitPlayer) {
    	MarryPlayer gamePlayer = null;
        if (!this.playerRegistry.containsKey(bukkitPlayer)) {
            gamePlayer = new MarryPlayer(bukkitPlayer);
            this.playerRegistry.put(bukkitPlayer, gamePlayer);
        }
        return gamePlayer;
    }
    
    public MarryPlayer unregister(@Nonnull final Player bukkitPlayer) {
    	Bukkit.getScheduler().runTaskAsynchronously(Main.get(), () -> DatabaseUtils.get().savePlayer(playerRegistry.get(bukkitPlayer)));
        return this.playerRegistry.remove(bukkitPlayer);
    }
    
    public boolean isRegistered(Player p) {
    	if(this.playerRegistry.containsKey(p)) {
    		return true;
    	}
    	return false;
    }
    
    public MarryPlayer get(@Nonnull final Player bukkitPlayer) {
    	MarryPlayer gamePlayer = null;
        if (!this.playerRegistry.containsKey(bukkitPlayer)) {
            gamePlayer = new MarryPlayer(bukkitPlayer);
            this.playerRegistry.put(bukkitPlayer, gamePlayer);
        }
        return this.playerRegistry.get(bukkitPlayer);
    }
    
    public Collection<MarryPlayer> getAll() {
        return this.playerRegistry.values();
    }
    
    public void shutdown() {
    	for (final MarryPlayer gamePlayer : this.playerRegistry.values()) {
            try {
            	DatabaseUtils.get().savePlayer(gamePlayer);
            }
            catch (Exception ex) {}
        }
        this.playerRegistry.clear();
    }
    
    public static PlayerController get() {
        if (PlayerController.instance == null) {
            PlayerController.instance = new PlayerController();
        }
        return PlayerController.instance;
    }
}
