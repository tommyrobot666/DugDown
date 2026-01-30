package lommie.dugdown;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class ModDigDownEvents {
    static void register(DigDownEvent event, String name){
        CommonClass.DIG_DOWN_EVENT_REGISTRY.register(ResourceKey.create(CommonClass.DIG_DOWN_EVENT_REGISTRY_KEY, Objects.requireNonNull(ResourceLocation.tryBuild("modid", name))),event, RegistrationInfo.BUILT_IN);
    }

    static void register(){
        register(ModDigDownEvents::PlaceLaveEvent,"place_lava");
        register(ModDigDownEvents::LightningDoomEvent,"lighntinng_doom");
        register(ModDigDownEvents::WarningMessageEvent,"warning_message");
    }

//    void Event(BlockPos pos, Level level, Player player, BlockState state);

    static void WarningMessageEvent(BlockPos pos, Level level, Player player, BlockState state) {
        player.sendSystemMessage(Component.literal("Watch out! You might fall in lava!").withStyle(ChatFormatting.RED));
    }

    static void LightningDoomEvent(BlockPos pos, Level level, Player player, BlockState state) {
        CommonClass.lightningTargets.put(player.getUUID(),100);
    }

    static void PlaceLaveEvent(BlockPos pos, Level level, Player player, BlockState state) {
        BlockPos laveCornerPos = player.blockPosition().below();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos lavePos = laveCornerPos.east(i).south(j);
                if (level.getBlockState(lavePos).is(CommonClass.EVENT_ACTIVATING_BLOCKS)) {
                    level.setBlockAndUpdate(lavePos, Blocks.LAVA.defaultBlockState());
                }
            }
        }
    }
}
