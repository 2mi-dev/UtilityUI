package navy.otter.UtilityUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class UtilityUI extends JavaPlugin implements CommandExecutor {

  private HashMap<Player, Inventory> brewingMap;

  @Override
  public void onEnable() {
    getLogger().info("UtilityUI enabled");
    this.getCommand("uui").setExecutor(this);
    this.getCommand("uui").setTabCompleter(new EmptyTabCompleter());
    this.brewingMap = new HashMap<>();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    final Player player = (Player) sender;
    if (!player.hasPermission("")) {
      return false;
    }

    Iterator<String> arg = Arrays.asList(args).iterator();
    String option = arg.hasNext() ? arg.next() : "";
    String verifier = arg.hasNext() ? arg.next() : "";

    boolean result = false;

    switch (option.toLowerCase()) {
      case "":
        result = false;
        break;
      case "debug":
        if (player.hasPermission("UtilityUI.debugstick")) {
          result = giveDebugStick(player);
        }
        break;
      case "cmd":
        if (player.hasPermission("UtilityUI.commandblock")) {
          result = giveCommandBlock(player);
        }
        break;
      case "anvil":
        if (player.hasPermission("UtilityUI.anvil")) {
          result = showAnvilGui(player);
        }
        break;
      case "brew":
        if (player.hasPermission("UtilityUI.brewing")) {
          result = showBrewingGui(player);
        }
        break;
      case "wb":
        if (player.hasPermission("UtilityUI.workbench")) {
          result = showWorkbenchGui(player);
        }
        break;
      case "creative":
        if (player.hasPermission("UtilityUI.creative")) {
          result = showCreativeInventoryGui(player);
        }
        break;
      default:
        player.sendMessage(ChatColor.RED + "Der eingegebene Befehl existiert nicht.");
    }
    return result;
  }

  public boolean giveDebugStick(Player player) {
    if (player != null) {
      PlayerInventory playerinventory = player.getInventory();
      if (checkForEmptySpace(playerinventory)) {
        playerinventory.addItem(new ItemStack(Material.DEBUG_STICK, 1));
        return true;
      } else {
        String sb = ChatColor.AQUA +
            "UtilityUI: " +
            ChatColor.RED +
            "Dein Inventar ist voll!";
        player.sendMessage(sb);
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean giveCommandBlock(Player player) {
    if (player != null) {
      PlayerInventory playerinventory = player.getInventory();
      if (checkForEmptySpace(playerinventory)) {
        playerinventory.addItem(new ItemStack(Material.COMMAND_BLOCK, 1));
        return true;
      } else {
        String sb = ChatColor.AQUA +
            "UtilityUI: " +
            ChatColor.RED +
            "Dein Inventar ist voll!";
        player.sendMessage(sb);
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean showAnvilGui(Player player) {
    if (player != null) {
      Bukkit.createInventory(player, InventoryType.ANVIL);
      return true;
    } else {
      return false;
    }
  }

  public boolean showWorkbenchGui(Player player) {
    if (player != null) {
      player.openWorkbench(null, true);
      return true;
    } else {
      return false;
    }
  }

  public boolean showBrewingGui(Player player) {
    if (player != null) {
      if (brewingMap.get(player) == null) {
        Inventory brewing = Bukkit.createInventory(player, InventoryType.BREWING);
        brewingMap.put(player, brewing);
      } else {
        Inventory brewing = brewingMap.get(player);
        player.openInventory(brewing);
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean showCreativeInventoryGui(Player player) {
    if (player != null) {
      Bukkit.createInventory(player, InventoryType.CREATIVE);
      return true;
    } else {
      return false;
    }
  }

  public boolean checkForEmptySpace(PlayerInventory inventory) {
    for (ItemStack item : inventory.getContents()) {
      if (item == null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onDisable() {
    getLogger().info("UtilityUI disabled");
  }
}
