/*
 * Copyright 2017 toyblocks All rights reserved.
 */
package jp.llv.nbt;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author toyblocks
 */
public final class LocationSerializable implements Serializable {
    
    private final UUID world;
    private final double x, y, z;
    private final float yaw, pitch;
    
    public LocationSerializable(UUID world, double x, double y, double z, float yaw, float pitch) {
        Objects.requireNonNull(world);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public LocationSerializable(World world, double x, double y, double z, float yaw, float pitch) {
        this(world.getUID(), x, y, z, yaw, pitch);
    }
    
    public LocationSerializable(UUID world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f);
    }
    
    public LocationSerializable(World world, double x, double y, double z) {
        this(world.getUID(), x, y, z, 0f, 0f);
    }
    
    public LocationSerializable(Location s) {
        this(s.getWorld(), s.getX(), s.getY(), s.getZ(), s.getYaw(), s.getPitch());
    }
    
    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    
    public UUID getWorldUUID() {
        return world;
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
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public VectorSerializable getDirection() {
        double ry = Math.toRadians(yaw), rp = Math.toRadians(pitch);
        double xz = Math.cos(rp);
        return new VectorSerializable(-xz * Math.sin(ry), -Math.sin(rp), xz * Math.cos(ry));
    }
    
    public LocationSerializable setDirection(VectorSerializable v) {
        double pi2 = Math.PI * 2D;
        double vx = v.getX(), vy = v.getY(), vz = v.getZ();
        if (vx == 0D && vz == 0D) {
            return new LocationSerializable(world, x, y, z, yaw, vy > 0 ? -90 : 90);
        } else {
            return new LocationSerializable(world, x, y, z,
                    (float) Math.toDegrees((Math.atan2(-vx, vz) + pi2) % pi2),
                    (float) Math.toDegrees(Math.atan(-vy / Math.sqrt((vx * vx) + (vz * vz))))
            );
        }
    }
    
    public LocationSerializable add(VectorSerializable v) {
        return new LocationSerializable(world, x + v.getX(), y + v.getY(), z + v.getZ(), yaw, pitch);
    }
    
    public LocationSerializable subtract(VectorSerializable v) {
        return new LocationSerializable(world, x - v.getX(), y - v.getY(), z - v.getZ(), yaw, pitch);
    }
    
    public VectorSerializable subtract(LocationSerializable l) {
        if (!l.world.equals(world)) {
            throw new IllegalArgumentException("Not in the same world");
        }
        return new VectorSerializable(x - l.getX(), y - l.getY(), z - l.getZ());
    }
    
    public double lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }
    
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    
    public double distanceSquared(LocationSerializable o) {
        if (!o.world.equals(world)) {
            throw new IllegalArgumentException("Not in the same world");
        }
        double dx = x - o.x, dy = y - o.y, dz = z - o.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }
    
    public double distance(LocationSerializable o) {
        return Math.sqrt(distanceSquared(o));
    }
    
    public static LocationSerializable zero(World world) {
        return new LocationSerializable(world, 0d, 0d, 0d, 0f, 0f);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.world);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 61 * hash + Float.floatToIntBits(this.yaw);
        hash = 61 * hash + Float.floatToIntBits(this.pitch);
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
        LocationSerializable other = (LocationSerializable) obj;
        return Objects.equals(world, other.world)
               && Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
               && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y)
               && Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z)
               && Float.floatToIntBits(yaw) == Float.floatToIntBits(other.yaw)
               && Float.floatToIntBits(pitch) == Float.floatToIntBits(other.pitch);
    }
    
    @Override
    public String toString() {
        return "Location{" + "world=" + world + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
    }
    
    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    private static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
    
}
