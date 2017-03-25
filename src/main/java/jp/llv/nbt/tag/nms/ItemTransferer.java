/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms;

import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.ItemStackSerializable;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author toyblocks
 */
public interface ItemTransferer {
    
    ItemStackSerializable transfer(ItemStack item) throws IncompatiblePlatformException;
    
    ItemStackSerializable load(TagCompound tag) throws IncompatiblePlatformException;
    
}
