package de.db.market.data;

import org.bukkit.Location;

public class Shop {

    private final String parentRegionName;
    private final String worldName;
    private final int x1, z1; // Kleinere Koordinaten
    private final int x2, z2; // Groessere Koordinaten
    private final int groundY; // Wir merken uns nur die Bodenhoehe fuer das Schild etc.

    private String owner;

    // Konstruktor zum Erstellen eines neuen Shops
    public Shop(String parentRegionName, Location pos1, Location pos2) {
        this.parentRegionName = parentRegionName;
        this.worldName = pos1.getWorld().getName();

        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        // Wir nehmen die niedrigere der beiden Y-Koordinaten als "Boden"
        this.groundY = Math.min(pos1.getBlockY(), pos2.getBlockY());

        this.owner = null;
    }

    // Konstruktor zum Laden eines Shops aus der Konfigurationsdatei
    public Shop(String parentRegionName, String worldName, int x1, int z1, int x2, int z2, int groundY, String owner) {
        this.parentRegionName = parentRegionName;
        this.worldName = worldName;
        this.x1 = x1; this.z1 = z1;
        this.x2 = x2; this.z2 = z2;
        this.groundY = groundY;
        this.owner = owner;
    }

    // --- Getter-Methoden ---
    public String getParentRegionName() { return parentRegionName; }
    public String getWorldName() { return worldName; }
    public int getX1() { return x1; }
    public int getZ1() { return z1; }
    public int getX2() { return x2; }
    public int getZ2() { return z2; }
    public int getGroundY() { return groundY; }
    public String getOwner() { return owner; }

    // --- Setter-Methoden ---
    public void setOwner(String owner) { this.owner = owner; }
}