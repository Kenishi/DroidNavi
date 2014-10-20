package pctelelog;

import pctelelog.events.AbstractEvent;

public interface EventListener {
		public void onEvent(AbstractEvent event);
		@Override
		public boolean equals(Object obj);
}
