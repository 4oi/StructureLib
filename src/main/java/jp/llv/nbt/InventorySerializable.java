/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.llv.nbt;

import java.io.Serializable;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author toyblocks
 */
public interface InventorySerializable extends Serializable {
    
    TagCompound toTag();
    
    void deserialize(Inventory dest);
    
}
