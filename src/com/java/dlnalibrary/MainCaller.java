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
		serv.selectDevice("d6ae4960-aacb-6493-0000-0000534ff697");
		//serv.selectDevice("7c94075d-88a7-c9ef-fbfd-6eabba1aa291");
		//serv.sendStream("http://192.168.1.55/Happy.mp4", "Solid" , "Until We Last", "Earthgazing", "00:04:34");
		serv.sendStream("http://192.168.1.55/ABC.mp3", "DWBH!" , "BMF", "ABC", "00:03:34");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serv.play();
	}
}
