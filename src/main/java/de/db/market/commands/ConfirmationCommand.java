package de.db.market.commands;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            player.sendMessage("§c[MarketSystem] §fDu hast keine offene Anfrage.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("yes")) {
            // FINALE ÄNDERUNG: Prüfen, ob der Spieler bereits einen Shop besitzt
            if (shopManager.hasShop(player.getName())) {
                player.sendMessage("§c[MarketSystem] §fDu besitzt bereits einen Shop!");
                confirmationManager.removePendingConfirmation(player); // Anfrage schließen
                return true;
            }

            Shop shop = confirmationManager.getPendingShop(player);
            shop.setOwner(player.getName());

            // Zurücksetzen auf 30 Tage
            long durationMillis = 30L * 24L * 60L * 60L * 1000L;
            shop.setExpirationTimestamp(System.currentTimeMillis() + durationMillis);

            shopManager.saveShops();
            shopManager.updateSign(shop);

            player.sendMessage("§a[MarketSystem] §fDu hast den Shop fuer 30 Tage gemietet!");
        } else {
            player.sendMessage("§a[MarketSystem] §fDu hast den Mietvorgang abgebrochen.");
        }

        confirmationManager.removePendingConfirmation(player);
        return true;
    }
}