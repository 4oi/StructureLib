/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt.storage;

import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.tag.TagCompound;

/**
 *
 * @author toyblocks
 */
public interface StorageFormat {

    TagCompound save(CuboidSerializable cuboid, boolean entity) throws IncompatiblePlatformException;

    void load(TagCompound source, LocationSerializable origin, LoadOption ... options) throws IncompatiblePlatformException;

}
