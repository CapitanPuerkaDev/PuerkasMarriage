package es.capitanpuerka.marriage.config;

import java.io.*;
import org.bukkit.plugin.*;
import org.bukkit.configuration.file.*;
import java.util.*;
import com.google.common.collect.*;
import java.util.regex.*;
import org.bukkit.*;

public final class Modules
{
    private static Modules instance;
    private static final Pattern COLOR_PATTERN;
    private final FileConfiguration storage;
    private final File storageFile;
    
    static {
        COLOR_PATTERN = Pattern.compile("(?i)(&|" + String.valueOf('\ufffd') + ")[0-9A-FK-OR]");
    }
    
    public Modules(final Plugin plugin) {
        this.storageFile = new File(plugin.getDataFolder(), "modules.yml");
        if (!this.storageFile.exists()) {
            plugin.saveResource("modules.yml", false);
        }
        this.storage = (FileConfiguration)YamlConfiguration.loadConfiguration(this.storageFile);
        Modules.instance = this;
    }
    
    public static String stripColor(final String input) {
        if (input == null) {
            return "";
        }
        return Modules.COLOR_PATTERN.matcher(input).replaceAll("");
    }
    
    public String getMessage(final String format) {
        if (this.storage.contains(format)) {
            return this.storage.getString(format);
        }
        return null;
    }
    
    public boolean reload() {
    	boolean b = false;
    	try {
    		this.storage.save(this.storageFile);
    		b = true;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		b = false;
    	}
    	return b;
    }
    
    public void addDefault(String where, String what) {
    	try {
    		this.storage.addDefault(where, what);
    		this.storage.save(this.storageFile);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void configure() {
    	if(getMessage("ModuleList.commands.seen.enable") == null) {
    		addDefault("ModuleList.commands.seen.enable", "true");
    	}
    	reload();
    }
    
    public FileConfiguration getConfig() {
    	return storage;
    }
    
    public static Modules getInstance() {
        return Modules.instance;
    }
    
}
