SimpleUpnpLibrary
=================

A simplified UPnP library which allows you to stream and control media on UPnP/DLNA devices. Uses cling, which can be found at http://4thline.org/projects/cling/

How to use this library?
========================

1. Include the SimpleUpnp.jar file in your project and create a UpnpServiceProvider object.
2. Call the startDiscovery() method to begin monitoring for Devices in your network. Allow the discovery procedure to run for a couple of seconds.
3. Use the getDeviceList() method to obtain a JSON String of the devices detected on the network and their UUIDs
4. Call the selectDevice(String uuid) method using the UUID of the desired device. The UUID for one particular device will remain the same throughout.
5. Use the sendStream(String url) method to send a target URI to the device. Alternatively, you can also provide media metadata using sendStream(String url, String title, String artist, String album, String duration);
6. You can now use the play(), pause(), stop() and seek(String absoluteTime) methods to control the device. For the seek method, time will be in the HH:MM:SS format.
7. You can invoke the getDetails() method at any point in time after you select the device to get status information for the device. It will return a JSON formatted string.
8. You can invoke the stopDiscovery() method to stop discovering devices. Should be called during cleanup operations.


To Do
=====
- Support for automatic metadata extraction.
- Playlists support. Next and Previous buttons.
