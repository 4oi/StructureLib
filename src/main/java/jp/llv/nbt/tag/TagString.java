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
import java.util.Objects;

/**
 *
 * @author toyblocks
 */
public class TagString extends TagBase {

    private String data;

    public TagString() {
        this.data = "";
    }

    public TagString(String data) {
        Objects.requireNonNull(data);
        this.data = data;
    }
    
    @Override
    /*package*/ void read(DataInput in) throws IOException {
        data = in.readUTF();
    }

    @Override
    /*package*/ void write(DataOutput out) throws IOException {
        out.writeUTF(data);
    }

    @Override
    public void write(StringBuilder out) {
        out.append('\"').append(this.data.replace("\"", "\\\"")).append('\"');
    }

    @Override
    public String get() {
        return data;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return ((TagString) obj).data.equals(this.data);
    }
    
}
