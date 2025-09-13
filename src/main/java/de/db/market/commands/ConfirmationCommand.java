package de.db.market.commands;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for the /yes and /no commands, used to confirm or deny shop rentals.
 */
public class ConfirmationCommand implements CommandExecutor {

    private final MarketPlugin plugin;
    private final ConfirmationManager confirmationManager;
    private final ShopManager shopManager;

    public ConfirmationCommand(MarketPlugin plugin, ConfirmationManager confirmationManager, ShopManager shopManager) {
        this.plugin = plugin;
        this.confirmationManager = confirmationManager;
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!confirmationManager.hasPendingConfirmation(player)) {
            player.sendMessage("§c[MarketSystem] §fYou do not have a pending request.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("yes")) {
            // Check if the player already owns a shop before renting a new one.
            if (shopManager.hasShop(player.getName())) {
                player.sendMessage("§c[MarketSystem] §fYou already own a shop!");
                confirmationManager.removePendingConfirmation(player);
                return true;
            }

            Shop shop = confirmationManager.getPendingShop(player);
            shop.setOwner(player.getName());

            // Set the lease duration to 30 days.
            long durationMillis = 30L * 24L * 60L * 60L * 1000L;
            shop.setExpirationTimestamp(System.currentTimeMillis() + durationMillis);

            shopManager.saveShops();
            shopManager.updateSign(shop);

            player.sendMessage("§a[MarketSystem] §fYou have successfully rented the shop for 30 days!");
        } else {
            // This block handles the /no command.
            player.sendMessage("§a[MarketSystem] §fYou have cancelled the rental process.");
        }

        // Always remove the pending confirmation after /yes or /no is used.
        confirmationManager.removePendingConfirmation(player);
        return true;
    }
}