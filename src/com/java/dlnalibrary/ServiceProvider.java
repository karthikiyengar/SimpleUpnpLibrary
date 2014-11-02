package com.java.dlnalibrary;

import java.util.Collection;
import java.util.HashMap;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.UDADeviceTypeHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;

public class ServiceProvider {
	
	HashMap<String, String> list = new HashMap<String, String>();
	UpnpService upnpService;	
	Registry globalRegistry;
	
	public void startDiscovery() {
	
		
		/* START REGISTRY LISTENER */
		
		RegistryListener listener = new RegistryListener() {	
			@Override
			public void localDeviceRemoved(Registry registry, LocalDevice device) {}
			@Override
			public void localDeviceAdded(Registry registry, LocalDevice device) {}	
			@Override
			public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {}
			@Override
			public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {}
			
			/* DISCOVERING REMOTE DEVICE */
			
			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				System.out.println("Found: " + device.getDisplayString());
				list.put(device.getIdentity().getUdn().toString(), device.getDisplayString());
				/*globalRegistry = registry;
				Collection<Device> devices = registry.getDevices();
				System.out.println("Ye achha wala" + devices.toString());*/
			}
			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				System.out.println("Removed: " + device.getDisplayString());
				list.remove(device.getIdentity().getUdn().toString());
			}
			@Override
			public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device,
					Exception ex) {
				// TODO Auto-generated method stub
				
			}
			
			/* SHUTDOWN EVENTS */
			@Override
			public void beforeShutdown(Registry registry) {
				// TODO Auto-generated method stub
			
			}
			
			@Override
			public void afterShutdown() {
			}
		};
		
		
		/* END REGISTRY LISTENER */
		
		/* INVOKING LISTENER */
		
		upnpService = new UpnpServiceImpl(listener);
        //upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("AVTransport")));
		upnpService.getControlPoint().search(new STAllHeader());
	}
	
	public HashMap<String, String> getDevicesHashMap() {
		return list;
	}
	
	
	public void stopDiscovery() {
		upnpService.shutdown();
	}
	
	public void selectDevice(String uid) {
		//upnpService.getRegistry().getDevice(new UDN, arg1)
		Device device = upnpService.getControlPoint().getRegistry().getDevice(new UDN("7c94075d-88a7-c9ef-fbfd-6eabba1aa291"), false); //boolean = false if embedded device
		//
		//UDN udn = new UDN("7c94075d-88a7-c9ef-fbfd-6eabba1aa291");
		//Device device = registry.getDevice(udn, true);
		
		//Collection<Device> devices = upnpService.getRegistry().getDevices();
		//System.out.println(devices.toString());
		
		/*Device outDevice = null;
		for (Device device : devices) {
			outDevice = device.findDevice(new UDN("7c94075d-88a7-c9ef-fbfd-6eabba1aa291"));
		}
		
		*/
		System.out.println(device == null);
		//Device d = device.findDevice(new UDN("b992bebc-d671-798d-328a-268d984773a3"));
		
		Service service = device.findService(new UDAServiceId("AVTransport"));
        ActionCallback setAVTransportURIAction =
        		new SetAVTransportURI(service, "http://192.168.1.55:8000/Happy.mp4", "NO METADATA") {
        
					@Override
					public void failure(ActionInvocation arg0,
							UpnpResponse arg1, String arg2) {
						// TODO Auto-generated method stub
						
					}
        		};


        ActionCallback playAction =
        new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        upnpService.getControlPoint().execute(setAVTransportURIAction);
        upnpService.getControlPoint().execute(playAction);
		
	
        
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*	
	
	
	
	
	
	
	          //Device d = device.findDevice(new UDN("b992bebc-d671-798d-328a-268d984773a3"));
              //service = d.findService(new UDAServiceId("AVTransport"));
	          
	          // This will create necessary network resources for UPnP right away
	          System.out.println("Starting Cling...");
	          UpnpService upnpService = new UpnpServiceImpl(listener);
	          
	          
	          
	          // Send a search message to all devices and services, they should respond soon
	          upnpService.getControlPoint().search(new STAllHeader());

	          
	          // Let's wait 10 seconds for them to respond
	          System.out.println("Waiting 10 seconds before shutting down...");
	          try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	          System.out.print(list.toString());
	          // Release all resources and advertise BYEBYE to other UPnP devices
	          System.out.println("Stopping Cling...");
	          upnpService.shutdown();
	}
	/*
	public void selectDevice(? id) {
		
	}
	
	public void selectDevice(? devicename) {
		
	}
}*/