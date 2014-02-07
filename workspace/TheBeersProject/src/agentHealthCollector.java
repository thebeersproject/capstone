//package com.hp.iwac.exp;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;



//import de.systemagmbh.common.util.CSysStdMessage;
import de.systemagmbh.components.message.vfei.CSysVfeiMessage;
import de.systemagmbh.interfaces.message.CSysParseException;
//import de.systemagmbh.interfaces.message.ISysMessage;
//import de.systemagmbh.interfaces.message.ISysMessageItem;

/**
 * 
 * @author
 *
 */
public class agentHealthCollector {
	
	private boolean vfeiMsg = true;

	public agentHealthCollector() throws CSysParseException, IOException {
		FileInputStream fstream;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream("AGENT_STATUS_13_11_14.txt");
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
			serviceData data = null;
			try {
				vfeiMsg  = true;
				msg.parse(strLine);
			} catch (CSysParseException e) {
				vfeiMsg  = false;
			}
			if (vfeiMsg) {
				data = new serviceData(msg, timeStamp);
				data_pack(data);
				//System.out.println(data);
			} else {
				//Assume timeStamp for next message
				timeStamp = strLine;
			}
			
		}

		//Close the input stream
		br.close();
	}
	
	/**
	 * Takes a set of data and calls the appropriate insert/query functions.
	 * @param db The database agent containing the data from the file.
	 */
	private void data_pack(serviceData data){
		int i = 0;
		for(serviceData.SrvData sd : data.services){
			if(sd.serviceCnt == 0 && sd.serviceTotalTime == 0)
				continue;
			
			if (i >= 30) //For Testing
				System.exit(i);
			i++;
			
			//Index table
			Integer index = databaseAgent.getIndex(data.agentName, data.instance, sd.serviceName);
			
			//basedata table
			String[] bd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), data.timeStamp.minute.toString(), data.timeStamp.second.toString(), sd.serviceCnt.toString(), sd.serviceTotalTime.toString(), data.startupTime};
			databaseAgent.writeData("basedata", bd_values);
			
			//6Min table
		}
	}
    	
       
}
