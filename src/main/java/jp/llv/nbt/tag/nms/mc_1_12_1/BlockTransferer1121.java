/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.llv.nbt.tag.nms.mc_1_12_1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author toyblocks
 */
public class BlockTransferer1121 implements BlockTransferer {

    private final String nms;
    private final String obc;
    private final TagTransferer tagTransferer;
    private final Class<?> classBlockPosition;
    private final Constructor<?> constroctorBlockPosition;
    private final Class<?> classWorld;
    private final Method methodWorldGetType;
    private final Method methodWorldSetTypeAndData;
    private final Method methodWorldGetTileEntity;
    private final Class<?> classCraftWorld;
    private final Field fieldCraftWorldWorld;
    private final Class<?> classNBTTagCompound;
    private final Class<?> classIBlockData;
    private final Class<?> classGameProfileSerializer;
    private final Method methodGameProfileSerializerSerialize;
    private final Method methodGameProfileSerializerDeserialize;
    private final Class<?> classTileEntity;
    private final Method methodTileEntitySave;
    private final Method methodTileEntityLoad;

    public BlockTransferer1121(String infix, TagTransferer tagTransferer) {
        nms = NMSConstants.NMS + infix;
        obc = NMSConstants.OBC + infix;
        this.tagTransferer = tagTransferer;
        try {
            classIBlockData = Class.forName(nms + "IBlockData");
            classBlockPosition = Class.forName(nms + "BlockPosition");
            constroctorBlockPosition = classBlockPosition.getConstructor(int.class, int.class, int.class);
            classWorld = Class.forName(nms + "World");
            methodWorldGetType = classWorld.getMethod("getType", classBlockPosition);
            methodWorldSetTypeAndData = classWorld.getMethod("setTypeAndData", classBlockPosition, classIBlockData, int.class);
            methodWorldGetTileEntity = classWorld.getMethod("getTileEntity", classBlockPosition);
            classCraftWorld = Class.forName(obc + "CraftWorld");
            fieldCraftWorldWorld = classCraftWorld.getDeclaredField("world");
            fieldCraftWorldWorld.setAccessible(true);
            classNBTTagCompound = Class.forName(nms + "NBTTagCompound");
            classGameProfileSerializer = Class.forName(nms + "GameProfileSerializer");
            methodGameProfileSerializerSerialize = classGameProfileSerializer.getMethod("a", classNBTTagCompound, classIBlockData);
            methodGameProfileSerializerDeserialize = classGameProfileSerializer.getMethod("d", classNBTTagCompound);
            classTileEntity = Class.forName(nms + "TileEntity");
            methodTileEntitySave = classTileEntity.getMethod("save", classNBTTagCompound);
            methodTileEntityLoad = classTileEntity.getMethod("load", classNBTTagCompound);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public BlockSerializable transfer(Block block) throws IncompatiblePlatformException {
        try {
            Location loc = block.getLocation();
            Object nmsWorld = fieldCraftWorldWorld.get(loc.getWorld());
            Object nmsPos = constroctorBlockPosition.newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            TagCompound data = (TagCompound) tagTransferer.transfer(
                    methodGameProfileSerializerSerialize.invoke(null,
                            tagTransferer.createTagCompound(), // arg0 : empty compound
                            methodWorldGetType.invoke(nmsWorld, nmsPos) // arg1 : block data
                    )
            );

            Object nmsTileEntity = methodWorldGetTileEntity.invoke(nmsWorld, nmsPos);
            if (nmsTileEntity == null) { // block is not a tile entity
                return new BlockSerializable1111(data);
            } else { // block is a tile entity
                Object nmsTile = tagTransferer.createTagCompound();
                methodTileEntitySave.invoke(nmsTileEntity, nmsTile);
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
                Object nmsWorld = fieldCraftWorldWorld.get(world);
                Object nmsPos = constroctorBlockPosition.newInstance(x, y, z);
                Object nmsData = methodGameProfileSerializerDeserialize.invoke(null, tagTransferer.transfer(data));
                boolean success = (boolean) methodWorldSetTypeAndData.invoke(nmsWorld, nmsPos, nmsData, 2); // I don't know what 2 means but it works well
                if (tag != null) { // if this is tile entity
                    Object nmsTile = methodWorldGetTileEntity.invoke(nmsWorld, nmsPos);
                    if (nmsTile != null) {
                        TagCompound llvTag = new TagCompound.Builder(tag)
                                .append("x", x).append("y", y).append("z", z).build();
                        methodTileEntityLoad.invoke(nmsTile, tagTransferer.transfer(llvTag));
                    }
                }
                return new Location(world, x, y, z).getBlock();
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

    }
    
}
