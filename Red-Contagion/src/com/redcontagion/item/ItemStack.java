package com.redcontagion.item;

import com.redcontagion.world.voxel.Block;

public class ItemStack {

	private int size;
	private int meta;
	public final Item ITEM;
	
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	public ItemStack(Item item, int count) {
		this(item, count, 0);
	}
	
	public ItemStack(Item item, int count, int meta) {
		if (item==null) throw new IllegalArgumentException("Cannot create item stack with null item");
		this.ITEM = item;
		setSize(count);
		this.meta = meta;
	}
	
	public ItemStack(Block block) {
		this(block, 1);
	}
	
	public ItemStack(Block block, int count) {
		this(block, count, 0);
	}
	
	public ItemStack(Block block, int count, int meta) {
		this(getItem(block), count, meta);
	}
	
	private static Item getItem(Block b) {
		if (b.ITEM_BLOCK==null) throw new IllegalArgumentException("Cannot create item stack for block with no item");
		return b.ITEM_BLOCK;
	}
	
	public int getSize() {
		return size;
	}
	
	public ItemStack setSize(int size) {
		this.size = Math.min(size, ITEM.getMaxStackSize(meta));
		return this;
	}
	
	public int getMeta() {
		return meta;
	}
	
	public ItemStack setMeta(int meta) {
		if (ITEM.hasMetadata()) this.meta = meta;
		return this;
	}
	
}