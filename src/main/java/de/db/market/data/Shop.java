package de.db.market.data;

import org.bukkit.Location;

public class Shop {

    private final String parentRegionName;
    private final String worldName;
    private final int x1, y1, z1; // Kleinere Koordinaten
    private final int x2, y2, z2; // Groessere Koordinaten

    private String owner; // Spielername des Besitzers

    // Konstruktor zum Erstellen eines neuen Shops
    public Shop(String parentRegionName, Location pos1, Location pos2) {
        this.parentRegionName = parentRegionName;
        this.worldName = pos1.getWorld().getName();

        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.y1 = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.y2 = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        this.owner = null; // Am Anfang hat der Shop keinen Besitzer
    }

    // Konstruktor zum Laden eines Shops aus der Konfigurationsdatei
    public Shop(String parentRegionName, String worldName, int x1, int y1, int z1, int x2, int y2, int z2, String owner) {
        this.parentRegionName = parentRegionName;
        this.worldName = worldName;
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
        this.owner = owner;
    }

    // --- Getter-Methoden ---

    public String getParentRegionName() {
        return parentRegionName;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getZ1() { return z1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
    public int getZ2() { return z2; }

    public String getOwner() {
        return owner;
    }

    // --- Setter-Methoden ---

    public void setOwner(String owner) {
        this.owner = owner;
    }
}