Droid Navi v1.1

Version 1.1 brings 2 new things.


1) NIO Networking: I removed the blocking/threaded system I was using on the desktop client.
	This has allowed me to simplify a lot of the code by a lot.



2) Multicasting Events: I have added the ability for phones to send events via UDP multicast.
	This helps a lot because the phone no longer has to worry about keeping a constant connection
	with the desktop app or managing numerous IPs. 
	One things to note though. Not all phones can do multicasting and some routers may filter multicast packets.


You can test the multicast from the Status screen by pressing the Multicast status button and pressing "Test".
If you receive an Incoming Call notification on the desktop program from "Multicast Test" then it works.


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
6.1) Use the QR Code in the app. 

7) Slide back status and press the Service Status item again to start it "Running."	
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
	