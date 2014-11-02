package com.java.dlnalibrary;
 
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
				
				HashMap<String, String> list = serv.getDeviceList();
				System.out.println(list.toString());
				
				
				/*
				 * BubbleSoft - Karthik - d6ae4960-aacb-6493-0000-0000534ff697
				 * 				Bala - 21e829cb-230c-5f94-0000-000039b28a64
				 * ArchLaptop - 7c94075d-88a7-c9ef-fbfd-6eabba1aa291
				 */
				
				//serv.selectDevice("7c94075d-88a7-c9ef-fbfd-6eabba1aa291");
				serv.selectDevice("21e829cb-230c-5f94-0000-000039b28a64");
				
				//serv.subscribeEvents();
				//serv.addStream();
				
				//serv.sendStream("http://192.168.1.55:8000/01%20-%20Mama%20Kin.mp3");
				
				//serv.seek("00:00:20");
				//serv.stop();
			}
		}, 4000);
		
		timer.schedule(new TimerTask() {
			public void run() {
				
				serv.sendStream("http://192.168.1.55:8000/ABC.mp3", "Crappy Hindi Song", "jnjl","hjk");
				//serv.sendStream("http://192.168.1.55:8000/Happy.mp4", "DWBH", "jnjl","hjk");
				serv.play();
				serv.getDetails();
			}
		}, 6000);
	}
}
