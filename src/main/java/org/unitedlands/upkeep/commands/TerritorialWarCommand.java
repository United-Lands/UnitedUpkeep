package org.unitedlands.upkeep.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.object.AddonCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.upkeep.UnitedUpkeep;
import org.unitedlands.upkeep.util.TerritorialMetaController;

public class TerritorialWarCommand implements TabExecutor {

    private UnitedUpkeep unitedUpkeep;

    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
            @NotNull String s, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> BaseCommand.getTownyStartingWith(args[0], "n");
            case 2 -> Arrays.asList("minor", "major");
            case 3 -> Arrays.asList("true", "false");
            default -> Collections.emptyList();
        };
    }

    public TerritorialWarCommand(UnitedUpkeep unitedUpkeep) {
        this.unitedUpkeep = unitedUpkeep;
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN_TOGGLE, "territorialWars", this));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (sender instanceof Player) {
            Town town = TownyAPI.getInstance().getTown((Player) (sender));
            Resident resident = TownyAPI.getInstance().getResident((Player) (sender));
            if (town == null) {
                TownyMessaging.sendErrorMsg(sender, this.unitedUpkeep.getConfig().getString("errors.noTown"));
                return true;
            }
            if (!town.isMayor(resident)) {
                TownyMessaging.sendErrorMsg(sender, this.unitedUpkeep.getConfig().getString("errors.notMayor"));
                return true;
            }
            if (town.isNeutral()) {
                TownyMessaging.sendErrorMsg(sender, this.unitedUpkeep.getConfig().getString("errors.neutralTown"));
                return true;
            }

            var lastSwitchTime = TerritorialMetaController.getTerritorialWarSwitchTime(town);
            if (lastSwitchTime != null) {
                // Convert config cooldowntime to milliseconds
                var cooldownTime = unitedUpkeep.getConfig().getLong("territorialwars.togglecooldown") * 60000;
                var timeDifference = (System.currentTimeMillis() - lastSwitchTime);

                if (timeDifference < cooldownTime) {
                    var error = this.unitedUpkeep.getConfig().getString("errors.onCooldown");
                    error = error.replace("{time}", formatDuration(cooldownTime - timeDifference));

                    TownyMessaging.sendErrorMsg(sender, error);
                    return true;
                }
            }

            TerritorialMetaController.toggleTerritorialWars(town);
            TerritorialMetaController.setTerritorialWarSwitchTime(town);

            TownyMessaging.sendPrefixedTownMessage(town,
                            (TerritorialMetaController.toggledTerritorialWars(town)
                                    ? (this.unitedUpkeep.getConfig().getString("messages.enabledTerritorial"))
                                    : (this.unitedUpkeep.getConfig().getString("messages.disabledTerritorial"))));
        } else {
            TownyMessaging.sendErrorMsg("You must be a player to use this command!");
        }
        return true;
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000 % 60;
        long minutes = millis / (1000 * 60) % 60;
        long hours = millis / (1000 * 60 * 60) % 24;
        long days = millis / (1000 * 60 * 60 * 24);

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days).append("d ");
        if (hours > 0 || days > 0)
            sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0)
            sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
