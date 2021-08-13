package es.capitanpuerka.marriage.database.backends.types;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.player.MarryPlayer;


public class MySQL {
	
private static MySQL instance;
	
	private static Connection connection;
	
	public Connection getConnection() {
		String host = Main.get().getConfig().getString("Backend.hostname");
		String db = Main.get().getConfig().getString("Backend.database");
		String user = Main.get().getConfig().getString("Backend.username");
		String password = Main.get().getConfig().getString("Backend.password");
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db + "?autoReconnect=true", user, password);
            return connection;
        } catch (SQLException ex) {
        	Main.get().getLogger().log(Level.SEVERE,"MySQL exception on initialize", ex);
        }
        return null;
    }
	
	public void close() {
        if (MySQL.connection != null) {
            try {
                MySQL.connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void setupConnection() throws SQLException {
        Throwable t = null;
        try {
            final Connection connection = this.getConnection();
            try {
                final Statement statement = connection.createStatement();
                final DatabaseMetaData metaData = connection.getMetaData();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `puerkasmarriage` (" + "`id` INT NOT NULL AUTO_INCREMENT, " + "`player_name` VARCHAR(255), " + " `uuid` varchar(255) UNIQUE, " + "`ismale` INT(2) DEFAULT '1', " + "`issingle` INT(1) DEFAULT '2', " + "`marrychat` INT(1) DEFAULT '2', " + "`enabled_pvp` INT(1) DEFAULT '2', " + "`family_chat` INT(1) DEFAULT '2', " + "`marriedwith` VARCHAR(20) DEFAULT 'Unknown', " + "`partner_last_login` VARCHAR(50) DEFAULT 'Unknown', " + "`familyname` VARCHAR(30) DEFAULT 'Unknown', " + "`adoptions` VARCHAR(1000) DEFAULT 'none', " + "PRIMARY KEY (id), " + "KEY `marrydata_username_idx` (`player_name`(255))" + ") ENGINE=InnoDB;");
                final String table_NAME = "puerkasmarriage";
                final String s = "player_name";
                final String s2 = "uuid";
                final String s3 = "ismale";
                final String s6 = "issingle";
                final String s7 = "marrychat";
                final String s8 = "enabled_pvp";
                final String s9 = "family_chat";
                final String s10 = "marriedwith";
                final String s11 = "partner_last_login";
                final String s12 = "familyname";
                final String s13 = "adoptions";//20, 50, 30, 1000
                final ResultSet columns = metaData.getColumns(null, null, table_NAME, "player_name");
                if (!columns.next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s2 + " VARCHAR(255) NOT NULL UNIQUE AFTER " + s + ";");
                }
                columns.close();
                final ResultSet columns2 = metaData.getColumns(null, null, table_NAME, s3);
                if (!columns2.next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s3 + " INT(1) DEFAULT 1 AFTER " + s3 + ";");
                }
                columns2.close();
                final ResultSet columns7 = metaData.getColumns(null, null, table_NAME, s6);
                if (!columns7.next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s6 + " INT(1) DEFAULT 1 AFTER " + s6 + ";");
                }
                columns7.close();
                if (!metaData.getColumns(null, null, table_NAME, s7).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s7 + " INT(1) DEFAULT 2 AFTER " + s7 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s8).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s8 + " INT(1) DEFAULT 2 AFTER " + s8 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s9).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s9 + " INT(1) DEFAULT 2 AFTER " + s9 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s10).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s10 + " VARCHAR(20) DEFAULT NULL AFTER " + s10 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s11).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s11 + " VARCHAR(50) DEFAULT NULL AFTER " + s11 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s12).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s12 + " VARCHAR(30) DEFAULT NULL AFTER " + s12 + ";");
                }
                if (!metaData.getColumns(null, null, table_NAME, s13).next()) {
                    statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s13 + " VARCHAR(100) DEFAULT NULL AFTER " + s13 + ";");
                }
                statement.close();
            }
            finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        finally {
            if (t == null) {
                final Throwable t2 = null;
                t = t2;
            }
            else {
                final Throwable t2 = null;
                if (t != t2) {
                    t.addSuppressed(t2);
                }
            }
        }
    }
	
	    public void loadPlayerData(MarryPlayer skyPlayer) {
	        try {
	            Throwable t = null;
	            try {
	                final Connection connection = this.getConnection();
	                try {
	                    PreparedStatement preparedStatement = connection.prepareStatement(String.format("SELECT * FROM puerkasmarriage WHERE uuid='%s'", skyPlayer.getPlayer().getUniqueId().toString()));
	                    preparedStatement.execute();
	                    final ResultSet resultSet = preparedStatement.getResultSet();
	                    if (resultSet.next()) {
	                    	if(resultSet.getInt("ismale") == 1) {
	                    		skyPlayer.setMale(true);
	                    	}else {
	                    		skyPlayer.setMale(false);
	                    	}
	                    	if(resultSet.getInt("issingle") == 1) {
	                    		skyPlayer.setSingle(true);
	                    	}else {
	                    		skyPlayer.setSingle(false);
	                    	}
	                    	if(resultSet.getInt("marrychat") == 1) {
	                    		skyPlayer.setMarryChat(true);
	                    	}else {
	                    		skyPlayer.setMarryChat(false);
	                    	}
	                    	if(resultSet.getInt("enabled_pvp") == 1) {
	                    		skyPlayer.setPvp(true);
	                    	}else {
	                    		skyPlayer.setPvp(false);
	                    	}
	                    	if(resultSet.getInt("family_chat") == 1) {
	                    		skyPlayer.setFamilyChat(true);
	                    	}else {
	                    		skyPlayer.setFamilyChat(false);
	                    	}
	                    	if(!skyPlayer.isSingle()) {
	                    		skyPlayer.setMarriedWith(resultSet.getString("marriedwith"));
	                    		skyPlayer.setPartnerLastLogged(resultSet.getString("partner_last_login"));
	                    		skyPlayer.setFamilyName(resultSet.getString("familyname"));
	                    		skyPlayer.setAdoptions(resultSet.getString("adoptions"));
	                    	}
	                    }
	                    else {
	                        preparedStatement.close();
	                        preparedStatement = connection.prepareStatement(String.format("INSERT INTO puerkasmarriage (player_name, uuid) VALUES ('%s', '%s')", skyPlayer.getName(), skyPlayer.getPlayer().getUniqueId().toString()));
	                        preparedStatement.executeUpdate();
	                        preparedStatement.close();
	                    }
	                    resultSet.close();
	                    preparedStatement.close();
	                }
	                finally {
	                    if (connection != null) {
	                        connection.close();
	                    }
	                }
	            }
	            finally {
	                if (t == null) {
	                    final Throwable t2 = null;
	                    t = t2;
	                }
	                else {
	                    final Throwable t2 = null;
	                    if (t != t2) {
	                        t.addSuppressed(t2);
	                    }
	                }
	            }
	        }
	        catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    public void unloadPlayer(MarryPlayer gamePlayer) {
	    	String querry1 = "UPDATE puerkasmarriage SET ismale=?, issingle=?, marrychat=?, enabled_pvp=?, family_chat=?, marriedwith=?, partner_last_login=?, familyname=?, adoptions=? " + "WHERE player_name=?";
	    	uploadPlayerData(gamePlayer, querry1);
	    }
	    
	    public void uploadPlayerData(MarryPlayer gamePlayer, String querry) {
	        try {
	            Throwable t = null;
	            try {
	                final Connection connection = this.getConnection();
	                try {
	                    final PreparedStatement prepareStatement = connection.prepareStatement(querry);
	                    prepareStatement.setInt(1, gamePlayer.isMale() ? 1 : 2);
	                    prepareStatement.setInt(2, gamePlayer.isSingle() ? 1 : 2);
	                    prepareStatement.setInt(3, gamePlayer.isMarryChat() ? 1 : 2);
	                    prepareStatement.setInt(4, gamePlayer.isPvpEnabled() ? 1 : 2);
	                    prepareStatement.setInt(5, gamePlayer.isFamilyChat() ? 1 : 2);
	                    prepareStatement.setString(6, gamePlayer.getPartner());
	                    prepareStatement.setString(7, gamePlayer.getPartnerLastLogged());
	                    prepareStatement.setString(8, gamePlayer.getFamilyName());
	                    prepareStatement.setString(9, gamePlayer.getStringAdoptions());
	                    prepareStatement.setString(10, gamePlayer.getName());
	                    prepareStatement.executeUpdate();
	                    prepareStatement.close();
	                }
	                finally {
	                    if (connection != null) {
	                        connection.close();
	                    }
	                }
	            }
	            finally {
	                if (t == null) {
	                    final Throwable t2 = null;
	                    t = t2;
	                }
	                else {
	                    final Throwable t2 = null;
	                    if (t != t2) {
	                        t.addSuppressed(t2);
	                    }
	                }
	            }
	        }
	        catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    /**
	     * Methods
	     */
	    
	    public boolean exist(String uuid) {
	    	boolean exist = false;
	        try {
	            final Statement s = getConnection().createStatement();
	            final ResultSet rs = s.executeQuery("SELECT * FROM `puerkasmarriage` WHERE uuid = '" + uuid + "';");
	            if (rs.next()) {
	            	exist = true;
	            	rs.close();
	            }
	            else {
	            	exist = false;
	            	rs.close();
	            }
	        }
	        catch (Exception e) {
	        	exist = false;
	            e.printStackTrace();
	        }
	        return exist;
	    }
	    
	    public String getPartner(String uuid) {
	    	String partner = "";
	        try {
	            final Statement s = getConnection().createStatement();
	            final ResultSet result = s.executeQuery("SELECT `marriedwith` FROM `puerkasmarriage` WHERE `uuid` = '" + uuid + "';");
	            if (result.next()) {
	            	partner = result.getString("marriedwith");
	            }
	        }
	        catch (Exception ex) {
	        	ex.printStackTrace();
	        }
	        return partner;
	    }
	    
	    public int isMarried(String uuid) {
	        int i = 0;
	        try {
	            final Statement s = getConnection().createStatement();
	            final ResultSet result = s.executeQuery("SELECT `issingle` FROM `puerkasmarriage` WHERE `uuid` = '" + uuid + "';");
	            if (result.next()) {
	                i = result.getInt("issingle");
	            }
	        }
	        catch (Exception ex) {
	        	ex.printStackTrace();
	        }
	        return i;
	    }
	
	public static MySQL getInstance() {
		if(MySQL.instance == null) {
			MySQL.instance = new MySQL();
		}
		return MySQL.instance;
	}

}
