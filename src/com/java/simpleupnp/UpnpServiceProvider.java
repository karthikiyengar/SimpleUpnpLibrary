package com.java.simpleupnp;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.*;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.*;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpnpServiceProvider {

    private static final Logger log = Logger.getLogger(UpnpServiceProvider.class.getName());
    JSONObject list = null;
    Hashtable <String, String> map = null;
    UpnpService upnpService = null;
    Service service = null;
    JSONArray jsonArray = new JSONArray();


    /* RegistryListener asynchronously listens for new devices on the network */

    public void startDiscovery() {

        RegistryListener listener = new RegistryListener() {
            @Override
            public void localDeviceRemoved(Registry registry, LocalDevice device) {
            }

            @Override
            public void localDeviceAdded(Registry registry, LocalDevice device) {
            }

            @Override
            public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            }

            @Override
            public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
                log.log(Level.SEVERE, "Device Discovery Started");
            }
			

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                /* Create JSON Array of objects */

                log.log(Level.INFO, "Found: " + device.getDisplayString());
                Map<String, String> map = new HashMap<String, String>();
                map.put("deviceName", device.getDisplayString());
                map.put("deviceUUID", device.getIdentity().getUdn().toString().substring(5));

                jsonArray.put(map);
            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                log.log(Level.INFO, "Removed: " + device.getDisplayString());
                list.remove(device.getDisplayString());
            }

            @Override
            public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
                log.log(Level.SEVERE, "Device Discovery Failed");
            }

            @Override
            public void beforeShutdown(Registry registry) {
                log.log(Level.INFO, "Shutting down!");
            }

            @Override
            public void afterShutdown() { }
        };

        upnpService = new UpnpServiceImpl(listener);


        /* Search for all Upnp Devices */

        upnpService.getControlPoint().search(new STAllHeader());

        //Choose only AVTransport Devices
        //upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("AVTransport")));


    }

    public String getDeviceList() {
        return jsonArray.toString();
    }


    public void stopDiscovery() {
        upnpService.shutdown();
    }


    /* Select device from uuid */

    public void selectDevice(String uid) {
        try {
            Device device = upnpService.getControlPoint().getRegistry().getDevice(new UDN(uid), false); //boolean = false if embedded device
            service = device.findService(new UDAServiceId("AVTransport"));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error: Device not found. Bad UUID format.");
        }
    }

    /* Send media to selected device. Overloaded, with and without metadata parameters */

    public void sendStream(String url) {
           try {
               ActionCallback setAVTransportURIAction =
                       new SetAVTransportURI(service, url, "NO METADATA") {
                           @Override
                           public void failure(ActionInvocation arg0,
                                               UpnpResponse arg1, String arg2) {
                               log.log(Level.SEVERE, "Sending media failed");
                           }
                       };
               upnpService.getControlPoint().execute(setAVTransportURIAction);
           }
           catch (NullPointerException e) {
               log.log(Level.SEVERE, "Error: Have you selected the device yet?");
           }
    }
    
    public void sendStream(String url, String title, String artist, String album, String duration) {

        String[] type = null;
        String metadata = null;
        Long contentLength = null;
        HttpURLConnection conn = null;

        try {
            /* Deriving contentLength */

            URL uri = new URL(url);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            contentLength = conn.getContentLengthLong();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Couldn't get content length");
        }

        /* Using Tika to get MIME Type */

	    TikaInputStream tikaIS = null;
		try {
			Detector DETECTOR = new DefaultDetector(
			MimeTypes.getDefaultMimeTypes());
			tikaIS = TikaInputStream.get(new URI(url));

        	Metadata metaDetect = new Metadata();
        
        	String contentType = DETECTOR.detect(tikaIS, metaDetect).toString();
        	type = contentType.split("/");
		}
		catch (Exception e) {
			log.log(Level.SEVERE, "Unable to parse");
		}
		finally {
			try {
				tikaIS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        /* Setting Track Metadata from parameters and MIME */

        DIDLContent didl = new DIDLContent();
        MimeType mimeType = new MimeType(type[0], type[1]);
        
        
        String creator = artist; // Required
        PersonWithRole personRole = new PersonWithRole(creator, "Performer");
        
        if (type[0].equals("video")) {
            didl.addItem(new VideoItem("1", "0", title, artist, new Res(mimeType, contentLength, duration, null, url)));
        }
        if (type[0].equals("audio")) {
        		didl.addItem(new MusicTrack(
                "1", "0", // 101 is the Item ID, 3 is the parent Container ID
                title,
                creator, album, personRole,
                        new Res(mimeType, contentLength, duration, null, url)
        ));
        }
        try {
			metadata = new DIDLParser().generate(didl);
		} catch (Exception e) {
			log.log(Level.WARNING, "Could not generate Metadata");
		}


        /* Setting media URI */

        ActionCallback setAVTransportURIAction =
                new SetAVTransportURI(service, url, metadata) {
                    @Override
                    public void failure(ActionInvocation arg0,
                                        UpnpResponse arg1, String arg2) {
                        log.log(Level.SEVERE, "Sending media failed");
                    }
                };
        upnpService.getControlPoint().execute(setAVTransportURIAction);
    }

    /* Control functions */

    public void play() {
        try {
            ActionCallback playAction =
                    new Play(service) {
                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            // Something was wrong
                        }
                    };
            upnpService.getControlPoint().execute(playAction);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Error: Have you selected the device yet?");
        }
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

    /* Seek using absolute time */

    public void seek(String time) {
        ActionCallback seekAction = new Seek(service, time) {
            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                log.log(Level.WARNING, "Seek Failed!");
            }
        };
        upnpService.getControlPoint().execute(seekAction);
    }

    /* Extracting track details */

    public void getDetails() {
        final JSONObject obj = new JSONObject();
        try {
            GetTransportInfo gti = new GetTransportInfo(service) {
                @Override
                public void received(ActionInvocation actionInvocation, TransportInfo transportInfo) {
                    obj.put("currentState", transportInfo.getCurrentTransportState());
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

                }
            };
            upnpService.getControlPoint().execute(gti);
            GetPositionInfo gpi = new GetPositionInfo(service) {

                @Override
                public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                    log.log(Level.SEVERE, "Unable to get position information");

                }

                @Override
                public void received(ActionInvocation arg0, PositionInfo arg1) {

                    //obj.put("currentStatus", lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue());
                    obj.put("elapsedSeconds", arg1.getTrackElapsedSeconds());
                    obj.put("absoluteTime", arg1.getAbsTime());
                    obj.put("elapsedPercent", arg1.getElapsedPercent());
                    obj.put("currentTrackURI", arg1.getTrackURI());
                    obj.put("duration", arg1.getTrackDuration());
                    obj.put("metadata", arg1.getTrackMetaData());
                    obj.put("remainingSeconds", arg1.getTrackRemainingSeconds());
                    System.out.println(obj);
                }
            };

            upnpService.getControlPoint().execute(gpi);

        } catch (NullPointerException e) {
            System.out.println("Null Pointer Exception");
        }
    }
}















