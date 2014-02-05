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
	String timeStamp = null;
	String startupTime = null;
	String newLine = System.getProperty("line.separator");

	public serviceData(CSysVfeiMessage msg, String timeStamp) {
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
		this.timeStamp = timeStamp;
		
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
}
