'''
Created on Jun 20, 2014

@author: Jeremy May
'''

import time

from PyQt4 import QtGui
from PyQt4 import Qt

class ProgressLoader(QtGui.QDialog):
    
    def __init__(self, parent=None):
        super(ProgressLoader, self).__init__(parent)
        
        self.initUi()
        self.setAttribute(Qt.Qt.WA_DeleteOnClose)
        
    def initUi(self):
        layout = QtGui.QVBoxLayout()
        
        self.msg = QtGui.QLabel("Starting server...")
        layout.addWidget(self.msg)
        
        self.progressBar = QtGui.QProgressBar()
        self.progressBar.setMinimum(0)
        self.progressBar.setMaximum(0)
        self.progressBar.setFormat("")
        layout.addWidget(self.progressBar)
        
        self.setLayout(layout)
        self.setModal(False)
    
    def error(self):
        self.progressBar.setMinimum(0)
        self.progressBar.setMaximum(100)
        self.progressBar.setValue(0)
        self.msg.setText("Failed to start server.")
        time.sleep(3)
        self.finish()
        
    def finish(self):
        self.deleteLater()
        self.close()