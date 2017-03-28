/*
 * Copyright 2017 SakuraServerDev All rights reserved.
 */
package jp.llv.nbt.storage.async;

import jp.llv.nbt.CuboidSerializable;
import jp.llv.nbt.LocationSerializable;
import jp.llv.nbt.storage.LoadOption;
import jp.llv.nbt.tag.TagCompound;

/**
 *
 * @author SakuraServerDev
 */
public interface AsyncStorageFormat {

    AsyncSerializeTask<TagCompound> save(CuboidSerializable cuboid, boolean entity);

    AsyncSerializeTask<Void> load(TagCompound source, LocationSerializable origin, LoadOption ... options);
    
}
