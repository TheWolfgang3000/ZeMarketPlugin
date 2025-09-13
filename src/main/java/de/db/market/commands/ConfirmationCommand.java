package de.db.market.commands;

import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ConfirmationCommand implements CommandExecutor {

    private final ConfirmationManager confirmationManager;
    private final ShopManager shopManager;

    public ConfirmationCommand(ConfirmationManager confirmationManager, ShopManager shopManager) {
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

        Shop shop = confirmationManager.getPendingShop(player);

        if (command.getName().equalsIgnoreCase("yes")) {
            shop.setOwner(player.getName());

            // Setze den Timer auf 30 Tage in der Zukunft
            long durationMillis = TimeUnit.MINUTES.toMillis(2);
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