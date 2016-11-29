package com.redcontagion.world.voxel;

import java.util.Arrays;

import com.redcontagion.item.ItemStack;
import com.redcontagion.world.IWorldEditor;
import com.redcontagion.world.WorldLocal;

public abstract class Block {

	public static final int MAX_BLOCKS = 65535;
	private static Block[] blocks = new Block[MAX_BLOCKS];
	private static int nextBlock = 0;
	
	public static Block getBlock(int id) {
		if (id <= 0) return null;
		return blocks[id - 1];
	}
	
	public static Block getBlock(String unlocName) {
		for(Block b : blocks) {
			if (b==null) return null;
			if (b.UNLOCALIZED_NAME.equals(unlocName)) return b;
		}
		return null;
	}
	
	public static Block[] getAllBlocks() {
		return Arrays.copyOf(blocks, nextBlock);
	}
	
	public static boolean isValidID(int id) {
		return id <= nextBlock;
	}
	
	public final int BLOCK_ID;
	public final String UNLOCALIZED_NAME;
	public final ItemBlock ITEM_BLOCK;
	
	public Block(String unlocName) {
		if (unlocName==null) throw new IllegalArgumentException("Block cannot have null unlocalized name");
		if (getBlock(unlocName)!=null) throw new IllegalArgumentException("Block with unlocalized name \"" + unlocName + "\" is already registered");
		UNLOCALIZED_NAME = unlocName;
		if (nextBlock==blocks.length) throw new IllegalStateException("Out of block IDs");
		blocks[nextBlock++] = this;
		BLOCK_ID = nextBlock;
		ITEM_BLOCK = getItemBlock();
	}
	
	protected ItemBlock getItemBlock() {
		return new ItemBlock(this);
	}
	
	// Events
	public void onGenerated(WorldLocal w, int x, int y, int z, int meta) { }
	
	public void onPlaced(WorldLocal w, int x, int y, int z, int meta) { }
	
	public void onPlacedByEntity(WorldLocal w, int x, int y, int z, int meta, IWorldEditor placer) { }
	
	public void onDestroyed(WorldLocal w, int x, int y, int z, int meta) { }
	
	public void onDestroyedByEntity(WorldLocal w, int x, int y, int z, int meta, IWorldEditor destoryer) { }
	
	// Getters
	public ItemStack[] getDroppedItems(WorldLocal w, int x, int y, int z, int meta) {
		if (ITEM_BLOCK!=null) return new ItemStack[] { new ItemStack(this) };
		else return new ItemStack[0];
	}
	
	public double getHardness(WorldLocal w, int x, int y, int z, int meta) {
		return 0;
	}
	
	// Testers
	public boolean hasTileEntity(int meta) { return false; }
	
	public boolean isOpaque() { return true; }
	
}