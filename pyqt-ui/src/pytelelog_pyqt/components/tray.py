'''
Created on Jul 14, 2014

@author: Jeremy May
'''
from functools import partial

from PyQt4.QtGui import QSystemTrayIcon
from PyQt4.QtGui import QMenu
from PyQt4.QtCore import pyqtSlot

from AppIcon import AppIcon

class Tray():

    def __init__(self, parent):
        
        icon = AppIcon.getAppIcon()
        if icon:
            self.tray = QSystemTrayIcon(icon)
        else:
            self.tray = QSystemTrayIcon()
        self.parent = parent
        
        self.tray.setToolTip("Droid Navi")
        
        # Menu
        self.menu = QMenu()
        self.menu.addAction("Show Connected List", partial(self.maximize))
        self.menu.addAction("Options", partial(self.parent.openSettings))
        self.menu.addAction("Exit", partial(self.parent.close))
        self.tray.setContextMenu(self.menu)
        
        # Connect handlers
        self.tray.activated.connect(self.activated)
    
    def getTray(self):
        return self.tray
     
    def display(self, show):
        ''' Toggle showing the tray '''
        
        if show:
            self.tray.show()
        else:
            self.tray.hide()
    
    @pyqtSlot()
    def maximize(self):
        ''' Show the main window and hide tray icon '''
        
        self.display(False)
        self.parent.maximizeFromTray()
    
    @pyqtSlot()
    def activated(self, reason):
        if reason == QSystemTrayIcon.DoubleClick:
            self.maximize()
    