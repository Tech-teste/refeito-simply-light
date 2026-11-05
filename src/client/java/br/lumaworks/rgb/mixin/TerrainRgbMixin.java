package br.lumaworks.rgb.mixin;
import br.lumaworks.rgb.NativeRgb;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ModelBlockRenderer.class)
public abstract class TerrainRgbMixin {
 @Shadow @Final private QuadInstance quadInstance;
 @Inject(method="putQuadWithTint",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/block/BlockQuadOutput;put(FFFLnet/minecraft/client/resources/model/geometry/BakedQuad;Lcom/mojang/blaze3d/vertex/QuadInstance;)V"))
 private void shade(BlockQuadOutput output,float x,float y,float z,BlockAndTintGetter view,BlockState state,BlockPos pos,BakedQuad quad,CallbackInfo ci){NativeRgb.tint(view,state,pos,quad,quadInstance);}
}
