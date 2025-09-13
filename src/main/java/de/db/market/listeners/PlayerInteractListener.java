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

// import java.util.concurrent.TimeUnit; // Entfernt

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
        if (!event.getAction().name().equals("RIGHT_CLICK_BLOCK")) return;

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();

        if (clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN) {
            Shop shop = shopManager.getShopAt(clickedBlock.getLocation());
            if (shop == null) return;

            Sign sign = (Sign) clickedBlock.getState();
            if (!sign.getLine(0).equalsIgnoreCase("[Market]")) return;

            if (shop.getOwner() == null) {
                confirmationManager.addPendingConfirmation(player, shop);
                return;
            }

            if (shop.getOwner().equalsIgnoreCase(player.getName())) {
                // KORREKTUR: Manuelle Zeitberechnung statt TimeUnit
                long durationMillis = 30L * 24L * 60L * 60L * 1000L; // 30 Tage in Millisekunden
                shop.setExpirationTimestamp(System.currentTimeMillis() + durationMillis);
                shopManager.saveShops();
                shopManager.updateSign(shop);
                player.sendMessage("§a[MarketSystem] §fDu hast deinen Shop um 2 Minuten verlaengert!");
            }
        }
    }
}