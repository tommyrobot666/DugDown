package lommie.dugdown;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface DigDownEvent {
    void event(BlockPos pos, Level level, Player player, BlockState state);
}
