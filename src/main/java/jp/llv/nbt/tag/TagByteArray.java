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
import java.util.Arrays;

/**
 *
 * @author toyblocks
 */
public class TagByteArray extends TagBase {

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private byte[] data;

    public TagByteArray() {
    }

    public TagByteArray(byte... data) {
        this.data = data;
    }

    @Override
    /*package*/ void read(DataInput in) throws IOException {
        int length = in.readInt();
        data = new byte[length];
        in.readFully(data);
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        if (data == null) {
            out.writeInt(0);
        } else {
            out.writeInt(data.length);
            out.write(data);
        }
    }

    @Override
    public void write(StringBuilder out) {
        char[] hexChars = new char[data.length * 2];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARS[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        out.append(hexChars);
    }

    @Override
    public byte[] get() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.BYTE_ARRAY;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Arrays.equals(this.data, ((TagByteArray) obj).data);
    }

}
