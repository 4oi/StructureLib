/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.InventorySerializable;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagList;
import jp.llv.nbt.tag.nms.InventoryTransferer;
import jp.llv.nbt.tag.nms.ItemTransferer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author toyblocks
 */
public class InventoryTransferer1111 implements InventoryTransferer {

    private static final String KEY = "contents";
    private final ItemTransferer items;

    public InventoryTransferer1111(ItemTransferer items) {
        this.items = items;
    }

    @Override
    public InventorySerializable transfer(Inventory inventory) throws IncompatiblePlatformException {
        ItemStack[] contents = inventory.getContents();
        TagCompound[] tags = new TagCompound[contents.length];
        for (int i = 0; i < contents.length; i++) {
            tags[i] = items.transfer(contents[i]).toTag();
        }
        TagList<TagCompound> list = new TagList<>(TagBase.Type.COMPOUND, Arrays.asList(tags));
        return new InventorySerializable1111(new TagCompound.Builder(KEY, list).build());
    }

    @Override
    public InventorySerializable load(TagCompound tag) throws IncompatiblePlatformException {
        return new InventorySerializable1111(tag);
    }

    private class InventorySerializable1111 implements InventorySerializable {

        private final TagCompound tag;

        public InventorySerializable1111(TagCompound tag) {
            this.tag = Objects.requireNonNull(tag);
        }

        @Override
        public TagCompound toTag() {
            return tag;
        }

        @Override
        public void deserialize(Inventory dest) {
            List<TagCompound> list = (List<TagCompound>) tag.contents().getList(KEY);
            TagCompound[] tags = list.toArray(new TagCompound[list.size()]);
            ItemStack[] contents = new ItemStack[list.size()];
            for (int i = 0; i < list.size(); i++) {
                contents[i] = items.load(tags[i]).deserialize();
            }
            dest.setContents(contents);
        }

    }

}
