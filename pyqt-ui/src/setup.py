'''
Created on May 26, 2014

Setup Script to create Compiled Windows Exe

@author: Jeremy May
'''

import site
import os
import sys
import glob
from distutils.core import setup

# Py2App Mac builder
if sys.platform == 'darwin':
	import py2app
	APP = ['DroidNavi.py']
	DATA_FILES = ['CHANGELOG.txt', 'README.txt', './lib/', 'logo.png']
	OPTIONS = {'argv_emulation': True,
	 'iconfile': 'logo.icns',
	 'packages': 'py4j',
	 'qt_plugins': 'imageformats'}

	setup(
	    app=APP,
	    data_files=DATA_FILES,
	    options={'py2app': OPTIONS},
	    setup_requires=['py2app'],
	)

# Py2exe builder	
elif sys.platform == 'win32':
	import py2exe
	site_package = site.getsitepackages()[1]


	dat_files = []
	# Get Libs
	for file_ in os.listdir("./lib"):
		f1 = "./lib/" + file_
		if os.path.isfile(f1):
			f2 = "lib", [f1]
			dat_files.append(f2)
		
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
	
	# Add Logo
	dat_files.append((".", ["./logo.png"]))

	# Add README
	dat_files.append((".", ["./README.txt"]))

	# Add CHANGELOG
	dat_files.append((".", ["./CHANGELOG.txt"]))

	setup(name = "DroidNavi",
		  version = "1.1",
		  author = "Jeremy May",
		  url = "http://github.com/Kenishi",
		  license = "GNU General Public License (GPL) v2",
		  data_files = dat_files,
		  windows = [{"script": "DroidNavi.py",
					  "icon_resources": [(1, './logo.ico')]}],
		  options = {"py2exe": {"bundle_files": 1,
								"compressed": True,
								"packages":["py4j", "pytelelog_pyqt"],
								"includes": ["sip", "PyQt4.QtCore", "PyQt4.QtGui"],
								"dll_excludes": ["MSVCP90.dll", "QtCore4.dll", "QtGui4.dll"]}}
		  )