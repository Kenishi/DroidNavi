
class EventType(object):
    
    '''
    The counterpart for EventType.java enum
    '''
    
    def __init__(self, gateway):
        self.INCOMING_CALL = gateway.jvm.pctelelog.events.EventType.INCOMING_CALL
        self.MISSED_CALL = gateway.jvm.pctelelog.events.EventType.MISSED_CALL
        self.CALL_ENDED = gateway.jvm.pctelelog.events.EventType.CALL_ENDED
        self.CLIENT_CONNECT = gateway.jvm.pctelelog.events.EventType.CLIENT_CONNECT
        self.SHUTDOWN = gateway.jvm.pctelelog.events.EventType.SHUTDOWN