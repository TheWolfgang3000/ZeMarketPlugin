package de.db.market.listeners;

import de.db.market.data.Shop;
import de.db.market.managers.RegionManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listens for block break and place events to enforce protection rules
 * in market regions and owned shops.
 */
public class PlayerProtectionListener extends BlockListener {

    private final RegionManager regionManager;
    private final ShopManager shopManager;

    public PlayerProtectionListener(RegionManager regionManager, ShopManager shopManager) {
        this.regionManager = regionManager;
        this.shopManager = shopManager;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return;

        Location location = event.getBlock().getLocation();
        if (regionManager.isLocationInMarketRegion(location)) {
            Shop shop = shopManager.getShopAt(location);
            if (shop != null) {
                // Allow building if the player is the owner of this shop.
                if (shop.getOwner() != null && shop.getOwner().equalsIgnoreCase(player.getName())) {
                    return;
                }
            }
            // If not the owner or not in a shop plot, cancel the event.
            event.setCancelled(true);
            player.sendMessage("§c[MarketSystem] §fYou cannot build here!");
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        // First, check for special protected blocks (signs and their bases).
        Material type = event.getBlock().getType();
        if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
            if (shopManager.getShopAt(location) != null) {
                player.sendMessage("§c[MarketSystem] §fThis sign cannot be destroyed.");
                event.setCancelled(true);
                return;
            }
        }
        if (shopManager.isShopBaseBlock(location)) {
            player.sendMessage("§c[MarketSystem] §fThis block is part of a shop and cannot be destroyed.");
            event.setCancelled(true);
            return;
        }

        // General build protection for non-OP players.
        if (player.isOp()) return;

        if (regionManager.isLocationInMarketRegion(location)) {
            Shop shop = shopManager.getShopAt(location);
            if (shop != null) {
                // Allow breaking blocks if the player is the owner.
                if (shop.getOwner() != null && shop.getOwner().equalsIgnoreCase(player.getName())) {
                    return;
                }
            }
            // If not the owner or not in a shop plot, cancel the event.
            event.setCancelled(true);
            player.sendMessage("§c[MarketSystem] §fYou cannot break blocks here!");
        }
    }
}