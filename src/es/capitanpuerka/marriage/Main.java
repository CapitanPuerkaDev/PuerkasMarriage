package es.capitanpuerka.marriage;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import es.capitanpuerka.marriage.commands.FamilyCMD;
import es.capitanpuerka.marriage.commands.MarryCMD;
import es.capitanpuerka.marriage.config.Modules;
import es.capitanpuerka.marriage.controller.GenderController;
import es.capitanpuerka.marriage.controller.LanguageController;
import es.capitanpuerka.marriage.database.DatabaseType;
import es.capitanpuerka.marriage.database.backends.types.MySQL;
import es.capitanpuerka.marriage.database.backends.types.SQLite;
import es.capitanpuerka.marriage.listener.PlayerListener;
import es.capitanpuerka.marriage.metrics.Metrics;
import es.capitanpuerka.marriage.placeholders.Placeholders;
import es.capitanpuerka.marriage.updater.UpdateChecker;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private static DatabaseType databaseType;
	
	public void onEnable() {
		Main.instance = this;
		Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aLoading plugin...");
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getCommand("marry").setExecutor(new MarryCMD());
		getCommand("family").setExecutor(new FamilyCMD());
		new Modules(this);
		Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aPlugin files loaded. Loading database...");
		setDatabaseType();
		if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders().register();
            Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aPlaceholders has been loaded!");
        }
		log("§aLoading Language System...");
		for(String langs : getConfig().getStringList("Languages")) {
        	LanguageController.get().initLanguage(langs);
        	log("§aLoaded language §6" + langs + "§a!");
        }
		log("§aAll languages has been loaded!");
		Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aChecking updates...");
		new UpdateChecker(this, 93194).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aThere is not a new update available.");
            } else {
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §9&m---------------------------------:");
            	Bukkit.getConsoleSender().sendMessage("§f");
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aThere is a new update available.");
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §ahttps://www.spigotmc.org/resources/authors/capitanpuerkas.920929/");
            	Bukkit.getConsoleSender().sendMessage("§f");
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §9&m---------------------------------:");
            }
        });
		Metrics metrics = new Metrics(this, 12289);
		metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
		Bukkit.getConsoleSender().sendMessage("§b[PuerkasMarriage] §aPlugin has been loaded!");
	}
	
	public void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "§7[§bPuerkasMarriage§7] " + s));
	}
	
	public void setDatabaseType() {
		String dbtype = getConfig().getString("Backend.type").toUpperCase();
		if(dbtype.equalsIgnoreCase("NONE")) {
			databaseType = DatabaseType.NONE;
			log("§aThe database has been set as 'null', therefore no statistics will be saved.");
		}
		else if(dbtype.equalsIgnoreCase("FILE")) {
			databaseType = DatabaseType.FILE;
			log("§aThe database has been set as 'File', the data will be saved in files.");
		}
		else if(dbtype.equalsIgnoreCase("SQLITE")) {
			databaseType = DatabaseType.SQL;
			try {
				SQLite.getInstance().setup();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			log("§aThe database has been set as 'SQL', the data will be saved in SQL Database.");
		}
		else if(dbtype.equalsIgnoreCase("MYSQL")) {
			databaseType = DatabaseType.MYSQL;
			try {
				MySQL.getInstance().setupConnection();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log("§aThe database has been set as 'MySQL', the data will be saved in MySQL Database.");
		}
		else {
			databaseType = DatabaseType.NONE;
			log("§aThe database has been set as 'null', therefore no data will be saved.");
		}
	}
	
	public void loadGenders() {
		for(String keys : getConfig().getConfigurationSection("Genders").getKeys(false)) {
			String key = keys;
			String name = getConfig().getString("Genders." + key + ".name");
			String simbol = getConfig().getString("Genders." + key + ".simbol");
			
			GenderController gender = new GenderController(key, name, simbol);
		}
		GenderController unknown = new GenderController("Unknown", "Unknown", "");
	}
	
	public static DatabaseType getDatabaseType() {
		return Main.databaseType;
	}
	
	public static Main get() {
		return Main.instance;
	}

}
