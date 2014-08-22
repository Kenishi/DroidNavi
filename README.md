
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
* [Py4j - A Python to Java communication library](http://py4j.sourceforge.net/)
* Netty 4.1.0-Beta3 on the Desktop Side
* PyQt - For the UI
* [Jackson JSON Processor](http://jackson.codehaus.org/)
* Maven  (Java server & Python UI only.)

### Building from source
I use Eclipse extensively and have included the project files in each subfolder for each part.
The Python UI (pyqt-ui) also has the pydev project files as well if you have that installed for Eclipse.

While the Java server and UI use Maven to manage dependencies at the moment. I have not setup the Android side to use maven yet. The main reason being that I use Eclipse for my development and currently (as far as I know) M2Eclipse, Eclipse's built in Maven plugin, does not play well with doing Android builds inside the environment. What this means is you will need to make sure the library dependencies are on your build path when you create the APK or build/run in the IDE. I've included the copy of the libraries I have been using in the 'ext_libs' directory in the Android project folder.

For all intents and purposes, I will have these assumptions:

1. You are using Eclipse.
2. You have PyQT installed for Python.
3. You have [Egit](http://www.eclipse.org/egit/) installed with Eclipse
4. You have Maven installed.
  * (Optional) You have [M2Eclipse](https://www.eclipse.org/m2e/) (Built maven support w/ Eclipse) if you want to get rid of reference errors in files and want to build/run inside Eclipse.
5. You have the latest Android SDK installed with Eclipse.

#### 1. Importing into Eclipse
1. In Eclipse, File > Import > Git > Projects from Git
2. Select Clone URI
3. Paste in the Clone URI from the repo. Next.
4. You'll want the 'master' branch. Next.
5. Select where you want the clone to go. This ideally should be where your local repos are stored. There are no submodules. Next.
6. Select "Import existing projects." Next.
7. You should see 3 projects.
  * DroidNavi-Android
  * DroidNavi-Server
  * DroidNavi-UI
8. Click finish and the projects will import.

#### 2. Fixing reference errors in Eclipse for DroidNavi-Server & UI (Requires M2Eclipse)
Likely after the projects are imported you will have errors due to Eclipse not being able to find dependencies.

1. Right click on DroidNavi-Server
2. Maven > Update Projects
3. Select DroidNavi-Server and DroidNavi-UI.
4. Select "Force Update of Snapshopts/Releases"
5. Click Ok and give it a second. The reference errors should dissapear from the Server and UI projects.

#### 3. Fix Android reference errors
Next the support library must be added to fix reference errors
* Right click DroidNavi-Android > Android Tools > Add support library...

This should fix all the remaining errors.
All the other external libraries are in the "ext_libs" folder and should already be part of the classpath if you used the Eclipse project files.

#### 4. Building, Running, Making distrib for desktop server
1. From command line/terminal navigate to the Root of the project git folder.
2. Run the command: `mvn clean install assembly:single`
  * This will build the server and copy the Python files into the "distribution" folder.
3. To create the python exe. Change Directory to: /distribution/target/droidnavi-VERSION-bin/
4. Run: `python setup.py py2exe`
  * This will build the python code into an exe. You can find this in the /dist folder.
5. Run "DroidNavi.exe" to start the server and UI.
