package com.redcontagion.item;

import com.redcontagion.world.voxel.Block;
import com.zekrom_64.ze.base.io.DynamicDataMap;

/** An item stack is a container that holds a number of items. Item stacks 
 * 
 * @author Zekrom_64
 *
 */
public class ItemStack {

	private int size;
	private int meta;
	/** A dynamic data map for dynamic data associated with the item stack */
	public DynamicDataMap dynData;
	/** The item contained in this stack */
	public final Item ITEM;
	
	/** Creates a new item stack of 1 of the given item.
	 * 
	 * @param item Item
	 */
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	/** Creates a new item stack of the given item.
	 * 
	 * @param item Item
	 * @param count Number of items
	 */
	public ItemStack(Item item, int count) {
		this(item, count, 0);
	}
	
	/** Creates a new item stack of the given item with the given metadata.
	 * 
	 * @param item Item
	 * @param count Number of items
	 * @param meta Metadata
	 */
	public ItemStack(Item item, int count, int meta) {
		if (item==null) throw new IllegalArgumentException("Cannot create item stack with null item");
		this.ITEM = item;
		setSize(count);
		this.meta = meta;
	}
	
	/** Creates a new item stack of 1 of the given block.If the block does not have
	 * and associated item, an IllegalArgumentException is thrown.
	 * 
	 * @param block Block
	 */
	public ItemStack(Block block) {
		this(block, 1);
	}
	
	/** Creates a new item stack of the given block. If the block does not have
	 * and associated item, an IllegalArgumentException is thrown.
	 * 
	 * @param block Block
	 * @param count Stack size
	 */
	public ItemStack(Block block, int count) {
		this(block, count, 0);
	}
	
	/** Creates a new item stack of the given block with the given metadata. If the block does not have
	 * and associated item, an IllegalArgumentException is thrown.
	 * 
	 * @param block Block
	 * @param count Stack size
	 * @param meta Metadata
	 */
	public ItemStack(Block block, int count, int meta) {
		this(getItem(block), count, meta);
	}
	
	private static Item getItem(Block b) {
		if (b.itemBlock==null) throw new IllegalArgumentException("Cannot create item stack for block with no item");
		return b.itemBlock;
	}
	
	/** Gets the size of the item stack.
	 * 
	 * @return Stack size
	 */
	public int getSize() {
		return size;
	}
	
	/** Sets the size of the item stack. If the given size is greater than the maximum stack
	 * size, the maximum stack size is used instead.
	 * 
	 * @param size Stack size
	 * @return The item stack
	 */
	public ItemStack setSize(int size) {
		this.size = Math.min(size, ITEM.getMaxStackSize(meta));
		return this;
	}
	
	/** Gets the metadata of the item stack.
	 * 
	 * @return Stack metadata
	 */
	public int getMeta() {
		return meta;
	}
	
	/** Sets the metadata for the item stack.
	 * 
	 * @param meta Metadata
	 * @return The item stack
	 */
	public ItemStack setMeta(int meta) {
		if (ITEM.hasMetadata()) this.meta = meta;
		return this;
	}
	
	/** Gets the unlocalized name of the item.
	 * 
	 * @return Item unlocalized name
	 */
	public String getUnlocalizedName() {
		return ITEM.getUnlocalizedName(this);
	}
	
}