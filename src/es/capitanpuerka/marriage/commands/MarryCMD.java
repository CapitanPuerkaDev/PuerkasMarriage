package es.capitanpuerka.marriage.commands;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import es.capitanpuerka.marriage.config.Modules;
import es.capitanpuerka.marriage.controller.GenderController;
import es.capitanpuerka.marriage.controller.LanguageController;
import es.capitanpuerka.marriage.controller.PlayerController;
import es.capitanpuerka.marriage.database.DatabaseUtils;
import es.capitanpuerka.marriage.player.MarryPlayer;

public class MarryCMD implements CommandExecutor {

	private static HashMap<UUID, UUID> marriedMap = new HashMap<UUID, UUID>();
	
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
			
			if(mp.isAdopted()) {
				status = new LanguageController.MessageFormatter().setVariable("player", mp.getAdoptFamilyName()).format("Messages.status.adopted", lang);
			}else {
				if(mp.isSingle()) {
					status = new LanguageController.MessageFormatter().format("Messages.status.single", lang);
				}else {
					status = new LanguageController.MessageFormatter().setVariable("partner", mp.getPartner()).format("Messages.status.married", lang);
				}
			}
			p.sendMessage(new LanguageController.MessageFormatter().setVariable("status", status).format("HelpCommand", lang));
		}
		else if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("toggleModule")) {
				if(!p.hasPermission("marriage.moduletoggle")) {
					p.sendMessage(ChatColor.RED + "You dont have permission to do that.");
					return true;
				}
				if(args.length == 2) {
					String module = args[1];
					
					if(module.equalsIgnoreCase("gift") || module.equalsIgnoreCase("pvp") || module.equalsIgnoreCase("home")  || module.equalsIgnoreCase("sethome") || module.equalsIgnoreCase("chat") || module.equalsIgnoreCase("tp") || module.equalsIgnoreCase("heal")) {
						
						boolean b = Modules.getInstance().getConfig().getBoolean("ModuleList.commands." + module.toLowerCase() + ".enable");
					
						if(b) {
							Modules.getInstance().getConfig().set("ModuleList.commands." + module.toLowerCase() + ".enable", false);
							Modules.getInstance().reload();
							p.sendMessage("§7[§dPuerkasMarriage] §aModule §3" + module.toUpperCase() + " §ahas been updated and §3disabled§a.");
						}else {
							Modules.getInstance().getConfig().set("ModuleList.commands." + module.toLowerCase() + ".enable", true);
							Modules.getInstance().reload();
							p.sendMessage("§7[§dPuerkasMarriage] §aModule §3" + module.toUpperCase() + " §ahas been updated and §3enabled§a.");
						}
					}else {
						p.sendMessage("§7[§dPuerkasMarriage] §cUnknown module. §2Avariable modules§7: §agift§7, §apvp§7, §ahome§7, §asethome§7, §achat§7, §atp§7, §aheal§7.");
					}
				}else {
					p.sendMessage("§7[§dPuerkasMarriage] §cUse command like /marry toggleModule <moduleName>");
				}
			}
			else if(args[0].equalsIgnoreCase("languages")) {
				p.sendMessage(new LanguageController.MessageFormatter().format("Messages.languages.avariable_languages", lang) + String.join(", ", LanguageController.get().getLanguages()));
			}
			else if(args[0].equalsIgnoreCase("setLang")) {
				if(args.length == 2) {
					String language = args[1];
					if(LanguageController.get().getLanguages().contains(language)) {
						mp.setLanguage(language);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("language", language).format("Messages.languages.lang_setted", lang));
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.languages.avariable_languages", lang) + String.join(", ", LanguageController.get().getLanguages()));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.languages.ussage", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("reload")) {
				if(p.hasPermission("marriage.reload")) {
					if(LanguageController.get().reload() && Modules.getInstance().reload()) {
						p.sendMessage(ChatColor.GREEN + "Messages file and Modules file has been reloaded!");
					}else {
						p.sendMessage(ChatColor.RED + "An error occurred trying to load the configuration. Look at the console");
					}
				}else {
					p.sendMessage(ChatColor.RED + "You dont have permission to do that.");
				}
			}
			else if(args[0].equalsIgnoreCase("enderchest") || args[0].equalsIgnoreCase("ec")) {
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.ender_chest.single", lang));
					return true;
				}
				if(mp.hasEnderChest()) {
					p.openInventory(p.getEnderChest());
					if(Bukkit.getPlayer(mp.getPartner()) != null) {
						Bukkit.getPlayer(mp.getPartner()).sendMessage(new LanguageController.MessageFormatter().format("Messages.ender_chest.partner_chest_open", lang));
					}
				}else {
					if(Bukkit.getPlayer(mp.getPartner()) != null) {
						Bukkit.getPlayer(mp.getPartner()).sendMessage(new LanguageController.MessageFormatter().format("Messages.ender_chest.partner_chest_open", lang));
						p.openInventory(Bukkit.getPlayer(mp.getPartner()).getEnderChest());
						
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.ender_chest.partner_offline", lang));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("gift")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.gift.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gift.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gift.single", lang));
				}else {
					Player partner = Bukkit.getPlayer(mp.getPartner());
					if(partner != null) {
						if(p.getItemInHand().getType() == Material.AIR) {
							p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gift.notOnline", lang));
							return true;
						}
						String plang = PlayerController.get().get(partner).getLanguage();
						ItemStack item = p.getItemInHand();
						p.getInventory().remove(item);
						partner.getInventory().addItem(item);
						partner.sendMessage(new LanguageController.MessageFormatter().setVariable("item", String.valueOf(item.getType())).format("Messages.gift.success", plang));
						
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gift.notOnline", lang));
					}
				}	
			}
			else if(args[0].equalsIgnoreCase("gender")) {
				if(args.length != 2) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gender.ussage", lang));
					return true;
				}
				String type = args[1].toUpperCase();
				
				if(GenderController.getGenerByName(type) != null) {
					
					
					
				}else {
					//Send message - gender is null
				}
				
				if(type.equalsIgnoreCase("MALE") || type.equalsIgnoreCase("FEMALE")) {
					if(type.equalsIgnoreCase("MALE")) {
						mp.setMale(true);
						String male = new LanguageController.MessageFormatter().format("Messages.gender.variable.male", lang);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("gender", male).format("Messages.gender.setted", lang));	
					}else {
						mp.setMale(false);
						String male = new LanguageController.MessageFormatter().format("Messages.gender.variable.female", lang);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("gender", male).format("Messages.gender.setted", lang));	
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.gender.ussage", lang));	
				}
			}
			else if(args[0].equalsIgnoreCase("pvp")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.pvp.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled", lang)));
					return true;
				}
				if(args.length != 2) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.ussage", lang));
					return true;
				}
				String value = args[1].toLowerCase();
				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
					Boolean b = Boolean.valueOf(value);
					if(mp.isSingle()) {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.single", lang));
						return true;
					}
					if(mp.isAdopted()) {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.adopted", lang));
						return true;
					}
					mp.setPvp(b);
					if(b == true) {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.enabled", lang));
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.disabled", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.pvp.ussage", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("home")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.home.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.home.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.home.single", lang));
					return true;
				}
				if(mp.getHomeLocation() == null) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.home.notHave", lang));
					return true;
				}
				p.teleport(mp.getHomeLocation());
				p.sendMessage(new LanguageController.MessageFormatter().format("Messages.home.teleported", lang));
			}
			else if(args[0].equalsIgnoreCase("setHome")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.sethome.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.setHome.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.setHome.single", lang));
					return true;
				}
				if(mp.getHomeLocation() != null) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.setHome.homeChanged", lang));
					mp.setHomeLocation(p.getLocation().clone().add(0.5, 0.1, 0.5));
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.setHome.homeSetted", lang));
					mp.setHomeLocation(p.getLocation().clone().add(0.5, 0.1, 0.5));
				}
				Player partner = Bukkit.getPlayer(mp.getPartner());
				if(partner != null) {
					MarryPlayer mpa = PlayerController.get().get(partner);
					mpa.setHomeLocation(p.getLocation().clone().add(0.5, 0.1, 0.5));
				}
			}
			else if(args[0].equalsIgnoreCase("chat")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.chat.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.chat.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.chat.single", lang));
					return true;
				}
				if(mp.isMarryChat()) {
					mp.setMarryChat(false);
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.chat.disabledChat", lang));
				}else {
					mp.setMarryChat(true);
					mp.setFamilyChat(false);
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.chat.enabledChat", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("tp")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.tp.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.teleport.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.teleport.single", lang));
					return true;
				}
				Player partner = Bukkit.getPlayer(mp.getPartner());
				if(partner != null) {
					p.teleport(partner.getLocation().clone().add(0.5, 0.1, 0.5));
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.teleport.teleported", lang));
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.teleport.notOnline", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("heal")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.heal.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.heal.single", lang));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.heal.adopted", lang));
					return true;
				}
				Player partner = Bukkit.getPlayer(mp.getPartner());
				if(partner != null) {
					if(p.getHealth() <= 4) {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.heal.cannot", lang));
						return true;
					}
					if(partner.getHealth() != partner.getMaxHealth()) {
						p.setHealth(p.getHealth() - 2.0);
						partner.setHealth(partner.getHealth() + 2.0);
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.heal.healed", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.heal.notOnline", lang));
				}
			}
			else if(args[0].equalsIgnoreCase("status")) {
				
				
				if(args.length == 2) {
					
					String from = args[1];
					String uuid = Bukkit.getOfflinePlayer(from).getUniqueId().toString();
					if(DatabaseUtils.get().isMarriedPlayer(uuid)) {
						String partner = DatabaseUtils.get().getPartner(uuid);
						String married = new LanguageController.MessageFormatter().setVariable("partner", partner).format("Messages.status.married", lang);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("variable", married).setVariable("player", from).format("Messages.status.message", lang));
						
					}else {
						String single = new LanguageController.MessageFormatter().format("Messages.status.single", lang);
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("variable", single).setVariable("player", from).format("Messages.status.message", lang));
					}
				}else {
					p.sendMessage("§cUse this command like: /marry status <name>");
				}
			}
			else if(args[0].equalsIgnoreCase("seen")) {
				if(!Modules.getInstance().getConfig().getBoolean("ModuleList.commands.seen.enable")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Modules.getInstance().getConfig().getString("Messages.module_disabled")));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.seen.adopted", lang));
					return true;
				}
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.seen.single", lang));
					return true;
				}
				p.sendMessage(new LanguageController.MessageFormatter().setVariable("lastlogin", mp.getPartnerLastLogged()).format("Messages.seen.lastLogin", lang));
			}
			else if(args[0].equalsIgnoreCase("divorce")) {
				if(mp.isSingle()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.divorce.single", lang));
					return true;
				}
				if(mp.isAdopted()) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.divorce.adopted", lang));
					return true;
				}
				Player partner = Bukkit.getPlayer(mp.getPartner());
				if(partner != null) {
					MarryPlayer mpa = PlayerController.get().get(partner);
					mpa.setSingle(true);
					mpa.setHomeLocation(null);
					mpa.setMarryChat(false);
					mpa.setPvp(true);
					mpa.setMarriedWith("none");
					
					/**
					 * Remove adoptions
					 */
					
					String plang = PlayerController.get().get(partner).getLanguage();
					partner.sendMessage(new LanguageController.MessageFormatter().setVariable("partner", p.getName()).format("Messages.divorce.partnerDivorced", plang));
				}
				/**
				 * Insert in database or if is online
				 */
				/**
				 * Remove adoptions
				 */
				p.sendMessage(new LanguageController.MessageFormatter().setVariable("partner", mp.getPartner()).format("Messages.divorce.divorced", lang));
				mp.setSingle(true);
				mp.setHomeLocation(null);
				mp.setMarryChat(false);
				mp.setPvp(true);
				mp.setMarriedWith("none");
			}
			else if(args[0].equalsIgnoreCase("accept")) {
				if(args.length != 2) {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.acceptUssage", lang));
					return true;
				}
				Player partner = Bukkit.getPlayer(args[1]);
				
				if(marriedMap.containsKey(partner.getUniqueId())) {
					if(marriedMap.get(partner.getUniqueId()) == p.getUniqueId()) {
						if(mp.isAdopted()) {
							p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.adopted", lang));
							return true;
						}
						if(mp.isSingle()) {
							
							mp.setSingle(false);
							mp.setMarriedWith(args[1]);
							
							p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.married", lang));
							
							if(partner != null) {
								String plang = PlayerController.get().get(partner).getLanguage();
								partner.sendMessage(new LanguageController.MessageFormatter().setVariable("player", args[1]).format("Messages.acceptBDeny.accepted", plang));
								MarryPlayer mpa = PlayerController.get().get(partner);
								mpa.setSingle(false);
								mpa.setMarriedWith(p.getName());
								
								boolean b = getRandomBoolean();
								if(b) {
									mp.setEnderChest(true);
									mpa.setEnderChest(false);
								}else {
									mp.setEnderChest(false);
									mpa.setEnderChest(true);
								}
							}
							
						}else {
							p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.alredyMarried", lang));	
						}
						
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("player", args[1]).format("Messages.acceptBDeny.playerNotAsk", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.acceptBDeny.nothing", lang));
				}
			}
			else {
				Player partner = Bukkit.getPlayer(args[0]);
				if(partner != null) {
					MarryPlayer mpa = PlayerController.get().get(partner);
					if(mpa.isSingle()) {
						String plang = PlayerController.get().get(partner).getLanguage();
						partner.sendMessage(new LanguageController.MessageFormatter().setVariable("player", p.getName()).format("Messages.marry.marry", plang));
						
						p.sendMessage(new LanguageController.MessageFormatter().setVariable("player", partner.getName()).format("Messages.marry.marrySended", lang));
						
						marriedMap.put(p.getUniqueId(), partner.getUniqueId());
						marriedMap.put(partner.getUniqueId(), p.getUniqueId());
						
					}else {
						p.sendMessage(new LanguageController.MessageFormatter().format("Messages.marry.playerMarried", lang));
					}
				}else {
					p.sendMessage(new LanguageController.MessageFormatter().format("Messages.marry.notOnline", lang));
				}
			}
		}
		return false;
	}
	
	public boolean getRandomBoolean() {
		Random gen = new Random();
		int prob = gen.nextInt(100);
		return prob <= 50;
	}

}
