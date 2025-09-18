package com.mumu17.client_vein_miner_reworked;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.PriorityQueue;
import java.util.Queue;

public final class ClientVeinMinerReworked {
    public static final String MOD_ID = "client_vein_miner_reworked";

    private static final Minecraft mc = Minecraft.getInstance();

    private final static BlockPos[] blockOffsets = {
            new BlockPos(0, 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    private final Queue<BlockPos> blocks = new PriorityQueue<>(ClientVeinMinerReworked::blockCompare);

    private Block blockType = null;
    private Direction direction = null;
    private ClientLevel world = null;

    public BlockPos currentBlock = null;

    private float startYaw = 0;
    private float startPitch = 0;

    public boolean working = false;

    public void onStopMining() {
        if (!working) return;

        blocks.clear();
        currentBlock = null;
        blockType = null;
        direction = null;
        working = false;

        if (Constants.config.snapBack && mc.player != null) {
            mc.player.setYRot(startYaw);
            mc.player.setXRot(startPitch);
        }

        if (Constants.config.sound && world != null && mc.player != null)
            world.playSound(mc.player, mc.player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 1f, 1f);

        if (Constants.config.automine)
            mc.options.keyAttack.setDown(false);
    }

    private boolean invalidDist(BlockPos blockPos) {
        if (mc.player == null) return true;
        double dx = (mc.player.getX() - 0.5) - (blockPos.getX() + direction.getStepX());
        double dy = (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose())) - (blockPos.getY() + direction.getStepY());
        double dz = (mc.player.getZ() - 0.5) - (blockPos.getZ() + direction.getStepZ());
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return distance >= mc.player.blockInteractionRange();
    }

    private boolean cannotBreak(BlockPos blockPos) {
        var state = world.getBlockState(blockPos);
        if (!mc.player.isCreative() && state.getDestroySpeed(mc.level, blockPos) < 0) return true;
        return state.getShape(mc.level, blockPos) == Shapes.empty();
    }

    private void addConnected(BlockPos pos) {
        for (BlockPos offset : blockOffsets) {
            var newPos = pos.offset(offset);
            if (world.getBlockState(newPos).getBlock() != blockType) continue;
            if (invalidDist(newPos)) continue;
            if (cannotBreak(newPos)) continue;
            if (blocks.contains(newPos)) continue;
            blocks.add(newPos);
            addConnected(newPos);
        }
    }

    public void onStartMining(BlockPos block, Direction direction, ClientLevel world) {
        startYaw = mc.player.getYRot();
        startPitch = mc.player.getXRot();
        onStopMining();
        working = true;
        this.direction = direction;
        this.blockType = world.getBlockState(block).getBlock();
        this.world = world;
        blocks.add(block);
        addConnected(block);
    }

    public void onTick() {
        if (!working) return;

        if (mc.player == null || world == null) {
            onStopMining();
            return;
        }

        if (currentBlock == null) {
            currentBlock = blocks.poll();

            if (currentBlock == null) {
                onStopMining();
                return;
            }
        } else {
            if (world.getBlockState(currentBlock).getBlock() != blockType
                    || invalidDist(currentBlock)
                    || cannotBreak(currentBlock)) {
                currentBlock = null;
                return;
            }

        }

        //world.addParticle(ParticleTypes.SMALL_FLAME, currentBlock.getX() + 0.5, currentBlock.getY() + 1.1, currentBlock.getZ()+ 0.5, 0, 0, 0);

        float targetYaw = getYaw(currentBlock);
        float targetPitch = getPitch(currentBlock);

        float yawDiff = mc.player.getYRot() - targetYaw;
        float pitchDiff = mc.player.getXRot() - targetPitch;

        float rotationSpeed = Mth.clamp(Constants.config.rotationSpeed, 0.001f, 1f);

        if (Math.abs(yawDiff) > 3f || Math.abs(pitchDiff) > 3f) {
            yawDiff = Mth.clamp(yawDiff, -100f, 100f);
            pitchDiff = Mth.clamp(pitchDiff, -100f, 100f);

            if (Math.abs(yawDiff) > 3f)
                mc.player.setYRot(mc.player.getYRot() - yawDiff * rotationSpeed);
            if (Math.abs(pitchDiff) > 3f)
                mc.player.setXRot(mc.player.getXRot() - pitchDiff * rotationSpeed);

            return;
        }

        if (Constants.config.raycast) {
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult)mc.hitResult;
                if (world.getBlockState(hit.getBlockPos()).getBlock() != blockType) {
                    currentBlock = null;
                    if (Constants.config.automine)
                        mc.options.keyAttack.setDown(false);
                    return;
                }
            }
        }
    }

    public static float getYaw(BlockPos pos) {
        return mc.player.getYRot() + Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() + 0.5 - mc.player.getZ(), pos.getX() + 0.5 - mc.player.getX())) - 90f - mc.player.getYRot());
    }

    public static float getPitch(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getX();
        double diffY = pos.getY() + 0.5 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() + 0.5 - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getXRot() + Mth.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getXRot());
    }

    public static int blockCompare(BlockPos p1, BlockPos p2) {
        return Integer.compare(
                mc.player.blockPosition().distManhattan(p1),
                mc.player.blockPosition().distManhattan(p2)
        );
    }
}
