package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopManager {

    private final List<Shop> shops = new ArrayList<>();
    private final File configFile;
    private final Configuration config;

    public ShopManager(MarketPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder(), "marketshops.yml");
        if (!configFile.exists()) {
            try { configFile.createNewFile(); } catch (Exception e) { e.printStackTrace(); }
        }
        this.config = new Configuration(configFile);
        config.load();
        loadShops(); // Ladefunktion direkt beim Start aufrufen
    }

    public void addShop(Shop shop) {
        shops.add(shop);
        saveShops();
    }

    public void saveShops() {
        config.removeProperty("shops");
        int shopId = 0;
        for (Shop shop : shops) {
            String path = "shops." + shopId;
            config.setProperty(path + ".region", shop.getParentRegionName());
            config.setProperty(path + ".world", shop.getWorldName());
            config.setProperty(path + ".x1", shop.getX1());
            config.setProperty(path + ".z1", shop.getZ1());
            config.setProperty(path + ".x2", shop.getX2());
            config.setProperty(path + ".z2", shop.getZ2());
            config.setProperty(path + ".groundY", shop.getGroundY()); // Speichere die Bodenhoehe
            config.setProperty(path + ".owner", shop.getOwner());
            shopId++;
        }
        config.save();
    }

    public void loadShops() {
        shops.clear();
        List<String> shopKeys = config.getKeys("shops");
        if (shopKeys == null) return;

        for (String key : shopKeys) {
            String path = "shops." + key;
            String region = config.getString(path + ".region");
            String world = config.getString(path + ".world");
            int x1 = config.getInt(path + ".x1", 0);
            int z1 = config.getInt(path + ".z1", 0);
            int x2 = config.getInt(path + ".x2", 0);
            int z2 = config.getInt(path + ".z2", 0);
            int groundY = config.getInt(path + ".groundY", 64); // Lade die Bodenhoehe
            String owner = config.getString(path + ".owner", null);

            Shop shop = new Shop(region, world, x1, z1, x2, z2, groundY, owner);
            shops.add(shop);
        }
        System.out.println("[MarketSystem] " + shops.size() + " Shops geladen.");
    }

    // Angepasste Pruef-Methode (ohne Y-Check)
    public Shop getShopAt(Location location) {
        for (Shop shop : shops) {
            if (!shop.getWorldName().equals(location.getWorld().getName())) continue;

            // Wir pruefen nur noch X und Z
            if (location.getBlockX() >= shop.getX1() && location.getBlockX() <= shop.getX2() &&
                    location.getBlockZ() >= shop.getZ1() && location.getBlockZ() <= shop.getZ2()) {
                return shop;
            }
        }
        return null;
    }
}