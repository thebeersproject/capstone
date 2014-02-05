import java.sql.*;
//import java.util.Arrays;

import com.mysql.jdbc.exceptions.jdbc4.*;

/**
 * Responsible for connecting to and querying the database.
 * @author
 *
 */
public class databaseAgent {
	private static Connection connection = null;
	// Change these properties to your own database info!
	
	//Nick Connection
	//private static final String database = "capstone";
	//private static final String host = "jdbc:mysql://localhost:8889/" + database;
	//private static final String user = "root";
	//private static final String pass = "root";
	
	//Ethan Connection
	private static final String database = "capstone";
	private static final String host = "jdbc:mysql://localhost:3306/" + database;
	private static final String user = "capstone";
	private static final String pass = "";
	
	/**
	 * Builds the insert query string
	 * @param table The table the data is to be inserted to.
	 * @param values The array of values are to be inserted.
	 * @param columns The array of columns that are to be inserted.
	 * @return The query string.
	 */
	private static String buildInsertQuery(String table, String[] values, String[] columns){
		String vals = stringifyValues(values);
		String cols = stringifyColumns(columns);
		
		String query = "INSERT INTO " + database + "." + table + " " + cols + " VALUES " + vals;
		//System.out.println("New Query: " + query);
		
		return query;
	}
	
	/**
	 * Attempts to connect to the database.
	 */
	public static void connectToDatabase() {
		//System.out.println("MySQL Database Testing!");
		 
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Missing MySQL JDBC Driver!");
			e.printStackTrace();
			return;
		}
	 
		System.out.println("Valid MySQL JDBC Driver");
	 
		try {
			connection = DriverManager
			.getConnection(host, user, pass);
	 
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check console");
			e.printStackTrace();
			return;
		}
	 
		if (connection != null) {
			System.out.println("Database connection successful!");
		} else {
			System.out.println("Failed to connect to database!");
		}
	}
	
	/**
	 * Provides an array of the columns for the specified table.
	 * @param table The table whose columns are needed.
	 * @return The arrray of columns.
	 */
	private static String[] getColumns(String table){
		String[] columns = null;
		
		
		if(table.compareToIgnoreCase("index") == 0){
			columns = new String[4];
			columns[0] = "Index";
			columns[1] = "Agent Name";
			columns[2] = "Instance";
			columns[3] = "Service Name";
		}
		else if(table.compareToIgnoreCase("basedata") == 0){
			columns = new String[5];
			columns[0] = "Index";
			columns[1] = "Service Calls";
			columns[2] = "Service Time";
			columns[3] = "StartupTime";
			columns[4] = "Timestamp";
			
		}
		//TODO other tables
		
		return columns;		
	}
	
	/**
	 * Gets an index to be used for new data. (Possible race condition?)
	 * @return The new index.
	 */
	private static Integer getNewIndex(){
		Integer index = null;
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		
		String query = "SELECT `Index` FROM `capstone`.`index` WHERE 1 ORDER BY `INDEX` DESC LIMIT 0 , 1";
		
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.first()){
				index = 0;
				//System.out.println("No results");
			}
			else
				index = rs.getInt(1) + 1;
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue reading from database, check console!");
			e.printStackTrace();
		}		
		
		return index;	
	}
	
	/**
	 * Gets the index of data. If the data is not in the database it will be added.
	 * @param agent The name of the agent
	 * @param instance The instance
	 * @param service The name of the service
	 * @return The index that refers to this data.
	 */
	public static Integer getIndex(String agent, String instance, String service){
		Integer index = null;		
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		String query = "Select `Index` FROM `capstone`.`index` WHERE `Agent Name` = '" + agent + "' AND `Instance` = '" + instance + "' AND `Service Name` = '" + service + "'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.first()){
				index = rs.getInt(1);
			}
			else {
				index = getNewIndex();
				String[] values = {index.toString(), agent, instance, service};
				databaseAgent.writeData("index", values);
			}
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue reading from database, check console!");
			e.printStackTrace();
		}	
		
		return index;		
	}
	
	/**
	 * Turns an array of column names into the proper format for an SQL query.
	 * @param columns The array of columns.
	 * @return The string of columns.
	 */
	private static String stringifyColumns(String[] columns){
		String cols = "(";
		for (int i = 0; i < columns.length; i++)
		{
			cols += "`" + columns[i] + "`";
			if (i != columns.length - 1) // if not last element then add comma (,)
				cols += ",";
			else
				cols += ")";
		}
		//System.out.println("QUERY: " + cols);
		
		return cols;
	}
	
	/**
	 * Turns an array of values into the proper format for an SQL query.
	 * @param values The array of values.
	 * @return The string of values.
	 */
	private static String stringifyValues(String[] values){
		String vals = "(";
		for (int i = 0; i < values.length; i++)
		{
			vals += "\"" + values[i] + "\"";
			if (i != values.length - 1) // if not last element then add comma (,)
				vals += ",";
			else
				vals += ")";
		}
		//System.out.println("QUERY: " + vals);
		
		return vals;
	}	
	
	
	/**
	 * Writes data to the database
	 * @param table The table that is to be written to.
	 * @param values The array values that are to be written (Columns will be determined with call to getColumns()).
	 */
	public static void writeData(String table, String[] values) {
		String[] columns = getColumns(table);
		if (columns == null){
			System.out.println("Invalid table");
			return;
		}
			
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} 
		catch (MySQLIntegrityConstraintViolationException e){
			//Primary key is already in database
			//TODO
			
		}catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		
		String query = buildInsertQuery(table, values, columns);
		
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Issue writing data to database, check console!");
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) {
		databaseAgent DB = new databaseAgent();
	}*/
}
