Droid Navi v0.1 alpha

DISCLAIMER: This is an alpha version of the software meaning there are still a large number of bugs as well
	as very bland and clunky GUIs. This is not a final version of the software. Its a release to show off the
	basic functionality.

INSTALLATION
============
1) Download the droidnavi-0.1-release.zip from GitHub.
	- Alternatively you can compile from the code on the hub. Please see the Repository README.md for instructions.
2) Extract the zip into a folder.
	- Inside you should find 2 folders. "android" and "desktop"
3) Copy the droidnavi-android-0.1.apk to your phone and install it by running it.
	- Note: An explanation of why certain permissions are required by the app
		can be found in the main repository's README.md
4) Go back to your computer and find the "desktop" folder again, run the DroidNavi.exe.
5) You may see a popup from your firewall for a Java program. You will need to allow this 
	in order for your phone to connect to your PC.
6) Run the android App.
	Note: In order for the program to work the phone and desktop must be on the same
		network. So you will need to have Wi-Fi running on the phone.
7) Slide to the right to get to the pairing screen.
8) Select pair and "Manually input IP"
9) Input the IP for the desktop and hit ok.
10) Slide back to the left and click the "Service Off" button. This will now light up and change
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
* Continual Missed Call Alerts: If you have a missed call on your phone, you will continue to receive alerts
	about the missed call until you go into Call Log on the phone and clear the status. This is not a bug.
	The purpose of the program is not to by pass having to go to your phone, its to let you know that 
	you need to check your phone.

* Rejecting a call counts as a Miss: If you decide to reject an incoming call and not answer it. A miss call
	alert will show up. While this is a bug in functionality, there isn't any way to avoid it due to how the
	android API handles reporting changes in Call State.
	
* What's with this GUI?! : Yes, I know. It's ugly. This is an initial release of the software meant to show
	off the BASIC functionality of the program. Improvements on the GUI along with settings and more features
	will come in later releases.