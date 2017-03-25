/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.util.function.BiFunction;
import java.util.function.Function;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.nms.BlockTransferer;
import jp.llv.nbt.tag.nms.EntityTransferer;
import jp.llv.nbt.tag.nms.ItemTransferer;
import jp.llv.nbt.tag.nms.TagTransferer;
import jp.llv.nbt.tag.nms.mc_1_11_1.BlockTransferer1111;
import jp.llv.nbt.tag.nms.mc_1_11_1.EntityTransferer1111;
import jp.llv.nbt.tag.nms.mc_1_11_1.ItemTransferer1111;
import jp.llv.nbt.tag.nms.mc_1_11_1.TagTransferer1111;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author toyblocks
 */
public interface StructureLibAPI {

    ItemStackSerializable serialize(ItemStack item);

    BlockSerializable serialize(Block block);

    EntitySerializable serialize(Entity entity);
    
    ItemStackSerializable loadItem(TagCompound tag);
    
    BlockSerializable loadBlock(TagCompound tag);
    
    BlockSerializable loadBlock(TagCompound data, TagCompound nbt);
    
    EntitySerializable loadEntity(TagCompound tag);

    enum Version implements StructureLibAPI {

        MC_1_11_1(
                "v1_11_R1.",
                TagTransferer1111::new,
                ItemTransferer1111::new,
                BlockTransferer1111::new,
                EntityTransferer1111::new
        ),;

        private final String infix;
        private final Function<String, ? extends TagTransferer> tagTransfererConstructor;
        private final BiFunction<String, TagTransferer, ? extends ItemTransferer> itemTransfererConstructor;
        private final BiFunction<String, TagTransferer, ? extends BlockTransferer> blockTransfererConstructor;
        private final BiFunction<String, TagTransferer, ? extends EntityTransferer> entityTransfererConstructor;

        private TagTransferer tagTransferer;
        private ItemTransferer itemTransferer;
        private BlockTransferer blockTransferer;
        private EntityTransferer entityTransferer;

        private Version(
                String infix,
                Function<String, ? extends TagTransferer> tag,
                BiFunction<String, TagTransferer, ? extends ItemTransferer> item,
                BiFunction<String, TagTransferer, ? extends BlockTransferer> block,
                BiFunction<String, TagTransferer, ? extends EntityTransferer> entity
        ) {
            this.infix = infix;
            this.tagTransfererConstructor = tag;
            this.itemTransfererConstructor = item;
            this.blockTransfererConstructor = block;
            this.entityTransfererConstructor = entity;
        }

        private void initialize() {
            if (tagTransferer == null) {
                tagTransferer = tagTransfererConstructor.apply(infix);
                itemTransferer = itemTransfererConstructor.apply(infix, tagTransferer);
                blockTransferer = blockTransfererConstructor.apply(infix, tagTransferer);
                entityTransferer = entityTransfererConstructor.apply(infix, tagTransferer);
            }
        }

        @Override
        public ItemStackSerializable serialize(ItemStack item) {
            return getItemTransferer().transfer(item);
        }

        @Override
        public BlockSerializable serialize(Block block) {
            return getBlockTransferer().transfer(block);
        }

        @Override
        public EntitySerializable serialize(Entity entity) {
            return getEntityTransferer().transfer(entity);
        }

        @Override
        public ItemStackSerializable loadItem(TagCompound tag) {
            return getItemTransferer().load(tag);
        }

        @Override
        public BlockSerializable loadBlock(TagCompound tag) {
            return getBlockTransferer().load(tag);
        }

        @Override
        public BlockSerializable loadBlock(TagCompound data, TagCompound nbt) {
            return getBlockTransferer().load(data, nbt);
        }

        @Override
        public EntitySerializable loadEntity(TagCompound tag) {
            return getEntityTransferer().load(tag);
        }

        public String getInfix() {
            return infix;
        }

        public TagTransferer getTagTransferer() {
            initialize();
            return tagTransferer;
        }

        public ItemTransferer getItemTransferer() {
            initialize();
            return itemTransferer;
        }

        public BlockTransferer getBlockTransferer() {
            initialize();
            return blockTransferer;
        }

        public EntityTransferer getEntityTransferer() {
            initialize();
            return entityTransferer;
        }
        
        public static Version getDetectedVersion(Object instance) {
            String infix = instance.getClass().getName().split("\\.")[3] + '.';
            for (Version version : values()) {
                if (version.getInfix().equals(infix)) {
                    return version;
                }
            }
            throw new IllegalArgumentException("Unsupported version");
        }
    }

}
