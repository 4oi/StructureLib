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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author toyblocks
 */
public class TagCompound extends TagBase {

    private Map<String, TagBase> data;
    private final Values values = new Values();

    public TagCompound() {
    }

    public TagCompound(UUID uuid) {
        this.data = new HashMap<>(2);
        data.put("Most", new TagLong(uuid.getMostSignificantBits()));
        data.put("Least", new TagLong(uuid.getLeastSignificantBits()));
    }

    public TagCompound(Map<String, TagBase> data) {
        for (Map.Entry<String, TagBase> entry : data.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Illegal key");
            } else if (entry.getValue() == null || entry.getValue() instanceof TagEnd) {
                throw new IllegalArgumentException("Illegal element");
            }
        }
        this.data = new HashMap<>(data);
    }

    @Override
    /*package*/ void read(DataInput in) throws IOException {
        data = new HashMap<>();
        while (true) {
            TagBase.Type type = TagBase.getType(in.readByte());
            if (type == TagBase.Type.END) {
                break;
            }
            String key = in.readUTF();
            TagBase value = type.newInstance();
            value.read(in);
            data.put(key, value);
        }
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, TagBase> entry : data.entrySet()) {
                out.writeByte(entry.getValue().getType().getID());
                out.writeUTF(entry.getKey());
                entry.getValue().write(out);
            }
        }
        out.writeByte(TagBase.Type.END.getID());
    }

    @Override
    public void write(StringBuilder out) {
        out.append('{');
        int i = 0;
        for (Map.Entry<String, TagBase> entry : data.entrySet()) {
            if (i++ != 0) {
                out.append(',');
            }
            out.append('"');
            out.append(entry.getKey());
            out.append("\":");
            entry.getValue().write(out);
        }
        out.append('}');
    }

    @Override
    public Map<String, TagBase> get() {
        return data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    public UUID getAsUUID() {
        TagBase most = data.get("Most");
        TagBase least = data.get("Least");
        if (most instanceof TagLong && least instanceof TagLong) {
            return new UUID(
                    ((TagLong) most).getAsLong(),
                    ((TagLong) least).getAsLong()
            );
        }
        throw new IllegalStateException("No UUID present");
    }

    public Values contents() {
        return values;
    }

    @Override
    public Type getType() {
        return Type.COMPOUND;
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
        return Objects.equals(data, ((TagCompound) obj).data);
    }

    public class Values {

        private Values() {
        }

        public TagBase getTag(String key) {
            if (TagCompound.this.data == null) {
                return null;
            } else {
                return TagCompound.this.data.get(key);
            }
        }

        public Object get(String key) {
            return getTag(key).get();
        }

        public byte getByte(String key) {
            return ((TagByte) getTag(key)).getAsByte();
        }

        public boolean getBoolean(String key) {
            return ((TagByte) getTag(key)).getAsBoolean();
        }

        public byte[] getByteArray(String key) {
            return ((TagByteArray) getTag(key)).get();
        }

        public TagCompound getCompound(String key) {
            return (TagCompound) getTag(key);
        }

        public UUID getUUID(String key) {
            return ((TagCompound) getTag(key)).getAsUUID();
        }

        public double getDouble(String key) {
            return ((TagDouble) getTag(key)).getAsDouble();
        }

        public float getFloat(String key) {
            return ((TagFloat) getTag(key)).getAsFloat();
        }

        public int getInt(String key) {
            return ((TagInt) getTag(key)).getAsInt();
        }

        public int[] getIntArray(String key) {
            return ((TagIntArray) getTag(key)).get();
        }

        public List<? extends TagBase> getList(String key) {
            return ((TagList) getTag(key)).get();
        }

        public long getLong(String key) {
            return ((TagLong) getTag(key)).getAsLong();
        }

        public short getShort(String key) {
            return ((TagShort) getTag(key)).getAsShort();
        }

        public String getString(String key) {
            return ((TagString) getTag(key)).get();
        }

        public boolean containsKey(String key) {
            if (TagCompound.this.data == null) {
                return false;
            } else {
                return TagCompound.this.data.containsKey(key);
            }
        }

        public int size() {
            if (TagCompound.this.data == null) {
                return 0;
            } else {
                return TagCompound.this.data.size();
            }
        }

    }

    public static class Builder {

        private final Map<String, TagBase> data = new HashMap<>();

        public Builder() {
        }

        public Builder(TagCompound... sources) {
            for (TagCompound source : sources) {
                data.putAll(source.data);
            }
        }

        public Builder(String key, TagBase value) {
            data.put(key, value);
        }

        public Builder append(String key, TagBase value) {
            if (value != null) {
                data.put(key, value);
            }
            return this;
        }

        public Builder append(String key, byte value) {
            data.put(key, new TagByte(value));
            return this;
        }

        public Builder append(String key, byte[] value) {
            if (value != null) {
                data.put(key, new TagByteArray(value));
            }
            return this;
        }

        public Builder append(String key, TagCompound.Builder value) {
            if (value != null) {
                data.put(key, value.build());
            }
            return this;
        }

        public Builder append(String key, UUID value) {
            if (value != null) {
                data.put(key, new TagCompound(value));
            }
            return this;
        }

        public Builder append(String key, double value) {
            data.put(key, new TagDouble(value));
            return this;
        }

        public Builder append(String key, float value) {
            data.put(key, new TagFloat(value));
            return this;
        }

        public Builder append(String key, int value) {
            data.put(key, new TagInt(value));
            return this;
        }

        public Builder append(String key, int[] value) {
            if (value != null) {
                data.put(key, new TagIntArray(value));
            }
            return this;
        }

        public <T extends TagBase> Builder append(String key, Class<T> clazz, List<T> value) {
            if (value != null) {
                data.put(key, new TagList<>(TagBase.getType(clazz), value));
            }
            return this;
        }

        public Builder append(String key, long value) {
            data.put(key, new TagLong(value));
            return this;
        }

        public Builder append(String key, short value) {
            data.put(key, new TagShort(value));
            return this;
        }

        public Builder append(String key, String value) {
            if (value != null) {
                data.put(key, new TagString(value));
            }
            return this;
        }

        public TagCompound build() {
            return new TagCompound(data);
        }

    }

}
