package com.mumu17.client_vein_miner_reworked.mixin;

import com.mumu17.client_vein_miner_reworked.ClientMinerConfig;
import com.mumu17.client_vein_miner_reworked.Constants;
import me.shedaniel.autoconfig.AutoConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin  {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Constants.veinKey.isDown() && !Constants.cvmr.working) {
            cir.cancel();
            Constants.cvmr.onStartMining(blockPos, direction, minecraft.level);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Constants.cvmr.working) {
            if (minecraft.options.keyAttack.isDown()) {
                Constants.cvmr.onTick();
            } else {
                if (Constants.config.automine && Constants.cvmr.working) minecraft.options.keyAttack.setDown(true);
                else Constants.cvmr.onStopMining();
            }
            if (Constants.stopKey.isDown()) {
                Constants.cvmr.onStopMining();
            }
        }
        if (Constants.configKey.isDown()) {
            try {
                minecraft.setScreen(AutoConfig.getConfigScreen(ClientMinerConfig.class, minecraft.screen).get());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
