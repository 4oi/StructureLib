/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.util.Optional;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.ItemStackSerializable;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.nms.ItemTransferer;
import jp.llv.nbt.tag.nms.NMSConstants;
import jp.llv.nbt.tag.nms.TagTransferer;
import jp.llv.reflection.Refl;
import jp.llv.reflection.Refl.RClass;
import jp.llv.reflection.Refl.RObject;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author toyblocks
 */
public class ItemTransferer1111 implements ItemTransferer {

    private final String infix;
    private final TagTransferer tagTransferer;

    public ItemTransferer1111(String infix, TagTransferer tag) {
        this.infix = infix;
        this.tagTransferer = tag;
    }

    @Override
    public ItemStackSerializable transfer(ItemStack bukkitItemStack) throws IncompatiblePlatformException {
        try {
            RClass<?> craftItemStack = Refl.getRClass(NMSConstants.OBC + infix + "inventory.CraftItemStack");
            RObject<?> item = craftItemStack.invoke("asNMSCopy", bukkitItemStack);
            Object nmsTag = tagTransferer.createTagCompound();
            item.invoke("save", nmsTag);
            return load((TagCompound) tagTransferer.transfer(nmsTag));
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public ItemStackSerializable load(TagCompound tag) {
        String id = tag.contents().getString("id");
        byte count = tag.contents().getByte("Count");
        short damage = tag.contents().getShort("Damage");
        if (tag.contents().containsKey("tag")) {
            TagCompound itemTag = tag.contents().getCompound("tag");
            return new ItemStackSerializable1112(id, damage, count, itemTag);
        } else {
            return new ItemStackSerializable1112(id, damage, count);
        }
    }

    private class ItemStackSerializable1112 implements ItemStackSerializable {

        private final String id;
        private final short damage;
        private final byte count;
        private final TagCompound tag;

        public ItemStackSerializable1112(String id, short damage, byte count, TagCompound tag) {
            this.id = id;
            this.damage = damage;
            this.count = count;
            this.tag = tag;
        }

        public ItemStackSerializable1112(String id, short damage, byte count) {
            this(id, damage, count, null);
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public short getDamage() {
            return damage;
        }

        @Override
        public byte getCount() {
            return count;
        }

        @Override
        public Optional<TagCompound> getTag() {
            return Optional.ofNullable(tag);
        }

        @Override
        public TagCompound toTag() {
            TagCompound.Builder builder = new TagCompound.Builder()
                    .append("id", id)
                    .append("Count", count)
                    .append("Damage", damage);
            if (tag != null) {
                builder.append("tag", tag);
            }
            return builder.build();
        }

        @Override
        public ItemStack deserialize() {
            try {
                Object nmsTag = ItemTransferer1111.this.tagTransferer.transfer(toTag());
                RObject<?> nmsItem = Refl.getRClass(NMSConstants.NMS + infix + "ItemStack")
                        .newInstance(nmsTag);
                return Refl.getRClass(NMSConstants.OBC + infix + "inventory.CraftItemStack")
                        .invoke("asBukkitCopy", nmsItem).unwrapAs(ItemStack.class);
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

    }

}
