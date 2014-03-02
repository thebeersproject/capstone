//package com.hp.iwac.exp;

//import de.systemagmbh.common.util.CSysStdMessage;
import de.systemagmbh.components.message.vfei.CSysVfeiMessage;
//import de.systemagmbh.interfaces.message.CSysParseException;
//import de.systemagmbh.interfaces.message.ISysMessage;
//import de.systemagmbh.interfaces.message.ISysMessageItem;

/**
 * Collects data from a VFeiMessage and sends it to the database.
 * @author
 *
 */
public class agentHealthCollector {
	
	/**
	 * Extracts the data from 
	 * @param timeStamp The time stamp for the data
	 * @param msg The VfeiMessage
	 */
	public agentHealthCollector(CSysVfeiMessage msg, String timeStamp){
		toDatabase(new serviceData(msg, timeStamp));
	}
	
	
	/**
	 * Takes a set of data and calls the appropriate insert/query functions.
	 * @param db The database agent containing the data from the file.
	 */
	private void toDatabase(serviceData data){
		//int i = 0;
		for(serviceData.SrvData sd : data.services){
			if(sd.serviceCnt == 0 && sd.serviceTotalTime == 0)
				continue;
			
			 //For Testing
			/*if (i >= 5){
				System.out.println("Terminated early for testing purposes. See line 53(ish) in agentHealthCollector to change.");
				System.exit(i);
			}
			i++; */
			
			//Index table
			Integer index = databaseAgent.getIndex(data.agentName, data.instance, sd.serviceName);
			
			//Calc Diff
			Long[] previous = databaseAgent.compareToPreviousBase(index, data.startupTime);
			Long[] diff = {sd.serviceCnt - previous[0], sd.serviceTotalTime - previous[1]};
			
			//basedata table -- Do this after cacluating difference from previous so that this data wont be used.
			String[] bd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), data.timeStamp.minute.toString(), data.timeStamp.second.toString(), sd.serviceCnt.toString(), sd.serviceTotalTime.toString(), data.startupTime};
			databaseAgent.writeData(databaseAgent.RAW_TABLE, bd_values);
			
			//totaldata -- Do this before min, hour, day tables so that avg is available.
			String[] td_values = {index.toString(), diff[0].toString(), diff[1].toString(), "0"}; //0 is a temporary value.
			databaseAgent.writeData(databaseAgent.TOTAL_TABLE, td_values);
			
			//Calc Average
			Double average = databaseAgent.calcAverage(index);
			databaseAgent.updateAverage(index, average);
			
			//6Min table
			String[] minD_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), determine_interval(data.timeStamp.minute).toString() , diff[0].toString(), diff[1].toString(), "0"};
			databaseAgent.writeData(databaseAgent.MINUTE_TABLE, minD_values);			
			databaseAgent.updateNorm(databaseAgent.MINUTE_TABLE, minD_values, average);
			
			//hourdata
			String[] hd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), data.timeStamp.hour.toString(), diff[0].toString(), diff[1].toString(), "0"};
			databaseAgent.writeData(databaseAgent.HOUR_TABLE, hd_values);			
			databaseAgent.updateNorm(databaseAgent.HOUR_TABLE, hd_values, average);
			
			//daydata
			String[] dd_values = {index.toString(), data.timeStamp.year.toString(), data.timeStamp.month.toString(), data.timeStamp.day.toString(), diff[0].toString(), diff[1].toString(), "0"};
			databaseAgent.writeData(databaseAgent.DAY_TABLE, dd_values);			
			databaseAgent.updateNorm(databaseAgent.DAY_TABLE, dd_values, average);
			
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
