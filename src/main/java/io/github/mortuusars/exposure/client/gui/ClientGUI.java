package io.github.mortuusars.exposure.client.gui;

import io.github.mortuusars.exposure.client.gui.screen.ExposureScreen;
import io.github.mortuusars.exposure.client.gui.screen.PhotographScreen;
import io.github.mortuusars.exposure.client.gui.screen.ViewfinderControlsScreen;
import io.github.mortuusars.exposure.item.PhotographItem;
import io.github.mortuusars.exposure.util.ItemAndStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientGUI {
    public static void showExposureViewScreen(ItemStack film) {
        Minecraft.getInstance().setScreen(new ExposureScreen(film));
    }

    public static void showPhotographScreen(List<ItemAndStack<PhotographItem>> photographs) {
        Minecraft.getInstance().setScreen(new PhotographScreen(photographs));
    }

    public static void showViewfinderConfigScreen() {
        Minecraft.getInstance().setScreen(new ViewfinderControlsScreen());
    }
}
