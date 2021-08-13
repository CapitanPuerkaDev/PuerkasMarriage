package es.capitanpuerka.marriage.controller;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationController {
	
	private static LocationController instance;

	public String get(final Location location) {
        return String.valueOf(String.valueOf(location.getWorld().getName())) + " " + String.format(Locale.US, "%.2f %.2f %.2f %.2f %.2f", location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
    
    public Location get(final String str) {
        return new Location(Bukkit.getWorld(str.split(" ")[0]), Double.parseDouble(str.split(" ")[1]), Double.parseDouble(str.split(" ")[2]), Double.parseDouble(str.split(" ")[3]), (Float)Float.parseFloat(str.split(" ")[4]), (Float)Float.parseFloat(str.split(" ")[5]));
    }
    
    public static LocationController get() {
    	if(LocationController.instance == null) {
    		LocationController.instance = new LocationController();
    	}
    	return LocationController.instance;
    }
	
}
