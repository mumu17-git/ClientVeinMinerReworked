package com.mumu17.client_vein_miner_reworked.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import com.mumu17.client_vein_miner_reworked.ClientMinerConfig;
import com.mumu17.client_vein_miner_reworked.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ClientVeinMinerReworkedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ClientMinerConfig.class, Toml4jConfigSerializer::new);
        Constants.config = AutoConfig.getConfigHolder(ClientMinerConfig.class).getConfig();

        Constants.veinKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clientminer.vein",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.clientminer.cat"
        ));
        Constants.stopKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clientminer.stop",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
                "category.clientminer.cat"
        ));
        Constants.configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clientminer.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.clientminer.cat"
        ));
    }
}
