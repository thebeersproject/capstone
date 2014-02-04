import java.sql.*;
//import java.util.Arrays;

import com.mysql.jdbc.exceptions.jdbc4.*;

/**
 * Responsible for connecting to and querying the database.
 * @author
 *
 */
public class databaseAgent {
	private Connection connection = null; // why was this static? Won't work if constructor isn't called first.
	// Change these properties to your own database info!
	
	//Nick Connection
	//private String database = "capstone";
	//private String host = "jdbc:mysql://localhost:8889/" + database;
	//private String user = "root";
	//private String pass = "root";
	
	//Ethan Connection
	private String database = "capstone";
	private String host = "jdbc:mysql://localhost:3306/" + database;
	private String user = "capstone";
	private String pass = "";
	
	/**
	 * Constructor. Calls connectToDatabase().
	 */
	public databaseAgent() {
		connectToDatabase();
	}
	
	
	
	
	/**
	 * Builds the insert query string
	 * @param table The table the data is to be inserted to.
	 * @param values The array of values are to be inserted.
	 * @param columns The array of columns that are to be inserted.
	 * @return The query string.
	 */
	private String buildInsertQuery(String table, String[] values, String[] columns){
		String vals = stringifyValues(values);
		String cols = stringifyColumns(columns);
		
		String query = "INSERT INTO " + database + "." + table + " " + cols + " VALUES " + vals;
		//System.out.println("New Query: " + query);
		
		return query;
	}
	
	/**
	 * Attempts to connect to the database.
	 */
	private void connectToDatabase() {
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
	private String[] getColumns(String table){
		String[] columns = null;
		
		
		if(table.compareToIgnoreCase("index") == 0){
			columns = new String[4];
			columns[0] = "Index";
			columns[1] = "Agent Name";
			columns[2] = "Instance";
			columns[3] = "Service Name";
		}
		//TODO other tables
		
		return columns;		
	}
	
	/**
	 * Attempts to return the next index to be used. (Possible race condition?)
	 * @return The next index.
	 */
	public Integer getMaxIndex(){
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
	private String stringifyColumns(String[] columns){
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
	private String stringifyValues(String[] values){
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
	public void writeData(String table, String[] values) {
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
