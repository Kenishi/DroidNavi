Droid Navi v1.2

Version 1.2 brings 1 major and 1 minor change

1) SWT UI: As stated in a prior release, I had been considering getting rid of the Python-Java setup. I had considered
	using Jambi wrappers to continue using Qt, but I didn’t like that the project appears to have become inactive.
	instead I settled on rewriting the UI using the Standard Widget Toolkit put out by Eclipse. This has worked 
	quite well but it has removed some of the nice meshing that Qt offered on Mac. There is a File and Help menu
	present on Mac, which does not follow standard Mac user interface standards.

	The benefits of the change are that there should no longer be any odd errors when trying to the start the PC
	app up.

	In addition to the new UI, I also added some new notification effects. There is now a slide-in and fade effect.
	You can now also change how long the notification will remain on the window.

	The pairing dialog on the desktop now populates a list and you can click the IP you want to use in the QRCode.
	Simply select the item in the list to change the QR Code.
	
2) Multicast Sockets are now OIO (Blocking): Due to Mac platform issues (i.e.: default Java is 1.6). This shouldn’t impact performance
	in any way. TCP connections are still NIO.

INSTALLATION
============
1) Extract the release zip.
2) Copy the APK in the 'android' folder to you phone and run.
	-Note: An explanation on required permissions can by found on the repository
		README
3) Run the desktop application. A firewall notice may show up, you will need to allow
	this in order for the program to work.
4) Run the android app.
5) Click "Service Status" item to start the service.

Using Multicast
1) To use multicast and avoid having to pair with computers. First
	look on the status screen and see if it says "Multicast Status: Available."
2) If it shows available, next touch the item.
3) (Make sure the desktop program is running) Next press "Test"
4.a) If the Incoming Call "Multicast Test" appears, press yes.
4.b) If the event did not appear, press no.
5) If the multicast worked, then you are ready to receive notifications on calls.
	If the multicast failed, check your router settings and see if there is a setting
	for allowing Multicast events/broadcasts and make sure they are allowed.
	You can re-run the test by pressing the "multicast status" item at any point.

Using Pairing
1) If the service is currently "Running" press the item and set it to "Off"
2) To strictly use pairing, press the multicast status item and say no.
3) Slide to the "Pair" tab.
4) Select "Pair with new PC"
5) Click "Pair" on PC App.

Manual Input Method:
6.1) Enter the IP on the PC in the App box.
6.2) Hit OK

QR Code:
6.1) Select an IP in the list on the desktop.
6.2) Use the QR Code that is display.

7) Slide back to status and press the Service Status item again to start it "Running."	
8) If everything worked correctly, you should see a "Phone Connected" screen pop up in the bottom
	right hand corner of your desktop screen. You should now receive alerts when you get a call or have
	missed calls.

What is this?
=============
Droid Navi is an application combo-set which will let you see notifications on your desktop
whenever you are receiving a phone call on your Android phone or have missed a call.

This works by having an application running on both your computer and your Android phone.

What's with the name?
=====================
'Droid' should be obvious, anDROID. Some might mistake Navi for NAVIgation but that's not the meaning.
In the Nintendo 64 game, Zelda: Ocarina of Time, there was a (annoying?) fairy that would constantly 
say "Hey Listen!" To bring your attention to certain things. That fairy's name was Navi. 

Why would I need this?
======================
Good question! Have you ever been playing a game or watching a movie with you headphones on
and completely missed an important phone call? Using this program you can now see a pop up in
the corner of your screen showing you the caller info.

BUGS, ISSUES, QUIRKS
====================

* Continual Missed Call Alerts: If you have a missed call on your phone, you will continue to receive alerts
	about the missed call until you go into Call Log on the phone and clear the status. This is not a bug.
	The purpose of the program is not to by pass having to go to your phone, its to let you know that 
	you need to check your phone.

* Rejecting a call counts as a Miss: If you decide to reject an incoming call and not answer it. A miss call
	alert will show up. While this is a bug in functionality, there isn't any way to avoid it due to how the
	android API handles reporting changes in Call State.

* Battery Drain: While I haven’t fully confirmed this. I think due to how I check for missed calls, there is a 
	moderately higher amount of battery power used. There are no continuous signals sent out by Android OS
	to let an application know that there still exist missed calls, so in order to know, you have to poll
	the call logs and check. Its a balance between polling too little and too often.
	