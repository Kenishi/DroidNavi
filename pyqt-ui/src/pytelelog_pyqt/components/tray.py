'''
Created on Jul 14, 2014

@author: Jeremy May
'''
from PyQt4.QtGui import QSystemTrayIcon
from PyQt4.QtGui import QMenu

from AppIcon import AppIcon

class Tray(QSystemTrayIcon):

    def __init__(self, parent):
        super(Tray, self).__init__(AppIcon())
        
        self.parent = parent
        
        # Menu
        self.menu = QMenu()
        self.menu.addAction("Show Connected List", self, self.maximize())
        self.menu.addAction("Options", self.parent, self.parent.openSettings)
        self.setContextMenu(self.menu)
        
    def display(self, show):
        ''' Toggle showing the tray '''
        
        if show:
            self.setVisible(True)
            self.show()
        else:
            self.setVisible(False)
            self.hide()
    
    def maximize(self):
        ''' Show the main window and hide tray icon '''
        
        self.display(False)
        self.parent.maximizeFromTray()
    
    def activated(self, reason):
        if type(reason) is QSystemTrayIcon.DoubleClick:
            self.maximize()