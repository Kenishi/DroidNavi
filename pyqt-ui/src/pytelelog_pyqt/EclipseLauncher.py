'''
Created on May 17, 2014

@author: Jeremy May
'''

import sys

from PyQt4 import QtGui
from py4j.java_gateway import JavaGateway
from MainWindow import MainWindow

class Launcher:
    def __init__(self):
        self.__gateway = JavaGateway(start_callback_server=True)
        
        app = QtGui.QApplication(sys.argv)
        app.setApplicationName("Droid Navi")
        app.setApplicationVersion("0.1")
        mainWindow = MainWindow(self.__gateway)
        sys.exit(app.exec_())
       
if __name__ == '__main__':
    Launcher()
    pass