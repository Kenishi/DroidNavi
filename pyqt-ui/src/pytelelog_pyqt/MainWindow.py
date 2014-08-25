'''
Created on May 6, 2014

@author: Jeremy May
'''

from Queue import Queue
from functools import partial

from PyQt4 import QtGui, QtCore
from components.NotifyWidget import NotifyWidget
from components.PairingDialog import PairingDialog
from components.settings import SettingsDialog, SettingsData
from components.about import AboutDialog
from components.AppIcon import AppIcon
from components.tray import Tray
from EventCallback import EventListener
from EventType import EventType

class MainWindow(QtGui.QMainWindow):
    
    receiveEvent = QtCore.pyqtSignal(object)
    
    def __init__(self, gateway):
        '''
        Constructor
        '''

        super(MainWindow,self).__init__();
        
        self.__gateway = gateway
        
        self.initUI()        
        self.initCallback(self.onEvent)
        self.initEventType()
        
    def initUI(self):
        '''
        Init main window UI
        
        '''
        
        central = QtGui.QWidget(self)
        grid = QtGui.QGridLayout(central)
        
        # Setup System Tray Icon
        self.tray = Tray(self)
        self.tray.display(False)
        
        # Set App Icon
        self.initIcon()
        
        # Menubar
        menuBar = self.createAppMenuBar()
        self.setMenuBar(menuBar)
        
        # Connected List
        connectLabel = QtGui.QLabel("Connected Devices:")
        grid.addWidget(connectLabel, 0, 0)
        
        self.connectList = ConnectList()
        grid.addWidget(self.connectList, 1, 0, 2, 1)
        
        # Pairing Helper Button
        pairHelper = QtGui.QPushButton()
        pairHelper.setText("Pair")
        pairHelper.clicked.connect(self.loadPairing)
        grid.addWidget(pairHelper, 4, 0)
        
        central.setLayout(grid)
        
        self.setCentralWidget(central)
        self.setGeometry(300, 300, 250, 100)
        self.setWindowTitle("Droid Navi")
        
        
        self.notifyHandler = NotifyHandler()
        self.show()
        self.raise_()
        self.activateWindow()
    
    def createAppMenuBar(self):
        '''
        Build the main window's Menu bar and return it
        
        '''
        
        menuBar = self.menuBar()
        fileMenu = menuBar.addMenu("&File")
        
        settingsAction = QtGui.QAction("&Options", self)
        settingsAction.setToolTip('Open program options')
        settingsAction.triggered.connect(lambda: self.openSettings(self))
        fileMenu.addAction(settingsAction)
        
        quitAction = QtGui.QAction("&Quit", self)
        quitAction.setToolTip("Quit program")
        quitAction.triggered.connect(self.closeEvent)
        fileMenu.addAction(quitAction)
        
        helpMenu = menuBar.addMenu("&Help")
        
        aboutActions = QtGui.QAction("&About", self)
        aboutActions.setToolTip("Show About dialog")
        aboutActions.triggered.connect(self.openAbout)
        helpMenu.addAction(aboutActions)
        
        return menuBar
        
    
    def initCallback(self, func):
        '''
        Setup the callback with the Java side
        
        '''
        
        # Hookup Signal
        self.receiveEvent.connect(self.handleEvent)
        
        self.callback = EventListener()
        self.callback.onEvent = self.onEvent
        try:
            self.__gateway.entry_point.addEventListener(self.callback)
        except:
            info = QtGui.QMessageBox()
            info.setText("Error setting up event callback. Exiting.")
            info.exec_()
            self.callback = None
            self.close()
            
    
    def initIcon(self):
        icon = AppIcon.getAppIcon()
        if icon:
            self.setWindowIcon(icon)
    
    def openAbout(self):
        '''
        Show the About dialog
        
        '''
        
        aboutDialog = AboutDialog()
        aboutDialog.exec_()
    
    @QtCore.pyqtSlot()
    def openSettings(self, parent=None):
        '''
        Show the Settings Dialog
        
        '''
        settingsDialog = SettingsDialog(parent)
        if parent:
            settingsDialog.exec_()
        else:
            self.settingsDialog = settingsDialog
            self.settingsDialog.show()
        pass
    
    def loadPairing(self):
        '''
        Show the pairing helper dialog
        
        '''
        
        pairingDialog = PairingDialog(self)
        pairingDialog.exec_()
        pass
    
    def initEventType(self):
        '''
        Setup a mock Enum style EventType for this instance.
        
        This is needed for interpreting events received from the Java server.
        
        '''
        
        self.EventType = EventType(self.__gateway)
    
    def displayEvent(self, event):
        self.editArea.append(event.toString())
    
    def onEvent(self, event):
        event.EventType = self.EventType
        
        self.receiveEvent.emit(event)
        
    def handleEvent(self, event):
      
        # Add/Remove on [Diss]Connect events
        if event.getEventType() == event.EventType.CLIENT_CONNECT:
            device = event.getDevice()
            if device:
                self.connectList.addDevice(device)
            pass
        elif event.getEventType() == event.EventType.SHUTDOWN:
            device = event.getDevice()
            if device:
                self.connectList.removeDevice(device) 
            pass
        
        # Check whether to display notification
        settingsData = SettingsData.fromPickle()
        if settingsData.shouldDisplayEvent(event):
            self.notifyHandler.queueEvent(event)
        
    
    def changeEvent(self, event):
        super(MainWindow, self).changeEvent(event)
        if type(event) is QtGui.QWindowStateChangeEvent:
            if self.isMinimized():
                QtCore.QTimer.singleShot(0, partial(self.minimizeToTray))
                event.ignore()
    
    def maximizeFromTray(self):
        self.show()
        self.raise_()
        self.activateWindow()
    
    def minimizeToTray(self):
        self.hide()
        self.tray.display(True)
        
    def closeEvent(self, event):
        if self.callback:
            self.__gateway.entry_point.removeEventListener(self.callback)
        if type(event) is bool:
            pass
        else:
            event.accept()
           
class ConnectList(QtGui.QListWidget):
    connectedDict = []
    
    def __init__(self):
        super(ConnectList, self).__init__()
        
    def addDevice(self, device):
        ip = device.getIP()
        if ip:
            ip_str = ip.toString()
            if ip_str and (ip_str not in self.connectedDict):
                self.connectedDict.append(ip_str)
                self.addItem(ip_str)
    
    def removeDevice(self, device):
        ip = device.getIP()
        if ip:
            ip_str = ip.toString()
            if ip_str and (ip_str in self.connectedDict):
                loc = self.connectedDict.index(ip_str)
                self.connectedDict.remove(ip_str)
                self.takeItem(loc)
                
            

class NotifyHandler:
    
    SHOW_TIME = 7  # Show the notification for 7 seconds 
    
    queue = Queue()
            
    def run(self):
        while True:
            if NotifyHandler.queue.empty():
                break
            event = self.queue.get()
            pixmap = self.getPixMap(event)
            self.display(pixmap)
            
    def queueEvent(self, event):
        NotifyHandler.queue.put(event)
        self.run()
        
    def display(self, pixmap):
        # Create the notification screen
        splash = QtGui.QSplashScreen(pixmap, QtCore.Qt.WindowStaysOnTopHint)
        
        # Figure out where to put it
        (x,y) = self.getDisplaySlot(splash)
        splash.setGeometry(x, y, splash.width(), splash.height())
        
        # Display for specified time
        self.splash = splash
        self.splash.show()
        QtCore.QTimer.singleShot(NotifyHandler.SHOW_TIME*1000, self.splash.close)
        
    
    def getPixMap(self, event):
        widget = NotifyWidget.createInstance(event)
        widget.resize(widget.sizeHint())
        pixmap = QtGui.QPixmap.grabWidget(widget)
        return pixmap
    
    @staticmethod
    def getDisplaySlot(splash):
        x_padding = 10
        y_padding = 10
        
        desktop = QtGui.QDesktopWidget()
        size = desktop.availableGeometry()
        x = size.width() - x_padding - splash.width()
        y = size.height() - y_padding - splash.height()
        
        return (x,y)
    
    pass