import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import de.systemagmbh.components.message.vfei.CSysVfeiMessage;
import de.systemagmbh.interfaces.message.CSysParseException;


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
	   	
    	String file = "AGENT_STATUS_13_11_14.txt";
    	
    	//String file = "AGENT_STATUS_14_02_03aa";
    	//String file = "AGENT_STATUS_14_02_03ab";
    	//String file = "AGENT_STATUS_14_02_03ac";
    	//String file = "AGENT_STATUS_14_02_03ad";
    	//String file = "AGENT_STATUS_14_02_03ae";
    	//String file = "AGENT_STATUS_14_02_03af";
    	//String file = "AGENT_STATUS_14_02_03ag";
    	//String file = "AGENT_STATUS_14_02_03ah";
    	//String file = "AGENT_STATUS_14_02_03ai";
    	//String file = "AGENT_STATUS_14_02_03aj";
    	//String file = "AGENT_STATUS_14_02_03ak";
    	//String file = "AGENT_STATUS_14_02_03al";
    	//String file = "AGENT_STATUS_14_02_03am";
    	//String file = "AGENT_STATUS_14_02_03an";
    	//String file = "AGENT_STATUS_14_02_03ao";
    	//String file = "AGENT_STATUS_14_02_03ap";
    	//String file = "AGENT_STATUS_14_02_03aq";
    	//String file = "AGENT_STATUS_14_02_03ar";
    	//String file = "AGENT_STATUS_14_02_03as";
    	//String file = "AGENT_STATUS_14_02_03at";
    	//String file = "AGENT_STATUS_14_02_03au";
    	//String file = "AGENT_STATUS_14_02_03av";
    	//String file = "AGENT_STATUS_14_02_03aw";
    	
    	//String file = "AGENT_STATUS_14_02_04aa";
    	//String file = "AGENT_STATUS_14_02_04ab";
    	//String file = "AGENT_STATUS_14_02_04ac";
    	//String file = "AGENT_STATUS_14_02_04ad";
    	//String file = "AGENT_STATUS_14_02_04ae";
    	//String file = "AGENT_STATUS_14_02_04af";
    	//String file = "AGENT_STATUS_14_02_04ag";
    	//String file = "AGENT_STATUS_14_02_04ah";
    	//String file = "AGENT_STATUS_14_02_04ai";
    	//String file = "AGENT_STATUS_14_02_04aj";
    	//String file = "AGENT_STATUS_14_02_04ak";
    	//String file = "AGENT_STATUS_14_02_04al";
    	//String file = "AGENT_STATUS_14_02_04am";
    	//String file = "AGENT_STATUS_14_02_04an";
    	//String file = "AGENT_STATUS_14_02_04ao";
    	//String file = "AGENT_STATUS_14_02_04ap";
    	//String file = "AGENT_STATUS_14_02_04aq";
    	//String file = "AGENT_STATUS_14_02_04ar";
    	//String file = "AGENT_STATUS_14_02_04as";
    	//String file = "AGENT_STATUS_14_02_04at";
    	//String file = "AGENT_STATUS_14_02_04au";
    	//String file = "AGENT_STATUS_14_02_04av";
    	//String file = "AGENT_STATUS_14_02_04aw";
    	
    	try{
			readData(file);
		} catch (CSysParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	
	   	System.out.println("Done"); //Won't print if exiting in HealthCollector.
	}
	
	/**
	 * ORIGINALLY in agentHealthCollector. Moved since primarily deals with reading file.
	 * Reads the service data from a text file.
	 * @throws CSysParseException
	 * @throws IOException
	 */
	private static void readData(String file) throws CSysParseException, IOException{
		boolean vfeiMsg = true;
		
		FileInputStream fstream;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fstream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String strLine;
		String timeStamp = "";

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			CSysVfeiMessage msg = new CSysVfeiMessage();
			try {
				vfeiMsg  = true;
				msg.parse(strLine);
			} catch (CSysParseException e) {
				vfeiMsg  = false;
			}
			if (vfeiMsg) {
				new agentHealthCollector(msg, timeStamp);
			} else {
				//Assume timeStamp for next message
				timeStamp = strLine;
			}
			
		}

		//Close the input stream
		br.close();
	}

}
