package com.redcontagion.world.voxel;

import com.redcontagion.item.Item;

public class ItemBlock extends Item {
	
	public final Block BLOCK;
	
	public ItemBlock(Block b) {
		super(b.UNLOCALIZED_NAME);
		BLOCK = b;
	}
	
}