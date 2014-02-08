//package com.hp.iwac.exp;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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

	/**
	 * Constructor for agent health collector. Reads data file.
	 * @throws CSysParseException
	 * @throws IOException
	 */
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
			
			 //For Testing
			if (i >= 30){
				System.out.println("Terminated early for testing purposes. See line 75(ish) in agentHealthCollector to change.");
				System.exit(i);
			}
			i++;
			
			//Index table
			Integer index = databaseAgent.getIndex(data.agentName, data.instance, sd.serviceName);
			
			//6Min table
			Long[] previous = databaseAgent.compareToPreviousBase(index);
			Long[] diff = {sd.serviceCnt - previous[0], sd.serviceTotalTime - previous[1]};
			String[] minD_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), determine_interval(data.timeStamp.minute).toString() , diff[0].toString(), diff[1].toString()};
			databaseAgent.writeData("6mindata", minD_values);
			
			//basedata table -- Do after 6Min so that this data wont be used to compare to the new data.
			String[] bd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), data.timeStamp.minute.toString(), data.timeStamp.second.toString(), sd.serviceCnt.toString(), sd.serviceTotalTime.toString(), data.startupTime};
			databaseAgent.writeData("basedata", bd_values);
			
			//hourdata
			String[] hd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), diff[0].toString(), diff[1].toString()};
			databaseAgent.writeData("hourdata", hd_values);
			
			//daydata
			String[] dd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), diff[0].toString(), diff[1].toString()};
			databaseAgent.writeData("daydata", dd_values);
			
			//totaldata
			String[] td_values = {index.toString(), diff[0].toString(), diff[1].toString()};
			databaseAgent.writeData("totaldata", td_values);
		}
	}
	
	/**
	 * Determines the interval the specified minute is in.
	 * @param minute The minute
	 * @return The interval
	 */
	private Integer determine_interval(Integer minute){
		if (minute >= 0 && minute <= 5)
			return 0;
		else if (minute >= 6 && minute <= 11)
			return 1;
		else if (minute >= 12 && minute <= 17)
			return 2;
		else if (minute >= 18 && minute <= 23)
			return 3;
		else if (minute >= 24 && minute <= 29)
			return 4;
		else if (minute >= 30 && minute <= 35)
			return 5;
		else if (minute >= 36 && minute <= 41)
			return 6;
		else if (minute >= 42 && minute <= 47)
			return 7;
		else if (minute >= 48 && minute <= 53)
			return 8;
		else if (minute >= 54 && minute <= 59)
			return 9;
		else
			return null;
	}
       
}
