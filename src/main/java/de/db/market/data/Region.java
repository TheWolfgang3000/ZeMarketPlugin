package de.db.market.data;

import org.bukkit.Location;

/**
 * A data class representing a large, protected market region.
 * Protection is based on X and Z coordinates, covering the full world height (0-128).
 */
public class Region {

    private final String name;
    private final String worldName;
    private final int x1, z1; // Min coordinates
    private final int x2, z2; // Max coordinates

    /**
     * Constructor used when creating a new region from in-game locations.
     * It automatically calculates the min/max corner points.
     * @param name The unique name of the region.
     * @param pos1 The first corner location selected by an admin.
     * @param pos2 The second corner location selected by an admin.
     */
    public Region(String name, Location pos1, Location pos2) {
        this.name = name;
        this.worldName = pos1.getWorld().getName();

        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    /**
     * Constructor used when loading region data from a configuration file.
     */
    public Region(String name, String worldName, int x1, int z1, int x2, int z2) {
        this.name = name;
        this.worldName = worldName;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getWorldName() { return worldName; }
    public int getX1() { return x1; }
    public int getZ1() { return z1; }
    public int getX2() { return x2; }
    public int getZ2() { return z2; }
}