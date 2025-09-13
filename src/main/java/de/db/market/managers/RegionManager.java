package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Region;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the loading, saving, and querying of all Market Regions.
 */
public class RegionManager {

    private final MarketPlugin plugin;
    private final Map<String, Region> regions = new HashMap<>(); // In-memory cache of regions for fast lookups.
    private final File configFile;
    private final Configuration config;

    public RegionManager(MarketPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "marketregions.yml");
        if (!configFile.exists()) {
            try { configFile.createNewFile(); } catch (Exception e) { e.printStackTrace(); }
        }
        this.config = new Configuration(configFile);
        config.load();
        loadRegions();
    }

    /**
     * Adds a new region to the manager and saves it to file.
     * @param region The Region object to add.
     */
    public void addRegion(Region region) {
        regions.put(region.getName().toLowerCase(), region);
        saveRegions();
    }

    /**
     * Removes a region from the manager and the configuration file.
     * @param name The name of the region to remove.
     */
    public void removeRegion(String name) {
        regions.remove(name.toLowerCase());
        config.removeProperty("regions." + name.toLowerCase());
        config.save();
    }

    /**
     * Checks if a region with the given name exists.
     * @param name The name to check.
     * @return True if the region exists, false otherwise.
     */
    public boolean regionExists(String name) {
        return regions.containsKey(name.toLowerCase());
    }

    /**
     * Loads all regions from the marketregions.yml file into memory.
     */
    public void loadRegions() {
        List<String> regionNames = config.getKeys("regions");
        if (regionNames == null) {
            System.out.println("[MarketSystem] No market regions found to load.");
            return;
        }

        for (String name : regionNames) {
            String path = "regions." + name;
            String world = config.getString(path + ".world");
            int x1 = config.getInt(path + ".x1", 0);
            int z1 = config.getInt(path + ".z1", 0);
            int x2 = config.getInt(path + ".x2", 0);
            int z2 = config.getInt(path + ".z2", 0);

            Region region = new Region(name, world, x1, z1, x2, z2);
            regions.put(name.toLowerCase(), region);
        }
        System.out.println("[MarketSystem] Loaded " + regions.size() + " market regions.");
    }

    /**
     * Saves all in-memory regions to the marketregions.yml file.
     */
    public void saveRegions() {
        config.removeProperty("regions");
        for (Region region : regions.values()) {
            String path = "regions." + region.getName();
            config.setProperty(path + ".world", region.getWorldName());
            config.setProperty(path + ".x1", region.getX1());
            config.setProperty(path + ".z1", region.getZ1());
            config.setProperty(path + ".x2", region.getX2());
            config.setProperty(path + ".z2", region.getZ2());
        }
        config.save();
    }

    /**
     * Checks if a given location is within any market region.
     * @param location The location to check.
     * @return True if the location is inside a region, false otherwise.
     */
    public boolean isLocationInMarketRegion(Location location) {
        return getRegionAt(location) != null;
    }

    /**
     * Retrieves the Region object at a specific location.
     * @param location The location to check.
     * @return The Region object, or null if no region is found at that location.
     */
    public Region getRegionAt(Location location) {
        for (Region region : regions.values()) {
            if (!region.getWorldName().equals(location.getWorld().getName())) {
                continue; // Skip if in a different world
            }
            int x = location.getBlockX();
            int z = location.getBlockZ();
            if (x >= region.getX1() && x <= region.getX2() && z >= region.getZ1() && z <= region.getZ2()) {
                return region;
            }
        }
        return null;
    }
}