//package com.hp.iwac.exp;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.systemagmbh.common.util.CSysStdMessage;
import de.systemagmbh.components.message.vfei.CSysVfeiMessage;
import de.systemagmbh.interfaces.message.CSysParseException;
import de.systemagmbh.interfaces.message.ISysMessage;
import de.systemagmbh.interfaces.message.ISysMessageItem;

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
				data = new serviceData(msg);
				System.out.println(data);
			} else {
				System.out.println("None VFEI msg");
			}
			
		}

		//Close the input stream
		br.close();
	}
	
	public static void main(String[] args) {
        try {
        	agentHealthCollector vParser = new agentHealthCollector();
		} catch (CSysParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
