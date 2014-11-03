package com.java.dlnalibrary;



public class MainCaller {
	public static void main(String args[]) {
		ServiceProvider serv = new ServiceProvider();
		serv.startDiscovery();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(serv.getDeviceList().toString());
		serv.selectDevice("7c94075d-88a7-c9ef-fbfd-6eabba1aa291");
		serv.sendStream("http://192.168.1.55:8000/ABC.mp3", "Water" , "Until We Last", "Earthgazing");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serv.play();
	}
}
