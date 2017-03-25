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

/**
 *
 * @author toyblocks
 */
public class TagEnd extends TagBase {
    
    public static final TagEnd INSTANCE = new TagEnd();
    
    private TagEnd() {
    }

    @Override
    /*package*/ void read(DataInput in) {
    }

    @Override
    /*package*/ void write(DataOutput out) {
    }

    @Override
    public void write(StringBuilder out) {
    }

    @Override
    public Void get() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.END;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
    
}
