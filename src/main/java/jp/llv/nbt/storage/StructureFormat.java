/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import jp.llv.nbt.BlockSerializable;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.EntitySerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.VectorSerializable;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 *
 * @author toyblocks
 */
public class StructureFormat implements StorageFormat {

    private final StructureLibAPI api;

    public StructureFormat(StructureLibAPI api) {
        this.api = api;
    }

    @Override
    public TagCompound save(CuboidSerializable cuboid, boolean entity) throws IncompatiblePlatformException {
        Palette palette = new Palette();
        Blocks blocks = new Blocks();
        Entities entities = new Entities();

        // blocks
        for (LocationSerializable loc : cuboid) {
            VectorSerializable pos = loc.subtract(cuboid.getOrigin());
            Block block = loc.toLocation().getBlock();
            if (block.getType() == Material.STRUCTURE_VOID) {
                continue;
            }
            BlockSerializable data = api.serialize(block);
            int index = palette.getIndexOf(data.getData());
            if (data.getTag().isPresent()) {
                blocks.add(pos, index, data.getTag().get());
            } else {
                blocks.add(pos, index);
            }
        }

        // entities
        if (entity) {
            for (Entity ent : cuboid.getWorld().getEntities()) {
                LocationSerializable loc = new LocationSerializable(ent.getLocation());
                if (!cuboid.isBlockIn(loc)) {
                    continue;
                }
                VectorSerializable pos = loc.subtract(cuboid.getOrigin());
                EntitySerializable data = api.serialize(ent);
                entities.add(pos, data.toTag());
            }
        }

        return new TagCompound.Builder("size", cuboid.getSize().add(1d).toBlockTagList())
                .append("entities", entities.toTag())
                .append("blocks", blocks.toTag())
                .append("author", "StructureLib by toyblocks")
                .append("palette", palette.toTag())
                .append("DataVersion", 922).build();
    }

    @Override
    public void load(TagCompound source, LocationSerializable origin, LoadOption... options) throws IncompatiblePlatformException {
        List<LoadOption> opts = Arrays.asList(options);
        VectorSerializable size = new VectorSerializable((TagList<?>) source.contents().getTag("size"));
        Entities entities = new Entities((TagList<TagCompound>) source.contents().getTag("entities"));
        Blocks blocks = new Blocks((TagList<TagCompound>) source.contents().getTag("blocks"));
        //String author = source.contents().getString("author");
        Palette palette = new Palette((TagList<TagCompound>) source.contents().getTag("palette"));
        //int version = source.contents().getInt("DataVersion");

        CuboidSerializable cuboid = new CuboidSerializable(origin, size.subtract(1d));
        
        if (opts.contains(LoadOption.REMOVE_BLOCKS_FIRST)) {
            for (LocationSerializable loc : cuboid) {
                loc.toLocation().getBlock().setType(Material.AIR);
            }
        }
        
        if (opts.contains(LoadOption.REMOVE_ENTITIES_FIRST)) {
            origin.getWorld().getEntities().stream()
                    .filter(e -> cuboid.isBlockIn(new LocationSerializable(e.getLocation())))
                    .forEach(Entity::remove);
        }
        
        if (opts.contains(LoadOption.LOAD_BLOCKS)) {
            for (TagCompound tag : blocks) {
                VectorSerializable pos = new VectorSerializable((TagList<?>) tag.contents().getTag("pos"));
                int state = tag.contents().getInt("state");
                TagCompound nbt = tag.contents().getCompound("nbt");
                BlockSerializable data = api.loadBlock(palette.get(state), nbt);
                LocationSerializable loc = origin.add(pos);
                data.deserialize(loc);
            }
        }

        if (opts.contains(LoadOption.LOAD_ENTITIES)) {
            for (TagCompound tag : entities) {
                EntitySerializable data = api.loadEntity(tag.contents().getCompound("nbt")).regenerateUUID();
                VectorSerializable pos = new VectorSerializable((TagList<?>) tag.contents().getTag("pos"));
                LocationSerializable loc = origin.add(pos);
                data.deserialize(loc);
            }
        }
    }

    public static class Palette {

        public final List<TagCompound> data = new ArrayList<>();

        public Palette() {
        }

        public Palette(TagList<TagCompound> data) {
            this.data.addAll(data.get());
        }

        public int getIndexOf(TagCompound tag) {
            int index = data.indexOf(tag);
            if (index < 0) {
                data.add(tag);
                return data.size() - 1;
            } else {
                return index;
            }
        }

        public TagCompound get(int index) {
            return data.get(index);
        }

        public TagList<TagCompound> toTag() {
            return new TagList<>(TagBase.Type.COMPOUND, data);
        }

    }

    public static class Blocks implements Iterable<TagCompound> {

        public final List<TagCompound> data = new ArrayList<>();

        public Blocks() {
        }

        public Blocks(TagList<TagCompound> data) {
            this.data.addAll(data.get());
        }

        public void add(VectorSerializable pos, int index) {
            data.add(new TagCompound.Builder()
                    .append("pos", pos.toBlockTagList())
                    .append("state", index).build()
            );
        }

        public void add(VectorSerializable pos, int index, TagCompound tag) {
            data.add(new TagCompound.Builder()
                    .append("pos", pos.toBlockTagList())
                    .append("state", index)
                    .append("nbt", tag).build()
            );
        }

        public TagList<TagCompound> toTag() {
            return new TagList<>(TagBase.Type.COMPOUND, data);
        }

        @Override
        public Iterator<TagCompound> iterator() {
            return data.iterator();
        }

    }

    public static class Entities implements Iterable<TagCompound> {

        public final List<TagCompound> data = new ArrayList<>();

        public Entities() {
        }

        public Entities(TagList<TagCompound> data) {
            this.data.addAll(data.get());
        }

        public void add(VectorSerializable pos, TagCompound tag) {
            add(pos, tag, pos);
        }

        public void add(VectorSerializable pos, TagCompound tag, VectorSerializable blockPos) {
            data.add(new TagCompound.Builder()
                    .append("pos", pos.toTagList())
                    .append("nbt", tag)
                    .append("blockPos", pos.toBlockTagList()).build()
            );
        }

        public TagList<TagCompound> toTag() {
            return new TagList<>(TagBase.Type.COMPOUND, data);
        }

        @Override
        public Iterator<TagCompound> iterator() {
            return data.iterator();
        }

    }

}
