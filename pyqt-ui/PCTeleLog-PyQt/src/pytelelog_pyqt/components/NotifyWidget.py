'''
Created on May 19, 2014

@author: Jeremy May
'''

from PyQt4 import QtGui
from notifications import *

class NotifyWidget(object):
    
    @staticmethod
    def createInstance(event, pref=None):
        # Create easy ref enum EventType
        EventType = event.EventType
        
        # Determine type and set correct Widget
        e_type = event.getEventType()
        if e_type == EventType.INCOMING_CALL:
            return IncomingCallNotify(event, pref)
        elif e_type == EventType.CALL_ENDED:
            return CallEndedNotify(event, pref)
        elif e_type == EventType.MISSED_CALL:
            return MissedCallNotify(event, pref)
        elif e_type == EventType.CLIENT_CONNECT:
            return ClientConnectNotify(event, pref)  
        elif e_type == EventType.SHUTDOWN:
            return DisconnectNotify(event, pref)   
        else:
            pass 
        pass
