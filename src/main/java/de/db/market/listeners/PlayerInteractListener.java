package de.db.market.listeners;

import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.concurrent.TimeUnit;

public class PlayerInteractListener extends PlayerListener {

    private final ShopManager shopManager;
    private final ConfirmationManager confirmationManager;

    public PlayerInteractListener(ShopManager shopManager, ConfirmationManager confirmationManager) {
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

            // Fall 1: Der Shop ist frei -> Mietprozess starten
            if (shop.getOwner() == null) {
                confirmationManager.addPendingConfirmation(player, shop);
                return;
            }

            // Fall 2: Der Spieler ist der Besitzer -> Miete verlaengern
            if (shop.getOwner().equalsIgnoreCase(player.getName())) {
                long durationMillis = TimeUnit.MINUTES.toMillis(2);
                shop.setExpirationTimestamp(System.currentTimeMillis() + durationMillis);
                shopManager.saveShops();
                shopManager.updateSign(shop);
                player.sendMessage("§a[MarketSystem] §fDu hast deinen Shop um 30 Tage verlaengert!");
            }
        }
    }
}