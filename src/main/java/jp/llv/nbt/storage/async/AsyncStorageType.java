/*
 * Copyright 2017 SakuraServerDev All rights reserved.
 */
package jp.llv.nbt.storage.async;

import java.util.function.Function;
import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.StructureLibAPI;
import jp.llv.nbt.storage.LoadOption;
import jp.llv.nbt.tag.TagCompound;

/**
 *
 * @author SakuraServerDev
 */
public enum AsyncStorageType {

    STRUCTURE(AsyncStructureFormat::new), //SCHEMATIC(SchematicFormat::new),
    ;

    private final Function<StructureLibAPI, AsyncStorageFormat> constructor;
    private AsyncStorageFormat instance;

    private AsyncStorageType(Function<StructureLibAPI, AsyncStorageFormat> constructor) {
        this.constructor = constructor;
    }

    private void initialize(StructureLibAPI api) {
        if (instance == null) {
            instance = constructor.apply(api);
        }
    }

    public AsyncSerializeTask<TagCompound> save(StructureLibAPI api, CuboidSerializable cuboid, boolean entity) {
        initialize(api);
        return instance.save(cuboid, entity);
    }

    public AsyncSerializeTask<Void> load(StructureLibAPI api, TagCompound source, LocationSerializable origin, LoadOption... options) {
        initialize(api);
        return instance.load(source, origin, options);
    }

}
