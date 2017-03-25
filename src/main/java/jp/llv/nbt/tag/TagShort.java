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

/**
 *
 * @author toyblocks
 */
public class TagShort extends TagBase {
    
    private short data;
    
    public TagShort() {
    }

    public TagShort(short data) {
        this.data = data;
    }

    @Override
    /*package*/ void read(DataInput in) throws IOException {
        data = in.readShort();
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        out.writeShort(data);
    }

    @Override
    public void write(StringBuilder out) {
        out.append(data).append('s');
    }

    @Override
    public Short get() {
        return data;
    }
    
    public short getAsShort() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.SHORT;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return ((TagShort) obj).data == this.data;
    }
    
}
