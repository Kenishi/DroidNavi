'''
Created on Jun 29, 2014

@author: Jeremy May
'''
import glob
from PyQt4 import QtGui

class AboutDialog(QtGui.QDialog):
    
    def __init__(self, parent=None):
        super(AboutDialog, self).__init__(parent)
        
        self.initUi()
        
    def initUi(self):
        self.setWindowTitle("About")
        
        layout = QtGui.QVBoxLayout()
        
        logoFile = None
        if glob.glob("../logo.png"):
            logoFile = "../logo.png"
        elif glob.glob("./logo.png"):
            logoFile = "./logo.png"

        if logoFile:            
            img = QtGui.QPixmap(logoFile)
            
        logoLabel = QtGui.QLabel()
        logoLabel.setPixmap(img)
        layout.addWidget(logoLabel)
        
        aboutStr =  ("Droid Navi v0.1\n" +
                    "\n" +
                    "Licensed under LGPL v2.0\n" +
                    "\n" +
                    "Libraries in use: \n" +
                    "Jackson JSON Processor 1.9.13\n" +
                    "Py4j 0.8.1\n" +
                    "Log4j2 2.0 RC1")
        aboutText = QtGui.QLabel(aboutStr)
        layout.addWidget(aboutText)
        
        okButton = QtGui.QPushButton("OK")
        okButton.clicked.connect(self.accept)
        layout.addWidget(okButton)
        
        self.setLayout(layout)
        