
from PyQt4 import QtGui, QtCore

class Name(QtGui.QLabel):
    
    def __init__(self, event, pref):
        super(Name, self).__init__()
        if pref:
            pass # Set prefs
        text = event.getContactInfo().getName().getDisplayName()
        self.setText(text)
        
class PhoneNumber(QtGui.QLabel):
    
    def __init__(self, event, pref):
        super(PhoneNumber, self).__init__()
        if pref:
            pass
        
        self.setText(event.getContactInfo().getNumber().toString())

class Email(QtGui.QLabel):
    
    def __init__(self, event, pref):
        super(Email, self).__init__()
        if pref:
            pass
        
        self.setText(event.getContactInfo().getEmail().toString())

class Photo(QtGui.QLabel):
    
    @classmethod
    def fromBytes(cls, event, pref):
        photo = event.getContactInfo().getPhoto()
        if photo:
            photo_bytes = photo.getDecodedData();
        else:
            return None
        
        print type(photo_bytes)
        if len(photo_bytes) > 0:
            pix = QtGui.QPixmap()
            if pix.loadFromData(QtCore.QByteArray(photo_bytes)):
                label = cls("")
                label.setPixmap(pix)
                return label
            else:
                return None
        else:
            return None
        
class Device(QtGui.QLabel):
    
    def __init__(self, event, pref):
        super(Device, self).__init__()
        if pref:
            pass
        
        name = event.getDevice().getName()
        ip = event.getDevice().getIP().getHostAddress()