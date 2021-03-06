package com.redcontagion.world;

import com.redcontagion.util.Map3D;
import com.redcontagion.world.voxel.Block;
import com.redcontagion.world.voxel.TileEntity;

public class WorldLocal implements IWorld {
	
	private class ChunkLocal implements IChunk {
		
		public int cx, cy, cz;
		public ChunkLocal(int x, int y, int z) {
			cx = x;
			cy = y;
			cz = z;
		}
		
		private int[] data = new int[4096];

		@Override
		public int[] get() {
			return data;
		}
		
	}
	
	
	private Map3D<ChunkLocal> chunks = new Map3D<>();
	private Map3D<TileEntity> tileentities = new Map3D<>();
	
	@Override
	public int get(int x, int y, int z) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		return chunk.data[index];
	}

	@Override
	public void set(int x, int y, int z, int data) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		chunk.data[index] = data;
	}

	@Override
	public int getBlockID(int x, int y, int z) {
		return get(x, y, z) & 0xFFFF;
	}

	@Override
	public void setBlockID(int x, int y, int z, int id) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		if (Block.isValidID(id)) chunk.data[index] = (chunk.data[index] & 0xFFFF0000) | id;
	}

	private Block lastBlock = null;
	
	@Override
	public Block getBlock(int x, int y, int z) {
		int id = getBlockID(x, y, z);
		if (lastBlock != null && lastBlock.blockID == id) return lastBlock;
		lastBlock = Block.getBlock(id);
		return lastBlock;
	}

	@Override
	public void setBlock(int x, int y, int z, Block b) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		chunk.data[index] = (chunk.data[index] & 0xFFFF0000) | (b == null ? 0 : b.blockID);
	}

	@Override
	public int getDamage(int x, int y, int z) {
		return get(x, y, z) >> 24;
	}

	@Override
	public void setDamage(int x, int y, int z, int dmg) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		chunk.data[index] = (chunk.data[index] & 0xFFFFFF) | dmg << 24;
	}

	@Override
	public void modDamage(int x, int y, int z, int by) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		chunk.data[index] = chunk.data[index] + by << 24;
	}

	@Override
	public int getMeta(int x, int y, int z) {
		return (get(x, y, z) >> 16) & 0xFF;
	}

	@Override
	public void setMeta(int x, int y, int z, int meta) {
		ChunkLocal chunk = getChunk0(x >> 4, y >> 4, z >> 4);
		int index = (x & 0xF) | ((y << 4) & 0xF0) | ((z << 8) & 0xF00);
		chunk.data[index] = (chunk.data[index] & 0xFF00FFFF) | ((meta & 0xFF) << 16);
	}
	
	private ChunkLocal lastChunk = null;
	
	private ChunkLocal getChunk0(int cx, int cy, int cz) {
		if (lastChunk != null && lastChunk.cx == cx && lastChunk.cy == cy && lastChunk.cz == cz) return lastChunk;
		lastChunk = chunks.get(cx, cy, cz);
		// TODO: Implement world generation if chunk is null
		return lastChunk;
	}

	@Override
	public IChunk getChunk(int cx, int cy, int cz) {
		return getChunk0(cx, cy, cz);
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		return tileentities.get(x, y, z);
	}

	@Override
	public boolean isRemote() {
		return false;
	}
	
}
