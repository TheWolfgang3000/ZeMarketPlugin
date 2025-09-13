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

public class PlayerInteractListener extends PlayerListener {

    private final ShopManager shopManager;
    private final ConfirmationManager confirmationManager;

    public PlayerInteractListener(ShopManager shopManager, ConfirmationManager confirmationManager) {
        this.shopManager = shopManager;
        this.confirmationManager = confirmationManager;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Wir interessieren uns nur fuer Rechtsklicks auf Bloecke
        if (!event.getAction().name().equals("RIGHT_CLICK_BLOCK")) return;

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();

        // Pruefen, ob der Block ein Schild ist
        if (clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) clickedBlock.getState();
            // Pruefen, ob es unser Marktschild ist und zum Verkauf steht
            if (sign.getLine(0).equalsIgnoreCase("[Market]") && sign.getLine(1).contains("For Sale")) {
                Shop shop = shopManager.getShopAt(clickedBlock.getLocation());
                if (shop != null) {
                    confirmationManager.addPendingConfirmation(player, shop);
                }
            }
        }
    }
}