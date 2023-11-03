package io.github.mortuusars.exposure.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.camera.capture.LastExposures;
import io.github.mortuusars.exposure.client.gui.ClientGUI;
import io.github.mortuusars.exposure.item.PhotographItem;
import io.github.mortuusars.exposure.util.ItemAndStack;
import io.github.mortuusars.exposure.util.ScheduledTasks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("exposure")
                .requires((stack) -> stack.hasPermission(2))
                .then(Commands.literal("latest")
                        .then(Commands.literal("show")
                                .executes(ClientCommands::showLast))
                        /*.then(Commands.literal("clear")
                                .executes(ClientCommands::clearLast))*/));
    }

    private static int showLast(CommandContext<CommandSourceStack> context) {
        Collection<String> exposureIds = LastExposures.get();
        if (exposureIds.size() == 0) {
            context.getSource().sendFailure(Component.translatable("command.exposure.latest.show.error.no_exposures"));
            return 0;
        }

        List<ItemAndStack<PhotographItem>> photographs = new ArrayList<>();

        for (String exposureId : exposureIds) {
            ItemStack stack = new ItemStack(Exposure.Items.PHOTOGRAPH.get());
            CompoundTag tag = new CompoundTag();
            tag.putString("Id", exposureId);
            stack.setTag(tag);

            photographs.add(new ItemAndStack<>(stack));
        }

        ScheduledTasks.schedule(new ScheduledTasks.Task(2, () -> ClientGUI.openPhotographScreen(photographs)));
        return 0;
    }

    private static int clearLast(CommandContext<CommandSourceStack> context) {
        if (LastExposures.get().size() > 0) {
            LastExposures.clear();
            context.getSource().sendSuccess(Component.translatable("command.exposure.latest.clear.success"), false);
        }
        else {
            context.getSource().sendFailure(Component.translatable("command.exposure.latest.clear.error.nothing_to_clear"));
        }

        return 0;
    }
}