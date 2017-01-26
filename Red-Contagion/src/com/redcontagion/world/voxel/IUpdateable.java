package com.redcontagion.world.voxel;

/** Interface used by tile entities that need to be updated
 * 
 * @author Zekrom_64
 *
 */
public interface IUpdateable {

	/** Called 100 times every second to update the tile entity.
	 * 
	 */
	public void update();
	
}
