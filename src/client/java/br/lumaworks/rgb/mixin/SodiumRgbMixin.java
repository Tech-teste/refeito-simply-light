package br.lumaworks.rgb.mixin;
import br.lumaworks.rgb.NativeRgb;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Optional integration, scoped to terrain. Sodium stores model colors as ARGB here. */
@Pseudo
@Mixin(targets="net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer",remap=false)
public abstract class SodiumRgbMixin extends AbstractBlockRenderContext {
 @Inject(method="processQuad",at=@At(value="INVOKE",target="Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;bufferQuad(Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;[FLnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;)V"))
 private void tintTerrain(MutableQuadViewImpl quad,CallbackInfo ci){
  var f=NativeRgb.field;if(!NativeRgb.enabled||f==null||!f.covers(pos)||state.getLightEmission()>0)return;
  for(int v=0;v<4;v++)quad.setColor(v,NativeRgb.multiply(quad.baseColor(v),NativeRgb.vertexFactor(f,pos,quad.getX(v),quad.getY(v),quad.getZ(v),quad.getLightFace())));
 }
}
