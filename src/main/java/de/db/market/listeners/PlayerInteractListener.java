package de.db.market.listeners;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Listens for player interaction events, specifically right-clicking on shop signs.
 */
public class PlayerInteractListener extends PlayerListener {

    private final MarketPlugin plugin;
    private final ShopManager shopManager;
    private final ConfirmationManager confirmationManager;

    public PlayerInteractListener(MarketPlugin plugin, ShopManager shopManager, ConfirmationManager confirmationManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
        this.confirmationManager = confirmationManager;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        // We only care about right-clicking a block.
        if (!event.getAction().name().equals("RIGHT_CLICK_BLOCK")) return;

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();

        // Check if the clicked block is a sign.
        if (clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN) {
            Shop shop = shopManager.getShopAt(clickedBlock.getLocation());
            // If the sign is not part of a shop, ignore it.
            if (shop == null) return;

            Sign sign = (Sign) clickedBlock.getState();
            // Ensure it's a market sign to avoid conflicts with other plugins.
            if (!sign.getLine(0).equalsIgnoreCase("[Market]")) return;

            // Case 1: The shop is available for rent.
            if (shop.getOwner() == null) {
                confirmationManager.addPendingConfirmation(player, shop);
                return;
            }

            // Case 2: The player is the owner and clicks their own sign to renew the lease.
            if (shop.getOwner().equalsIgnoreCase(player.getName())) {
                long durationMillis = 30L * 24L * 60L * 60L * 1000L; // Renew for 30 days
                shop.setExpirationTimestamp(System.currentTimeMillis() + durationMillis);
                shopManager.saveShops();
                shopManager.updateSign(shop);
                player.sendMessage("§a[MarketSystem] §fYou have renewed your shop lease for 30 days!");
            }
        }
    }
}