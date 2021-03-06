package com.redcontagion.util;

public class Map3D<T> {
	
	public static interface HashFunc3D {
		
		public int hashTableSize();
		
		public int hash(int x, int y, int z);
		
	}
	
	public static final HashFunc3D SMALL_HASHING = new HashFunc3D() {

		@Override
		public int hashTableSize() {
			return 4096;
		}

		@Override
		public int hash(int x, int y, int z) {
			int hash = x & 0xF;
			hash |= (x << 4) & 0xF0;
			hash |= (y << 8) & 0xF00;
			return hash;
		}
		
	};
	
	public static final HashFunc3D MEDIUM_HASHING = new HashFunc3D() {

		@Override
		public int hashTableSize() {
			return 4096;
		}

		@Override
		public int hash(int x, int y, int z) {
			int hash = (x >> 4) & 0xF;
			hash |= y & 0xF0;
			hash |= (z << 4) & 0xF00;
			return hash;
		}
		
	};
	
	public final HashFunc3D hashFunc;
	
	public Map3D() {
		this(SMALL_HASHING);
	}
	
	public Map3D(HashFunc3D func) {
		hashFunc = func == null ? SMALL_HASHING : func;
	}
	
	@SuppressWarnings("unchecked")
	private Map3D<T>.Map3DEntry[] entries = (Map3D<T>.Map3DEntry[]) new Map3D<?>.Map3DEntry[4096];
	
	private class Map3DEntry {
		
		public int x, y, z;
		public T t;
		public Map3DEntry next;
		
		public Map3DEntry(int x, int y, int z, T t) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.t = t;
		}
		
		@Override
		public int hashCode() {
			return hashFunc.hash(x,y,z);
		}
		
	}
	
	/** Gets a value in the 3d map. If no value is found, <b>null</b> is returned.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Value stored, or <b>null</b>
	 */
	public T get(int x, int y, int z) {
		Map3DEntry e = entries[hashFunc.hash(x,y,z)];
		if (e==null) return null;
		do {
			if (e.x == x && e.y == y && e.z == z) return e.t;
			e = e.next;
		} while(e != null);
		return null;
	}
	
	/** Sets a value in the 3d map. Returns the last value stored or <b>null</b> if the value was added.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param t Value to set
	 * @return Previous value, or <b>null</b>
	 */
	public T set(int x, int y, int z, T t) {
		int hash = hashFunc.hash(x,y,z);
		Map3DEntry e = entries[hash];
		if (e!=null) {
			do {
				if (e.x == x && e.y == y && e.z == z) {
					T old = e.t;
					e.t = t;
					return old;
				} else if (e.next == null) {
					e.next = new Map3DEntry(x, y, z, t);
					return null;
				} else e = e.next;
			} while (true);
		} else {
			entries[hash] = new Map3DEntry(x, y, z, t);
			return null;
		}
	}
	
	/** Removes a value from the 3D map. Returns the value previously stored, or <b>null</b> if the value did not exist.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Previous value, or <b>null</b>
	 */
	public T remove(int x, int y, int z) {
		int hash = hashFunc.hash(x,y,z);
		Map3DEntry e = entries[hash];
		if (e == null) return null;
		if (e.x == x && e.y == y && e.z == z) {
			entries[hash] = e.next;
			return e.t;
		}
		Map3DEntry last;
		while(e != null) {
			last = e;
			e = e.next;
			if (e.x == x && e.y == y & e.z == z) {
				last.next = e.next;
				return e.t;
			}
		}
		return null;
	}
	
	public int size() {
		int size = 0;
		for(Map3DEntry e : entries) {
			while(e != null) {
				size++;
				e = e.next;
			}
		}
		return size;
	}
	
}
