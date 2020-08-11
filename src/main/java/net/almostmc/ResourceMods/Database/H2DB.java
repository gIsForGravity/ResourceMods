package net.almostmc.ResourceMods.Database;

import net.almostmc.ResourceMods.CraftPlugin;
import net.almostmc.ResourceMods.ResourceAPI;
import net.almostmc.ResourceMods.ResourceMaterial;
import org.h2.Driver;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class H2DB {
    private final static String urlBase = "jdbc:h2:tcp://localhost/";
    private final static String dbName = "blocks";

    private Connection con;

    public H2DB(String filename) {
        try {
            // Register JDBC Driver
            Class.forName(org.h2.Driver.class.getCanonicalName());
            // Note on why I use class.getCanonicalName instead of just a string: A string causes a runtime error if you don't have the dependency,
            // class.getCanonicalName causes a compiler error if you don't have the dependency, and that makes things a lot easier.

            // Open connection
            con = DriverManager.getConnection(urlBase + filename);

            // Checks if the database has any tables
            var dmb = con.getMetaData();
            ResultSet tables = dmb.getTables(null, null, dbName, null);

            // If the database has no tables...
            if (!tables.next()) {
                Statement stmt = con.createStatement();
                // Create a table named dbName and an x int, y int, z int, and a String named Material (for material fullID's)
                stmt.execute("CREATE TABLE " + dbName + "(" +
                        "x DOUBLE," +
                        "y DOUBLE," +
                        "z DOUBLE," +
                        "Material VARCHAR(255)" +
                        ");");
            }
        } catch (ClassNotFoundException | SQLException e) {
            // If the H2 class isn't found you have a major issue
            e.printStackTrace();
        }
    }

    public ResourceMaterial checkEntry(double x, double y, double z) {
        try {
            Statement stmt = con.createStatement();

            // Gets a list of every material string that has this x, y, and z. Because of the way SQL is setup
            // in this plugin, this list should be max 1 item long, but it could be more if I did something wrong
            // -Gavin
            var query = "SELECT Material FROM blocks WHERE x=" + x + ", y=" + y + ", z=" + z + ";";

            ResultSet result = stmt.executeQuery(query);

            if (result.next()) {
                // Gets the first result in the list from earlier and gets the material with that ID.
                return ResourceAPI.singleton.getMaterialFromId(result.getString("Material"));
            } else return null;
        } catch (SQLException e) {
            // Throw exception and warn admin
            throwSQLException(e);

            // Return null if there is an SQLException
            return null;
        }
    }

    public void setMaterial(String fullID, double x, double y, double z) {
        try {
            Statement stmt = con.createStatement();

            // Check if material for this block has already been set
            var query = "SELECT Material FROM blocks WHERE x=" + x + ", y=" + y + ", z=" + z + ";";

            ResultSet result = stmt.executeQuery(query);

            if (result.next()) {
                // If there is already an entry then update it
                query = "UPDATE blocks SET Material='" + fullID + "' WHERE x=" + x + ", y=" + y + ", z=" + z + ";";


            } else {
                // If there isn't already an entry then add it instead of updating
                query = "INSERT INTO blocks (x, y, z, Material) VALUES (" + x + ", " + y + ", " + z + ", '" + fullID + "');";
            }

            stmt.execute(query);
        } catch (SQLException e) {
            throwSQLException(e);
        }
    }

    private static void throwSQLException(SQLException e) {
        e.printStackTrace();

        // Warn admin about potentially critical sql error
        CraftPlugin.getInstance().getLogger().severe("SQL Error!");
        CraftPlugin.getInstance().getLogger().warning("Attempting to ignore the SQL error and continue. If you keep encountering this issue \n" +
                "or see ghost blocks, you might have to run \"/rmconfig reset wipe\". If this does\n" +
                "not fix the error, report it to the plugin developers immediately."); // TODO: Add /rmconfig and rmconfig wipe module
    }
}
