'''
Created on Jun 25, 2014

@author: Jeremy May
'''
import pickle

from PyQt4 import QtGui

class SettingsDialog(QtGui.QDialog):
    
    def __init__(self, parent=None):
        '''
        Constructor
        '''
        
        super(SettingsDialog, self).__init__(parent)
        
        self.parent = parent
        self.settingsData = self.loadSettings()
        self.setWindowTitle("Settings")
        
        if parent:
            self.setModal(True)
        else:
            self.setModal(False)
            
        self.initUi()

    
    def initUi(self):
        layout = QtGui.QVBoxLayout()
        
        # Show notifications selection box
        notifyBox = self.createNotifySettingsBox()
        layout.addWidget(notifyBox)
        
        # Cancel/Apply/Ok
        actionLayout = self.createActionButtons()
        layout.addLayout(actionLayout)
        
        self.setLayout(layout)
        
    def onApply(self):
        self.settingsData.save()
        self.applyBtn.setEnabled(False)
        pass
    
    def onOK(self):
        self.settingsData.save()
        if self.isModal():
            self.accept()
        else:
            self.close()
        pass
    
    def onOptionChanged(self):
        self.applyBtn.setEnabled(True)
        
    def loadSettings(self):
        return SettingsData.fromPickle()
        
    def createNotifySettingsBox(self):
        notifyBox = QtGui.QGroupBox("Event Notifications")
        
        notifyBoxLayout = QtGui.QVBoxLayout()
        
        connectChk = QtGui.QCheckBox("Phone Connect")
        connectChk.setToolTip("Show connect notification when a phone connects to the program.")
        connectChk.setChecked(self.settingsData.getConnect())
        notifyBoxLayout.addWidget(connectChk)
        
        disconnectChk = QtGui.QCheckBox("Phone Disconnect")
        disconnectChk.setToolTip("Show disconnect notification when a phone disconnects from the program.")
        disconnectChk.setChecked(self.settingsData.getDisconnect())
        notifyBoxLayout.addWidget(disconnectChk)
        
        incomingChk = QtGui.QCheckBox("Incoming Call")
        incomingChk.setToolTip("Show incoming call notification when a call is being received.")
        incomingChk.setChecked(self.settingsData.getIncoming())
        notifyBoxLayout.addWidget(incomingChk)
        
        missedChk = QtGui.QCheckBox("Missed Call")
        missedChk.setToolTip("Show missed call notification when a call is missed.")
        missedChk.setChecked(self.settingsData.getMissed())
        notifyBoxLayout.addWidget(missedChk)
        
        notifyBox.setLayout(notifyBoxLayout)
        return notifyBox
    
    def createActionButtons(self):
        actionLayout = QtGui.QHBoxLayout()
        
        cancelBtn = QtGui.QPushButton("Cancel")
        if self.isModal():
            cancelBtn.clicked.connect(self.reject)
        else:
            cancelBtn.clicked.connect(self.close)
        actionLayout.addWidget(cancelBtn)
        
        self.applyBtn = QtGui.QPushButton("Apply")
        self.applyBtn.clicked.connect(self.onApply)
        self.applyBtn.setEnabled(False)
        actionLayout.addWidget(self.applyBtn)
        
        okBtn = QtGui.QPushButton("OK")
        okBtn.clicked.connect(self.onOK)
        actionLayout.addWidget(okBtn)
        
        return actionLayout
        
class SettingsData(object):
    
    def __init__(self):
        self.notify = { "connect" : True,
                        "disconnect" : True,
                        "incoming" : True, 
                        "missed" : True }
        pass
    
    def getConnect(self):
        return self.notify.get("connect")
    
    def getDisconnect(self):
        return self.notify.get("disconnect")
    
    def getIncoming(self):
        return self.notify.get("incoming")
    
    def getMissed(self):
        return self.notify.get("missed")
    
    def shouldDisplayEvent(self, event):
        if event.getEventType() == event.EventType.CLIENT_CONNECT:
            return self.getConnect()
        elif event.getEventType() == event.EventType.INCOMING_CALL:
            return self.getIncoming()
        elif event.getEventType() == event.EventType.MISSED_CALL:
            return self.getMissed()
        elif event.getEventType == event.EventType.SHUTDOWN:
            return self.getDisconnect()
    
    def save(self):
        
        pickleFile = None
        try:
            pickleFile = open("setting.dat", 'wb')
        except Exception:
            return False
        
        if pickleFile:
            pickle.dump(self, pickleFile)
            pickleFile.close()
        else:
            return False
        
        return True
    
    @classmethod
    def fromPickle(cls):
        
        settingData = cls()
        
        pickleFile = None
        try:
            pickleFile = open("setting.dat", 'rb')
        except Exception:
            pass
        
        if pickleFile:
            settingData = pickle.load(pickleFile)
            pickleFile.close()
        
        return settingData    