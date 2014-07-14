'''
Created on Jul 14, 2014

@author: Jeremy May
'''

import glob
from PyQt4 import QtGui


class AppIcon(QtGui.QIcon):

    def __init__(self):
        f = glob.glob('./logo.png')[0]
        if f:
            super(AppIcon, self).__init__(f)
            return
        f = glob.glob('../logo.png')[0]
        if f:
            super(AppIcon, self).__init__(f)
            return
        