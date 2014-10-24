
# ![Droid Navi](/logo.png)DroidNavi
##'Hey Listen!'

### What does it do?
Receive alerts on your computer when your Android phone rings or you miss a call.

### What's with the name?
Droid should be obvious. 'Navi' however does not mean *Navi* gation. Navi comes from the [annoying] fairy in Zelda: Ocarina of Time, who would draw your attention to important things.

### Permissions Explanation
The Android app requires a number of permissions.
* *read phone status and identity* & *reroute outgoing calls* 
  
  This permission is required in order for the app to be able to know when a phone call occurs. In addition it allows the app to gather information such as the phone number of the caller. 
* *read call log*
  
  Required in order to check whether there has been any missed calls.
* *read your contacts*
  
  Required in order to pair contact information with the phone number of a caller, so it can be sent to the desktop server.
* *full network access*
  
  Required so the app can connect to the desktop server and send information.

### What stuff do you use?
* [Standard Widget Toolkit](http://www.eclipse.org/swt/)
* Netty 4.1.0-Beta3 on the Desktop Side
* [Jackson JSON Processor](http://jackson.codehaus.org/)
* [QRGen w/ ZXING core](https://github.com/kenglxn/QRGen) for QRCode generation on desktop
* Maven

### Building from source
The easiest way to build from source is to install Maven.

1. Clone the Repo
2. Change Directory(cd) to the repo folder
3. Run from the command line: `mvn package`

You can find the desktop application in:
`distribution/target/droid-<version>-bin/swt-ui/target/`

Building the Android side is a little more difficult and requires Eclipse, as far as I know, to do it. You'll also need to have M2Eclipse installed in order to resolve maven dependencies.

1. Open eclipse
2. Import the eclipse project in `/server/` this is DroidNavi-Server.
  * The Android app uses some of the classes from this project in order to communicate with the server.
3. Import the eclipse project in `/android/AndroidTelelog/` this is DroidNavi-Android.
4. Check the Build Path properties for the android project and make sure DroidNavi-Server is listed as a required project. If not, add it.
  * This is done under the "Projects" tab in "Java Build Path."
5. Also check that all the library jars in the `/ext_libs/` folder in the android project folder, are added under "Libraries" in the Java Build Path.
6. Right click DroidNavi-Server's project in the Package Explorer window.
7. Select Maven > Update Project.
8. Do an update on the DroidNavi-Server project.
  * This should resolve any of the reference errors present in the android project.
9. Right click the Android project in Package Explorer.
10. Select Android > Fix support library.
  * This will fix reference errors for the support-v4 library.
11. At this point all the reference errors should be gone. You should now be able to build and run the application in an emulator (API 16+) or create your own signed APK.
