package navy.otter.UtilityUI;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Set;
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

  private final HashMap<Player, Inventory> brewingMap = new HashMap<>();
  private final HashMap<Player, HashMap<String, Inventory>> playerCustomChests = new HashMap<>();

  @Override
  public void onEnable() {
    this.getCommand("uui").setExecutor(this);
    this.getCommand("uui").setTabCompleter(new UtiltiyTabCompleter());
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    final Player player = (Player) sender;

    Iterator<String> arg = Arrays.asList(args).iterator();
    String option = arg.hasNext() ? arg.next() : "";
    String argument = arg.hasNext() ? arg.next() : "";
    String detail = arg.hasNext() ? arg.next() : "";

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
      case "chests":
        if (player.hasPermission("UtilityUI.chests")) {
          result = chestManager(player, argument, detail);
        }
        break;
      default:
        player.sendMessage(ChatColor.RED + "Der eingegebene Befehl existiert nicht!");
    }
    return result;
  }

  public boolean giveDebugStick(Player player) {
    PlayerInventory playerinventory = player.getInventory();
    if (hasFreeSlot(playerinventory)) {
      playerinventory.addItem(new ItemStack(Material.DEBUG_STICK, 1));
      return true;
    } else {
      String sb = ChatColor.AQUA
          + "[UtilityUI] "
          + ChatColor.RED
          + "Dein Inventar ist voll!";
      player.sendMessage(sb);
      return false;
    }
  }

  public boolean giveCommandBlock(Player player) {
    PlayerInventory playerinventory = player.getInventory();
    if (hasFreeSlot(playerinventory)) {
      playerinventory.addItem(new ItemStack(Material.COMMAND_BLOCK, 1));
      return true;
    } else {
      String sb = ChatColor.AQUA
          + "[UtilityUI] "
          + ChatColor.RED
          + "Dein Inventar ist voll!";
      player.sendMessage(sb);
      return false;
    }
  }

  public boolean chestManager(Player player, String argument, String detail) {

    if(detail.isEmpty()) {
      return false;
    }

    if(!this.playerCustomChests.containsKey(player)) {
      String sb = ChatColor.AQUA
          + "[UtilityUI] "
          + ChatColor.RED
          + "Du hast noch keine Kisten erstellt.";
      player.sendMessage(sb);
      return false;
    }

    switch (argument.toLowerCase()) {
      case "add":
        createNewChest(this.playerCustomChests, player, detail);
        break;
      case "open":
        if (!this.playerCustomChests.get(player).containsKey(detail)) {
          String sb = ChatColor.AQUA
              + "[UtilityUI] "
              + ChatColor.RED
              + "Diese Kiste existiert nicht.";
          player.sendMessage(sb);
          return false;
        }
        break;
      case "list":
        Set<String> keys = this.playerCustomChests.get(player).keySet();
        String str =
            ChatColor.GRAY + "["
            + ChatColor.AQUA + "UtilityUI"
            + ChatColor.GRAY + "] "
            + "Deine temporären Chests:";
        player.sendMessage(str);
        player.sendMessage(ChatColor.YELLOW + "------------------------");
        for(String key : keys) {
          player.sendMessage(ChatColor.GRAY + key);
        }
        player.sendMessage(ChatColor.YELLOW + "------------------------");
      default:
        String sb = ChatColor.AQUA
            + "[UtilityUI] "
            + ChatColor.RED
            + "Argumente ungültig. Nutze /ui chests <open|add> <name>!";
        player.sendMessage(sb);
        return false;
    }

    player.openInventory(this.playerCustomChests.get(player).get(detail));
    return true;
  }

  private void createNewChest(HashMap<Player, HashMap<String, Inventory>> chestMap, Player player,
      String chestName) {
    Inventory chest = Bukkit.createInventory(player, InventoryType.CHEST, chestName);
    if (!chestMap.containsKey(player)) {
      chestMap.put(player, new HashMap<>());
    }
    chestMap.get(player).put(chestName, chest);
  }

  public boolean showWorkbenchGui(Player player) {
    player.openWorkbench(null, true);
    return true;
  }

  public boolean showBrewingGui(Player player) {
    if (brewingMap.get(player) == null) {
      Inventory brewing = Bukkit.createInventory(player, InventoryType.BREWING);
      player.openInventory(brewing);
      brewingMap.put(player, brewing);
    } else {
      Inventory brewing = brewingMap.get(player);
      player.openInventory(brewing);
    }
    return true;
  }

  public boolean hasFreeSlot(PlayerInventory inventory) {
    return inventory.firstEmpty() != -1;
  }
}
