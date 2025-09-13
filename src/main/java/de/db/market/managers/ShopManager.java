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
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.config = new Configuration(configFile);
        this.config.load();
        // Lade-Funktion kommt spaeter, wenn wir mehr Daten haben
    }

    // Methode zum Finden eines Shops anhand der Location (z.B. des Schilds)
    public Shop getShopAt(Location location) {
        for (Shop shop : shops) {
            if (!shop.getWorldName().equals(location.getWorld().getName())) continue;

            if (location.getBlockX() >= shop.getX1() && location.getBlockX() <= shop.getX2() &&
                    location.getBlockY() >= shop.getY1() && location.getBlockY() <= shop.getY2() &&
                    location.getBlockZ() >= shop.getZ1() && location.getBlockZ() <= shop.getZ2()) {
                return shop;
            }
        }
        return null;
    }

    // Lade-Funktion (jetzt wichtig!)
    public void loadShops() {
        shops.clear();
        List<String> shopKeys = config.getKeys("shops");
        if (shopKeys == null) return;

        for (String key : shopKeys) {
            String path = "shops." + key;
            String region = config.getString(path + ".region");
            String world = config.getString(path + ".world");
            int x1 = config.getInt(path + ".x1", 0);
            int y1 = config.getInt(path + ".y1", 0);
            int z1 = config.getInt(path + ".z1", 0);
            int x2 = config.getInt(path + ".x2", 0);
            int y2 = config.getInt(path + ".y2", 0);
            int z2 = config.getInt(path + ".z2", 0);
            String owner = config.getString(path + ".owner", null); // Wichtig: null als Standard

            Shop shop = new Shop(region, world, x1, y1, z1, x2, y2, z2, owner);
            shops.add(shop);
        }
        System.out.println("[MarketSystem] " + shops.size() + " Shops geladen.");
    }

    public void addShop(Shop shop) {
        shops.add(shop);
        saveShops();
    }

    public void saveShops() {
        // Loeschen, um sauber zu speichern
        config.removeProperty("shops");

        // Wir nutzen eine Zahl als eindeutige ID, da Shops keinen Namen haben
        int shopId = 0;
        for (Shop shop : shops) {
            String path = "shops." + shopId;
            config.setProperty(path + ".region", shop.getParentRegionName());
            config.setProperty(path + ".world", shop.getWorldName());
            config.setProperty(path + ".x1", shop.getX1());
            config.setProperty(path + ".y1", shop.getY1());
            config.setProperty(path + ".z1", shop.getZ1());
            config.setProperty(path + ".x2", shop.getX2());
            config.setProperty(path + ".y2", shop.getY2());
            config.setProperty(path + ".z2", shop.getZ2());
            config.setProperty(path + ".owner", shop.getOwner());
            shopId++;
        }
        config.save();
    }

    // Diese Methode wird spaeter wichtig, um zu pruefen, ob ein Shop schon existiert.
    public boolean isLocationInShop(Location location) {
        for (Shop shop : shops) {
            if (!shop.getWorldName().equals(location.getWorld().getName())) {
                continue;
            }
            if (location.getX() >= shop.getX1() && location.getX() <= shop.getX2() &&
                    location.getY() >= shop.getY1() && location.getY() <= shop.getY2() &&
                    location.getZ() >= shop.getZ1() && location.getZ() <= shop.getZ2()) {
                return true;
            }
        }
        return false;
    }
}