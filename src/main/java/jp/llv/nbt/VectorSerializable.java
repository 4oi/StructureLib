/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import jp.llv.nbt.tag.TagBase;
import jp.llv.nbt.tag.TagCompound;
import jp.llv.nbt.tag.TagDouble;
import jp.llv.nbt.tag.TagInt;
import jp.llv.nbt.tag.TagList;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 *
 * @author toyblocks
 */
public final class VectorSerializable implements Serializable {

    private static VectorSerializable zero;
    private final double x, y, z;

    public VectorSerializable(double x, double y, double z) {
        if (!(Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z))) {
            throw new IllegalArgumentException("Numbers are not finite");
        }
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public VectorSerializable(Vector v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public VectorSerializable(BlockVector v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public VectorSerializable(TagList<?> tag) {
        TagBase tx = tag.contents().getTag(0);
        if (tx instanceof TagInt) {
            x = ((TagInt) tx).getAsInt();
            y = tag.contents().getInt(1);
            z = tag.contents().getInt(2);
        } else if (tx instanceof TagDouble) {
            x = ((TagDouble) tx).getAsDouble();
            y = tag.contents().getDouble(1);
            z = tag.contents().getDouble(2);
        } else {
            throw new IllegalArgumentException("tag indexed 0,1,2 must have int or double value");
        }
    }

    public VectorSerializable(TagCompound tag) {
        TagBase tx = tag.contents().getTag("x");
        if (tx instanceof TagInt) {
            x = ((TagInt) tx).getAsInt();
            y = tag.contents().getInt("y");
            z = tag.contents().getInt("z");
        } else if (tx instanceof TagDouble) {
            x = ((TagDouble) tx).getAsDouble();
            y = tag.contents().getDouble("y");
            z = tag.contents().getDouble("z");
        } else {
            throw new IllegalArgumentException("tag named x,y,z must have int or double value");
        }
    }

    public VectorSerializable add(VectorSerializable v) {
        return new VectorSerializable(x + v.x, y + v.y, z + v.z);
    }

    public VectorSerializable subtract(VectorSerializable v) {
        return new VectorSerializable(x - v.x, y - v.y, z - v.z);
    }

    public VectorSerializable multiply(VectorSerializable v) {
        return new VectorSerializable(x * v.x, y * v.y, z * v.z);
    }

    public VectorSerializable divide(VectorSerializable v) {
        return new VectorSerializable(x / v.x, y / v.y, z / v.z);
    }

    public double lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double distanceSquared(VectorSerializable v) {
        double dx = x - v.x, dy = y - v.y, dz = z - v.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public double distance(VectorSerializable v) {
        return Math.sqrt(distanceSquared(v));
    }

    public double dot(VectorSerializable v) {
        return (x * v.x) + (y * v.y) + (z * v.z);
    }

    public double angle(VectorSerializable v) {
        double cos = dot(v) / Math.sqrt(lengthSquared() * v.lengthSquared());
        return Math.acos(cos);
    }

    public VectorSerializable midpoint(VectorSerializable v) {
        return new VectorSerializable((x + v.x) / 2D, (y + v.y) / 2D, (z + v.z) / 2D);
    }

    public VectorSerializable add(double m) {
        return new VectorSerializable(m + x, m + y, m + z);
    }

    public VectorSerializable subtract(double m) {
        return new VectorSerializable(m - x, m - y, m - z);
    }

    public VectorSerializable multiply(double m) {
        return new VectorSerializable(m * x, m * y, m * z);
    }

    public VectorSerializable divide(double m) {
        return new VectorSerializable(x / m, y / m, z / m);
    }

    public VectorSerializable crossProduct(VectorSerializable v) {
        return new VectorSerializable(y * v.z - v.y * z, z * v.z - v.x * z, x * v.y - v.x - y);
    }

    public VectorSerializable normalize() {
        return divide(length());
    }

    public static VectorSerializable zero() {
        if (zero == null) {
            zero = new VectorSerializable(0D, 0D, 0D);
        }
        return zero;
    }

    public boolean isInAABB(VectorSerializable min, VectorSerializable max) {
        return min.x <= x && x <= max.x && min.y <= y && y <= max.y && min.z <= z && z <= max.z;
    }

    public boolean isInSphere(VectorSerializable origin, double radius) {
        return distanceSquared(origin) <= (radius * radius);
    }

    public double getX() {
        return x;
    }

    public int getBlockX() {
        return floor(x);
    }

    public VectorSerializable setX(double nx) {
        return new VectorSerializable(nx, y, z);
    }

    public double getY() {
        return y;
    }

    public int getBlockY() {
        return floor(y);
    }

    public VectorSerializable setY(double ny) {
        return new VectorSerializable(x, ny, z);
    }

    public double getZ() {
        return z;
    }

    public int getBlockZ() {
        return floor(z);
    }

    public VectorSerializable setZ(double nz) {
        return new VectorSerializable(x, y, nz);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VectorSerializable other = (VectorSerializable) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x)
               && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y)
               && Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }

    @Override
    public String toString() {
        char s = ',';
        return new StringBuilder().append(x).append(s).append(y).append(s).append(z).toString();
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public BlockVector toBlockVector() {
        return new BlockVector(x, y, z);
    }

    public TagCompound toTagCompound() {
        return new TagCompound.Builder().append("x", x).append("y", y).append("z", z).build();
    }

    public TagList<TagDouble> toTagList() {
        return new TagList.Builder<>(TagDouble.class).append(x).append(y).append(z).build();
    }

    public TagList<TagInt> toBlockTagList() {
        return new TagList.Builder<>(TagInt.class)
                .append(getBlockX())
                .append(getBlockY())
                .append(getBlockZ()).build();
    }

    public static VectorSerializable getMinimum(VectorSerializable v1, VectorSerializable v2) {
        return new VectorSerializable(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }

    public static VectorSerializable getMaximum(VectorSerializable v1, VectorSerializable v2) {
        return new VectorSerializable(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    private static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

}
