'''
Created on May 18, 2014

@author: Kei
'''
import sys

from PyQt4 import QtGui
from py4j.java_gateway import JavaGateway, GatewayClient
from MainWindow import MainWindow
from logging import StreamHandler, Logger, getLogger, DEBUG
import subprocess

class Launcher:
    '''
    Launcher is used for deployment launching when the 
    JavaGateway will be in the 'lib' subfolder.
    '''
    
    def __init__(self):             
        command = ["java", "-jar", "./lib/pctelelog-gateway-server-0.0.1-SNAPSHOT.jar"]
        serverProc = subprocess.Popen(command)
        if(serverProc != None):
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
    Launcher()
    pass