package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Region;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration; // Der korrekte Import für die alte API

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {

    private final MarketPlugin plugin;
    private final Map<String, Region> regions = new HashMap<>();
    private final File configFile;
    private final Configuration config; // Das ist die Konfigurationsklasse von Uberbukkit

    public RegionManager(MarketPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "marketregions.yml");

        // Wir erstellen die Datei, wenn sie nicht existiert.
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                System.err.println("[MarketSystem] Fehler beim Erstellen der marketregions.yml!");
                e.printStackTrace();
            }
        }

        // Wir laden die Konfiguration mit der alten Methode.
        this.config = new Configuration(configFile);
        this.config.load(); // Wichtig: expliziter Lade-Befehl
        loadRegions();
    }

    public void addRegion(Region region) {
        regions.put(region.getName().toLowerCase(), region);
        saveRegions();
    }

    public boolean isLocationInMarketRegion(Location location) {
        // Wir gehen jede einzelne geladene Region durch
        for (Region region : regions.values()) {
            // Zuerst pruefen, ob die Welt uebereinstimmt.
            if (!region.getWorldName().equals(location.getWorld().getName())) {
                continue; // Wenn nicht, ist es diese Region nicht, also zur naechsten.
            }

            int x = location.getBlockX();
            int z = location.getBlockZ();

            // Jetzt pruefen, ob die X- und Z-Koordinaten innerhalb der Region liegen.
            // Da wir x1/z1 immer als die kleinere Koordinate gespeichert haben, ist diese Pruefung einfach.
            if (x >= region.getX1() && x <= region.getX2() && z >= region.getZ1() && z <= region.getZ2()) {
                return true; // Wir haben eine Region gefunden!
            }
        }
        return false; // Keine der Regionen hat gepasst.
    }

    public void removeRegion(String name) {
        // Zuerst aus unserem schnellen Speicher (der Map) entfernen
        regions.remove(name.toLowerCase());

        // Jetzt den kompletten Eintrag aus der Konfiguration entfernen.
        // Das ist der saubere Weg, der keine leeren Titel hinterlaesst.
        config.removeProperty("regions." + name.toLowerCase()); // .toLowerCase() hier ist wichtig für Konsistenz
        config.save();
    }

    public boolean regionExists(String name) {
        return regions.containsKey(name.toLowerCase());
    }

    public void loadRegions() {
        // Wir holen uns eine Liste aller Regionen-Namen.
        List<String> regionNames = config.getKeys("regions");

        if (regionNames == null) {
            System.out.println("[MarketSystem] Keine Regionen zum Laden gefunden.");
            return;
        }

        for (String name : regionNames) {
            // Der Pfad zu den Daten einer einzelnen Region.
            String path = "regions." + name;

            String world = config.getString(path + ".world");
            int x1 = config.getInt(path + ".x1", 0);
            int z1 = config.getInt(path + ".z1", 0);
            int x2 = config.getInt(path + ".x2", 0);
            int z2 = config.getInt(path + ".z2", 0);

            Region region = new Region(name, world, x1, z1, x2, z2);
            regions.put(name.toLowerCase(), region);
        }
        System.out.println("[MarketSystem] " + regions.size() + " Marktregionen erfolgreich geladen!");
    }

    public Region getRegionAt(Location location) {
        for (Region region : regions.values()) {
            if (!region.getWorldName().equals(location.getWorld().getName())) {
                continue;
            }
            int x = location.getBlockX();
            int z = location.getBlockZ();
            if (x >= region.getX1() && x <= region.getX2() && z >= region.getZ1() && z <= region.getZ2()) {
                return region;
            }
        }
        return null; // Wichtig: null zurueckgeben, wenn keine Region gefunden wurde
    }

    public void saveRegions() {
        // Wir gehen alle geladenen Regionen durch und speichern sie.
        for (Region region : regions.values()) {
            String path = "regions." + region.getName();
            config.setProperty(path + ".world", region.getWorldName());
            config.setProperty(path + ".x1", region.getX1());
            config.setProperty(path + ".z1", region.getZ1());
            config.setProperty(path + ".x2", region.getX2());
            config.setProperty(path + ".z2", region.getZ2());
        }

        // Wichtig: expliziter Speicher-Befehl
        config.save();
    }
}