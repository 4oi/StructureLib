/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.llv.nbt.tag.nms;

import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.InventorySerializable;
import jp.llv.nbt.tag.TagCompound;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author toyblocks
 */
public interface InventoryTransferer {
    
    InventorySerializable transfer(Inventory inventory) throws IncompatiblePlatformException;
    
    InventorySerializable load(TagCompound tag) throws IncompatiblePlatformException;
    
}
