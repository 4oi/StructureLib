/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import java.util.Optional;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author toyblocks
 */
public interface ItemStackSerializable extends Serializable {
    
    String getID();
    
    short getDamage();
    
    byte getCount();
    
    Optional<TagCompound> getTag();
    
    TagCompound toTag();
    
    ItemStack deserialize();
    
}
