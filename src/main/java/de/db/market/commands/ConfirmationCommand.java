package de.db.market.commands;

import de.db.market.data.Shop;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            // Logik fuer /yes
            shop.setOwner(player.getName());
            shopManager.saveShops(); // Wichtig: Aenderung speichern

            // Schild aktualisieren
            Block signBlock = player.getWorld().getBlockAt(shop.getX1(), shop.getY1(), shop.getZ1());
            if (signBlock.getType() == Material.SIGN_POST || signBlock.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) signBlock.getState();
                sign.setLine(1, "§c" + player.getName());
                sign.setLine(2, "Besetzt");
                sign.update();
            }

            player.sendMessage("§a[MarketSystem] §fDu hast den Shop erfolgreich gemietet!");
        } else {
            // Logik fuer /no
            player.sendMessage("§a[MarketSystem] §fDu hast den Mietvorgang abgebrochen.");
        }

        confirmationManager.removePendingConfirmation(player);
        return true;
    }
}