'''
Created on Jul 14, 2014

@author: Jeremy May
'''

import glob
from PyQt4 import QtGui


class AppIcon(QtGui.QIcon):

    def __init__(self, f):
        super(AppIcon, self).__init__(f)
    
    @classmethod
    def getAppIcon(cls):
        f = glob.glob('./logo.png')
        if f:
            f = f[0]
            return cls(f)
        f = glob.glob('../logo.png')
        if f:
            f = f[0]
            return cls(f)
        
        return None
        
        