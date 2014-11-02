package com.java.dlnalibrary;

import java.util.Collection;
import java.util.HashMap;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
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
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.avtransport.callback.Stop;
import org.teleal.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.SeekMode;

public class ServiceProvider {
	
	HashMap<String, String> list = new HashMap<String, String>();
	UpnpService upnpService;	
	Registry globalRegistry;
	Service service;
	
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
		
		//Device d = device.findDevice(new UDN("b992bebc-d671-798d-328a-268d984773a3"));
		
		service = device.findService(new UDAServiceId("AVTransport"));
		System.out.println("Service " + service == null);
	}

	public void sendStream(String url) {
		  ActionCallback setAVTransportURIAction =
	        		new SetAVTransportURI(service, url, "NO METADATA") {
						@Override
						public void failure(ActionInvocation arg0,
								UpnpResponse arg1, String arg2) {
							// TODO Auto-generated method stub
							
						}
	        		};
	       upnpService.getControlPoint().execute(setAVTransportURIAction);
	}
	
	public void play() {
		 ActionCallback playAction =
			        new Play(service) {
			            @Override
			            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
			                // Something was wrong
			            }
			        };
			        upnpService.getControlPoint().execute(playAction);
	}
	
	public void stop() {
		 ActionCallback stopAction = new Stop(service) {
			
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				// TODO Auto-generated method stub
				
			}
		};
		upnpService.getControlPoint().execute(stopAction);
	}
	
	public void pause() {
		ActionCallback pauseAction = new Pause(service) {
			
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				// TODO Auto-generated method stub
				
			}
		};
		upnpService.getControlPoint().execute(pauseAction);
	}
	
	public void subscribeEvents() {
		System.out.println("In subscribe");
		SubscriptionCallback callback = new SubscriptionCallback(service, 1000) {
			
			@Override
			protected void failed(GENASubscription arg0, UpnpResponse arg1,
					Exception arg2, String arg3) {
				// TODO Auto-generated method stub
				System.out.println("FAIL HUA!" + arg2.getMessage());
			}
			
			@Override
			protected void eventsMissed(GENASubscription arg0, int arg1) {
				// TODO Auto-generated method stub
				System.out.println("HUAAA!");
			}
			
			@SuppressWarnings("fallthrough")
			@Override
			protected void eventReceived(GENASubscription sub) {
			    try {
					LastChange lastChange = new LastChange(
					          new AVTransportLastChangeParser(),
					          sub.getCurrentValues().get("LastChange").toString()
					  );
					
					
					System.out.println(lastChange.getEventedValue(0,AVTransportVariable.TransportState.class).getValue());
				    System.out.println("Duration: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackDuration.class).getValue());
				    System.out.println("CurrentItem: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrack.class).getValue());
				    System.out.println("Status: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentPlayMode.class).getValue());
				    System.out.println("Metadata: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackMetaData.class).getValue());
			    
			    } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			    
			}
			
			@Override
			protected void established(GENASubscription arg0) {
				// TODO Auto-generated method stub

			}
			
			@Override
			protected void ended(GENASubscription arg0, CancelReason arg1,
					UpnpResponse arg2) {
				// TODO Auto-generated method stub
				
			}

		};
		upnpService.getControlPoint().execute(callback);
	}
	
	public void getDetails() {
		/*System.out.println(lastChange.getEventedValue(0,AVTransportVariable.TransportState.class).getValue());
        System.out.println("Duration: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackDuration.class).getValue());
        System.out.println("CurrentItem: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrack.class).getValue());
        System.out.println("Status: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentPlayMode.class).getValue());
        System.out.println("Metadata: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackMetaData.class).getValue());*/
	}
	public void seek(String time) {
		ActionCallback seekAction = new Seek(service, time) {
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				// TODO Auto-generated method stub
				System.out.println("FAILED!");
			}
		};
		upnpService.getControlPoint().execute(seekAction);
	}
}















