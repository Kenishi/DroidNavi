'''
Created on May 6, 2014

@author: Kei
'''

from Queue import Queue

from PyQt4 import QtGui, QtCore
from components.NotifyWidget import NotifyWidget
from EventCallback import EventListener
from pytelelog_pyqt.EventType import EventType


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
        
        central = QtGui.QWidget(self)
        grid = QtGui.QGridLayout(central)
        
        self.editArea = QtGui.QTextEdit(central)
        self.setEnabled(False)
        grid.addWidget(self.editArea, 0,0,1,1)
        
        central.setLayout(grid)
        
        self.setCentralWidget(central)
        self.setGeometry(300, 300, 250, 100)
        self.setWindowTitle("Droid Navi")
        
        
        self.notifyHandler = NotifyHandler()
        self.show()
    
    def initCallback(self, func):
        # Hookup Signal
        self.receiveEvent.connect(self.handleEvent)
        
        self.callback = EventListener()
        self.callback.onEvent = self.onEvent
        self.__gateway.entry_point.addEventListener(self.callback)
    
    def initEventType(self):
        self.EventType = EventType(self.__gateway)
    
    def shutdownGateway(self):
        self.__gateway.entry_point.removeEventListener(self.callback)
        self.__gateway.shutdown()
    
    def displayEvent(self, event):
        self.editArea.append(event.toString())
    
    def onEvent(self, event):
        event.EventType = self.EventType
        
        self.receiveEvent.emit(event)
        
    def handleEvent(self, event):
        print "Event: " + event.toString()
        # (Impl later) Check pref if should handle event
        # (Impl later) Get notify prefs
        # Create notify widget if applicable
        self.notifyHandler.handleEvent(event)
        #widget = NotifyWidget.createInstance(event)
        
    def closeEvent(self, event):
        self.shutdownGateway()  
        super(MainWindow,self).closeEvent(event) 

class NotifyHandler:
    ''' Show the notification for 7 seconds '''
    SHOW_TIME = 7 
    
    queue = Queue()
            
    def run(self):
        while True:
            if NotifyHandler.queue.empty():
                break
            event = self.queue.get()
            pixmap = self.getPixMap(event)
            self.display(pixmap)
            
    def handleEvent(self, event):
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