'''
Created on May 18, 2014

@author: Jeremy May
'''
import sys
import os
import glob
import time

import PyQt4.Qt as Qt
from PyQt4 import QtGui
from py4j.java_gateway import JavaGateway
from MainWindow import MainWindow
import subprocess
from pytelelog_pyqt.components.ProgressLoader import ProgressLoader

class POpenLauncher:
    '''
    Launcher is used for deployment launching when the 
    JavaGateway will be in the 'lib' subfolder.
    '''
    
    def __init__(self):
        # Start QT
        app = QtGui.QApplication(sys.argv)
        app.setApplicationName("Droid Navi")
        app.aboutToQuit.connect(self.exitHandler)
        self.app = app
        
        # Look for the gateway server
        jar_file = glob.glob('../lib/droidnavi-gateway-server*')
        if len(jar_file) == 0:
            jar_file = glob.glob('./lib/droidnavi-gateway-server*')
        if len(jar_file) == 0:
            info = QtGui.QMessageBox()
            info.setText("pctelelog-gateway-server JAR could not be found. Please make sure the JAR is in the 'lib' folder. Exiting")
            info.exec_()
     
        ## Start Server as Subprocess
        command = ["java", "-jar", jar_file[0]]
        
        if os.name == "nt":
            # Set process to have no window
            startupinfo = subprocess.STARTUPINFO()
            startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW
        
            self.serverProc = subprocess.Popen(args=command, startupinfo=startupinfo)
        else:
            self.serverProc = subprocess.Popen(args=command)
        
        if(self.serverProc != None):
            time.sleep(1) # Give the server time to start up
            
            '''Attempt to setup connection to gateway'''
            attempt_count = 0
            self.__gateway = None
            
            while not self.__gateway:
                try:
                    self.__gateway = JavaGateway(start_callback_server=True)
                except:
                    self.__gateway = None
                    attempt_count += 1
                    if attempt_count > 5:
                        break
                    time.sleep(3)

            # Exit if we got no connection
            if not self.__gateway:
                self.serverProc.kill()
                QtGui.QMessageBox.critical(None, "Error", "Failed to start java server. Exiting.")
                sys.exit(1)
                return
            
            app.setQuitOnLastWindowClosed(False)
            
            mainWindow = MainWindow(self.__gateway)
            mainWindow.setAttribute(Qt.Qt.WA_DeleteOnClose)
            mainWindow.destroyed.connect(self.quitApp)
            
            result = self.app.exec_()

            self.serverProc.kill()
            self.serverProc.wait()
            sys.exit(result)
    
    def quitApp(self, obj):
        self.app.quit()
    
    def exitHandler(self):
        self.__gateway.shutdown(True)
       
if __name__ == '__main__':
    POpenLauncher()
    pass