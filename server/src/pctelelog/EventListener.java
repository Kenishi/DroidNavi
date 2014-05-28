package pctelelog;

import pctelelog.events.AbstractEvent;

public interface EventListener {
		public void onEvent(AbstractEvent event);
		public boolean equals(Object obj);
}
