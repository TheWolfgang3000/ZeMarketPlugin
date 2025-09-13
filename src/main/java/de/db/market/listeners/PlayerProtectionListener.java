package de.db.market.listeners;

import de.db.market.data.Shop;
import de.db.market.managers.RegionManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Location;
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
        handleBlockEvent(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockEvent(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    private void handleBlockEvent(Player player, Location location, org.bukkit.event.Cancellable event) {
        if (player.isOp()) return;

        // Ist der Spieler in einer grossen Marktregion?
        if (regionManager.isLocationInMarketRegion(location)) {
            // Ja. Ist er zusaetzlich in einem kleinen Shop?
            Shop shop = shopManager.getShopAt(location);
            if (shop != null) {
                // Ja. Ist er der Besitzer dieses Shops?
                if (shop.getOwner() != null && shop.getOwner().equalsIgnoreCase(player.getName())) {
                    // Ja. Er darf bauen.
                    return;
                }
            }
            // Ansonsten: Er ist in der grossen Region, aber nicht in seinem Shop -> verboten.
            event.setCancelled(true);
            player.sendMessage("§c[MarketSystem] §fDu kannst hier nicht bauen!");
        }
    }
}