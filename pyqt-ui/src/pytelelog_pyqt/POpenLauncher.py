'''
Created on May 18, 2014

@author: Kei
'''
import sys
import glob
import time

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
        
        # Show Starting Server dialog
        loadingDialog = ProgressLoader()
        loadingDialog.show()
        
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
        
        # Set process to have no window
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW
        
        serverProc = subprocess.Popen(args=command, startupinfo=startupinfo)
        
        if(serverProc != None):
            time.sleep(1) # Give the server time to start up
            
            '''Attempt to setup connection to gateway'''
            attempt_count = 0 #
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
                serverProc.kill()
                loadingDialog.error()
                sys.exit(1)
            
            loadingDialog.finish()
            mainWindow = MainWindow(self.__gateway)
            
            result = app.exec_()
            serverProc.kill()
            sys.exit(result)
            
       
if __name__ == '__main__':
    POpenLauncher()
    pass