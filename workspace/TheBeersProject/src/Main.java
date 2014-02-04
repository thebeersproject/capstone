import java.io.IOException;

import de.systemagmbh.interfaces.message.CSysParseException;


/**
 * Main class
 * @author Ethan
 *
 */
public class Main {

	/**
	 * Likely to undergo heavy modification.
	 * @param db The database agent containing the data from the file.
	 */
	public static void data_pack(databaseAgent db){
		 try {
	        	// Possible implementation here can change, right now
	        	// I left it as Mr. Beers wrote it, which is where all
	        	// the functionality is in the instantiation of the 
	        	// agenthealthcollector object... but this should change
	        	// We should extract that out...
	        	agentHealthCollector vParser = new agentHealthCollector();
	        	int i = 0; //For Testing
	        	for(serviceData data : vParser.data_List){
	    			for(serviceData.SrvData sd : data.services){
	    				//System.out.println(sd.serviceName);
	    				String table = "Index";
	    				String[] values = {db.getMaxIndex().toString(), data.agentName, data.instance, sd.serviceName};
	    				db.writeData(table, values);
	    				
	    				i++;
		    			if (i > 30) //For Testing
		    				break;
	    			}
	    			if (i > 30) //For Testing
	    				break;
	    		}
			} catch (CSysParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	
	/**
	 * main for running the program
	 * @param args Not needed
	 */
	public static void main(String[] args) {
	   	databaseAgent db = new databaseAgent();
	   	data_pack(db);
	   	
	   	System.out.println("Done");
	}

}
