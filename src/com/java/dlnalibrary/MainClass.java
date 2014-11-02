package com.java.dlnalibrary;
 
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;

public class MainClass {
	public static void main(String args[]) {
	
		
		
		final ServiceProvider serv = new ServiceProvider();
		
		
		/* Call StartDiscovery method to invoke device listener */
		
		serv.startDiscovery();  
		
				
		/* Waiting for devices to respond: Could show ProgressBar on UI */
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				
				//Retrieve HashMap of devices
				
				HashMap<String, String> list = serv.getDevicesHashMap();
				System.out.println(list.toString());
				
				//Shutdown Discovery
				//serv.stopDiscovery();
				serv.selectDevice("");
			}
		}, 6000);
		
		
	}
}
