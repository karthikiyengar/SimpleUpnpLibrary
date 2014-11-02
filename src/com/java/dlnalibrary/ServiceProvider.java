package com.java.dlnalibrary;

import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.GetMediaInfo;
import org.teleal.cling.support.avtransport.callback.GetTransportInfo;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.avtransport.callback.Stop;
import org.teleal.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.contentdirectory.callback.Browse.Status;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.MediaInfo;

public class ServiceProvider {
	
	HashMap<String, String> list = new HashMap<String, String>();
	UpnpService upnpService;	
	Service service;
	LastChange lastChange;
	private static final Logger log = Logger.getLogger(ServiceProvider.class.getName() );
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
				log.log(Level.INFO, "Found: " + device.getDisplayString());
				list.put(device.getIdentity().getUdn().toString(), device.getDisplayString());
			}
			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				log.log(Level.INFO, "Removed: " + device.getDisplayString());
				list.remove(device.getIdentity().getUdn().toString());
			}
			@Override
			public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device,
					Exception ex) {
				
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
	
	public HashMap<String, String> getDeviceList() {
		return list;
	}
	
	
	public void stopDiscovery() {
		upnpService.shutdown();
	}
	
	public void selectDevice(String uid) {
		try {
			Device device = upnpService.getControlPoint().getRegistry().getDevice(new UDN(uid), false); //boolean = false if embedded device
			service = device.findService(new UDAServiceId("AVTransport"));
			subscribeEvents();
		}
		catch (Exception e) {
			log.log(Level.SEVERE, "Error: Device not found. Bad UUID format.");
		}
	}

	public void sendStream(String url, String title, String artist, String album) {
		String metadata;
		metadata = "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\" xmlns:sec=\"http://www.sec.co.kr/\"><item id=\"\" parentID=\"\" restricted=\"\"><upnp:class>object.item.audioItem.musicTrack</upnp:class>object.item.audioItem.musicTrack<dc:title>" + title + "</dc:title><dc:creator></dc:creator><upnp:artist>" + artist + "</upnp:artist><upnp:albumArtURI></upnp:albumArtURI><dc:date></dc:date><upnp:album>" + album + "</upnp:album><upnp:originalTrackNumber>1</upnp:originalTrackNumber><res protocolInfo=\"\" size=\"\" duration=\"\"></res></item></DIDL-Lite>";

		ActionCallback setAVTransportURIAction =
					new SetAVTransportURI(service, url, metadata) {
						@Override
						public void failure(ActionInvocation arg0,
								UpnpResponse arg1, String arg2) {
							// TODO Auto-generated method stub
						}
	        		};
	        		//setAVTransportURIAction.getActionInvocation().setInput("CurrentURI", "WHATEVER!");
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
		SubscriptionCallback callback = new SubscriptionCallback(service, 1000) {
			
			@Override
			protected void failed(GENASubscription arg0, UpnpResponse arg1,
					Exception arg2, String arg3) {
				log.log(Level.WARNING, "GENA Subscription failed!" + arg2.getMessage());
			}
			
			@Override
			protected void eventsMissed(GENASubscription arg0, int arg1) {
				log.log(Level.INFO, "Event Missed!");
			}
			
			
			@Override
			protected void eventReceived(GENASubscription sub) {
			    try {
					lastChange = new LastChange(
					          new AVTransportLastChangeParser(),
					          sub.getCurrentValues().get("LastChange").toString()
					  );
			    } catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			protected void established(GENASubscription arg0) {
				log.log(Level.INFO, "GENA Subscription Established");
			}
			
			@Override
			protected void ended(GENASubscription arg0, CancelReason arg1,
					UpnpResponse arg2) {
				log.log(Level.INFO, "GENA Subscription Ended");
			}

		};
		upnpService.getControlPoint().execute(callback);
	}
	
	 
	/* getDetails() will cause a NullPointerException exception if called immediately after subscribeEvents() */
	/* Can use GetMediaInfo()? */
	public void getDetails() {
		try {
        System.out.println("Duration: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackDuration.class).getValue());
        System.out.println("CurrentItem: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackURI.class).getValue());
        System.out.println("Status: "+lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue());
        System.out.println("Metadata: "+lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackMetaData.class).getValue());
        
		}
		catch (NullPointerException e) {
			System.out.println("None");
		}
	}
	public void seek(String time) {
		ActionCallback seekAction = new Seek(service, time) {
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				log.log(Level.WARNING, "Seek Failed!");
			}
		};
		upnpService.getControlPoint().execute(seekAction);
	}
}















