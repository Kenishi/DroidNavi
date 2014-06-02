'''
Created on May 26, 2014

Setup Script to create Compiled Windows Exe

@author: Jeremy May
'''

import site
import os
import glob
from distutils.core import setup

import py2exe

site_package = site.getsitepackages()[1]


dat_files = []
# Get Libs
for file_ in os.listdir("./lib"):
    f1 = "./lib/" + file_
    if os.path.isfile(f1):
        f2 = "lib", [f1]
        dat_files.append(f2)

# Get Imageformats
imageformats = site_package + "/PyQt4/plugins/imageformats/"
for file_ in os.listdir(imageformats):
    f1 = imageformats + file_
    if os.path.isfile(f1):
        f2 = "imageformats", [f1]
        dat_files.append(f2)

# Get QT Dlls
pyqt4_site = site_package + "/PyQt4/"
qtcore = ".", [pyqt4_site + "QtCore4.dll"]
qtgui = ".", [pyqt4_site + "QtGui4.dll"] 

dat_files.append(qtcore)
dat_files.append(qtgui)
    

setup(name = "DroidNavi",
      version = "0.1",
      author = "Jeremy May",
      url = "http://github.com/Kenishi",
      license = "GNU General Public License (GPL) v2",
      data_files = dat_files,
      windows = [{"script": "launcher.py"}],
      options = {"py2exe": {"bundle_files": 1,
                            "compressed": True,
                            "packages":["py4j", "pytelelog_pyqt"],
                            "includes": ["sip", "PyQt4.QtCore", "PyQt4.QtGui"],
                            "dll_excludes": ["MSVCP90.dll", "QtCore4.dll", "QtGui4.dll"]}}
      )