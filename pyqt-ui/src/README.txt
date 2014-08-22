Droid Navi v1.1

Version 1.1 brings 2 new things.

1) NIO Networking: I removed the blocking/threaded system I was using on the desktop client.
	This has allowed me to simplify a lot of the code by a lot.

2) Multicasting Events: I have added the ability for phones to send events via UDP multicast.
	This helps a lot because the phone no longer has to worry about keeping a constant connection
	with the desktop app or managing numerous IPs. 
	One things to note though. Not all phones can do multicasting and some routers may filter multicast packets.
	If you do not have multicasting, you can still fall back on connecting to computers using IPs.
	In addition, if you phone doesn’t support multicasting, the desktop client will now serve as a multicast relay!
	See Bugs and quirks below for a few notes on multicasting.

Note on using on Mac: The app can work on Mac but OS X handles Apps slightly different and
	I can't guarantee the app will shutdown cleanly on the Mac. Plus you will need to start
	it from Terminal. This is on my TODO list now.

INSTALLATION
============
1) Download the droidnavi-1.0-release.zip from GitHub.
	- Alternatively you can compile from the code on the hub. Please see the Repository README.md for instructions.
2) Extract the zip into a folder.
	- Inside you should find 2 folders. "android" and "desktop"
3) Copy the droidnavi-android-1.0.apk to your phone and install it by running it.
	- Note: An explanation of why certain permissions are required by the app
		can be found in the main repository's README.md
4) Go back to your computer and find the "desktop" folder again, run the DroidNavi.exe.
5) You may see a popup from your firewall for a Java program. You will need to allow this 
	in order for your phone to connect to your PC.
6) Run the android App.
	Note: In order for the program to work the phone and desktop must be on the same
		network. So you will need to have Wi-Fi running on the phone.
7) Slide to the "Pair" tab.
8) Select "Pair with new PC"
9) Click "Pair" on PC App.

Manual Input Method:
9.1) Enter the IP on the PC in the App box.
9.2) Hit OK

QR Code:
9.1) Use the QR Code in the app. 

10) Slide back to the left and click the "Service Stopped" button. This will now light up and change
	to "Service On."	
11) If everything worked correctly, you should see a "Phone Connected" screen pop up in the bottom
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
* Multicast: If your phone can do multicast but your router/network is blocking the packets. Then it won’t be possible
	use the app at all. I’m working on a “Full Multicast Test” that will help figure out if multicast is fully 
	supported and if not, fall back on TCP/IP. Either that or just add an option to enable/disable it.
	If you have this issue, use v1.0 till then, functionally its identical to v1.1.

* Continual Missed Call Alerts: If you have a missed call on your phone, you will continue to receive alerts
	about the missed call until you go into Call Log on the phone and clear the status. This is not a bug.
	The purpose of the program is not to by pass having to go to your phone, its to let you know that 
	you need to check your phone.

* Rejecting a call counts as a Miss: If you decide to reject an incoming call and not answer it. A miss call
	alert will show up. While this is a bug in functionality, there isn't any way to avoid it due to how the
	android API handles reporting changes in Call State.
	