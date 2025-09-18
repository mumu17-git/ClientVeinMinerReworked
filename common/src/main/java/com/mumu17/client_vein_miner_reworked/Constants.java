package com.mumu17.client_vein_miner_reworked;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Constants {
    public static KeyMapping veinKey = new KeyMapping(
        "key.clientminer.vein",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_GRAVE_ACCENT,
        "category.clientminer.cat"
    );
    public static KeyMapping stopKey = new KeyMapping(
        "key.clientminer.stop",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_APOSTROPHE,
        "category.clientminer.cat"
    );
    public static KeyMapping configKey = new KeyMapping(
        "key.clientminer.config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "category.clientminer.cat"
    );

    public static final ClientVeinMinerReworked cvmr = new ClientVeinMinerReworked();
    public static ClientMinerConfig config;
}
