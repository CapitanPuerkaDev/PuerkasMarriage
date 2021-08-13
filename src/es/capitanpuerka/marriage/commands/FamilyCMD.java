package es.capitanpuerka.marriage.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.capitanpuerka.marriage.controller.LanguageController;
import es.capitanpuerka.marriage.controller.PlayerController;
import es.capitanpuerka.marriage.player.MarryPlayer;

public class FamilyCMD implements CommandExecutor {

	private static HashMap<UUID, UUID> adoptionMap = new HashMap<UUID, UUID>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player)sender;
		MarryPlayer mp = PlayerController.get().get(p);
		String lang = PlayerController.get().get(p).getLanguage();
		if(args.length == 0) {
			String status = "";
			if(mp.isSingle()) {
				status = new LanguageController.MessageFormatter().format("Messages.family_status.single", lang);
			}else {
				if(mp.getFamilyName().equalsIgnoreCase("none")) {
					status = new LanguageController.MessageFormatter().format("Messages.family_status.no_name", lang);
				}else {
					status = mp.getFamilyName();
				}
			}
			p.sendMessage(new LanguageController.MessageFormatter().setVariable("family_name", status).format("FamilyCommand", lang));
		}
		else if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("setname")) {
				if(!mp.isSingle()) {
					String name = "";
					for(int i = 1; i < args.length; i++) {
						name = name + args[i] + " ";
		            }
					if(name.length() <= 17) {
						mp.setFamilyName(name);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("name", name).format("Messages.family_name.setted", lang));
						Player partner = Bukkit.getPlayer(mp.getPartner());
						if(partner != null) {
							MarryPlayer mpartner = PlayerController.get().get(partner);
							mpartner.setFamilyName(name);
							mpartner.getPlayer().sendMessage(new LanguageController.MessageFormatter().setVariable("name", name).format("Messages.family_name.setted", lang));
						}else {
							/**
							 * Insert in database
							 */
						}
						for(String adopted : mp.getAdoptedMembers()) {
							Player ap = Bukkit.getPlayer(adopted);
							MarryPlayer mpap = PlayerController.get().get(ap);
							mpap.setFamilyName(name);
							if(ap != null) {
								ap.sendMessage(new LanguageController.MessageFormatter().setVariable("name", name).format("Messages.family_name.setted", lang));
							}
						}
						
					}
					else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.family_name.name_lenght", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.family_name.not_married", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("chat")) {
				if(!mp.isSingle()) {
					if(mp.isFamilyChat()) {
						mp.setFamilyChat(false);
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.family_chat.disabledChat", lang));
					}
					else {
						mp.setFamilyChat(true);
						mp.setMarryChat(false);
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.family_chat.enabledChat", lang));
					}		
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.family_chat.not_married", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("adopt")) {
				if(!mp.isSingle()) {
					Player padoption = Bukkit.getPlayer(args[1]);
					if(padoption != null) {
						MarryPlayer mpa = PlayerController.get().get(padoption);
						if(mpa.isSingle()) {
							if(!mp.getAdoptedMembers().contains(padoption.getName())) {
								padoption.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).format("Messages.adopt.adopt", lang));
								p.sendMessage(new LanguageController.MessageFormatter().setVariable("player", padoption.getName()).format("Messages.adopt.adoptSended", lang));
								adoptionMap.put(p.getUniqueId(), padoption.getUniqueId());
								adoptionMap.put(padoption.getUniqueId(), p.getUniqueId());
							}else {
								p.sendMessage(new LanguageController.MessageFormatter().setVariable("name", mpa.getPlayer().getName()).format("Messages.adopt.alredyAdopted", lang));
							}
						}else {
							p.sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.playerAdopted", lang));
						}
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.notOnline", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.not_married", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("accept")) {
				if(args.length != 2) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.acceptUssage", lang));
					return true;
				}
				Player partner = Bukkit.getPlayer(args[1]);
				if(adoptionMap.containsKey(partner.getUniqueId())) {
					if(adoptionMap.get(partner.getUniqueId()) == p.getUniqueId()) {
						MarryPlayer mpa = PlayerController.get().get(partner);
						mp.setSingle(true);
						mp.setAdopted(true);
						mp.setAdoptedFamilyName(partner.getName());
						mpa.getAdoptedMembers().add(mp.getName());
						mp.getPlayer().sendMessage(new LanguageController.MessageFormatter().setVariable("player", mpa.getPlayer().getName()).format("Messages.adopt.adopted_by", mp.getLanguage()));
						mpa.getPlayer().sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.adopt_accept", mpa.getLanguage()));
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.no_request", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.adopt.no_request", lang));
				}
				
				
			}
		}
		return false;
	}

}
