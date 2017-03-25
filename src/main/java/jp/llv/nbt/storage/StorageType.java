/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.storage;

import java.util.function.Function;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.tag.TagCompound;

/**
 *
 * @author toyblocks
 */
public enum StorageType {

    STRUCTURE(StructureFormat::new), //SCHEMATIC(SchematicFormat::new),
    ;

    private final Function<StructureLibAPI, StorageFormat> constructor;
    private StorageFormat instance;

    private StorageType(Function<StructureLibAPI, StorageFormat> constructor) {
        this.constructor = constructor;
    }

    private void initialize(StructureLibAPI api) {
        if (instance == null) {
            instance = constructor.apply(api);
        }
    }

    public TagCompound save(StructureLibAPI api, CuboidSerializable cuboid, boolean entity) throws IncompatiblePlatformException {
        initialize(api);
        return instance.save(cuboid, entity);
    }

    public void load(StructureLibAPI api, TagCompound source, LocationSerializable origin, LoadOption ... options) throws IncompatiblePlatformException {
        initialize(api);
        instance.load(source, origin, options);
    }

}
