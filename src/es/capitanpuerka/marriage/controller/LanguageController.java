package es.capitanpuerka.marriage.controller;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.*;

import es.capitanpuerka.marriage.Main;

public class LanguageController {
	
	private static LanguageController instance;
	
	private static List<String> languages;
	
	private static Map<String, FileConfiguration> loadedLanguages;
	HashMap<String, String> message = new HashMap<String, String>();
	private static String folder = "PuerkasMarriage";
	
	
	static {
		loadedLanguages = new HashMap<String, FileConfiguration>();
		languages = new ArrayList<String>();
	}
	
	public List<String> getLanguages() {
		return languages;
	}
	
	public String getMessage(String lang, String format) {
		if(get(lang).contains(format)) {
			return this.get(lang).getString(format);
		}
		return null;
	}
	
	public int getInt(String lang, String format) {
		if(get(lang).contains(format)) {
			return this.get(lang).getInt(format);
		}
		return 0;
	}
	
	public boolean reload() {
    	boolean b = false;
    	try {
    		for(String langs : getLanguages()) {
    			FileConfiguration fileConfig = get(langs);
    			File file = new File("plugins/"+folder+"/languages", langs + ".yml");
    			fileConfig.save(file);
    		}
    		b = true;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		b = false;
    	}
    	return b;
    }
	
	public List<String> getListMessage(String lang, String format) {
		List<String> msg = new ArrayList<String>();
		if(get(lang).contains(format)) {
			for(String s : get(lang).getStringList(format)) {
				msg.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			return msg;
		}
		return null;
	}
	
	public List<String> getListMessage(String lang, String format, String replacer, String toReplace) {
		List<String> msg = new ArrayList<String>();
		if(get(lang).contains(format)) {
			for(String s : get(lang).getStringList(format)) {
				msg.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll(replacer, toReplace)));
			}
			return msg;
		}
		return null;
	}
	
	
	public void initLanguage(String lang) {
		languages.add(lang);
		this.saveLang(lang);
		this.load(lang);
	}
	
	public FileConfiguration get(String lang) {
        if (isLanguageLoaded(lang)) {
            return loadedLanguages.get(lang);
        }
        return null;
    }
	
	private static void copyDefaults(final File file) {
        FileConfiguration playerConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        Reader defConfigStream = new InputStreamReader(Main.get().getResource("default_messages.yml"));
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            playerConfig.options().copyDefaults(true);
            playerConfig.setDefaults(defConfig);
            try {
                playerConfig.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void load(String lang) {
		File mierda = new File("plugins/"+folder+"/languages/");
        final File file = new File("plugins/"+folder+"/languages", lang + ".yml");
        if (!file.exists()) {
            try {
            	if (!mierda.exists()) {
                    mierda.mkdir();
                }
            	file.createNewFile();
            	copyDefaults(file);
            }
            catch (Exception e) {
            	e.printStackTrace();
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasDuels] §cAn error occurred while trying to load the language '"+lang+"'");
            }
        }
        if (!isLanguageLoaded(lang)) {
        	loadedLanguages.put(lang.replaceAll(".yml", ""), (FileConfiguration)YamlConfiguration.loadConfiguration(file));
        }
    }
	
	public void saveLang(String lang) {
        final File file = new File("plugins/"+folder+"/languages/", lang + ".yml");
        if (isLanguageLoaded(lang)) {
            try {
            	loadedLanguages.get(lang).save(file);
            }
            catch (Exception e) {
            	e.printStackTrace();
            	Bukkit.getConsoleSender().sendMessage("§b[PuerkasDuels] §cAn error occurred while trying to save the language '"+lang+"'");
            }
        }
    }
	
	public boolean isLanguageLoaded(String lang) {
        return loadedLanguages.containsKey(lang);
    }

	public static LanguageController get() {
		if(LanguageController.instance == null) {
			LanguageController.instance = new LanguageController();
		}
		return LanguageController.instance;
	}
	
	public static class MessageFormatter
    {
        private static final Pattern PATTERN;
        private final Map<String, String> variableMap;
        
        static {
            PATTERN = Pattern.compile("(?i)(\\{[a-z0-9_]+\\})");
        }
        
        public MessageFormatter() {
            this.variableMap = new HashMap<String, String>();
        }
        
        public MessageFormatter setVariable(final String format, final String value) {
            if (format != null && !format.isEmpty()) {
                if (value == null) {
                    this.variableMap.remove(format);
                }
                else {
                    this.variableMap.put(format, value);
                }
            }
            return this;
        }
        
        public String format(String message, String lang) {
            if (message == null || message.isEmpty()) {
                return "";
            }
            if(LanguageController.get().getMessage(lang, message) != null) {
            	message = LanguageController.get().getMessage(lang, message);
            }
            final Matcher matcher = MessageFormatter.PATTERN.matcher(message);
            while (matcher.find()) {
                String variable = matcher.group();
                variable = variable.substring(1, variable.length() - 1);
                String value = this.variableMap.get(variable);
                if (value == null) {
                    value = "";
                }
                message = message.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(value));
            }
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        
        public MessageFormatter setVariable(final String format, final int number) {
            final String value = Integer.toString(number);
            if (format != null && !format.isEmpty()) {
                if (value == null) {
                    this.variableMap.remove(format);
                }
                else {
                    this.variableMap.put(format, value);
                }
            }
            return this;
        }
    }
}
