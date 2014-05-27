package com.aberdyne.androidtelelog;

import com.aberdyne.androidtelelog.PairListFragment.ListItem;

public interface ListEditable {
	public void onAdd(ListItem item);
	public void onRemove(ListItem item);
}