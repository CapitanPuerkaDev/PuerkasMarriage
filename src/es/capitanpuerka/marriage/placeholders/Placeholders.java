package es.capitanpuerka.marriage.placeholders;

import me.clip.placeholderapi.expansion.*;
import org.bukkit.entity.*;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.controller.LanguageController;
import es.capitanpuerka.marriage.controller.PlayerController;

public class Placeholders extends PlaceholderExpansion
{
    public void load() {
        this.register();
    }
    
    public boolean persist() {
        return true;
    }
    
    public boolean canRegister() {
        return true;
    }
    
    public String getIdentifier() {
        return "puerkasmarriage";
    }
    
    public String getAuthor() {
        return Main.get().getDescription().getAuthors().toString();
    }
    
    public String getVersion() {
        return Main.get().getDescription().getVersion();
    }
    
    public String onPlaceholderRequest(final Player player, final String identifier) {
        if (player == null) {
            return "";
        }
        String lang = PlayerController.get().get(player).getLanguage();
        if(identifier.equalsIgnoreCase("married")) {
        	String notmarried = LanguageController.get().getMessage(lang, "Placeholders.married_status.not_married");
        	String married = LanguageController.get().getMessage(lang, "Placeholders.married_status.married");
        	String toReturn = "";
        	if(!PlayerController.get().get(player).isSingle()) {
        		toReturn = married;
        	}
        	else {
        		toReturn = notmarried;
        	}
        	return String.valueOf(toReturn);
        }
        if(identifier.equalsIgnoreCase("married_text")) {
        	String notmarried = LanguageController.get().getMessage(lang, "Placeholders.married_text.not_married");
        	String married = LanguageController.get().getMessage(lang, "Placeholders.married_text.married");
        	String toReturn = "";
        	if(!PlayerController.get().get(player).isSingle()) {
        		toReturn = married;
        	}
        	else {
        		toReturn = notmarried;
        	}
        	return String.valueOf(toReturn);
        }
        if(identifier.equalsIgnoreCase("gender")) {
        	String male = LanguageController.get().getMessage(lang, "Placeholders.gender.male");
        	String female = LanguageController.get().getMessage(lang, "Placeholders.gender.female");
        	String toReturn = "";
        	if(PlayerController.get().get(player).isMale()) {
        		toReturn = male;
        	}else {
        		toReturn = female;
        	}
        	return String.valueOf(toReturn);
        }
        if(identifier.equalsIgnoreCase("gender_text")) {
        	String male = LanguageController.get().getMessage(lang, "Placeholders.gender_text.male");
        	String female = LanguageController.get().getMessage(lang, "Placeholders.gender_text.female");
        	String toReturn = "";
        	if(PlayerController.get().get(player).isMale()) {
        		toReturn = male;
        	}else {
        		toReturn = female;
        	}
        	return String.valueOf(toReturn);
        }
        if(identifier.equalsIgnoreCase("marriedwith")) {
        	String toReturn = "";
        	if(!PlayerController.get().get(player).isSingle()) {
        		toReturn = PlayerController.get().get(player).getPartner();
        	}else {
        		toReturn = LanguageController.get().getMessage(lang, "Placeholders.marriedwith.not_married");
        	}
        	return String.valueOf(toReturn);
        }
        if(identifier.equalsIgnoreCase("partnerlastloggedin")) {
        	String toReturn = "";
        	if(!PlayerController.get().get(player).isSingle()) {
        		toReturn = PlayerController.get().get(player).getPartnerLastLogged();
        	}else {
        		toReturn = LanguageController.get().getMessage(lang, "Placeholders.partner_last_logged_in.not_married");;
        	}
        	return String.valueOf(toReturn);
        }
        return null;
    }
}
