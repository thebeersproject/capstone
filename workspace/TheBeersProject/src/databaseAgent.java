import java.sql.*;

import com.mysql.jdbc.exceptions.jdbc4.*;

/**
 * Responsible for connecting to and querying the database.
 * @author Nick and Ethan
 *
 */
public class DatabaseAgent {
	private static Connection connection = null;
	
	// Change these properties to your own database info!
	
	//Nick Connection
	//private static final String DATABASE = "capstone";
	//private static final String HOST = "jdbc:mysql://localhost:8889/" + DATABASE;
	//private static final String USER = "root";
	//private static final String PASS = "root";
	
	//Ethan Connection
	private static final String DATABASE = "capstone";
	private static final String HOST = "jdbc:mysql://localhost:3306/" + DATABASE;
	private static final String USER = "capstone";
	private static final String PASS = "";
	
	//Randy Connection
	//private static final String DATABASE = "";
	//private static final String HOST = "";
	//private static final String USER = "";
	//private static final String PASS = "";
	
	//Capstone Connection
	//private static final String DATABASE = "cs462-team18";
	//private static final String HOST = "jdbc:mysql://mysql.cs.orst.edu/" + DATABASE;
	//private static final String USER = "cs462-team18";
	//private static final String PASS = "yw54jDDMnPMaxEqt";
	
	//Tables
	public static final String INDEX_TABLE = "AM_INDEXTABLE";
	public static final String RAW_TABLE = "AM_BASEDATA";
	public static final String MINUTE_TABLE = "AM_6MINDATA";
	public static final String HOUR_TABLE = "AM_HOURDATA";
	public static final String DAY_TABLE = "AM_DAYDATA";
	public static final String TOTAL_TABLE = "AM_TOTALDATA";
	
	//Columns
	//Common to multiple tables
	private static final String INDEX_COL = "INDEX_COLUMN";
	private static final String YEAR = "YEAR";
	private static final String MONTH = "MONTH";
	private static final String DAY = "DAY";
	private static final String HOUR = "HOUR";
	private static final String SERVICE_CALLS = "SERVICE_CALLS";
	private static final String SERVICE_TIME = "SERVICE_TIME";
	private static final String NORMAL = "NORM";
	//INDEX_TABLE
	private static final String AGNT_NAME = "AGENT_NAME";
	private static final String INST = "INSTANCE";
	private static final String SRVC_NAME = "SERVICE_NAME";
	//RAW_TABLE
	private static final String MINUTE = "MINUTE";
	private static final String SECOND = "SECOND";
	private static final String STARTUP_TIME = "STARTUP_TIME";
	//MIN_TABLE
	private static final String INTERVAL = "INTRVL";
	//TOTAL_TABLE
	private static final String AVERAGE = "AVERAGE";
	
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
		
		String query = "INSERT INTO `" + table + "` " + cols + " VALUES " + vals;
		
		return query;
	}
	
	/**
	 * Calculates the average from the total table for the specified index.
	 * @param index The index of the data
	 * @return The current average
	 */
	public static Double calcAverage(Integer index){
		Double average = null;
		
		Statement statement = createStmnt();
		
		String query = "SELECT `" + SERVICE_CALLS + "`, `" + SERVICE_TIME + "` FROM `" + TOTAL_TABLE + "` WHERE `" + INDEX_COL + "` = " + index.toString();
		
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.first()){
				System.out.println("Could not find data to calculate average");
			}
			else{
				Double calls = rs.getDouble(1);
				Double time = rs.getDouble(2);
				if(calls == 0 || time == 0)
					average = new Double (0);
				else
					average = new Double(time / calls);
			}
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue reading from database, check console!");
			e.printStackTrace();
		}
		
		return average;
	}

	/**
	 * Attempts to find the previous values for service time and service calls.
	 * @param index The index of the data
	 * @return An array containing the previous values or 0 if no values;
	 */
	public static Long[] compareToPreviousBase(Integer index, String StartUp){
		Long[] previous = null;
		
		Statement statement = createStmnt();
		
		String query = "SELECT `" + SERVICE_CALLS + "`, `" + SERVICE_TIME + "` FROM `" + RAW_TABLE + "` WHERE `" + INDEX_COL + "` = " + index.toString() + " AND `" + STARTUP_TIME + "` = \"" + StartUp + "\" ORDER BY `" + YEAR + "` DESC, `" + MONTH + "` DESC, `" + DAY + "` DESC, `" + HOUR + "` DESC, `" + MINUTE + "` DESC, `" + SECOND + "` DESC LIMIT 0 , 1";
		
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
			.getConnection(HOST, USER, PASS);
	 
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
	 * Attempts to create a statement for the connection
	 * @return The statement
	 */
	private static Statement createStmnt() {
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error allocating statement on network!");
			e.printStackTrace();
		}
		return statement;
	}
	
	/**
	 * Provides an array of the columns for the specified table.
	 * @param table The table whose columns are needed.
	 * @return The arrray of columns.
	 */
	private static String[] getColumns(String table){
		String[] columns = null;		

		if(table.compareToIgnoreCase(INDEX_TABLE) == 0){
			columns = new String[4];
			columns[0] = INDEX_COL;
			columns[1] = AGNT_NAME;
			columns[2] = INST;
			columns[3] = SRVC_NAME;
		}
		else if(table.compareToIgnoreCase(RAW_TABLE) == 0){
			columns = new String[10];
			columns[0] = INDEX_COL;
			columns[1] = YEAR;
			columns[2] = MONTH;
			columns[3] = DAY;
			columns[4] = HOUR;
			columns[5] = MINUTE;
			columns[6] = SECOND;
			columns[7] = SERVICE_CALLS;
			columns[8] = SERVICE_TIME;
			columns[9] = STARTUP_TIME;			
		}
		else if(table.compareToIgnoreCase(MINUTE_TABLE) == 0){
			columns = new String[9];
			columns[0] = INDEX_COL;
			columns[1] = YEAR;
			columns[2] = MONTH;
			columns[3] = DAY;
			columns[4] = HOUR;
			columns[5] = INTERVAL;
			columns[6] = SERVICE_CALLS;
			columns[7] = SERVICE_TIME;
			columns[8] = NORMAL;
		}
		else if(table.compareToIgnoreCase(HOUR_TABLE) == 0){
			columns = new String[8];
			columns[0] = INDEX_COL;
			columns[1] = YEAR;
			columns[2] = MONTH;
			columns[3] = DAY;
			columns[4] = HOUR;
			columns[5] = SERVICE_CALLS;
			columns[6] = SERVICE_TIME;
			columns[7] = NORMAL;
		}
		else if(table.compareToIgnoreCase(DAY_TABLE) == 0){
			columns = new String[7];
			columns[0] = INDEX_COL;
			columns[1] = YEAR;
			columns[2] = MONTH;
			columns[3] = DAY;
			columns[4] = SERVICE_CALLS;
			columns[5] = SERVICE_TIME;
			columns[6] = NORMAL;
		}
		else if(table.compareToIgnoreCase(TOTAL_TABLE) == 0){
			columns = new String[4];
			columns[0] = INDEX_COL;
			columns[1] = SERVICE_CALLS;
			columns[2] = SERVICE_TIME;
			columns[3] = AVERAGE;
		}
		else
			System.out.println("unknow table");
		
		return columns;		
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
		Statement statement = createStmnt();
		String query = "Select `" + INDEX_COL + "` FROM `" + INDEX_TABLE + "` WHERE `" + AGNT_NAME + "` = '" + agent + "' AND `" + INST + "` = '" + instance + "' AND `" + SRVC_NAME + "` = '" + service + "'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.first()){
				index = rs.getInt(1);
			}
			else {
				index = getNewIndex();
				String[] values = {index.toString(), agent, instance, service};
				DatabaseAgent.writeData(INDEX_TABLE, values);
			}
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue reading from database, check console!");
			e.printStackTrace();
		}	
		
		return index;		
	}
	
	/**
	 * Gets an index to be used for new data. (Possible race condition?)
	 * @return The new index.
	 */
	private static Integer getNewIndex(){
		Integer index = null;
		Statement statement = createStmnt();
		
		String query = "SELECT MAX(`" + INDEX_COL + "`) FROM `" + INDEX_TABLE + "` WHERE 1";
		
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.first()){
				index = 0;
			}
			/*else if(rs.getString(1) == null) //If 0 needs to be first index
				index = 0;*/
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
	 * Update the average value in the total table.
	 * @param index The index that needs to be updated
	 * @param average The average value
	 */
	public static void updateAverage(Integer index, Double average){
		Statement statement = createStmnt();
		
		String query = "UPDATE `" + TOTAL_TABLE + "` SET `" + AVERAGE + "` = " + average.toString() + " WHERE `" + INDEX_COL + "` = " + index.toString();
		
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Issue updating database, check console!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the norm in the database
	 * @param table The table in the database
	 * @param values The values that need to be inserted and the values for the where clause.
	 */
	public static void updateNorm(String table, String[] values, Double average){
		Double norm = null;
		boolean skip = false;
		//get data
		Statement statement = createStmnt();	
		String query =  "SELECT `" + SERVICE_CALLS + "`, `" + SERVICE_TIME + "` FROM `" + table + "` WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3];		
		if (table.compareToIgnoreCase(MINUTE_TABLE) == 0)
			query += " and `" + HOUR + "` = " + values[4] + " and `" + INTERVAL + "` = " + values[5];
		else if (table.compareToIgnoreCase(HOUR_TABLE) == 0)
			query += " and `" + HOUR + "` = " + values[4];
		else if (table.compareToIgnoreCase(DAY_TABLE) == 0)
			;//No changes
		else
			System.out.println("Unrecognized table");
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.first()){
				System.out.println("Could not find data to calculate norm");
				return;
			}
			else{
				Double time = rs.getDouble(2);
				Double calls = rs.getDouble(1);
				if(time == 0 || calls == 0 || average == 0)
					skip = true;
				else
					norm = ( time / calls ) / average;
			}
			rs.close(); //Might not have to do.
		} catch (SQLException e) {
			System.out.println("Issue updating database, check console!");
			e.printStackTrace();
		}
		
		
		if(!skip){ //If the norm is zero don't need to update.
			if(norm >= 10.0)
				System.out.println("Hight norm for index " + values[0] + ".");
			//update data
			try {
				statement = connection.createStatement();
			}catch (SQLException e) {
				System.out.println("Error allocating statement on network!");
				e.printStackTrace();
			}
			
			query = null;
			if (table.compareToIgnoreCase(MINUTE_TABLE) == 0)
				query = "UPDATE `" + table + "` SET `" + NORMAL + "` = " + norm.toString() + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3] + " and `" + HOUR + "` = " + values[4] + " and `" + INTERVAL + "` = " + values[5];
			else if (table.compareToIgnoreCase(HOUR_TABLE) == 0)
				query = "UPDATE `" + table + "` SET `" + NORMAL + "` = " + norm.toString() + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3] + " and `" + HOUR + "` = " + values[4];
			else if (table.compareToIgnoreCase(DAY_TABLE) == 0)
				query = "UPDATE `" + table + "` SET `" + NORMAL + "` = " + norm.toString() + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3];
			else
				System.out.println("Unrecognized table");
			
			try {
				statement.executeUpdate(query);
			} catch (SQLException e) {
				System.out.println("Issue updating database, check console!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates data in the database
	 * @param table The table in the database
	 * @param values The values that need to be inserted and the values for the where clause.
	 */
	public static void updateSrvcData(String table, String[] values){
		Statement statement = createStmnt();
		
		String query = null;
		if (table.compareToIgnoreCase(MINUTE_TABLE) == 0)
			query = "UPDATE `" + table + "` SET `" + SERVICE_CALLS + "` = `" + SERVICE_CALLS + "` + " + values[6] + ", `" + SERVICE_TIME + "` = `" + SERVICE_TIME + "` + " + values[7] + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3] + " and `" + HOUR + "` = " + values[4] + " and `" + INTERVAL + "` = " + values[5];
		else if (table.compareToIgnoreCase(HOUR_TABLE) == 0)
			query = "UPDATE `" + table + "` SET `" + SERVICE_CALLS + "` = `" + SERVICE_CALLS + "` + " + values[5] + ", `" + SERVICE_TIME + "` = `" + SERVICE_TIME + "` + " + values[6] + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3] + " and `" + HOUR + "` = " + values[4];
		else if (table.compareToIgnoreCase(DAY_TABLE) == 0)
			query = "UPDATE `" + table + "` SET `" + SERVICE_CALLS + "` = `" + SERVICE_CALLS + "` + " + values[4] + ", `" + SERVICE_TIME + "` = `" + SERVICE_TIME + "` + " + values[5] + " WHERE `" + INDEX_COL + "` = " + values[0] + " and `" + YEAR + "` = " + values[1] + " and `" + MONTH + "` = " + values[2] + " and `" + DAY + "` = " + values[3];
		else if (table.compareToIgnoreCase(TOTAL_TABLE) == 0)
			query = "UPDATE `" + table + "` SET `" + SERVICE_CALLS + "` = `" + SERVICE_CALLS + "` + " + values[1] + ", `" + SERVICE_TIME + "` = `" + SERVICE_TIME + "` + " + values[2] + " WHERE `" + INDEX_COL + "` = " + values[0];
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
			
		Statement statement = createStmnt();
		
		String query = buildInsertQuery(table, values, columns);
		
		try {
			statement.executeUpdate(query);
		}catch (MySQLIntegrityConstraintViolationException e){
			//Primary key is already in database
			if(table.compareToIgnoreCase(INDEX_TABLE) != 0 && table.compareToIgnoreCase(RAW_TABLE) != 0){
				updateSrvcData(table, values);
			}
		} catch (SQLException e) {
			System.out.println("Issue writing data to database, check console!");
			e.printStackTrace();
		}
	}
	
}
