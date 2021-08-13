package es.capitanpuerka.marriage.controller;

import java.util.ArrayList;
import java.util.HashMap;

public class GenderController {
	
	private static HashMap<String, GenderController> gendersMap = new HashMap<String, GenderController>();
	private static ArrayList<GenderController> genders = new ArrayList<GenderController>();
	
	private String keyName;
	private String name;
	private String simbol;
	
	public GenderController(String keyName, String name, String simbol) {
		this.keyName = keyName;
		this.name = name;
		this.simbol = simbol;
		genders.add(this);
		gendersMap.put(keyName, this);
	}
	
	public String getKeyName() {
		return this.keyName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSimbol() {
		return this.simbol;
	}
	
	public static GenderController getGender(String key) {
		return gendersMap.get(key);
	}
	
	public static GenderController getGenerByName(String name) {
		for(GenderController arraygenders : genders) {
			if(arraygenders.getName().equalsIgnoreCase(name)) {
				return arraygenders;
			}
		}
		return null;
	}
	
	public static ArrayList<GenderController> getAllGenders(){
		return genders;
	}
	
}
