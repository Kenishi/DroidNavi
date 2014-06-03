package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.PairListFragment.ListItem;

public interface ListEditable {
	public void onAdd(ListItem item);
	public void onRemove(ListItem item);
}