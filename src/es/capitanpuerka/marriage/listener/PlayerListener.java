package es.capitanpuerka.marriage.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.controller.LanguageController;
import es.capitanpuerka.marriage.controller.PlayerController;
import es.capitanpuerka.marriage.player.MarryPlayer;
import es.capitanpuerka.marriage.updater.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
		
		Bukkit.getScheduler().runTaskAsynchronously(Main.get(), () -> {
			PlayerController.get().register(e.getPlayer());
			PlayerController.get().get(e.getPlayer()).setPartnerLastLogged(format.format(date));

        });
		if(e.getPlayer().hasPermission("puerkasmarriage.admin")) {
			new UpdateChecker(Main.get(), 94028).getVersion(version -> {
	            if (!Main.get().getDescription().getVersion().equalsIgnoreCase(version)) {
	            	e.getPlayer().sendMessage("§7[§dPuerkas Marriage§7] §aThere is a pending update.");
	            } 
	        });
        }
	}
	
	/**
	 * if(!Database.getData().getString("puerkas_marry", "marriedwith", "player", partner).equalsIgnoreCase(e.getPlayer().getName())) {
			PlayerController.get().get(e.getPlayer()).setSingle(true);
			PlayerController.get().get(e.getPlayer()).setMarriedWith("none");
		}
		
		Boolean b = Boolean.valueOf(Database.getData().getString("puerkas_marry", "enderchest", "player", mp.getPartner()));
			if(!b) {
				mp.setEnderChest(true);
			}
	 * @param e
	 */
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		PlayerController.get().unregister(e.getPlayer());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		PlayerController.get().unregister(e.getPlayer());
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getDamager() instanceof Player) {
				Player entity = (Player)e.getEntity();
				Player damager = (Player)e.getDamager();
				MarryPlayer mp = PlayerController.get().get(entity);
				MarryPlayer mpart = PlayerController.get().get(damager);
				if(!mp.isPvpEnabled()) {
					e.setCancelled(true);
				}
				if(!mpart.isPvpEnabled()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		MarryPlayer mp = PlayerController.get().get(p);
		String lang = mp.getLanguage();
		if(mp.isMarryChat()) {
			e.setCancelled(true);
			p.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).setVariable("message", ChatColor.translateAlternateColorCodes('&', "&7" + e.getMessage())).format("MarryChat.format", lang));
			Player partner = Bukkit.getPlayer(mp.getPartner());
			if(partner != null) {
				partner.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).setVariable("message", ChatColor.translateAlternateColorCodes('&', "&7" + e.getMessage())).format("MarryChat.format", lang));
			}
		}else {
			String format = e.getFormat();
			String male = LanguageController.get().getMessage(lang, "Placeholders.gender.male");
        	String female = LanguageController.get().getMessage(lang, "Placeholders.gender.female");
			String gender = mp.isMale() ? male : female;
			format = format.replace(p.getName(), gender + " §7" + p.getName());
		}
		if(mp.isFamilyChat()) {
			e.setCancelled(true);
			p.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).setVariable("message", ChatColor.translateAlternateColorCodes('&', "&7" + e.getMessage())).format("FamilyChat.format", lang));
			Player partner = Bukkit.getPlayer(mp.getPartner());
			if(partner != null) {
				partner.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).setVariable("message", ChatColor.translateAlternateColorCodes('&', "&7" + e.getMessage())).format("FamilyChat.format", lang));
			}
			for(String adoptions : mp.getAdoptedMembers()) {
				if(Bukkit.getPlayer(adoptions) != null) {
					Bukkit.getPlayer(adoptions).sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).setVariable("message", ChatColor.translateAlternateColorCodes('&', "&7" + e.getMessage())).format("FamilyChat.format", lang));
				}
			}
		}else {
			String format = e.getFormat();
			String male = LanguageController.get().getMessage(lang, "Placeholders.gender.male");
        	String female = LanguageController.get().getMessage(lang, "Placeholders.gender.female");
			String gender = mp.isMale() ? male : female;
			format = format.replace(p.getName(), gender + " §7" + p.getName());
		}
	}

}
