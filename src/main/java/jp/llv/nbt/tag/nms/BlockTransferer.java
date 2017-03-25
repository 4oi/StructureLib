/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms;

import jp.llv.nbt.BlockSerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.block.Block;

/**
 *
 * @author toyblocks
 */
public interface BlockTransferer {
    
    BlockSerializable transfer(Block block) throws IncompatiblePlatformException;
    
    BlockSerializable load(TagCompound tag) throws IncompatiblePlatformException;
    
    BlockSerializable load(TagCompound data, TagCompound tag) throws IncompatiblePlatformException;
    
}
