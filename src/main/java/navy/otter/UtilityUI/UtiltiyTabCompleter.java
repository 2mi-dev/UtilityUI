package navy.otter.UtilityUI;

import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class UtiltiyTabCompleter implements TabCompleter {

  public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
    ArrayList<String> list = new ArrayList<>();
    if (!(arg0 instanceof Player)) {
      return new ArrayList<>();
    }

    final Player player = (Player) arg0;

    if (player.hasPermission("UtilityUI.anvil")) {
      list.add("anvil");
    }
    if (player.hasPermission("UtilityUI.brewing")) {
      list.add("brew");
    }
    if (player.hasPermission("UtilityUI.commandblock")) {
      list.add("cmd");
    }
    if (player.hasPermission("UtilityUI.debugstick")) {
      list.add("debug");
    }
    if (player.hasPermission("UtilityUI.workbench")) {
      list.add("wb");
    }

    return list
        .stream()
        .filter((string) -> string.startsWith(arg3[0]))
        .collect(Collectors.toList());
  }
}