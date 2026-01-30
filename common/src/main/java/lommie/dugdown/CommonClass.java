package lommie.dugdown;

import lommie.dugdown.notamixin.IMixinPlayer;
import lommie.dugdown.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {
    public static final String MOD_ID = "dugdown";
    static int blocksDownTilActivate;
    static final int defaultBlocksDownTilActivate = 5;
    static final String defaultConfigFile = "How many blocks can the player dig down without consequences?\nblocksDownTilActivate: 5";
    static Path configFile;
    static final Map<UUID,Integer> lightningTargets = new HashMap<>();
    //https://misode.github.io/tags/block/
    static final TagKey<Block> EVENT_ACTIVATING_BLOCKS = TagKey.create(BuiltInRegistries.BLOCK.key(), Objects.requireNonNull(ResourceLocation.tryBuild(Constants.MOD_ID, "event_activating")));
    public static final ResourceKey<Registry<DigDownEvent>> DIG_DOWN_EVENT_REGISTRY_KEY = ResourceKey.createRegistryKey(Objects.requireNonNull(ResourceLocation.tryBuild(MOD_ID, "dig_down_events")));
    public static MappedRegistry<DigDownEvent> DIG_DOWN_EVENT_REGISTRY;

    //Just remember that the method to register more EntityDataSerializer is public;
    //public static final EntityDataSerializer<Custom> BLOCKS_DUG_DOWN = EntityDataSerializer.forValueType(ByteBufCodec for Custom);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Constants.LOG.info("dugdown on modloader '{}' is placing lava under you in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.

        configFile = Services.PLATFORM.getConfigDirectory().resolve("dugdown.txt");
        try {
            blocksDownTilActivate = loadConfig();
        } catch (IOException | NumberFormatException e ) {
            blocksDownTilActivate = defaultBlocksDownTilActivate;
            Constants.LOG.error("Couldn't load config");
            Constants.LOG.error("Using default values");
            Constants.LOG.error(e.getMessage());
            for (StackTraceElement element : e.getStackTrace()){
                Constants.LOG.error(element.toString());
            }
        }

        ModDigDownEvents.register();
    }

    static int loadConfig() throws IOException, NumberFormatException {
        if (Files.exists(configFile)){
            Constants.LOG.info("Config found... reading file");
            for (String line : Files.readAllLines(configFile)){
                if (line.startsWith("blocksDownTilActivate:")){
                    return Integer.parseInt(line.substring(22).strip());
                }
            }
            Constants.LOG.error("No lines in config started with 'blocksDownTilActivate:'");
            Constants.LOG.error("Add a new line with blocksDownTilActivate:<positiveNumber> to fix this");
        } else {
            Constants.LOG.info("No config found... creating file");
            Files.writeString(configFile,defaultConfigFile, StandardOpenOption.CREATE);
        }
        return defaultBlocksDownTilActivate;
    }

    public static void onPlayerDig(BlockPos pos, Level level, Player player, BlockState state) {
        BlockPos playerPos = player.blockPosition();
        if (pos == playerPos && state.is(Blocks.FIRE)) return; // Player might accidentally break fire during lightning event
        if (!dugDown(pos,playerPos) || !state.is(EVENT_ACTIVATING_BLOCKS)){
            ((IMixinPlayer) player).dugDown$setBlocksDugDown(0);
            return;
        }

        int blocksDugDown = ((IMixinPlayer) player).dugDown$getBlocksDugDown()+1;
        ((IMixinPlayer) player).dugDown$setBlocksDugDown(blocksDugDown);
        if (blocksDugDown >= blocksDownTilActivate){
            int eventId = level.random.nextIntBetweenInclusive(0,DIG_DOWN_EVENT_REGISTRY.size()-1);
            Constants.LOG.debug("Starting Dig Down Event #{}, id:{}", eventId, List.copyOf(DIG_DOWN_EVENT_REGISTRY.keySet()).get(eventId).toString());
            Objects.requireNonNull(DIG_DOWN_EVENT_REGISTRY.byId(eventId)).event(pos, level, player, state);
        }
    }

    public static void onWorldTick(Level level){
        if (level.isClientSide()) return;
        ServerLevel serverLevel = ((ServerLevel) level);

        for (UUID uuid : lightningTargets.keySet()) {
            Player player = level.getPlayerByUUID(uuid);
            int timesLeft = lightningTargets.get(uuid)-1;
            if (player == null){
                continue;
            }

            EntityType.LIGHTNING_BOLT.spawn(serverLevel, player.blockPosition(), MobSpawnType.COMMAND);

            if (timesLeft>0) {
                lightningTargets.put(uuid, timesLeft);
            } else {
                lightningTargets.remove(uuid);
            }
        }
    }

    public static void onDeathOrRespawn(Player player){
        lightningTargets.remove(player.getUUID());
    }

    static boolean dugDown(BlockPos pos, BlockPos playerPos){
        return pos.getY() == playerPos.getY()-1 &&
                pos.getX() <= playerPos.getX()+1 && pos.getX() >= playerPos.getX()-1 &&
                pos.getZ() <= playerPos.getZ()+1 && pos.getZ() >= playerPos.getZ()-1;
    }
}
