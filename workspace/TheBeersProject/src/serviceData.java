//package com.hp.iwac.exp;

import java.util.ArrayList;
import java.util.List;

import de.systemagmbh.components.message.vfei.CSysVfeiMessage;
import de.systemagmbh.interfaces.message.ISysMessageItem;

/**
 * 
 * @author
 *
 */
public class serviceData {
	String agentName = null;
	String instance = null;
	List<SrvData> services = new ArrayList<SrvData>();
	TimeStamp timeStamp = null;
	String startupTime = null;
	String newLine = System.getProperty("line.separator");

	public serviceData(CSysVfeiMessage msg, String timeStampStr) {
		//parse this message
		int size = msg.size();
		//System.out.println("vfie msg size = " + size);
		for (int x=0; x<size; x++) {
			ISysMessageItem i = msg.getItem(x);
			//int type = i.getType();
			String msgName = (String) i.getKey();
			if ("APPLICATION".equals(msgName)) {
				agentName = (String)i.getValue();
			}
			if ("INSTANCE".equals(msgName)) {
				instance = (String)i.getValue();
			}
			if ("SERVICES".equals(msgName) && i.isList()) {
				CSysVfeiMessage msg2 = (CSysVfeiMessage) i.getValue();
				int cnt = 0;
				boolean itemsExist = true;
				while (itemsExist) {
					String serviceName = "SERVICE_" + Integer.toString(cnt);
					cnt++;
					CSysVfeiMessage serviceData = null;
					try {
						ISysMessageItem i2 = msg2.getItem(serviceName);
						serviceData = (CSysVfeiMessage) i2.getValue();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						itemsExist = false;
					}
					if (itemsExist) {
						services.add(new SrvData(serviceData));
					}
				}


			}
			if("STARTUP".equals(msgName))
				startupTime = (String)i.getValue();
		}
		timeStamp = new TimeStamp(timeStampStr);
		
	}
	
	

	/**
	 * The string representation of the object.
	 */
	public String toString() {
		String retString = null;
		retString = "Agent name = " + agentName + " Instance = " + instance + newLine;
		for (SrvData s : services) {
			retString += s.toString();
			retString += newLine;
		}
		return retString;
		
	}
	
	
	
	
	
	
	
	/**
	 * Stores service information.
	 * @author
	 *
	 */
	public class SrvData { //Changed to public
		String serviceName;
		Long serviceCnt;
		Long serviceTotalTime; //Bad name?
		
		/**
		 * 
		 * @param srvData
		 */
		public SrvData(CSysVfeiMessage srvData) {
			// srvData should be a list of 5 items, 
			ISysMessageItem  serviceCallsMsg = null;
			// System.out.println("srvData is a list message = " + srvData.toString());
			CSysVfeiMessage info = srvData;
			// System.out.println("info = " + info.toString());
			ISysMessageItem serviceMsg = info.getItem("SERVICE_NAME");
			serviceName = serviceMsg.getValue().toString();
			
			serviceCallsMsg = info.getItem("SERVICE_CALLS");
			serviceCnt = ((Long) serviceCallsMsg.getValue()).longValue();
			serviceCallsMsg = info.getItem("SERVICE_TIME");
			serviceTotalTime = ((Long) serviceCallsMsg.getValue()).longValue();
		}
		
		
		/**
		 * The string representation of the object.
		 */
		public String toString() {
			String retString = "Service name = " + serviceName 
					+ " Count = " + Long.toString(serviceCnt) 
					+ " Total Time " + Long.toString(serviceTotalTime);
			return retString;
		}
	}
	
	
	
	
	/**
	 * Stores the timestamp information
	 * @author Ethan
	 *
	 */
	public class TimeStamp{
		Integer year = null;
		Integer month = null;
		Integer day = null;
		Integer hour = null;
		Integer minute = null;
		Integer second = null;
		String timeStamp = null;
		
		public TimeStamp(String timeStampStr){
			timeStamp = timeStampStr;
			
			//System.out.println(timeStamp);
			
			//Year
			char[] tempArr = new char[4];
			timeStamp.getChars(24, 28, tempArr, 0);
			year = Integer.parseInt(new String(tempArr));
			
			//Month
			tempArr = new char[3];
			timeStampStr.getChars(4, 7, tempArr, 0);
			String tempStr = new String(tempArr);
			if(tempStr.equalsIgnoreCase("Jan"))
				month = 1;
			else if(tempStr.equalsIgnoreCase("Feb"))
				month = 2;
			else if(tempStr.equalsIgnoreCase("Mar"))
				month = 3;
			else if(tempStr.equalsIgnoreCase("Apr"))
				month = 4;
			else if(tempStr.equalsIgnoreCase("May"))
				month = 5;
			else if(tempStr.equalsIgnoreCase("Jun"))
				month = 6;
			else if(tempStr.equalsIgnoreCase("Jul"))
				month = 7;
			else if(tempStr.equalsIgnoreCase("Aug"))
				month = 8;
			else if(tempStr.equalsIgnoreCase("Sep"))
				month = 9;
			else if(tempStr.equalsIgnoreCase("Oct"))
				month = 10;
			else if(tempStr.equalsIgnoreCase("Nov"))
				month = 11;
			else if(tempStr.equalsIgnoreCase("Dec"))
				month = 12;
			else{
				month = null;
				System.out.println("Unrecongnized month");
			}
			
			//Day
			tempArr = new char[2];
			timeStampStr.getChars(8, 10, tempArr, 0);
			day = Integer.parseInt(new String(tempArr));
			
			//Hour
			tempArr = new char[2];
			timeStampStr.getChars(11, 13, tempArr, 0);
			hour = Integer.parseInt(new String(tempArr));
			
			//Minute
			tempArr = new char[2];
			timeStampStr.getChars(14, 16, tempArr, 0);
			minute = Integer.parseInt(new String(tempArr));
			
			//Second
			tempArr = new char[2];
			timeStampStr.getChars(17, 19, tempArr, 0);
			second = Integer.parseInt(new String(tempArr));
		}
		
		/**
		 * Returns the string version of the timeStamp which includes all information.
		 */
		public String toString(){
			return timeStamp;
		}
	}
}
