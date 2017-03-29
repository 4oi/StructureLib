/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import jp.llv.nbt.EntitySerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagList;
import jp.llv.nbt.tag.nms.EntityTransferer;
import jp.llv.nbt.tag.nms.NMSConstants;
import jp.llv.nbt.tag.nms.TagTransferer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author toyblocks
 */
public class EntityTransferer1111 implements EntityTransferer {

    private final String nms;
    private final String obc;
    private final TagTransferer tagTransferer;
    private final Class<?> classNBTTagCompound;
    private final Class<?> classEntity;
    private final Method methodEntitySerialize;
    private final Class<?> classCraftServer;
    private final Class<?> classCraftWorld;
    private final Field fieldCraftWorldWorld;
    private final Class<?> classCraftEntity;
    private final Field fieldCraftEntityEntity;
    private final Method methodCraftEntityGetEntity;
    private final Class<?> classWorld;
    private final Class<?> classChunkRegionLoader;
    private final Method methodChunkRegionLoaderSpawnEntity;

    public EntityTransferer1111(String infix, TagTransferer tag) {
        nms = NMSConstants.NMS + infix;
        obc = NMSConstants.OBC + infix;
        this.tagTransferer = tag;
        try {
            classNBTTagCompound = Class.forName(nms + "NBTTagCompound");
            classEntity = Class.forName(nms + "Entity");
            methodEntitySerialize = classEntity.getMethod("d", classNBTTagCompound);
            classCraftServer = Class.forName(obc + "CraftServer");
            classCraftWorld = Class.forName(obc + "CraftWorld");
            fieldCraftWorldWorld = classCraftWorld.getDeclaredField("world");
            fieldCraftWorldWorld.setAccessible(true);
            classCraftEntity = Class.forName(obc + "entity.CraftEntity");
            fieldCraftEntityEntity = classCraftEntity.getDeclaredField("entity");
            fieldCraftEntityEntity.setAccessible(true);
            methodCraftEntityGetEntity = classCraftEntity.getMethod("getEntity",
                    classCraftServer, classEntity
            );
            classWorld = Class.forName(nms + "World");
            classChunkRegionLoader = Class.forName(nms + "ChunkRegionLoader");
            methodChunkRegionLoaderSpawnEntity = classChunkRegionLoader.getMethod("spawnEntity",
                    classNBTTagCompound,
                    classWorld,
                    double.class, double.class, double.class,
                    boolean.class,
                    CreatureSpawnEvent.SpawnReason.class
            );
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public EntitySerializable transfer(Entity entity) throws IncompatiblePlatformException {
        try {
            Object nmsTag = tagTransferer.createTagCompound();
            Object nmsEntity = fieldCraftEntityEntity.get(entity);
            methodEntitySerialize.invoke(nmsEntity, nmsTag);
            return new EntitySerializable1111((TagCompound) tagTransferer.transfer(nmsTag));
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public EntitySerializable load(TagCompound tag) throws IncompatiblePlatformException {
        Objects.requireNonNull(tag);
        return new EntitySerializable1111(tag);
    }

    private class EntitySerializable1111 implements EntitySerializable {

        private final TagCompound tag;

        public EntitySerializable1111(TagCompound tag) {
            this.tag = tag;
        }

        @Override
        public TagCompound toTag() {
            return tag;
        }

        @Override
        public Entity deserialize(World world) {
            TagList.Values pos = ((TagList) tag.contents().getTag("Pos")).contents();
            return deserialize(world, pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        }

        @Override
        public Entity deserialize(World world, double x, double y, double z, boolean flag, CreatureSpawnEvent.SpawnReason reason) throws IncompatiblePlatformException {
            try {
                Object nmsTag = tagTransferer.transfer(tag);
                Object nmsWorld = fieldCraftWorldWorld.get(world);
                Object nmsEntity = methodChunkRegionLoaderSpawnEntity.invoke(null, nmsTag, nmsWorld, x, y, z, flag, reason);
                if (nmsEntity == null) {
                    return null;
                }
                return (Entity) methodCraftEntityGetEntity.invoke(null, Bukkit.getServer(), nmsEntity);
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

        @Override
        public EntitySerializable regenerateUUID() {
            UUID uuid = UUID.randomUUID();
            return new EntitySerializable1111(new TagCompound.Builder(tag)
                    .append("UUIDMost", uuid.getMostSignificantBits())
                    .append("UUIDLeast", uuid.getLeastSignificantBits()).build()
            );
        }

    }

}
