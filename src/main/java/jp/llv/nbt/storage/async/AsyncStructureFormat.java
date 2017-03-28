/*
 * Copyright 2017 SakuraServerDev All rights reserved.
 */
package jp.llv.nbt.storage.async;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jp.llv.nbt.BlockSerializable;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.EntitySerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.VectorSerializable;
import jp.llv.nbt.storage.LoadOption;
import jp.llv.nbt.storage.StructureFormat;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 *
 * @author SakuraServerDev
 */
public class AsyncStructureFormat implements AsyncStorageFormat {

    private final StructureLibAPI api;

    public AsyncStructureFormat(StructureLibAPI api) {
        this.api = api;
    }

    @Override
    public AsyncSerializeTask<TagCompound> save(CuboidSerializable cuboid, boolean entity) {
        return new StructureSaveTask(cuboid, entity);
    }

    @Override
    public AsyncSerializeTask<Void> load(TagCompound source, LocationSerializable origin, LoadOption... options) {
        return new StructureLoadTask(source, origin, options);
    }

    private class StructureSaveTask implements AsyncSerializeTask<TagCompound> {

        private final StructureFormat.Blocks blocks = new StructureFormat.Blocks();
        private final StructureFormat.Entities entities = new StructureFormat.Entities();
        private final StructureFormat.Palette palette = new StructureFormat.Palette();
        private final CuboidSerializable cuboid;
        private TagCompound result;
        private long estimatedStepsRemaining = 1; //init with 1: Tag generation task
        private final Iterator<LocationSerializable> blockIterator;
        private final Iterator<Entity> entityIterator;

        public StructureSaveTask(CuboidSerializable cuboid, boolean entity) {
            this.cuboid = cuboid;
            estimatedStepsRemaining += cuboid.size();
            this.blockIterator = cuboid.iterator();
            Collection<Entity> es = entity
                    ? cuboid.getWorld().getEntities()
                    : Collections.emptySet();
            estimatedStepsRemaining += es.size();
            entityIterator = es.iterator();
        }

        @Override
        public void step() {
            estimatedStepsRemaining--;
            if (blockIterator.hasNext()) {
                LocationSerializable loc = blockIterator.next();
                VectorSerializable pos = loc.subtract(cuboid.getOrigin());
                Block block = loc.toLocation().getBlock();
                if (block.getType() == Material.STRUCTURE_VOID) {
                    return;
                }
                BlockSerializable data = api.serialize(block);
                int index = palette.getIndexOf(data.getData());
                if (data.getTag().isPresent()) {
                    blocks.add(pos, index, data.getTag().get());
                } else {
                    blocks.add(pos, index);
                }
            } else if (entityIterator.hasNext()) {
                Entity ent = entityIterator.next();
                LocationSerializable loc = new LocationSerializable(ent.getLocation());
                if (!cuboid.isBlockIn(loc)) {
                    return;
                }
                VectorSerializable pos = loc.subtract(cuboid.getOrigin());
                EntitySerializable data = api.serialize(ent);
                entities.add(pos, data.toTag());
            } else {
                result = new TagCompound.Builder("size", cuboid.getSize().add(1d).toBlockTagList())
                        .append("entities", entities.toTag())
                        .append("blocks", blocks.toTag())
                        .append("author", "StructureLib by toyblocks")
                        .append("palette", palette.toTag())
                        .append("DataVersion", 922).build();
            }
        }

        @Override
        public boolean isFinished() {
            return result != null;
        }

        @Override
        public TagCompound getResult() throws IncompatiblePlatformException {
            return result;
        }

        @Override
        public long getEstimatedStepsRemaining() {
            return estimatedStepsRemaining;
        }

    }

    private class StructureLoadTask implements AsyncSerializeTask<Void> {

        private final Iterator<LocationSerializable> blockBreakIterator;
        private final Iterator<Entity> entityRemoveterator;
        private final Iterator<TagCompound> blockPlaceIterator;
        private final Iterator<TagCompound> entitySpawnIterator;
        private final StructureFormat.Palette palette;
        private final LocationSerializable origin;
        private final CuboidSerializable cuboid;
        private long estimatedStepsRemaining = 0;

        public StructureLoadTask(TagCompound source, LocationSerializable origin, LoadOption... options) {
            List<LoadOption> optionList = Arrays.asList(options);
            VectorSerializable size = new VectorSerializable((TagList<?>) source.contents().getTag("size"));
            palette = new StructureFormat.Palette((TagList<TagCompound>) source.contents().getTag("palette"));
            this.origin = origin;
            cuboid = new CuboidSerializable(origin, size.subtract(1d));
            if (optionList.contains(LoadOption.REMOVE_BLOCKS_FIRST)) {
                estimatedStepsRemaining += cuboid.size();
                blockBreakIterator = cuboid.iterator();
            } else {
                blockBreakIterator = Collections.emptyIterator();
            }
            if (optionList.contains(LoadOption.REMOVE_ENTITIES_FIRST)) {
                Collection<Entity> entities = cuboid.getWorld().getEntities();
                estimatedStepsRemaining += entities.size();
                entityRemoveterator = entities.iterator();
            } else {
                entityRemoveterator = Collections.emptyIterator();
            }
            if (optionList.contains(LoadOption.LOAD_BLOCKS)) {
                TagList<TagCompound> list = (TagList<TagCompound>) source.contents().getTag("blocks");
                estimatedStepsRemaining += list.size();
                blockPlaceIterator = list.iterator();
            } else {
                blockPlaceIterator = Collections.emptyIterator();
            }
            if (optionList.contains(LoadOption.LOAD_ENTITIES)) {
                TagList<TagCompound> list = (TagList<TagCompound>) source.contents().getTag("blocks");
                estimatedStepsRemaining += list.size();
                entitySpawnIterator = list.iterator();
            } else {
                entitySpawnIterator = Collections.emptyIterator();
            }
        }

        @Override
        public void step() {
            estimatedStepsRemaining--;
            if (blockBreakIterator.hasNext()) {
                blockBreakIterator.next().toLocation().getBlock().setType(Material.AIR);
            } else if (entityRemoveterator.hasNext()) {
                Entity ent = entityRemoveterator.next();
                if (cuboid.isBlockIn(new LocationSerializable(ent.getLocation()))) {
                    ent.remove();
                }
            } else if (blockPlaceIterator.hasNext()) {
                TagCompound tag = blockPlaceIterator.next();
                VectorSerializable pos = new VectorSerializable((TagList<?>) tag.contents().getTag("pos"));
                int state = tag.contents().getInt("state");
                TagCompound nbt = tag.contents().getCompound("nbt");
                BlockSerializable data = api.loadBlock(palette.get(state), nbt);
                LocationSerializable loc = origin.add(pos);
                data.deserialize(loc);
            } else if (entitySpawnIterator.hasNext()) {
                TagCompound tag = entitySpawnIterator.next();
                EntitySerializable data = api.loadEntity(tag.contents().getCompound("nbt")).regenerateUUID();
                VectorSerializable pos = new VectorSerializable((TagList<?>) tag.contents().getTag("pos"));
                LocationSerializable loc = origin.add(pos);
                data.deserialize(loc);
            }
        }

        @Override
        public boolean isFinished() {
            return estimatedStepsRemaining <= 0;
        }

        @Override
        public Void getResult() throws IncompatiblePlatformException {
            return null;
        }

        @Override
        public long getEstimatedStepsRemaining() {
            return estimatedStepsRemaining;
        }

    }

}
