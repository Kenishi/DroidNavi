    '''
Created on May 26, 2014

Setup Script to create Compiled Windows Exe

@author: Jeremy May
'''

from distutils.core import setup
import py2exe

setup(name = "DroidNavi",
      version = "1.0",
      author = "Jeremy May",
      url = "http://github.com/Kenishi",
      license = "GNU General Public License (GPL) v2",
      packages = ['pytelelog_pyqt'],
      scripts = ["bin/pytelelog_pyqt"],
      package_data = [{"library": "lib/*"}],
      windows = ["POpenLauncher.py"],
      options = {"py2exe": {"skip_archive": True, "includes": ["sip"]}}
      )
