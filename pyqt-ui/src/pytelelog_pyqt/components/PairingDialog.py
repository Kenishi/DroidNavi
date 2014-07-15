'''
Created on Jun 18, 2014

@author: Jeremy May
'''
import qrcode
from PyQt4 import QtGui, QtCore
from qrcode.image.pure import PymagingImage

from StringIO import StringIO

class PairingDialog(QtGui.QDialog):

    def __init__(self, parent=None):
        super(PairingDialog, self).__init__(parent)
        self.setWindowTitle("Pairing")
    
    def exec_(self, *args, **kwargs):
        if self.initUi():
            ret = QtGui.QDialog.exec_(self, *args, **kwargs)
        else:
            ret = QtGui.QDialog.Rejected
        return ret
    
    def initUi(self):
        layout = QtGui.QVBoxLayout()
        
        # Get IPs
        ip_str = self.getComputerIpAddresses()
        if ip_str:
    
            # Add Comp. IP Address Frame
            ip_sec_label = QtGui.QLabel("Manual IP Pairing Method")
            ip_sec_label.setAlignment(QtCore.Qt.AlignHCenter)
            layout.addWidget(ip_sec_label)
            
            ip_frame = QtGui.QFrame()
            ip_frame.setFrameStyle(ip_frame.Sunken|ip_frame.StyledPanel)
            ip_frame.setToolTip("The IP Addresses currently assigned to this computer.\nYou should use the one that the phone will be on the same network with.")
            
            ip_frame_layout = QtGui.QVBoxLayout()
            ip_str_label = QtGui.QLabel(ip_str)
            ip_str_label.setAlignment(QtCore.Qt.AlignHCenter)
            ip_frame_layout.addWidget(ip_str_label)
            
            ip_frame.setLayout(ip_frame_layout)
            layout.addWidget(ip_frame)
            
            # Add QR Code
            qr_sec_label = QtGui.QLabel("QR Code Pairing Method")
            qr_sec_label.setAlignment(QtCore.Qt.AlignHCenter)
            layout.addWidget(qr_sec_label)
            
            img_data =  StringIO()
            qrcode.make(ip_str, image_factory=PymagingImage).save(img_data)
            
            qr_pix = QtGui.QPixmap()
            qr_pix.loadFromData(img_data.getvalue(), "PNG")
            qr_label = QtGui.QLabel()
            qr_label.setPixmap(qr_pix)
            layout.addWidget(qr_label)
            
            self.setLayout(layout)
            return True
        else:
            QtGui.QMessageBox.information(self, "Error", "Error retrieving computer IP. Check network connection is enabled/connected.")
            return False
        
    
    def getComputerIpAddresses(self):
        ''' 
        Retrieve IP addresses on the system
        by opening UDP sockets and retrieving
        socket info
        '''
        
        import socket
        try:
            ret = [(s.connect(('8.8.8.8', 80)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1]
        except:
            ret = None
        return ret

