package com.redcontagion.item;

/** The {@link Item} class defines the behavior for an item. Items are stored in {@link ItemStack}s with metadata that
 * can be used to specify more data about items, or have sub-items of an {@link Item} class.
 * 
 * @author Zekrom_64
 * @see com.redcontagion.item.ItemStack
 *
 */
public class Item {
	
	/** The maximum number of possible items */
	public static final int MAX_ITEMS = 65535;
	
	private static Item[] items = new Item[MAX_ITEMS];
	private static int nextItem = 0;
	
	/** Finds an item by ID number. If no item is found for the ID number, <b>null</b> is returned.
	 * 
	 * @param id Item ID
	 * @return Corresponding item, or <b>null</b>
	 */
	public static Item getItem(int id) {
		if (id <= 0) return null;
		return items[id];
	}
	
	/** Finds an item by unlocalized name. If no item is found for the unlocalized name, <b>null</b> is returned.
	 * 
	 * @param unlocName Unlocalized name
	 * @return Corresponding item, or <b>null</b>
	 */
	public static Item getItem(String unlocName) {
		for(Item i : items) {
			if (i == null) return null; // End of entries has been reached, no need to go further
			if (i.UNLOCALIZED_NAME.equals(unlocName)) return i;
		}
		return null;
	}
	
	/** Unlocalized name of the item */
	public final String UNLOCALIZED_NAME;
	/** ID for the item */
	public final int ITEM_ID;
	
	/** Creates a new item with an unlocalized name. When constructed, the item is automatically registered.
	 * 
	 * @param unlocName Unlocalized name
	 */
	public Item(String unlocName) {
		if (unlocName==null) throw new IllegalArgumentException("Item cannot have null unlocalized name");
		UNLOCALIZED_NAME = unlocName;
		if (nextItem==items.length) throw new IllegalStateException("Out of item IDs");
		ITEM_ID = nextItem++;
		items[ITEM_ID] = this;
	}
	
	/** Returns <b>true</b> if the item has metadata (damage or otherwise).
	 * 
	 * @return If the item has metadata
	 */
	public boolean hasMetadata() {
		return false;
	}
	
	/** Gets the maximum stack size for an item given its metadata.
	 * 
	 * @param meta Item metadata
	 * @return Maximum stack size
	 */
	public int getMaxStackSize(int meta) {
		return 100;
	}
	
}