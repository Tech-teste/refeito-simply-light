package br.lumaworks.rgb.mixin;
import br.lumaworks.rgb.NativeRgb;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientLevel.class)
public abstract class ClientLevelRgbMixin {
 @Inject(method="sendBlockUpdated",at=@At("TAIL"))
 private void changed(BlockPos pos,BlockState old,BlockState state,int flags,CallbackInfo ci){NativeRgb.changed(pos,old,state);}
 @Inject(method="onChunkLoaded",at=@At("TAIL"))
 private void loaded(ChunkPos pos,CallbackInfo ci){NativeRgb.chunkChanged(pos.x(),pos.z());}
 @Inject(method="unload",at=@At("TAIL"))
 private void unloaded(LevelChunk chunk,CallbackInfo ci){var pos=chunk.getPos();NativeRgb.chunkChanged(pos.x(),pos.z());}
}
