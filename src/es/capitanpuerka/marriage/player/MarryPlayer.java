package es.capitanpuerka.marriage.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import es.capitanpuerka.marriage.controller.GenderController;
import es.capitanpuerka.marriage.database.DatabaseUtils;

public class MarryPlayer {
	
	private Player player;
	
	private String language;
	
	private String partner;
	
	private boolean hasChest;
	
	private String partnerLastLogged;
	
	private boolean marryChat;
	private boolean familyChat;
	private boolean isSingle;
	
	private boolean isMale;
	
	private GenderController gender;
	
	private boolean isAdopted;
	private String adoptFamilyName;
	
	private boolean pvp;
	
	private Location marryHome;
	
	private String adoptions;
	private List<String> adopted;
	private String familyName;
	
	public MarryPlayer(Player p) {
		this.player = p;
		this.partner = "Unknown";
		this.marryChat = false;
		this.familyChat = false;
		this.language = "English";
		this.isSingle = true;
		this.isMale = true;
		this.hasChest = false;
		this.isAdopted = false;
		this.pvp = true;
		this.marryHome = null;
		this.partnerLastLogged = "Unknown";
		this.adoptFamilyName = "Unknown";
		this.familyName = "none";
		this.adopted = new ArrayList<String>();
		this.adoptions = "";
		this.gender = GenderController.getGender("Unknown");
		DatabaseUtils.get().loadPlayer(this);
	}
	
	public GenderController getGender() {
		return this.gender;
	}
	
	public void setGender(GenderController gender) {
		this.gender = gender;
	}
	
	public String getAdoptFamilyName() {
		return this.adoptFamilyName;
	}
	
	public void setAdoptedFamilyName(String name) {
		this.adoptFamilyName = name;
	}
	
	public boolean isAdopted() {
		return this.isAdopted;
	}
	
	public void setAdopted(boolean b) {
		this.isAdopted = b;
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getStringAdoptions() {
		return this.adoptions;
	}
	
	public void setAdoptions(String adoptions) {
		this.adoptions = adoptions;
	}
	
	public boolean isFamilyChat() {
		return this.familyChat;
	}
	
	public void setFamilyChat(boolean b) {
		this.familyChat = b;
	}
	
	public String getFamilyName() {
		return this.familyName;
	}
	
	public void setFamilyName(String name) {
		this.familyName = name;
	}
	
	public List<String> getAdoptedMembers() {
		return this.adopted;
	}
	
	public boolean hasEnderChest() {
		return this.hasChest;
	}
	
	public void setEnderChest(boolean b) {
		this.hasChest = b;
	}
	
	public void setMarriedWith(String partner) {
		this.partner = partner;
	}
	
	public void setPartnerLastLogged(String s) {
		this.partnerLastLogged = s;
	}
	
	public void setMarryChat(boolean chat) {
		this.marryChat = chat;
	}
	
	public void setSingle(boolean single) {
		this.isSingle = single;
	}
	
	public void setMale(boolean male) {
		this.isMale = male;
	}
	
	public void setHomeLocation(Location loc) {
		this.marryHome = loc;
	}
	
	public void setPvp(boolean b) {
		this.pvp = b;
	}
	
	public boolean isMale() {
		return this.isMale;
	}
	
	public boolean isSingle() {
		return this.isSingle;
	}
	
	public boolean isMarryChat() {
		return this.marryChat;
	}
	
	public boolean isPvpEnabled() {
		return this.pvp;
	}
	
	public Location getHomeLocation() {
		return this.marryHome;
	}
	
	public String getPartnerLastLogged() {
		return this.partnerLastLogged;
	}
	
	public String getPartner() {
		return this.partner;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String getName() {
		return getPlayer().getName();
	}

}
