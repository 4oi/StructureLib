/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.World;

/**
 *
 * @author toyblocks
 */
public final class CuboidSerializable implements Serializable, Iterable<LocationSerializable> {

    private final LocationSerializable origin;
    private final VectorSerializable size;

    public CuboidSerializable(UUID world, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.origin = new LocationSerializable(world, Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.size = new VectorSerializable(Math.abs(x1 - x2), Math.abs(y1 - y2), Math.abs(z1 - z2));
    }

    public CuboidSerializable(LocationSerializable o, VectorSerializable s) {
        this.origin = new LocationSerializable(o.getWorldUUID(),
                o.getBlockX() + (s.getBlockX() >= 0 ? 0 : s.getBlockX()),
                o.getBlockY() + (s.getBlockY() >= 0 ? 0 : s.getBlockY()),
                o.getBlockZ() + (s.getBlockZ() >= 0 ? 0 : s.getBlockZ())
        );
        this.size = new VectorSerializable(
                Math.abs(s.getX()),
                Math.abs(s.getY()),
                Math.abs(s.getZ())
        );
    }

    public CuboidSerializable(LocationSerializable o1, LocationSerializable o2) {
        this(o1, o2.subtract(o1));
        if (!o1.getWorldUUID().equals(o2.getWorldUUID())) {
            throw new IllegalArgumentException("Not in the same world");
        }
    }

    public int size() {
        return size.getBlockX() * size.getBlockY() * size.getBlockZ();
    }

    public VectorSerializable getSize() {
        return this.size;
    }

    public VectorSerializable getBlockSize() {
        return new VectorSerializable(
                getBlockWidth(),
                getBlockHeight(),
                getBlockDepth()
        );
    }

    public double getWidth() {
        return size.getX();
    }

    public double getHeight() {
        return size.getY();
    }

    public double getDepth() {
        return size.getZ();
    }

    public int getBlockWidth() {
        return size.getBlockX() + 1;
    }

    public int getBlockHeight() {
        return size.getBlockY() + 1;
    }

    public int getBlockDepth() {
        return size.getBlockZ() + 1;
    }

    public LocationSerializable getOrigin() {
        return this.origin;
    }
    
    public LocationSerializable getCorner() {
        return new LocationSerializable(origin.getWorldUUID(), getX2(), getY2(), getZ2());
    }

    public double getX1() {
        return origin.getX();
    }

    public double getY1() {
        return origin.getY();
    }

    public double getZ1() {
        return origin.getZ();
    }

    public double getX2() {
        return origin.getX() + size.getX();
    }

    public double getY2() {
        return origin.getY() + size.getY();
    }

    public double getZ2() {
        return origin.getZ() + size.getZ();
    }

    public int getBlockX1() {
        return origin.getBlockX();
    }

    public int getBlockY1() {
        return origin.getBlockY();
    }

    public int getBlockZ1() {
        return origin.getBlockZ();
    }

    public int getBlockX2() {
        return origin.getBlockX() + size.getBlockX();
    }

    public int getBlockY2() {
        return origin.getBlockY() + size.getBlockY();
    }

    public int getBlockZ2() {
        return origin.getBlockZ() + size.getBlockZ();
    }

    public World getWorld() {
        return origin.getWorld();
    }

    public UUID getWorldUUID() {
        return origin.getWorldUUID();
    }

    public boolean isIn(LocationSerializable loc) {
        return getX1() <= loc.getX()
               && getY1() <= loc.getY()
               && getZ1() <= loc.getZ()
               && loc.getX() <= getX2()
               && loc.getY() <= getY2()
               && loc.getZ() <= getZ2();
    }

    public boolean isBlockIn(LocationSerializable loc) {
        return getBlockX1() <= loc.getBlockX()
               && getBlockY1() <= loc.getBlockY()
               && getBlockZ1() <= loc.getBlockZ()
               && loc.getBlockX() <= getBlockX2()
               && loc.getBlockY() <= getBlockY2()
               && loc.getBlockZ() <= getBlockZ2();
    }

    @Override
    public Iterator<LocationSerializable> iterator() {
        class CuboidIterator implements Iterator<LocationSerializable> {

            int cx = getBlockX1() - 1, cy = getBlockY1(), cz = getBlockZ1();

            @Override
            public boolean hasNext() {
                return cy != getBlockY2() || cz != getBlockZ2() || cx != getBlockX2();
            }

            @Override
            public LocationSerializable next() {
                if (++cx > getBlockX2()) {
                    cx = getBlockX1();
                    if (++cz > getBlockZ2()) {
                        cz = getBlockZ1();
                        if (++cy > getBlockY2()) {
                            throw new IllegalStateException("Iteration finished");
                        }
                    }
                }

                return new LocationSerializable(origin.getWorldUUID(), cx, cy, cz);
            }

        }
        return new CuboidIterator();
    }

}
