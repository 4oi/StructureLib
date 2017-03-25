/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.llv.nbt.BlockSerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.nms.BlockTransferer;
import jp.llv.nbt.tag.nms.NMSConstants;
import jp.llv.nbt.tag.nms.TagTransferer;
import jp.llv.reflection.Refl;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author toyblocks
 */
public class BlockTransferer1111 implements BlockTransferer {

    private final String infix;
    private final TagTransferer tagTransferer;

    public BlockTransferer1111(String infix, TagTransferer tagTransferer) {
        this.infix = infix;
        this.tagTransferer = tagTransferer;
    }

    @Override
    public BlockSerializable transfer(Block block) throws IncompatiblePlatformException {
        try {
            Location loc = block.getLocation();
            Refl.RObject<?> nmsWorld = Refl.wrap(block.getWorld()).get("world");
            Refl.RObject<?> nmsPos = Refl.getRClass(NMSConstants.NMS + infix + "BlockPosition")
                    .newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            TagCompound data = (TagCompound) tagTransferer.transfer(
                    Refl.getRClass(NMSConstants.NMS + infix + "GameProfileSerializer")
                    .invoke("a",
                            tagTransferer.createTagCompound(), // arg0 : empty compound
                            nmsWorld.invoke("getType", nmsPos) // arg1 : block data
                    ).unwrap()
            );

            Refl.RObject<?> nmsTileEntity = nmsWorld.invoke("getTileEntity", nmsPos);
            if (nmsTileEntity == null) { // block is not a tile entity
                return new BlockSerializable1111(data);
            } else { // block is a tile entity
                Object nmsTile = tagTransferer.createTagCompound();
                nmsTileEntity.invoke("save", nmsTile);
                TagCompound tile = (TagCompound) tagTransferer.transfer(nmsTile);
                return new BlockSerializable1111(data, tile);
            }
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public BlockSerializable load(TagCompound tag) throws IncompatiblePlatformException {
        Map<String, TagBase> nbt = tag.get();
        return load((TagCompound) nbt.get("data"), (TagCompound) nbt.get("tag"));
    }

    @Override
    public BlockSerializable load(TagCompound data, TagCompound tag) throws IncompatiblePlatformException {
        Objects.requireNonNull(data);
        return new BlockSerializable1111(data, tag);
    }

    private class BlockSerializable1111 implements BlockSerializable {

        private final TagCompound data;
        private final TagCompound tag;

        public BlockSerializable1111(TagCompound data) {
            this(data, null);
        }

        public BlockSerializable1111(TagCompound data, TagCompound tag) {
            Objects.requireNonNull(data);
            this.data = data;
            this.tag = tag;
        }

        @Override
        public TagCompound getData() {
            return data;
        }

        @Override
        public Optional<TagCompound> getTag() {
            return Optional.ofNullable(tag);
        }

        @Override
        public TagCompound toTag() {
            return new TagCompound.Builder("data", data).append("tag", tag).build();
        }

        @Override
        public Block deserialize(World world) throws IncompatiblePlatformException {
            if (tag == null) {
                throw new IncompatiblePlatformException("No location present neither from serialized data nor from arguments");
            }
            int x = tag.contents().getInt("x"), y = tag.contents().getInt("y"), z = tag.contents().getInt("z");
            return this.deserialize(world, x, y, z);
        }

        @Override
        public Block deserialize(World world, int x, int y, int z) throws IncompatiblePlatformException {
            try {
                Refl.RObject<?> nmsWorld = Refl.wrap(world).get("world");
                Refl.RObject<?> nmsPos = Refl.getRClass(NMSConstants.NMS + infix + "BlockPosition")
                        .newInstance(x, y, z);
                Refl.RObject<?> nmsData = Refl.getRClass(NMSConstants.NMS + infix + "GameProfileSerializer")
                        .invoke("d", tagTransferer.transfer(data));
                boolean success = nmsWorld.invoke("setTypeAndData", nmsPos, nmsData, 2).unwrapAsBoolean(); // I don't know what 2 means but it works well
                if (success && tag != null) { // if this is tile entity
                    Refl.RObject<?> nmsTile = nmsWorld.invoke("getTileEntity", nmsPos);
                    if (nmsTile != null) {
                        TagCompound llvTag = new TagCompound.Builder(tag)
                                .append("x", x).append("y", y).append("z", z).build();
                        nmsTile.invoke("a", tagTransferer.transfer(llvTag));
                    }
                }
                return new Location(world, x, y, z).getBlock();
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

    }

}
