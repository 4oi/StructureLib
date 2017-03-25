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
public class TagIntArray extends TagBase {

    private int[] data;

    public TagIntArray() {
    }

    public TagIntArray(int... data) {
        this.data = data;
    }

    @Override
    /*package*/ void read(DataInput in) throws IOException {
        int length = in.readInt();
        data = new int[length];
        for (int i = 0; i < length; i++) {
            data[i] = in.readInt();
        }
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        if (data == null || data.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(data.length);
            for (int element : data) {
                out.writeInt(element);
            }
        }
    }

    @Override
    public void write(StringBuilder out) {
        if (data == null || data.length == 0) {
            out.append("[]");
        } else {
            out.append('[');
            for (int i = 0; i < data.length; i++) {
                if (i != 0) {
                    out.append(',');
                }
                out.append(data[i]);
            }
            out.append(']');
        }
    }

    @Override
    public int[] get() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.INT_ARRAY;
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
        return Arrays.equals(this.data, ((TagIntArray) obj).data);
    }

}
