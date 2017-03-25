/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms;

import jp.llv.nbt.EntitySerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.entity.Entity;

/**
 *
 * @author toyblocks
 */
public interface EntityTransferer {
    
    EntitySerializable transfer(Entity entity) throws IncompatiblePlatformException;
    
    EntitySerializable load(TagCompound tag) throws IncompatiblePlatformException;
    
}
