
/**
 * Main class for running program
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
	   	
    	new agentHealthCollector("AGENT_STATUS_13_11_14.txt");		
	   	
	   	System.out.println("Done"); //Won't print if exiting in HealthCollector.
	}

}
