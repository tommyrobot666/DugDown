package lommie.dugdown;

import lommie.dugdown.mixin.IMixinPlayer;
import lommie.dugdown.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.LavaFluid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {
    static int blocksDownTilActivate;
    static Map<UUID,Integer> lightningTargets = new HashMap<>();

    //Just remember that the method to register more EntityDataSerializer is public;
    //public static final EntityDataSerializer<Custom> BLOCKS_DUG_DOWN = EntityDataSerializer.forValueType(ByteBufCodec for Custom);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        if (Services.PLATFORM.isModLoaded("dugdown")) {
            Constants.LOG.info("Hello to dugdown");
        }

        //read config
        blocksDownTilActivate = 5;
    }

    public static void onPlayerDig(BlockPos pos, Level level, Player player) {
        BlockPos playerPos = player.blockPosition();
        if (!dugDown(pos,playerPos)){
            ((IMixinPlayer) player).setBlocksDugDown(0);
            return;
        }

        int blocksDugDown = ((IMixinPlayer) player).getBlocksDugDown();
        if (blocksDugDown >= blocksDownTilActivate-1){
            int eventId = level.random.nextIntBetweenInclusive(0,2);
            Constants.LOG.info("Starting Dig Down Event #{}", eventId);
            switch (eventId){
                case 0:
                    level.setBlockAndUpdate(playerPos.below(), Blocks.LAVA.defaultBlockState());
                    return;
                case 1:
                    lightningTargets.put(player.getUUID(),100);
                    return;
                case 2:
                    player.sendSystemMessage(Component.literal("Watch out! You might fall in lava!"));
                    return;
                default:
                    Constants.LOG.error("Event #{} doesn't exist",eventId);
            }
        } else {
            ((IMixinPlayer) player).setBlocksDugDown(blocksDugDown+1);
        }
    }

    public static void onWorldTick(Level level){
        if (level.isClientSide()) return;
        ServerLevel serverLevel = ((ServerLevel) level);

        for (UUID uuid : lightningTargets.keySet()) {
            Player player = level.getPlayerByUUID(uuid);
            if (player == null){
                continue;
            }

            EntityType.LIGHTNING_BOLT.spawn(serverLevel, player.blockPosition(), MobSpawnType.COMMAND);
        }
    }

    static boolean dugDown(BlockPos pos, BlockPos playerPos){
        return pos.getY() == playerPos.getY()-1 &&
                pos.getX() <= playerPos.getX()+1 && pos.getX() >= playerPos.getX()-1 &&
                pos.getZ() <= playerPos.getZ()+1 && pos.getZ() >= playerPos.getZ()-1;
    }
}
