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

public class PlayerProtectionListener extends BlockListener {

    private final RegionManager regionManager;
    private final ShopManager shopManager;

    public PlayerProtectionListener(RegionManager regionManager, ShopManager shopManager) {
        this.regionManager = regionManager;
        this.shopManager = shopManager;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBuildEvent(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    // Ersetze diese Methode in PlayerProtectionListener.java
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        // Schutz für das Schild
        Material type = event.getBlock().getType();
        if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
            if (shopManager.getShopAt(location) != null) {
                player.sendMessage("§c[MarketSystem] §fDieses Schild kann nicht zerstoert werden.");
                event.setCancelled(true);
                return;
            }
        }

        // NEU: Schutz für den Steinblock darunter
        if (shopManager.isShopBaseBlock(location)) {
            player.sendMessage("§c[MarketSystem] §fDieser Block gehoert zu einem Shop und kann nicht zerstoert werden.");
            event.setCancelled(true);
            return;
        }

        handleBuildEvent(player, location, event);
    }

    private void handleBuildEvent(Player player, Location location, org.bukkit.event.Cancellable event) {
        if (player.isOp()) return;

        if (regionManager.isLocationInMarketRegion(location)) {
            Shop shop = shopManager.getShopAt(location);
            if (shop != null) {
                if (shop.getOwner() != null && shop.getOwner().equalsIgnoreCase(player.getName())) {
                    return;
                }
            }
            event.setCancelled(true);
            player.sendMessage("§c[MarketSystem] §fDu kannst hier nicht bauen!");
        }
    }
}