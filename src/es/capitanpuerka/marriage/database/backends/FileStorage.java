package es.capitanpuerka.marriage.database.backends;

import org.bukkit.entity.*;

import es.capitanpuerka.marriage.Main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;
import java.io.*;

public class FileStorage
{
    private File file;
    private FileConfiguration config;
    private Player p;
    
    public FileStorage(Player p) {
        this.p = p;
        copyDefaults(this.file = new File("plugins/PuerkasMarriage/player_data/" + this.p.getUniqueId().toString() + ".yml"));
        new YamlConfiguration();
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
    }
    
    public void createDataPlayer() {
        try {
            if (!this.file.exists()) {
            	Main.get().log("&aA new file has been created to save "+this.p.getName()+"'s adta");
                this.file.createNewFile();
            }
            this.config.set("player_name", this.p.getName());
            this.config.save(this.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public FileConfiguration get() {
        return this.config;
    }
    
    public void save() {
        try {
            this.config.save(this.file);
        }
        catch (Exception e) {
        	Main.get().log("&cAn error occurred while trying to save "+p.getName()+"'s data.");
            e.printStackTrace();
        }
    }
    
    private static void copyDefaults(final File playerFile) {
        FileConfiguration playerConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(playerFile);
        Reader defConfigStream = new InputStreamReader(Main.get().getResource("player_data.yml"));
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            playerConfig.options().copyDefaults(true);
            playerConfig.setDefaults(defConfig);
            try {
                playerConfig.save(playerFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
