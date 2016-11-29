package com.redcontagion.world;

import com.redcontagion.world.voxel.Block;
import com.redcontagion.world.voxel.TileEntity;

/** An IWorld provides access to 
 * 
 * @author Zekrom_64
 *
 */
public interface IWorld {
	
	/** A chunk is a cube of blocks 16 blocks long on each side. Chunks are at coordinates aligned at intervals
	 * of 16 blocks.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public interface IChunk {
		
		public int[] get();
		
	}
	
	/** Gets the value of a block in the world, including all metadata.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Full block value
	 */
	public int get(int x, int y, int z);
	
	/** Sets the value of a block in the world. including all metadata.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param data Full block value
	 */
	public void set(int x, int y, int z, int data);
	
	/** Gets the ID of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block ID
	 */
	public int getBlockID(int x, int y, int z);
	
	/** Sets the ID of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param id Block ID
	 */
	public void setBlockID(int x, int y, int z, int id);
	
	/** Gets a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block
	 */
	public Block getBlock(int x, int y, int z);
	
	/** Sets a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param b Block
	 */
	public void setBlock(int x, int y, int z, Block b);
	
	/** Gets the damage of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Damage of block
	 */
	public int getDamage(int x, int y, int z);
	
	/** Sets the damage of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param dmg Damage of block
	 */
	public void setDamage(int x, int y, int z, int dmg);
	
	/** Modifies (adds to) the damage of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param by Damage mod by
	 */
	public void modDamage(int x, int y, int z, int by);
	
	/** Gets the metadata value of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block metadata
	 */
	public int getMeta(int x, int y, int z);
	
	/** Sets the metadata value of a block in the world.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param meta Block metadata
	 */
	public void setMeta(int x, int y, int z, int meta);
	
	/** Gets a chunk at chunk coordinates
	 * 
	 * @param cx
	 * @param cy
	 * @param cz
	 * @return
	 */
	public IChunk getChunk(int cx, int cy, int cz);
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public TileEntity getTileEntity(int x, int y, int z);

}
