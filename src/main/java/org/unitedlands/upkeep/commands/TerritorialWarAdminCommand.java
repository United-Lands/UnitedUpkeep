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

public class TerritorialWarAdminCommand implements TabExecutor {

    private UnitedUpkeep unitedUpkeep;

    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
            @NotNull String s, @NotNull String[] args) {
        return switch (args.length) {
            default -> Collections.emptyList();
        };
    }

    public TerritorialWarAdminCommand(UnitedUpkeep unitedUpkeep) {
        this.unitedUpkeep = unitedUpkeep;
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWNYADMIN_TOWN, "territorialWars", this));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {

        if (sender instanceof Player player) {

            Town town = TownyAPI.getInstance().getTown(args[0]);
            if (town == null) {
                TownyMessaging.sendErrorMsg(sender, this.unitedUpkeep.getConfig().getString("errors.noTown"));
                return true;
            }

            TerritorialMetaController.toggleTerritorialWars(town);
            TerritorialMetaController.setTerritorialWarSwitchTime(town);

            String message = "Territorial wars for town " + town.getName() + " have been ";
            if (TerritorialMetaController.toggledTerritorialWars(town))
                message += "§aenabled";
            else
                message += "§cdisabled";
            player.sendMessage(message);
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
