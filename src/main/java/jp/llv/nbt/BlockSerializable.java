/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import java.util.Optional;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author toyblocks
 */
public interface BlockSerializable extends Serializable {
    
    TagCompound getData();
    
    Optional<TagCompound> getTag();
    
    TagCompound toTag();
    
    Block deserialize(World world) throws IncompatiblePlatformException;
    
    Block deserialize(World world, int x, int y, int z) throws IncompatiblePlatformException;
    
    default Block deserialize(LocationSerializable loc) throws IncompatiblePlatformException {
        return deserialize(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
}
