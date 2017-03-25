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
import jp.llv.reflection.Refl.RObject;

/**
 *
 * @author toyblocks
 */
public class TagTransferer1111 implements TagTransferer {

    private final String nms;

    public TagTransferer1111(String infix) {
        Objects.requireNonNull(infix);
        this.nms = NMSConstants.NMS + infix;
    }

    @Override
    public Object transfer(TagByte tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagByte").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagByteArray tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagByteArray").newInstance((Object) tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object createTagCompound() throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagCompound").newInstance().unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagCompound tag) throws IncompatiblePlatformException {
        try {
            Refl.RObject compound = Refl.getRClass(nms + "NBTTagCompound").newInstance();
            for (Map.Entry<String, TagBase> entry : tag.get().entrySet()) {
                compound.invoke("set", entry.getKey(), transfer(entry.getValue()));
            }
            return compound.unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagDouble tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagDouble").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagEnd tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagEnd").newInstance().unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException();
        }
    }

    @Override
    public Object transfer(TagFloat tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagFloat").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagInt tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagInt").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagIntArray tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagIntArray").newInstance((Object) tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagList<?> tag) throws IncompatiblePlatformException {
        try {
            RObject<?> nmsTag = Refl.getRClass(nms + "NBTTagList").newInstance();
            for (TagBase element : tag.get()) {
                nmsTag.invoke("add", transfer(element));
            }
            return nmsTag.unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagLong tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagLong").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagShort tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagShort").newInstance(tag.get()).unwrap();
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

    @Override
    public Object transfer(TagString tag) throws IncompatiblePlatformException {
        try {
            return Refl.getRClass(nms + "NBTTagString").newInstance(tag.get()).unwrap();
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
            RObject rTag = Refl.wrap(tag);
            TagBase.Type type = TagBase.getType((byte) rTag.invoke("getTypeId").unwrap());
            switch (type) {
                case COMPOUND: {
                    Map<String, Object> source = (Map<String, Object>) rTag.get("map").unwrap();
                    Map<String, TagBase> dest = new HashMap<>(source.size());
                    for (Map.Entry<String, Object> entry : source.entrySet()) {
                        dest.put(entry.getKey(), transfer(entry.getValue()));
                    }
                    return new TagCompound(dest);
                }
                case END:
                    return TagEnd.INSTANCE;
                case LIST: {
                    List<Object> source = (List<Object>) rTag.get("list").unwrap();
                    List<TagBase> dest = new ArrayList<>(source.size());
                    for (Object element : source) {
                        dest.add(transfer(element));
                    }
                    TagBase.Type contentsType = TagBase.getType((byte) rTag.get("type").unwrap());
                    return new TagList(contentsType, dest);
                }
                default:
                    return type.newInstance(rTag.get("data").unwrap());
            }
        } catch (ReflectiveOperationException ex) {
            throw new IncompatiblePlatformException(ex);
        }
    }

}
