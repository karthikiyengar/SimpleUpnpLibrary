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
				serv.selectDevice("some uuid");
				serv.subscribeEvents();
				//serv.getDetails();
				//serv.sendStream("http://192.168.1.55:8000/01%20-%20Mama%20Kin.mp3");
				//serv.sendStream("http://192.168.1.55:8000/ABC.mp3");
				//serv.play();
				//serv.seek("00:00:20");
				//serv.stop();
			}
		}, 4000);
		
		
	}
}
