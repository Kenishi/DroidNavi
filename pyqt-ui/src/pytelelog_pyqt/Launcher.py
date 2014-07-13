'''
Created on May 5, 2014

@author: Jeremy May
'''
import sys

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
        
        self.__gateway = JavaGateway.launch_gateway(jarpath="./lib/py4j-0.8.1.jar", classpath="./lib/pctelelog*.jar", die_on_exit=True)
        print self.__gateway
        app = QtGui.QApplication(sys.argv)
        mainWindow = MainWindow(self.__gateway)
        sys.exit(app.exec_())
       
if __name__ == '__main__':
    Launcher()
    pass