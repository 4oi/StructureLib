/*
 * Copyright (C) 2016 toyblocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.llv.nbt.tag.nms.mc_1_11_1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jp.llv.nbt.tag.TagLong;
import jp.llv.nbt.tag.TagDouble;
import jp.llv.nbt.tag.TagByte;
import jp.llv.nbt.tag.TagByteArray;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagInt;
import jp.llv.nbt.tag.nms.TagTransferer;
import jp.llv.nbt.tag.TagEnd;
import jp.llv.nbt.tag.TagString;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagFloat;
import jp.llv.nbt.tag.TagList;
import jp.llv.nbt.tag.TagIntArray;
import jp.llv.nbt.tag.TagShort;
import java.util.Map;
import java.util.Objects;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.nms.NMSConstants;
import jp.llv.reflection.Refl;

/**
 *
 * @author toyblocks
 */
public class TagTransferer1111 implements TagTransferer {

    private final String nms;
    private final Class<?> classBase;
    private final Method methodBaseGetType;
    private final Class<?> classByte;
    private final Constructor<?> constructorByte;
    private final Field fieldByteData;
    private final Class<?> classByteArray;
    private final Constructor<?> constructorByteArray;
    private final Field fieldByteArrayData;
    private final Class<?> classCompound;
    private final Constructor<?> constructorCompound;
    private final Method methodCompoundSet;
    private final Field fieldCompoundMap;
    private final Class<?> classDouble;
    private final Constructor<?> constructorDouble;
    private final Field fieldDoubleData;
    private final Class<?> classEnd;
    private final Constructor<?> constructorEnd;
    private final Class<?> classFloat;
    private final Constructor<?> constructorFloat;
    private final Field fieldFloatData;
    private final Class<?> classInt;
    private final Constructor<?> constructorInt;
    private final Field fieldIntData;
    private final Class<?> classIntArray;
    private final Constructor<?> constructorIntArray;
    private final Field fieldIntArrayData;
    private final Class<?> classList;
    private final Constructor<?> constructorList;
    private final Method methodListAdd;
    private final Field fieldListList;
    private final Field fieldListType;
    private final Class<?> classLong;
    private final Constructor<?> constructorLong;
    private final Field fieldLongData;
    private final Class<?> classShort;
    private final Constructor<?> constructorShort;
    private final Field fieldShortData;
    private final Class<?> classString;
    private final Constructor<?> constructorString;
    private final Field fieldStringData;

    public TagTransferer1111(String infix) {
        try {
            Objects.requireNonNull(infix);
            nms = NMSConstants.NMS + infix;
            classBase = Class.forName(nms + "NBTBase");
            methodBaseGetType = classBase.getMethod("getTypeId");
            classByte = Class.forName(nms + "NBTTagByte");
            constructorByte = classByte.getConstructor(byte.class);
            fieldByteData = classByte.getDeclaredField("data");
            fieldByteData.setAccessible(true);
            classByteArray = Class.forName(nms + "NBTTagByteArray");
            constructorByteArray = classByteArray.getConstructor(byte[].class);
            fieldByteArrayData = classByteArray.getDeclaredField("data");
            fieldByteArrayData.setAccessible(true);
            classCompound = Class.forName(nms + "NBTTagCompound");
            constructorCompound = classCompound.getConstructor();
            methodCompoundSet = classCompound.getMethod("set", String.class, classBase);
            fieldCompoundMap = classCompound.getDeclaredField("map");
            fieldCompoundMap.setAccessible(true);
            classDouble = Class.forName(nms + "NBTTagDouble");
            constructorDouble = classDouble.getConstructor(double.class);
            fieldDoubleData = classDouble.getDeclaredField("data");
            fieldDoubleData.setAccessible(true);
            classEnd = Class.forName(nms + "NBTTagEnd");
            constructorEnd = classEnd.getDeclaredConstructor();
            constructorEnd.setAccessible(true);
            classFloat = Class.forName(nms + "NBTTagFloat");
            constructorFloat = classFloat.getConstructor(float.class);
            fieldFloatData = classFloat.getDeclaredField("data");
            fieldFloatData.setAccessible(true);
            classInt = Class.forName(nms + "NBTTagInt");
            constructorInt = classInt.getConstructor(int.class);
            fieldIntData = classInt.getDeclaredField("data");
            fieldIntData.setAccessible(true);
            classIntArray = Class.forName(nms + "NBTTagIntArray");
            constructorIntArray = classIntArray.getConstructor(int[].class);
            fieldIntArrayData = classIntArray.getDeclaredField("data");
            fieldIntArrayData.setAccessible(true);
            classList = Class.forName(nms + "NBTTagList");
            constructorList = classList.getConstructor();
            methodListAdd = classList.getMethod("add", classBase);
            fieldListList = classList.getDeclaredField("list");
            fieldListList.setAccessible(true);
            fieldListType = classList.getDeclaredField("type");
            fieldListType.setAccessible(true);
            classLong = Class.forName(nms + "NBTTagLong");
            constructorLong = classLong.getConstructor(long.class);
            fieldLongData = classLong.getDeclaredField("data");
            fieldLongData.setAccessible(true);
            classShort = Class.forName(nms + "NBTTagShort");
            constructorShort = classShort.getConstructor(short.class);
            fieldShortData = classShort.getDeclaredField("data");
            fieldShortData.setAccessible(true);
            classString = Class.forName(nms + "NBTTagString");
            constructorString = classString.getConstructor(String.class);
            fieldStringData = classString.getDeclaredField("data");
            fieldStringData.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object transfer(TagByte tag) throws IncompatiblePlatformException {
        try {
            return constructorByte.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagByteArray tag) throws IncompatiblePlatformException {
        try {
            return constructorByteArray.newInstance((Object) tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object createTagCompound() throws IncompatiblePlatformException {
        try {
            return constructorCompound.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagCompound tag) throws IncompatiblePlatformException {
        try {
            Object compound = createTagCompound();
            for (Map.Entry<String, TagBase> entry : tag.get().entrySet()) {
                methodCompoundSet.invoke(compound, entry.getKey(), transfer(entry.getValue()));
            }
            return compound;
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagDouble tag) throws IncompatiblePlatformException {
        try {
            return constructorDouble.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagEnd tag) throws IncompatiblePlatformException {
        try {
            return constructorEnd.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException();
        }
    }

    @Override
    public Object transfer(TagFloat tag) throws IncompatiblePlatformException {
        try {
            return constructorFloat.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagInt tag) throws IncompatiblePlatformException {
        try {
            return constructorInt.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagIntArray tag) throws IncompatiblePlatformException {
        try {
            return constructorIntArray.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagList<?> tag) throws IncompatiblePlatformException {
        try {
            Object list = constructorList.newInstance();
            for (TagBase element : tag.get()) {
                methodListAdd.invoke(list, transfer(element));
            }
            return list;
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagLong tag) throws IncompatiblePlatformException {
        try {
            return constructorLong.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagShort tag) throws IncompatiblePlatformException {
        try {
            return constructorShort.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagString tag) throws IncompatiblePlatformException {
        try {
            return constructorString.newInstance(tag.get());
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object parse(String mojangson) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "MojangsonParser").invoke("parse", mojangson).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public TagBase transfer(Object tag) throws IncompatiblePlatformException {
        try {
            TagBase.Type type = TagBase.getType((byte) methodBaseGetType.invoke(tag));
            switch (type) {
                case BYTE:
                    return new TagByte(fieldByteData.getByte(tag));
                case BYTE_ARRAY:
                    return new TagByteArray((byte[]) fieldByteArrayData.get(tag));
                case COMPOUND: {
                    Map<String, Object> source = (Map<String, Object>) fieldCompoundMap.get(tag);
                    Map<String, TagBase> dest = new HashMap<>(source.size());
                    for (Map.Entry<String, Object> entry : source.entrySet()) {
                        dest.put(entry.getKey(), transfer(entry.getValue()));
                    }
                    return new TagCompound(dest);
                }
                case DOUBLE:
                    return new TagDouble(fieldDoubleData.getDouble(tag));
                case END:
                    return TagEnd.INSTANCE;
                case FLOAT:
                    return new TagFloat(fieldFloatData.getFloat(tag));
                case INT:
                    return new TagInt(fieldIntData.getInt(tag));
                case INT_ARRAY:
                    return new TagIntArray((int[]) fieldIntArrayData.get(tag));
                case LIST: {
                    List<Object> source = (List<Object>) fieldListList.get(tag);
                    List<TagBase> dest = new ArrayList<>(source.size());
                    for (Object element : source) {
                        dest.add(transfer(element));
                    }
                    TagBase.Type contentsType = TagBase.getType(fieldListType.getByte(tag));
                    return new TagList(contentsType, dest);
                }
                case LONG:
                    return new TagLong(fieldLongData.getLong(tag));
                case SHORT:
                    return new TagShort(fieldShortData.getShort(tag));
                case STRING:
                    return new TagString((String) fieldStringData.get(tag));
                default:
                    throw new IncompatiblePlatformException();
            }
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

}
