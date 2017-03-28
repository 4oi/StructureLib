/*
 * Copyright (C) 2017 toyblocks
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
package jp.llv.nbt.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author toyblocks
 */
public class TagList<T extends TagBase> extends TagBase implements Iterable<T> {

    private TagBase.Type type;
    private List<T> data;
    private final Values values = new Values();

    public TagList() {
    }

    public TagList(Type type, List<T> data) {
        if (type == TagBase.Type.END) {
            if (!data.isEmpty()) {
                throw new IllegalArgumentException("Type mismatch");
            }
        } else {
            this.type = type;
            for (T t : data) {
                if (!type.is(t)) {
                    throw new IllegalArgumentException("Type mismatch");
                }
            }
            this.data = new ArrayList<>(data);
        }
    }

    @Override
    /*package*/ void read(DataInput in) throws IOException {
        type = TagBase.getType(in.readByte());
        int length = in.readInt();
        if (type == TagBase.Type.END && length > 0) {
            throw new IOException("Missing list type");
        }
        data = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            T element = (T) type.newInstance();
            element.read(in);
            data.add(element);
        }
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        if (this.data == null || this.data.isEmpty()) {
            out.writeByte(TagBase.Type.END.getID());
            out.writeInt(0);
        } else {
            out.writeByte(type.getID());
            out.writeInt(data.size());
            for (T element : data) {
                element.write(out);
            }
        }
    }

    @Override
    public void write(StringBuilder out) {
        if (data == null) {
            out.append("[]");
        } else {
            out.append('[');
            for (int i = 0; i < data.size(); i++) {
                if (i > 0) {
                    out.append(',');
                }
                data.get(i).write(out);
            }
            out.append(']');
        }
    }

    @Override
    public List<T> get() {
        return data == null ? Collections.emptyList() : Collections.unmodifiableList(data);
    }

    public TagBase.Type getContentType() {
        return type;
    }

    public Values contents() {
        return values;
    }

    @Override
    public Type getType() {
        return Type.LIST;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(data, ((TagList) obj).data);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    public class Values {

        private Values() {
        }

        public TagBase getTag(int index) {
            if (TagList.this.data == null) {
                return null;
            } else {
                return TagList.this.data.get(index);
            }
        }

        public Object get(int index) {
            return getTag(index).get();
        }

        public byte getByte(int index) {
            return ((TagByte) getTag(index)).getAsByte();
        }

        public boolean getBoolean(int index) {
            return ((TagByte) getTag(index)).getAsBoolean();
        }

        public byte[] getByteArray(int index) {
            return ((TagByteArray) getTag(index)).get();
        }

        public TagCompound getCompound(int index) {
            return (TagCompound) getTag(index);
        }

        public UUID getUUID(int index) {
            return ((TagCompound) getTag(index)).getAsUUID();
        }

        public double getDouble(int index) {
            return ((TagDouble) getTag(index)).getAsDouble();
        }

        public float getFloat(int index) {
            return ((TagFloat) getTag(index)).getAsFloat();
        }

        public int getInt(int index) {
            return ((TagInt) getTag(index)).getAsInt();
        }

        public int[] getIntArray(int index) {
            return ((TagIntArray) getTag(index)).get();
        }

        public List<? extends TagBase> getList(int index) {
            return ((TagList) getTag(index)).get();
        }

        public long getLong(int index) {
            return ((TagLong) getTag(index)).getAsLong();
        }

        public short getShort(int index) {
            return ((TagShort) getTag(index)).getAsShort();
        }

        public String getString(int index) {
            return ((TagString) getTag(index)).get();
        }

        public int size() {
            if (TagList.this.data == null) {
                return 0;
            } else {
                return TagList.this.data.size();
            }
        }

    }

    public static class Builder<T extends TagBase> {

        private final Class<T> clazz;
        private final TagBase.Type type;
        private final List<T> data = new ArrayList<>();

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
            this.type = TagBase.getType(clazz);
        }

        public Builder(Class<T> clazz, T element) {
            this(clazz);
            data.add(element);
        }

        public Builder<T> append(T element) {
            data.add(element);
            return this;
        }
        
        public Builder<T> append(Object element) {
            return append((T) type.newInstance(element));
        }

        public TagList<T> build() {
            return new TagList<>(TagBase.getType(clazz), data);
        }

    }

}
