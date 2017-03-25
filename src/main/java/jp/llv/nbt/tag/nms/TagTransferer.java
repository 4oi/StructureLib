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
package jp.llv.nbt.tag.nms;

import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagByte;
import jp.llv.nbt.tag.TagByteArray;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagDouble;
import jp.llv.nbt.tag.TagEnd;
import jp.llv.nbt.tag.TagFloat;
import jp.llv.nbt.tag.TagInt;
import jp.llv.nbt.tag.TagIntArray;
import jp.llv.nbt.tag.TagList;
import jp.llv.nbt.tag.TagLong;
import jp.llv.nbt.tag.TagShort;
import jp.llv.nbt.tag.TagString;

/**
 *
 * @author toyblocks
 */
public interface TagTransferer {
    
    Object transfer(TagByte tag) throws IncompatiblePlatformException;
    
    Object transfer(TagByteArray tag) throws IncompatiblePlatformException;
    
    Object transfer(TagCompound tag) throws IncompatiblePlatformException;
    
    Object createTagCompound() throws IncompatiblePlatformException;
    
    Object transfer(TagDouble tag) throws IncompatiblePlatformException;
    
    Object transfer(TagEnd tag) throws IncompatiblePlatformException;
    
    Object transfer(TagFloat tag) throws IncompatiblePlatformException;
    
    Object transfer(TagInt tag) throws IncompatiblePlatformException;
    
    Object transfer(TagIntArray tag) throws IncompatiblePlatformException;
    
    Object transfer(TagList<? extends TagBase> tag) throws IncompatiblePlatformException;
    
    Object transfer(TagLong tag) throws IncompatiblePlatformException;
    
    Object transfer(TagShort tag) throws IncompatiblePlatformException;
    
    Object transfer(TagString tag) throws IncompatiblePlatformException;
    
    TagBase transfer(Object tag) throws IncompatiblePlatformException;
    
    Object parse(String mojangson) throws IncompatiblePlatformException;
    
    default Object transfer(TagBase tag) throws IncompatiblePlatformException {
        if (tag instanceof TagByte) {
            return transfer((TagByte) tag);
        } else if (tag instanceof TagByteArray) {
            return transfer((TagByteArray) tag);
        } else if (tag instanceof TagCompound) {
            return transfer((TagCompound) tag);
        } else if (tag instanceof TagDouble) {
            return transfer((TagDouble) tag);
        } else if (tag instanceof TagEnd) {
            return transfer((TagEnd) tag);
        } else if (tag instanceof TagFloat) {
            return transfer((TagFloat) tag);
        } else if (tag instanceof TagInt) {
            return transfer((TagInt) tag);
        } else if (tag instanceof TagIntArray) {
            return transfer((TagIntArray) tag);
        } else if (tag instanceof TagList) {
            return transfer((TagList) tag);
        } else if (tag instanceof TagLong) {
            return transfer((TagLong) tag);
        } else if (tag instanceof TagShort) {
            return transfer((TagShort) tag);
        } else if (tag instanceof TagString) {
            return transfer((TagString) tag);
        }
        throw new IncompatiblePlatformException();
    }
    
}
