#DroidNavi
##'Hey Listen'
=========

### What does it do?
Receive alerts on your computer when your Android phone rings or you miss a call.

### What's with the name?
Droid should be obvious. 'Navi' however does not mean Navigation. Navi comes from the [annoying] fairy in Zelda: Ocarina of Time, who would draw your attention to important things.

### What stuff do you use?
* [Py4j - A Python to Java communication library](http://py4j.sourceforge.net/)
* PyQt - For the UI
* [Jackson JSON Processor](http://jackson.codehaus.org/)
* Maven  (Java server & Python UI only.)

### Building from source
I use Eclipse extensively and have included the project files in each subfolder for each part.
The Python UI (pyqt-ui) also has the pydev project files as well if you have that installed for Eclipse.

While the Java server and UI use Maven to manage dependencies at the moment. I have not setup the Android side to use maven yet. The main reason being that I use Eclipse for my development and currently (as far as I know) M2Eclipse, Eclipse's built in Maven plugin, does not play well with doing Android builds inside the environment. What this means is you will need to make sure the library dependencies are on your build path when you create the APK or build/run in the IDE. I've included the copy of the libraries I have been using in the android/lib on the repository.

For all intents and purposes, I will have these assumptions:
1. You are using Eclipse.
2. You have [Egit](http://www.eclipse.org/egit/) installed with Eclipse
3. You have Maven installed.
--* (Optional) You have [M2E](https://www.eclipse.org/m2e/) (Built maven support w/ Eclipse) if you want to get rid of reference errors in files and want to build/run inside Eclipse.
4. You have the latest Android SDK installed with Eclipse.

#### 1. Importing into Eclipse
1. In Eclipse, File > Import > Git > Projects from Git
2. Select Clone URI
3. Paste in the Clone URI from the repo. Next.
4. You'll want the 'master' branch. Next.
5. Select where you want the clone to go. This ideally should be where your local repos are stored. There are no submodules. Next.
6. Select "Import existing projects." Next.
7. You should see 3 projects.
--* DroidNavi-Android
--* DroidNavi-Server
--* DroidNavi-UI
8. Click finish and the projects will import.

#### 2. Fixing reference errors in Eclipse for DroidNavi-Server & UI (Requires M2Eclipse)
Likely after the projects are imported you will have errors due to Eclipse not being able to find dependencies.

1. Right click on DroidNavi-Server
2. Maven > Update Projects
3. Select DroidNavi-Server and DroidNavi-UI.
4. Select "Force Update of Snapshopts/Releases"
5. Click Ok and give it a second. The reference errors should dissapear from the Server and UI projects.

#### 3. Fix Android reference errors
First we need to add the Android Support Lib.
* Right click DroidNavi-Android > Android Tools > Add support library...

This should fix a large number of errors.

Next we need to add some libraries to the build path.
1. Right click DroidNavi-Android
2. Build Path > Configure build path...
3. Click the "Libraries" tab.

You should see a number of libraries already in here, some you have already. 
