'''
Created on May 26, 2014

Setup Script to create Compiled Windows Exe

@author: Jeremy May
'''

from distutils.core import setup
import py2exe

files = ["lib/*"]

setup(name = "DroidNavi",
      version = "0.1",
      author = "Jeremy May",
      url = "http://github.com/Kenishi",
      license = "GNU General Public License (GPL) v2",
      packages = ['pytelelog_pyqt','pytelelog_pyqt/components'],
      package_data = {'package' : files},
      windows = ["pytelelog_pyqt/POpenLauncher.py"],
      options = {"py2exe": {"skip_archive": True, "bundle_files": 1, "compressed": True, "includes": ["sip"],"dll_excludes": ["MSVCP90.dll"]}}
      )