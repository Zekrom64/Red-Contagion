package com.redcontagion.world.voxel;

import java.util.Arrays;

import com.redcontagion.item.ItemStack;
import com.redcontagion.world.IWorld;
import com.redcontagion.world.IWorldEditor;

public abstract class Block {
	
	public static void reset() {
		Arrays.fill(blocks, null);
		nextBlock = 0;
	}

	/** Maximum number of usable blocks */
	public static final int MAX_BLOCKS = 65535;
	private static Block[] blocks = new Block[MAX_BLOCKS];
	private static int nextBlock = 0;
	
	/** Gets a block by an id. If the id is that of nothing (id 0) or is invalid, <b>null</b> is returned.
	 * 
	 * @param id Block ID
	 * @return Block, or <b>null</b>
	 */
	public static Block getBlock(int id) {
		if (id <= 0 || id > MAX_BLOCKS) return null;
		return blocks[id - 1];
	}
	
	/** Gets a block by an unlocalized name. If a block does not have the given unlocalized name, <b>null</b> is returned.
	 * 
	 * @param unlocName
	 * @return
	 */
	public static Block getBlock(String unlocName) {
		for(Block b : blocks) {
			// If the next block to test against is null, we are at the end of the list
			if (b==null) return null;
			if (b.unlocalizedName.equals(unlocName)) return b;
		}
		return null;
	}
	
	/** Gets an array of all the available blocks.
	 * 
	 * @return Array of available blocks
	 */
	public static Block[] getAllBlocks() {
		return Arrays.copyOf(blocks, nextBlock);
	}
	
	/** Tests if the given block ID is valid.
	 * 
	 * @param id Block ID
	 * @return If the ID is a valid block ID.
	 */
	public static boolean isValidID(int id) {
		return id <= nextBlock && id > 0;
	}
	
	/** The ID of the block */
	public final int blockID;
	/** The unlocalized name of the block */
	public final String unlocalizedName;
	/** The {@link ItemBlock} associated with the block */
	public final ItemBlock itemBlock;
	
	/** Creates a new block template. When the class is instantiated, it is automatically registered with a block
	 * ID. This block ID may be different than one for the same block class on a remote connection.
	 * 
	 * @param unlocName Unlocalized name of the block
	 */
	public Block(String unlocName) {
		if (unlocName==null) throw new IllegalArgumentException("Block cannot have null unlocalized name");
		if (getBlock(unlocName)!=null) throw new IllegalArgumentException("Block with unlocalized name \"" + unlocName + "\" is already registered");
		unlocalizedName = unlocName;
		if (nextBlock==blocks.length) throw new IllegalStateException("Out of block IDs");
		blocks[nextBlock++] = this;
		blockID = nextBlock;
		itemBlock = getItemBlock();
	}
	
	/** Called to construct the {@link ItemBlock} for this block. This can be modified to return a custom ItemBlock.
	 * 
	 * @return ItemBlock for this block.
	 */
	protected ItemBlock getItemBlock() {
		return new ItemBlock(this);
	}
	
	// Events
	/** Called when a block is placed in a world by a world generator.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 */
	public void onGenerated(IWorld w, int x, int y, int z, int meta) { }
	
	/** Called when a block is placed in a world, but the placer is unknown.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 */
	public void onPlaced(IWorld w, int x, int y, int z, int meta) { }
	
	/** Called when a block is placed in a world by an {@link IWorldEditor}.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 * @param placer Placer object
	 */
	public void onPlacedByEntity(IWorld w, int x, int y, int z, int meta, IWorldEditor placer) { }
	
	/** Called when a block is destroyed in the world, but the destroyer is unknown.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 */
	public void onDestroyed(IWorld w, int x, int y, int z, int meta) { }
	
	/** Called when a block is destroyed in the world by an {@link IWorldEditor}.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 * @param destoryer Destroyer object
	 */
	public void onDestroyedByEntity(IWorld w, int x, int y, int z, int meta, IWorldEditor destoryer) { }
	
	// Getters
	/** Gets the items dropped when a block is destroyed.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata of block
	 * @return Items to drop
	 */
	public ItemStack[] getDroppedItems(IWorld w, int x, int y, int z, int meta) {
		if (itemBlock!=null) return new ItemStack[] { new ItemStack(this) };
		else return new ItemStack[0];
	}
	
	/** Gets the hardness value of a block in the world. The inverse of this number is added to
	 * the block damage, so a value of {@link Double#POSITIVE_INFINITY} will have infinite hardness.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 * @return Hardness
	 */
	public double getHardness(IWorld w, int x, int y, int z, int meta) {
		return 1 / 16d;
	}
	
	// Testers
	/** Tests if the block has a tile entity for the given metadata.
	 * 
	 * @param meta Metadata value
	 * @return If the block has a tile entity for the metadata
	 */
	public boolean hasTileEntity(int meta) { return false; }
	
	/** Tests if a block face is completely opaque.
	 * 
	 * @param w World
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Metadata value of block
	 * @return If the block face is opaque
	 */
	public boolean isOpaque(IWorld w, int x, int y, int z, int meta, Direction face) { return true; }
	
}
