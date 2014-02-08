import java.sql.*;

import com.mysql.jdbc.exceptions.jdbc4.*;

/**
 * Responsible for connecting to and querying the database.
 * @author Nick and Ethan
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
	
	//Randy Connection
	//private static final String database = "";
	//private static final String host = "";
	//private static final String user = "";
	//private static final String pass = "";
	
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
		
		return query;
	}
	
	/**
	 * Attempts to find the previous values for service time and service calls.
	 * @param index The index of the data
	 * @return An array containing the previous values or 0 if no values;
	 */
	public static Long[] compareToPreviousBase(Integer index){
		Statement statement = null;
		Long[] previous = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		
		String query = "SELECT `Service Calls`, `Service Time` FROM `" + database + "`.`basedata` WHERE `index` = " + index.toString() + " ORDER BY `year`, `month`, `day`, `hour`, `minute`, `second` DESC LIMIT 0 , 1";
		
		try {
			ResultSet rs = statement.executeQuery(query);
			previous = new Long[2];
			if (!rs.first()){
				previous[0] = previous[1] = new Long(0);
			}
			else{
				previous[0] = rs.getLong(1);
				previous[1] = rs.getLong(2);				
			}
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue reading from database, check console!");
			e.printStackTrace();
		}
		
		return previous;
	}
	
	/**
	 * Attempts to connect to the database.
	 */
	public static void connectToDatabase() {
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
			columns = new String[10];
			columns[0] = "Index";
			columns[1] = "Year";
			columns[2] = "Month";
			columns[3] = "Day";
			columns[4] = "Hour";
			columns[5] = "Minute";
			columns[6] = "Second";
			columns[7] = "Service Calls";
			columns[8] = "Service Time";
			columns[9] = "StartupTime";			
		}
		else if(table.compareToIgnoreCase("6mindata") == 0){
			columns = new String[8];
			columns[0] = "Index";
			columns[1] = "Year";
			columns[2] = "Month";
			columns[3] = "Day";
			columns[4] = "Hour";
			columns[5] = "Interval";
			columns[6] = "Service Calls";
			columns[7] = "Service Time";
		}
		else if(table.compareToIgnoreCase("hourdata") == 0){
			columns = new String[7];
			columns[0] = "Index";
			columns[1] = "Year";
			columns[2] = "Month";
			columns[3] = "Day";
			columns[4] = "Hour";
			columns[5] = "Service Calls";
			columns[6] = "Service Time";
		}
		else if(table.compareToIgnoreCase("daydata") == 0){
			columns = new String[6];
			columns[0] = "Index";
			columns[1] = "Year";
			columns[2] = "Month";
			columns[3] = "Day";
			columns[4] = "Service Calls";
			columns[5] = "Service Time";
		}
		else if(table.compareToIgnoreCase("totaldata") == 0){
			columns = new String[3];
			columns[0] = "Index";
			columns[1] = "Service Calls";
			columns[2] = "Service Time";
		}
		else
			System.out.println("unknow table");
		
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
		
		String query = "SELECT `Index` FROM `" + database + "`.`index` WHERE 1 ORDER BY `INDEX` DESC LIMIT 0 , 1";
		
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.first()){
				index = 0;
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
		String query = "Select `Index` FROM `" + database + "`.`index` WHERE `Agent Name` = '" + agent + "' AND `Instance` = '" + instance + "' AND `Service Name` = '" + service + "'";
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
		
		return vals;
	}	
	
	/**
	 * Updates data in the database
	 * @param table The table in the database
	 * @param values The values that need to be inserted and the values for the where clause.
	 */
	public static void updateData(String table, String[] values){
		Statement statement = null;
		try {
			statement = connection.createStatement();
		}catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		
		String query = null;
		if (table.compareToIgnoreCase("6mindata") == 0)
			query = "UPDATE `" + database + "`.`" + table + "` SET `Service Calls` = `Service Calls` + " + values[6] + ", `Service Time` = `Service Time` + " + values[7] + " WHERE `Index` = " + values[0] + " and `Year` = " + values[1] + " and `Month` = " + values[2] + " and `Day` = " + values[3] + " and `Hour` = " + values[4] + " and `Interval` = " + values[5];
		else if (table.compareToIgnoreCase("hourdata") == 0)
			query = "UPDATE `" + database + "`.`" + table + "` SET `Service Calls` = `Service Calls` + " + values[5] + ", `Service Time` = `Service Time` + " + values[6] + " WHERE `Index` = " + values[0] + " and `Year` = " + values[1] + " and `Month` = " + values[2] + " and `Day` = " + values[3] + " and `Hour` = " + values[4];
		else if (table.compareToIgnoreCase("daydata") == 0)
			query = "UPDATE `" + database + "`.`" + table + "` SET `Service Calls` = `Service Calls` + " + values[4] + ", `Service Time` = `Service Time` + " + values[5] + " WHERE `Index` = " + values[0] + " and `Year` = " + values[1] + " and `Month` = " + values[2] + " and `Day` = " + values[3];
		else if (table.compareToIgnoreCase("totaldata") == 0)
			query = "UPDATE `" + database + "`.`" + table + "` SET `Service Calls` = `Service Calls` + " + values[1] + ", `Service Time` = `Service Time` + " + values[2] + " WHERE `Index` = " + values[0];
		else
			System.out.println("Unrecognized table");
		
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Issue updating database, check console!");
			e.printStackTrace();
		}
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
		}catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		
		String query = buildInsertQuery(table, values, columns);
		
		try {
			statement.executeUpdate(query);
		}catch (MySQLIntegrityConstraintViolationException e){
			//Primary key is already in database
			if(table.compareToIgnoreCase("index") != 0 && table.compareToIgnoreCase("basedata") != 0){
				updateData(table, values);
			}
		} catch (SQLException e) {
			System.out.println("Issue writing data to database, check console!");
			e.printStackTrace();
		}
	}
	
}
