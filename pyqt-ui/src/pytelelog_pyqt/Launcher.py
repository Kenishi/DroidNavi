'''
Created on May 5, 2014

@author: Jeremy May
'''
import sys
import glob

from PyQt4 import QtGui
from py4j.java_gateway import JavaGateway, GatewayClient
from MainWindow import MainWindow
from logging import StreamHandler, Logger, getLogger, DEBUG

class Launcher:
    '''
    Launcher is used for deployment launching when the 
    JavaGateway will be in the 'lib' subfolder.
    '''
    
    def __init__(self):     
        my_logger = getLogger('py4j')
        handler = StreamHandler()
        handler.setLevel(DEBUG)
        my_logger.setLevel(DEBUG)
        my_logger.addHandler(handler)
        
        if glob.glob("./lib/py4j-0.8.1.jar"):
            jarpath = glob.glob("./lib/py4j-0.8.1.jar")[0]
        elif glob.glob("./py4j-0.8.1.jar"):
            jarpath = glob.glob("./py4j-0.8.1")
        else:
            jarpath = None
        
        if glob.glob("./lib/droidnavi-gateway-server*"):
            classpath = glob.glob("./lib/droidnavi-gateway-server*")
        elif glob.glob("./droidnavi-gateway-server*"):
            classpath = glob.glob("./droidnavi-gateway-server*")[0]
        else:
            classpath = None
            
        self.__gateway = JavaGateway.launch_gateway(jarpath=jarpath, classpath=classpath, die_on_exit=True)
        print self.__gateway
        app = QtGui.QApplication(sys.argv)
        mainWindow = MainWindow(self.__gateway)
        sys.exit(app.exec_())
       
if __name__ == '__main__':
    Launcher()
    pass