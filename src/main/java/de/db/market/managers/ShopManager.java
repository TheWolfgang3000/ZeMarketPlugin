package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShopManager {

    private final MarketPlugin plugin;
    private final List<Shop> shops = new ArrayList<>();
    private final File configFile;
    private final Configuration config;

    public ShopManager(MarketPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "marketshops.yml");
        if (!configFile.exists()) {
            try { configFile.createNewFile(); } catch (Exception e) { e.printStackTrace(); }
        }
        this.config = new Configuration(configFile);
        config.load();
        loadShops();
    }

    public void addShop(Shop shop) {
        shops.add(shop);
        saveShops();
    }

    public List<Shop> getAllShops() {
        return new ArrayList<>(shops);
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
            config.setProperty(path + ".groundY", shop.getGroundY());
            config.setProperty(path + ".owner", shop.getOwner());
            config.setProperty(path + ".expiration", String.valueOf(shop.getExpirationTimestamp()));
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
            int groundY = config.getInt(path + ".groundY", 64);
            String owner = config.getString(path + ".owner", null);
            long expiration = Long.parseLong(config.getString(path + ".expiration", "0"));

            Shop shop = new Shop(region, world, x1, z1, x2, z2, groundY, owner, expiration);
            shops.add(shop);
        }
        System.out.println("[MarketSystem] " + shops.size() + " Shops geladen.");
    }

    public Shop getShopAt(Location location) {
        for (Shop shop : shops) {
            if (!shop.getWorldName().equals(location.getWorld().getName())) continue;
            if (location.getBlockX() >= shop.getX1() && location.getBlockX() <= shop.getX2() &&
                    location.getBlockZ() >= shop.getZ1() && location.getBlockZ() <= shop.getZ2()) {
                return shop;
            }
        }
        return null;
    }

    public boolean isShopBaseBlock(Location location) {
        for (Shop shop : shops) {
            if (shop.getWorldName().equals(location.getWorld().getName()) &&
                    shop.getX1() == location.getBlockX() &&
                    shop.getGroundY() - 1 == location.getBlockY() &&
                    shop.getZ1() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public void updateSign(Shop shop) {
        Block signBlock = plugin.getServer().getWorld(shop.getWorldName()).getBlockAt(shop.getX1(), shop.getGroundY(), shop.getZ1());
        if (signBlock.getType() != Material.SIGN_POST && signBlock.getType() != Material.WALL_SIGN) return;

        Sign sign = (Sign) signBlock.getState();
        if (shop.getOwner() != null) {
            long remainingMillis = shop.getExpirationTimestamp() - System.currentTimeMillis();
            long remainingDays = Math.max(0, TimeUnit.MILLISECONDS.toDays(remainingMillis));

            sign.setLine(0, "[Market]");
            sign.setLine(1, "§c" + shop.getOwner());
            sign.setLine(2, "Tage: " + remainingDays);
            sign.setLine(3, "");
        } else {
            sign.setLine(0, "[Market]");
            sign.setLine(1, "§aFor Sale");
            sign.setLine(2, "");
            sign.setLine(3, "");
        }
        sign.update();
    }

    public void resetShop(Shop shop) {
        shop.setOwner(null);
        shop.setExpirationTimestamp(0);

        Location corner = new Location(plugin.getServer().getWorld(shop.getWorldName()), shop.getX1(), shop.getGroundY(), shop.getZ1());
        for (int x = shop.getX1(); x <= shop.getX2(); x++) {
            for (int z = shop.getZ1(); z <= shop.getZ2(); z++) {
                for (int y = shop.getGroundY(); y < shop.getGroundY() + 10; y++) {
                    if (y > 127) break;
                    corner.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        Block baseBlock = corner.getWorld().getBlockAt(shop.getX1(), shop.getGroundY() - 1, shop.getZ1());
        baseBlock.setType(Material.STONE);

        updateSign(shop);
        saveShops();
    }

    // --- NEUE LÖSCH-METHODEN ---

    public void removeShop(Shop shop) {
        removeShopBlocks(shop);
        shops.remove(shop);
        saveShops();
    }

    public void removeShopsInRegion(String regionName) {
        Iterator<Shop> iterator = shops.iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            Shop shop = iterator.next();
            if (shop.getParentRegionName().equalsIgnoreCase(regionName)) {
                removeShopBlocks(shop);
                iterator.remove();
                changed = true;
            }
        }
        if (changed) {
            saveShops();
        }
    }

    private void removeShopBlocks(Shop shop) {
        World world = plugin.getServer().getWorld(shop.getWorldName());
        if (world == null) return;

        world.getBlockAt(shop.getX1(), shop.getGroundY(), shop.getZ1()).setType(Material.AIR);
        world.getBlockAt(shop.getX1(), shop.getGroundY() - 1, shop.getZ1()).setType(Material.AIR);
    }
}