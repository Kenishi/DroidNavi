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

class POpenLauncher:
    '''
    Launcher is used for deployment launching when the 
    JavaGateway will be in the 'lib' subfolder.
    '''
    
    def __init__(self):
        # Look for the gateway server
        jar_file = glob.glob('../lib/pctelelog-gateway-server*')
        if len(jar_file) == 0:
            jar_file = glob.glob('./lib/pctelelog-gateway-server*')
        if len(jar_file) == 0:
            raise IOError("pctelelog-gateway-server JAR could not be found. Please make sure the JAR is in the 'lib' folder.")
     
        ## Start Server as Subprocess
        command = ["java", "-jar", jar_file[0]]
        
        # Set process to have no window
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW
        
        serverProc = subprocess.Popen(args=command, startupinfo=startupinfo)
        
        if(serverProc != None):
            time.sleep(1) # Give the server time to start up
            try:
                self.__gateway = JavaGateway(start_callback_server=True)
            except:
                try:
                    self.__gateway.shutdown()
                except:
                    sys.exit(1)
                sys.exit(1)
                
            print self.__gateway
            
            app = QtGui.QApplication(sys.argv)
            mainWindow = MainWindow(self.__gateway)
            
            result = app.exec_()
            serverProc.kill()
            sys.exit(result)
            
       
if __name__ == '__main__':
    POpenLauncher()
    pass