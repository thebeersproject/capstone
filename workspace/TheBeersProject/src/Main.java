import java.io.IOException;

import de.systemagmbh.interfaces.message.CSysParseException;


/**
 * Main class
 * @author Ethan
 *
 */
public class Main {

	/**
	 * main for running the program
	 * @param args Not needed
	 */
	public static void main(String[] args) {
	   	databaseAgent.connectToDatabase();
	   	
	   	try {
        	// Possible implementation here can change, right now
        	// I left it as Mr. Beers wrote it, which is where all
        	// the functionality is in the instantiation of the 
        	// agenthealthcollector object... but this should change
        	// We should extract that out...
        	new agentHealthCollector();
		} catch (CSysParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	
	   	System.out.println("Done"); //Won't print if exiting in HealthCollector.
	}

}
