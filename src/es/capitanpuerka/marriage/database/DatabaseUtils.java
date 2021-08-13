package es.capitanpuerka.marriage.database;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.database.backends.FileStorage;
import es.capitanpuerka.marriage.database.backends.types.MySQL;
import es.capitanpuerka.marriage.database.backends.types.SQLite;
import es.capitanpuerka.marriage.player.MarryPlayer;

public class DatabaseUtils {
	
	private static DatabaseUtils instance;
	
	public void loadPlayer(MarryPlayer gp) {
		DatabaseType dbtype = Main.getDatabaseType();
		if(dbtype != DatabaseType.NONE) {
			if(dbtype == DatabaseType.FILE) {
				FileStorage fstorage = new FileStorage(gp.getPlayer());
				fstorage.createDataPlayer();
				if(fstorage.get().getInt("Marriage.issingle") == 1) {
					gp.setSingle(true);
				}else {
					gp.setSingle(false);
				}
				int isMale = fstorage.get().getInt("Marriage.ismale");
				if(isMale == 1) {
					gp.setMale(true);
				}else {
					gp.setMale(false);
				}
				if(fstorage.get().getInt("Marriage.marrychat") == 1) {
					gp.setMarryChat(true);
				}else {
					gp.setMarryChat(false);
				}
				if(!gp.isSingle()) {
					gp.setMarriedWith(fstorage.get().getString("Marriage.marriedwith"));
					gp.setPartnerLastLogged(fstorage.get().getString("Marriage.partner_last_login"));
				}
				if(fstorage.get().getInt("Family.hasFamily") == 1) {
					if(fstorage.get().getString("Family.familyname").equalsIgnoreCase("Unknown")) {
						gp.setFamilyName(fstorage.get().getString("Family.familyname"));
					}
					if(fstorage.get().getInt("Family.family_chat") == 1) {
						if(!gp.isMarryChat()) {
							gp.setFamilyChat(true);
						}
					}
				}
			}
			else if(dbtype == DatabaseType.SQL) {
				SQLite.getInstance().loadPlayerData(gp);
			}
			else if(dbtype == DatabaseType.MYSQL) {
				MySQL.getInstance().loadPlayerData(gp);
			}
		}
	}
	
	public void savePlayer(MarryPlayer gp) {
		DatabaseType dbtype = Main.getDatabaseType();
		if(dbtype != DatabaseType.NONE) {
			if(dbtype == DatabaseType.FILE) {
				FileStorage fstorage = new FileStorage(gp.getPlayer());
				if(gp.isSingle()) {
					fstorage.get().set("Marriage.issingle", 1);
				}else {
					fstorage.get().set("Marriage.issingle", 2);
				}
				if(gp.isMale()) {
					fstorage.get().set("Marriage.ismale", 1);
				}else {
					fstorage.get().set("Marriage.ismale", 2);
				}
				if(gp.isMarryChat()) {
					fstorage.get().set("Marriage.marrychat", 1);
				}else {
					fstorage.get().set("Marriage.marrychat", 2);
				}
				if(gp.isPvpEnabled()) {
					fstorage.get().set("Marriage.enabled_pvp", 1);
				}else {
					fstorage.get().set("Marriage.enabled_pvp", 2);
				}
				fstorage.get().set("Marriage.marriedwith", gp.getPartner());
				fstorage.get().set("Marriage.partner_last_login", gp.getPartnerLastLogged());
				
				if(!gp.isSingle()) {
					if(gp.isFamilyChat()) {
						fstorage.get().set("Family.family_chat", 1);
					}else {
						fstorage.get().set("Family.family_chat", 2);
					}
					fstorage.get().set("Family.familyname", gp.getFamilyName());
					List<String> adoptions = gp.getAdoptedMembers();
					fstorage.get().set("Family.adoptions", adoptions);
				}
				fstorage.save();
			}
			else if(dbtype == DatabaseType.SQL) {
				SQLite.getInstance().unloadPlayer(gp);
			}
			else if(dbtype == DatabaseType.MYSQL) {
				MySQL.getInstance().unloadPlayer(gp);
			}
		}
	}
	
	public boolean isMarriedPlayer(String uuid) {
		boolean married = false;
		if(Main.getDatabaseType() == DatabaseType.FILE) {
			if(FileExist(uuid)) {
				FileConfiguration pconfig = (FileConfiguration)YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
				if(pconfig.getInt("Marriage.issingle") == 1) {
					married = false;
				}else {
					married = true;
				}
			}else {
				married = false;
			}
		}
		else if(Main.getDatabaseType() == DatabaseType.SQL) {
			int mierda = SQLite.getInstance().isMarried(uuid);
			if(mierda == 1) {
				married = true;
			}else {
				married = false;
			}
		}
		else if(Main.getDatabaseType() == DatabaseType.MYSQL) {
			int mierda = MySQL.getInstance().isMarried(uuid);
			if(mierda == 1) {
				married = true;
			}else {
				married = false;
			}
		}
		return married;
	}
	
	public String getPartner(String uuid) {
		String partner = "";
		if(Main.getDatabaseType() == DatabaseType.FILE) {
			if(FileExist(uuid)) {
				FileConfiguration pconfig = (FileConfiguration)YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
				partner = pconfig.getString("marriedwith");
			}else {
				partner = "Unknown";
			}
		}
		else if(Main.getDatabaseType() == DatabaseType.SQL) {
			return SQLite.getInstance().getPartner(uuid);
		}
		else if(Main.getDatabaseType() == DatabaseType.MYSQL) {
			return MySQL.getInstance().getPartner(uuid);
		}
		return partner;
	}
	
	/**
	 * File System
	 */
	
	public boolean FileExist(String uuid) {
        final File file = new File("plugins/PuerkasMarriage/games/" + uuid + ".yml");
        return file.exists();
    }
	
	public File getPlayerFile(String uuid) {
		if(FileExist(uuid)) {
			File file = new File("plugins/PuerkasMarriage/games/" + uuid + ".yml");
			return file;
		}
        return null;
    }
	
	public String getFilePartner(String uuid) {
		String partner = "";
		if(FileExist(uuid)) {
			FileConfiguration pconfig = (FileConfiguration)YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
			partner = pconfig.getString("Marriage.marriedwith");
		}
		return partner;
	}
	
	public static DatabaseUtils get() {
		if(DatabaseUtils.instance == null) {
			DatabaseUtils.instance = new DatabaseUtils();
		}
		return DatabaseUtils.instance;
	}

}
