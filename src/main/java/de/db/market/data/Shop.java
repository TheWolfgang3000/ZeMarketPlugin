package de.db.market.data;

import org.bukkit.Location;

/**
 * A data class representing a single, rentable shop plot within a parent Market Region.
 */
public class Shop {

    private final String parentRegionName;
    private final String worldName;
    private final int x1, z1; // Min coordinates
    private final int x2, z2; // Max coordinates
    private final int groundY; // The Y-level of the shop's floor, used for placing the sign.

    private String owner;
    private long expirationTimestamp; // The exact time the lease expires, in milliseconds.

    /**
     * Constructor for creating a new shop from in-game selections.
     */
    public Shop(String parentRegionName, Location pos1, Location pos2) {
        this.parentRegionName = parentRegionName;
        this.worldName = pos1.getWorld().getName();
        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.groundY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.owner = null;
        this.expirationTimestamp = 0; // 0 indicates the shop is not rented.
    }

    /**
     * Constructor for loading a shop from the configuration file.
     */
    public Shop(String parentRegionName, String worldName, int x1, int z1, int x2, int z2, int groundY, String owner, long expirationTimestamp) {
        this.parentRegionName = parentRegionName;
        this.worldName = worldName;
        this.x1 = x1; this.z1 = z1;
        this.x2 = x2; this.z2 = z2;
        this.groundY = groundY;
        this.owner = owner;
        this.expirationTimestamp = expirationTimestamp;
    }

    // --- Getters & Setters ---
    public String getParentRegionName() { return parentRegionName; }
    public String getWorldName() { return worldName; }
    public int getX1() { return x1; }
    public int getZ1() { return z1; }
    public int getX2() { return x2; }
    public int getZ2() { return z2; }
    public int getGroundY() { return groundY; }
    public String getOwner() { return owner; }
    public long getExpirationTimestamp() { return expirationTimestamp; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setExpirationTimestamp(long expirationTimestamp) { this.expirationTimestamp = expirationTimestamp; }
}