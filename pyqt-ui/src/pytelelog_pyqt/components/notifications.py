'''
Created on May 19, 2014

@author: Jeremy May
'''

from PyQt4 import QtGui, QtCore

from NotifyParts import Name, PhoneNumber, Email, Device, Photo

class AbstractNotify(QtGui.QDialog):
    
    dialogColor = "color:white;"
    textColor = "color:black;"
    fontSize = "font-size: 20pt;"
    
    def __init__(self, event, pref):
        super(AbstractNotify, self).__init__()
        
        # Dialog styling
        styleSheet = "QDialog { %(fontsize)s %(bgcolor)s }" % {"fontsize" : self.fontSize,
                                                               "bgcolor" : self.dialogColor }
        self.setStyleSheet(styleSheet)
        
        layout = QtGui.QGridLayout()
        layout.setSizeConstraint(QtGui.QLayout.SetMinimumSize)
        
        # Notify Bar
        self.notify_text = QtGui.QLabel(self.text)
        self.notify_text.setSizePolicy(QtGui.QSizePolicy.Minimum | QtGui.QSizePolicy.Preferred,
                                       QtGui.QSizePolicy.Minimum | QtGui.QSizePolicy.Preferred)
        
        styleSheet = "QLabel { %(color)s }" % { "color" : self.textColor }
        self.notify_text.setStyleSheet(styleSheet)
        layout.addWidget(self.notify_text, 0, 0, 1, 3, QtCore.Qt.AlignHCenter)
        
        if pref:
            pass
        else:
            pass
            #PhoneWidget()
          
        self.setLayout(layout)
        
    pass

    def show(self, pref=None):
        if pref:
            pass
        else:
            self.resize(self.sizeHint())
            pass
        
        super(AbstractNotify, self).show()
        pass
        
        
class AbstractCallNotify(AbstractNotify):
    
    def __init__(self, event, pref):
        super(AbstractCallNotify, self).__init__(event, pref)
        layout = self.layout()
        
        if pref:
            pass
        else:
            notifyDivider = QtGui.QFrame()
            notifyDivider.setFrameStyle(QtGui.QFrame.HLine)
            layout.addWidget(notifyDivider, 1, 0, 1, 3)
            layout.addWidget(Name(event, pref), 2, 0, 1, 1)
            layout.addWidget(PhoneNumber(event, pref), 2, 1, 1 ,1)
            layout.addWidget(Email(event, pref), 3, 0, 1, 1)
            layout.addWidget(Device(event, pref), 3, 1, 1 ,1)
            
            photo = Photo.fromBytes(event, pref)
            if photo:
                layout.addWidget(photo, 2, 2, 2, 1)
    pass

class IncomingCallNotify(AbstractCallNotify):
    '''
    
    Widget to display for incoming calls
    
    '''
    
    text = "Incoming Call"
    textColor = "color:green;"

    def __init__(self, event, pref):
        super(IncomingCallNotify, self).__init__(event, pref)
        pass

class CallEndedNotify(AbstractCallNotify):
    '''
    
    Widget to display for call ended
    
    '''
    
    text = "Call Ended"
    textColor = "color:black;"
    
    def __init__(self, event, pref):
        super(CallEndedNotify, self).__init__(event, pref)
        pass

class MissedCallNotify(AbstractCallNotify):
    '''
    
    Widget to display for missed call
    
    '''
    
    text = "Missed Call"
    textColor = "color:red;"
    
    def __init__(self, event, pref):
        super(MissedCallNotify, self).__init__(event, pref)

class ClientConnectNotify(AbstractNotify):
    '''
    
    Widget to display for client/phone connecting
    
    '''
    
    text = "Phone Connected"
    textColor = "color:black;"
    
    def __init__(self, event, pref):
        super(ClientConnectNotify, self).__init__(event, pref)

class DisconnectNotify(AbstractNotify):
    '''
    
    Widget to display for client/phone disconnecting
    
    '''
    
    text = "Phone Disconnected"
    textColor = "color:black;"
    
    def __init__(self, event, pref):
        super(DisconnectNotify, self).__init__(event, pref)
        
        
        