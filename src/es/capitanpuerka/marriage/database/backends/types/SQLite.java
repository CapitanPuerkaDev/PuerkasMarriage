package es.capitanpuerka.marriage.database.backends.types;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import es.capitanpuerka.marriage.Main;
import es.capitanpuerka.marriage.player.MarryPlayer;

public class SQLite {
	
private static SQLite instance;
	
	private static Connection connection;
	
	public Connection getSQLConnection() {
        File dataFolder = new File(Main.get().getDataFolder(), "puerkasmarriage.db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Main.get().getLogger().log(Level.SEVERE, "File write error: puerkasmarriage.db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
        	Main.get().getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
        	Main.get().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
	
	public void setup() throws SQLException {
		getSQLConnection();
        Statement statement = null;
        Statement statement2 = null;
        ResultSet set = null;
        try {
            statement = SQLite.connection.createStatement();
            final DatabaseMetaData metaData = SQLite.connection.getMetaData();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'puerkasmarriage' ('id' INTEGER PRIMARY KEY, " + "'player_name' TEXT(255), " + "'uuid' TEXT(255), " + "'ismale' INT(1) DEFAULT '1', " + "'issingle' INT(1) DEFAULT '1', " + "'marrychat' INT(1) DEFAULT '2', " + "'enabled_pvp' INT(1) DEFAULT '2', " + "'family_chat' INT(1) DEFAULT '2', " + "`marriedwith` VARCHAR(20) DEFAULT 'Unknown', " + "`partner_last_login` VARCHAR(50) DEFAULT 'Unknown', " + "`familyname` VARCHAR(30) DEFAULT 'Unknown', " + "`adoptions` VARCHAR(1000) DEFAULT 'none' ); " + "CREATE INDEX IF NOT EXISTS marrydata_player_name ON puerkasmarriage (player_name);" + "CREATE INDEX IF NOT EXISTS marrydata_uuid ON puerkasmarriage (uuid);");
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
            final String s14 = "language";//20, 50, 30, 1000
            set = metaData.getColumns(null, null, table_NAME, "player_name");
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s + " VARCHAR(255) NOT NULL UNIQUE;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s2);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s2 + " VARCHAR(255) DEFAULT NULL;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s3);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s3 + " INT(12) DEFAULT 0;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s6);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s6 + " INT(12) DEFAULT 0;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s7);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s7 + " INT(12) DEFAULT 0;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s8);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s8 + " INT(12) DEFAULT 0;");
            }
            set.close();
            set = metaData.getColumns(null, null, table_NAME, s9);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s9 + " INT(12) DEFAULT 0;");
            }
            set = metaData.getColumns(null, null, table_NAME, s10);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s10 + " VARCHAR(20) DEFAULT NULL;");
            }
            set = metaData.getColumns(null, null, table_NAME, s11);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s11 + " VARCHAR(50) DEFAULT NULL;");
            }
            set = metaData.getColumns(null, null, table_NAME, s12);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s12 + " VARCHAR(30) DEFAULT NULL;");
            }
            set = metaData.getColumns(null, null, table_NAME, s13);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s13 + " VARCHAR(1000) DEFAULT NULL;");
            }
            set = metaData.getColumns(null, null, table_NAME, s14);
            if (!set.next()) {
                statement.executeUpdate("ALTER TABLE " + table_NAME + " ADD COLUMN " + s14 + " VARCHAR(20) DEFAULT NULL;");
            }
            set.close();
            statement.close();
        }
        finally {
            this.close(set);
            this.close(statement);
            this.close(statement2);
        }
        this.close(set);
        this.close(statement);
        this.close(statement2);
    }
	
	private void close(final Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
	
	public void close() {
        try {
            if (SQLite.connection != null && !SQLite.connection.isClosed()) {
                SQLite.connection.close();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
	
	private void close(final ResultSet set) {
        if (set != null) {
            try {
                set.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
	
	public void loadPlayerData(MarryPlayer skyPlayer) {
        try {
            Throwable t = null;
            try {
                final Connection connection = this.getSQLConnection();
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
                final Connection connection = this.getSQLConnection();
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
    
    public int isMarried(String uuid) {
        int i = 0;
        try {
            final Statement s = getSQLConnection().createStatement();
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
    
    public boolean exist(String uuid) {
    	boolean exist = false;
        try {
            final Statement s = getSQLConnection().createStatement();
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
            final Statement s = getSQLConnection().createStatement();
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
	
	public static SQLite getInstance() {
		if(SQLite.instance == null) {
			SQLite.instance = new SQLite();
		}
		return SQLite.instance;
	}

}
