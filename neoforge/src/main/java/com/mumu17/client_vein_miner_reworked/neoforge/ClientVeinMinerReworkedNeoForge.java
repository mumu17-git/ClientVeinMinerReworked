package com.mumu17.client_vein_miner_reworked.neoforge;

import com.mumu17.client_vein_miner_reworked.ClientMinerConfig;
import com.mumu17.client_vein_miner_reworked.ClientVeinMinerReworked;
import com.mumu17.client_vein_miner_reworked.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ClientVeinMinerReworked.MOD_ID)
public final class ClientVeinMinerReworkedNeoForge {
    public ClientVeinMinerReworkedNeoForge() {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        // Submit our event bus to let Architectury API register our content on the right time.
        // EventBuses.registerModEventBus(ClientVeinMinerReworked.MOD_ID, bus);

        AutoConfig.register(ClientMinerConfig.class, Toml4jConfigSerializer::new);
        Constants.config = AutoConfig.getConfigHolder(ClientMinerConfig.class).getConfig();
        if (FMLEnvironment.dist.isClient()) {
            ClientVeinMinerReworkedNeoForge.registerScreen();

            bus.addListener(this::registerKeyBinding);
        };
    }

    public static void registerScreen() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ClientVeinMinerReworkedNeoForge::getModConfigScreenFactory);
    }

    public static IConfigScreenFactory getModConfigScreenFactory() {
        return (client,parent) -> AutoConfig.getConfigScreen(ClientMinerConfig.class, parent).get();
    }

    private void registerKeyBinding(final RegisterKeyMappingsEvent event) {
        event.register(Constants.configKey);
        event.register(Constants.stopKey);
        event.register(Constants.veinKey);
    }
}
