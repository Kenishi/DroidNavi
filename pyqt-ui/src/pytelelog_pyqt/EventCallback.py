'''
Created on May 5, 2014

@author: Kei
'''
import uuid

class EventListener(object):
    id = uuid.uuid4()
    
    def onEvent(self, event):
        return

    def equals(self, obj):
        return self.__eq__(obj)
    
    def __eq__(self, obj):
        if obj == None:
            return False
        if obj.id == self.id:
            return True
        else:
            return False
    
    class Java:
        implements = ['pctelelog.EventListener']
        pass