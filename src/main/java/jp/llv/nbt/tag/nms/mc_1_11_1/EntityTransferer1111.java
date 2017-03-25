/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.util.Objects;
import java.util.UUID;
import jp.llv.nbt.EntitySerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagList;
import jp.llv.nbt.tag.nms.EntityTransferer;
import jp.llv.nbt.tag.nms.NMSConstants;
import jp.llv.nbt.tag.nms.TagTransferer;
import jp.llv.reflection.Refl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 * @author toyblocks
 */
public class EntityTransferer1111 implements EntityTransferer {

    private final String infix;
    private final TagTransferer tagTransferer;

    public EntityTransferer1111(String infix, TagTransferer tag) {
        this.infix = infix;
        this.tagTransferer = tag;
    }

    @Override
    public EntitySerializable transfer(Entity entity) throws IncompatiblePlatformException {
        try {
            Object nmsTag = tagTransferer.createTagCompound();
            Refl.wrap(entity).get("entity").invoke("d", nmsTag);
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
                Object nmsWorld = Refl.wrap(world).get("world").unwrap();
                Object nmsEntity = Refl.getRClass(NMSConstants.NMS + infix + "ChunkRegionLoader")
                        .invoke("spawnEntity", nmsTag, nmsWorld, x, y, z, flag, reason).unwrap();
                return Refl.getRClass(NMSConstants.OBC + infix + "entity.CraftEntity")
                        .invoke("getEntity", Bukkit.getServer(), nmsEntity).unwrapAs(Entity.class);
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
