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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import jp.llv.nbt.IncompatiblePlatformException;
import jp.llv.reflection.Refl;

/**
 *
 * @author toyblocks
 */
public abstract class TagBase implements Serializable {

    /*package*/ TagBase() {
    }

    /*package*/ abstract void read(DataInput in) throws IOException;

    public void read(InputStream in, boolean gzip) throws IOException {
        InputStream is = new BufferedInputStream(in);
        if (gzip) {
            is = new GZIPInputStream(is);
        }
        try (DataInputStream dis = new DataInputStream(is)) {
            dis.readByte();// Root compound tag type
            dis.readUTF(); // Root compound tag name
            this.read(dis);
        }
    }

    public void read(Path file, boolean gzip) throws IOException {
        try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ)) {
            read(is, gzip);
        }
    }

    public void read(byte[] binary, boolean gzip) throws IOException {
        try (InputStream is = new ByteArrayInputStream(binary)) {
            read(is, gzip);
        }
    }

    /*package*/ abstract void write(DataOutput out) throws IOException;

    public void write(OutputStream out, boolean gzip) throws IOException {
        OutputStream os = new BufferedOutputStream(out);
        if (gzip) {
            os = new GZIPOutputStream(os);
        }
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeByte(0x0A); // Root compound tag type
            dos.writeUTF("");    // Root compound tag name
            this.write(dos);
        }
    }

    public void write(Path file, boolean gzip) throws IOException {
        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            write(os, gzip);
        }
    }

    public byte[] write(boolean gzip) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            write(os, gzip);
            return os.toByteArray();
        }
    }

    public abstract void write(StringBuilder out);

    public abstract Object get();

    public abstract Type getType();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        write(sb);
        return sb.toString();
    }

    public static Type getType(byte id) {
        for (Type type : Type.values()) {
            if (type.getID() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown nbt tag type");
    }

    public static Type getType(Class<? extends TagBase> clazz) {
        for (Type type : Type.values()) {
            if (type.getTypeClass() == clazz) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown nbt tag type");
    }

    public static enum Type {

        END(TagEnd.class, (byte) 0),
        BYTE(TagByte.class, (byte) 1),
        SHORT(TagShort.class, (byte) 2),
        INT(TagInt.class, (byte) 3),
        LONG(TagLong.class, (byte) 4),
        FLOAT(TagFloat.class, (byte) 5),
        DOUBLE(TagDouble.class, (byte) 6),
        BYTE_ARRAY(TagByteArray.class, (byte) 7),
        STRING(TagString.class, (byte) 8),
        LIST(TagList.class, (byte) 9),
        COMPOUND(TagCompound.class, (byte) 10),
        INT_ARRAY(TagIntArray.class, (byte) 11),;

        private final Class<? extends TagBase> clazz;
        private final byte id;

        private Type(Class<? extends TagBase> clazz, byte id) {
            this.clazz = clazz;
            this.id = id;
        }

        public TagBase newInstance() {
            try {
                return clazz.newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

        public TagBase newInstance(Object arg) {
            try {
                return Refl.wrap(clazz).newInstance(arg).unwrap();
            } catch (ReflectiveOperationException ex) {
                throw new IncompatiblePlatformException(ex);
            }
        }

        public Class<? extends TagBase> getTypeClass() {
            return clazz;
        }

        public byte getID() {
            return id;
        }

        public boolean is(TagBase tag) {
            return clazz.isInstance(tag);
        }

    }

}
