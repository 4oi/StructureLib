/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author toyblocks
 */
public interface EntitySerializable extends Serializable {
    
    TagCompound toTag();
    
    Entity deserialize(World world);
    
    default Entity deserialize(LocationSerializable loc) {
        return deserialize(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }
    
    default Entity deserialize(LocationSerializable loc, boolean flag) {
        return deserialize(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), flag);
    }
    
    default Entity deserialize(LocationSerializable loc, boolean flag, CreatureSpawnEvent.SpawnReason reason) {
        return deserialize(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), flag, reason);
    }
    
    default Entity deserialize(World world, double x, double y, double z) {
        return deserialize(world, x, y, z, true);
    }
    
    default Entity deserialize(World world, double x, double y, double z, boolean flag) throws IncompatiblePlatformException {
        return deserialize(world, x, y, z, flag, CreatureSpawnEvent.SpawnReason.DEFAULT);
    }
    
    Entity deserialize(World world, double x, double y, double z, boolean flag, CreatureSpawnEvent.SpawnReason reason) throws IncompatiblePlatformException;
    
    EntitySerializable regenerateUUID();
    
}
