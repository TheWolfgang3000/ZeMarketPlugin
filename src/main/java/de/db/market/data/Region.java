package de.db.market.data;

import org.bukkit.Location;

public class Region {

    private String name;
    private String worldName;
    private int x1, z1; // Kleinere Koordinaten
    private int x2, z2; // Groessere Koordinaten

    // Dieser Konstruktor wird verwendet, wenn wir eine Region aus dem Spiel heraus erstellen.
    public Region(String name, Location pos1, Location pos2) {
        this.name = name;
        this.worldName = pos1.getWorld().getName();

        // Stellt sicher, dass x1/z1 immer die kleineren Werte sind.
        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    // Dieser Konstruktor wird verwendet, wenn wir die Daten aus der Konfigurationsdatei laden.
    public Region(String name, String worldName, int x1, int z1, int x2, int z2) {
        this.name = name;
        this.worldName = worldName;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    // --- Getter-Methoden ---

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX1() {
        return x1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getZ2() {
        return z2;
    }
}