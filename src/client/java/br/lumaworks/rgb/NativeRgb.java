package br.lumaworks.rgb;

import br.lumaworks.LumaWorks;
import br.lumaworks.ClientLightingConfig;
import com.mojang.blaze3d.vertex.QuadInstance;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Experimental client-only bounded RGB bake. No server state, block entities or shader pack. */
@EventBusSubscriber(modid=LumaWorks.ID,value=Dist.CLIENT)
public final class NativeRgb {
 public static final int WIDTH=48,HEIGHT=32,CELLS=WIDTH*HEIGHT*WIDTH;
 private static final long BUDGET_NS=2_000_000L;
 private static final Map<Block,Integer> COLORS=new IdentityHashMap<>();
 // Same original palette used by tools/generate_resources.py, not the vanilla dye swatch.
 private static final int[] LIGHT_COLORS={0xe8f3f5,0xf7a345,0xd266d7,0x7ec9ed,0xf5df6a,0xa7d564,0xf5a4c0,0x646d79,0xb6c1c9,0x43b9bd,0x9869d6,0x658bea,0xae8264,0x6aaf71,0xe56d70,0x414953};
 private static final LinkedHashSet<Long> PENDING=new LinkedHashSet<>();
 private static long queuedSections,submittedSections;
 public static volatile boolean enabled=false;
 private static Boolean configuredEnabled;
 public static volatile Field field;
 private static ClientLevel world;private static Job job;private static long revision;private static boolean dirty=true;private static int sinceChange;
 private static long jobs,discarded,totalWorkNs,maxSliceNs,lastBuildNs;private static int lastSources,lastVisits;private static String error="";
 public record Field(int x,int y,int z,int skyDarken,int[] rgb,byte[] opacity,int[] factors){
  public boolean covers(BlockPos p){return p.getX()>=x-1&&p.getX()<x+WIDTH&&p.getY()>=y-1&&p.getY()<y+HEIGHT&&p.getZ()>=z-1&&p.getZ()<z+WIDTH;}
  public int factor(int wx,int wy,int wz){int dx=wx-x,dy=wy-y,dz=wz-z;return dx<0||dx>=WIDTH||dy<0||dy>=HEIGHT||dz<0||dz>=WIDTH?0xffffff:factors[(dy*WIDTH+dz)*WIDTH+dx];}
  public int sample(int wx,int wy,int wz){int dx=wx-x,dy=wy-y,dz=wz-z;return dx<0||dx>=WIDTH||dy<0||dy>=HEIGHT||dz<0||dz>=WIDTH?0:rgb[(dy*WIDTH+dz)*WIDTH+dx];}
  public boolean blocked(int wx,int wy,int wz){int dx=wx-x,dy=wy-y,dz=wz-z;return dx<0||dx>=WIDTH||dy<0||dy>=HEIGHT||dz<0||dz>=WIDTH||opacity[(dy*WIDTH+dz)*WIDTH+dx]>=15;}
 }
 public static void setEnabled(boolean value){
  if(enabled==value)return;
  enabled=value;dirty=true;revision++;
  if(!value){var old=field;field=null;job=null;if(old!=null)invalidate(old,null);}
 }
 public static void changed(BlockPos pos,BlockState old,BlockState state){
  if(old.getLightEmission()==state.getLightEmission()&&old.getLightDampening()==state.getLightDampening()&&source(old)==source(state))return;
  var f=field;var j=job;
  if(f==null||inside(pos,f.x,f.y,f.z)||j!=null&&inside(pos,j.x,j.y,j.z)){dirty=true;revision++;sinceChange=0;}
 }
 public static void chunkChanged(int cx,int cz){
  Field f=field;Job j=job;
  if(f==null||overlapsChunk(cx,cz,f.x,f.z)||j!=null&&overlapsChunk(cx,cz,j.x,j.z)){dirty=true;revision++;sinceChange=0;}
 }
 private static boolean overlapsChunk(int cx,int cz,int x,int z){return (cx<<4)<x+WIDTH&&((cx+1)<<4)>x&&(cz<<4)<z+WIDTH&&((cz+1)<<4)>z;}
 private static boolean inside(BlockPos p,int x,int y,int z){return p.getX()>=x&&p.getX()<x+WIDTH&&p.getY()>=y&&p.getY()<y+HEIGHT&&p.getZ()>=z&&p.getZ()<z+WIDTH;}
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){
  var mc=Minecraft.getInstance();var level=mc.level;
  if(world!=level){world=level;field=null;job=null;dirty=true;revision++;COLORS.clear();PENDING.clear();}
  if(level==null||mc.player==null)return;
  boolean requested=ClientLightingConfig.NATIVE_RGB.get();
  if(configuredEnabled==null||configuredEnabled!=requested){configuredEnabled=requested;setEnabled(requested);}
  flushSections();
  if(!enabled)return;
  if(COLORS.isEmpty())initializeColors();
  int x=(mc.player.blockPosition().getX()>>3)*8-WIDTH/2,y=(mc.player.blockPosition().getY()>>3)*8-HEIGHT/2,z=(mc.player.blockPosition().getZ()>>3)*8-WIDTH/2;
  if(field!=null&&(field.x!=x||field.y!=y||field.z!=z)&&job==null){dirty=true;revision++;}
  if(field!=null&&field.skyDarken!=level.getSkyDarken()&&job==null){dirty=true;revision++;}
  sinceChange++;
  if(job==null&&dirty&&sinceChange>=2){job=new Job(level,x,y,z,revision);dirty=false;}
  if(job==null)return;
  long begin=System.nanoTime();
  try{
   boolean done=job.step(level,begin+BUDGET_NS);long used=System.nanoTime()-begin;totalWorkNs+=used;maxSliceNs=Math.max(maxSliceNs,used);
   if(done){
    Job finished=job;job=null;
    if(finished.revision!=revision){discarded++;dirty=true;return;}
    var next=new Field(finished.x,finished.y,finished.z,level.getSkyDarken(),finished.rgb,finished.opacity,finished.factors);var old=field;
    field=next;jobs++;lastSources=finished.sources;lastVisits=finished.visits;lastBuildNs=System.nanoTime()-finished.start;
    invalidate(old,next);
   }
  }catch(Throwable e){error=e.toString();job=null;setEnabled(false);}
 }
 private static void initializeColors(){
  for(var entry:LumaWorks.LIGHTS.entrySet())for(DyeColor dye:DyeColor.values())if(entry.getKey().startsWith(dye.getName()+"_")){
   int c=LIGHT_COLORS[dye.ordinal()],r=c>>16&255,g=c>>8&255,b=c&255,m=Math.max(r,Math.max(g,b));
   COLORS.put(entry.getValue().get(),(r*255/m)<<16|(g*255/m)<<8|(b*255/m));break;
  }
 }
 private static int source(BlockState s){Integer color=COLORS.get(s.getBlock());if(color==null)return 0;int level=s.getLightEmission();if(level==0)return 0;return ((color>>16&255)*level/15)<<16|((color>>8&255)*level/15)<<8|(color&255)*level/15;}
 private static int visual(Field f,int x,int y,int z){if(f==null)return 0xffffff;return f.factor(x,y,z);}
 private static void invalidate(Field old,Field next){
  Field base=next==null?old:next;if(base==null)return;
  int minX=base.x,minY=base.y,minZ=base.z,maxX=minX+WIDTH,maxY=minY+HEIGHT,maxZ=minZ+WIDTH;
  if(old!=null){minX=Math.min(minX,old.x);minY=Math.min(minY,old.y);minZ=Math.min(minZ,old.z);maxX=Math.max(maxX,old.x+WIDTH);maxY=Math.max(maxY,old.y+HEIGHT);maxZ=Math.max(maxZ,old.z+WIDTH);}
  for(int sx=(minX-1)>>4;sx<=maxX>>4;sx++)for(int sy=(minY-1)>>4;sy<=maxY>>4;sy++)for(int sz=(minZ-1)>>4;sz<=maxZ>>4;sz++){
   boolean changed=false;
   outer:for(int y=Math.max(minY,(sy<<4)-1);y<Math.min(maxY,(sy<<4)+17);y++)for(int z=Math.max(minZ,(sz<<4)-1);z<Math.min(maxZ,(sz<<4)+17);z++)for(int x=Math.max(minX,(sx<<4)-1);x<Math.min(maxX,(sx<<4)+17);x++){
    if(visual(old,x,y,z)!=visual(next,x,y,z)){changed=true;break outer;}
   }
   if(changed&&PENDING.add(SectionPos.asLong(sx,sy,sz)))queuedSections++;
  }
 }
 private static void flushSections(){
  var it=PENDING.iterator();for(int n=0;n<2&&it.hasNext();n++){long key=it.next();it.remove();Minecraft.getInstance().levelRenderer.setSectionDirty(SectionPos.x(key),SectionPos.y(key),SectionPos.z(key));submittedSections++;}
 }
 public static int multiply(int old,int factor){return old&0xff000000|((old>>16&255)*(factor>>16&255)/255)<<16|((old>>8&255)*(factor>>8&255)/255)<<8|(old&255)*(factor&255)/255;}
 public static int vertexFactor(Field f,BlockPos pos,float x,float y,float z,Direction face){return corner(f,pos.getX()+x,pos.getY()+y,pos.getZ()+z,face);}
 public static void tint(BlockAndTintGetter view,BlockState state,BlockPos pos,BakedQuad baked,QuadInstance quad){
  Field f=field;if(!enabled||f==null||!f.covers(pos)||state.getLightEmission()>0)return;
  applyTint(f,view,pos,baked,quad);
 }
 /** All coplanar faces sample the same physical corner, regardless of model/UV rotation. */
 public static void applyTint(Field f,BlockAndTintGetter view,BlockPos pos,BakedQuad baked,QuadInstance quad){
  Direction face=baked.direction();
  for(int v=0;v<4;v++){
   var local=baked.position(v);double x=pos.getX()+local.x(),y=pos.getY()+local.y(),z=pos.getZ()+local.z();
   int factor=corner(f,x,y,z,face),old=quad.getColor(v);
   quad.setColor(v,multiply(old,factor));
  }
 }
 private static int corner(Field f,double x,double y,double z,Direction face){
  int normal;double u,v;
  switch(face.getAxis()){
   case X->{normal=(int)Math.floor(x+face.getStepX()*.001);u=y-.5;v=z-.5;}
   case Y->{normal=(int)Math.floor(y+face.getStepY()*.001);u=x-.5;v=z-.5;}
   default->{normal=(int)Math.floor(z+face.getStepZ()*.001);u=x-.5;v=y-.5;}
  }
  int a=(int)Math.floor(u),b=(int)Math.floor(v);float fu=(float)(u-a),fv=(float)(v-b),r=0,g=0,blue=0,weight=0;
  for(int du=0;du<2;du++)for(int dv=0;dv<2;dv++){
   float w=(du==0?1-fu:fu)*(dv==0?1-fv:fv);if(w==0)continue;
   int sx,sy,sz;
   switch(face.getAxis()){case X->{sx=normal;sy=a+du;sz=b+dv;}case Y->{sx=a+du;sy=normal;sz=b+dv;}default->{sx=a+du;sy=b+dv;sz=normal;}}
   int c=f.sample(sx,sy,sz);if(c==0&&f.blocked(sx,sy,sz))continue;
   int color=f.factor(sx,sy,sz);r+=(color>>16&255)*w;g+=(color>>8&255)*w;blue+=(color&255)*w;weight+=w;
  }
  return weight==0?0xffffff:Math.round(r/weight)<<16|Math.round(g/weight)<<8|Math.round(blue/weight);
 }
 private static int cellFactor(int c,int sky,int block){
  if(c==0)return 0xffffff;
  int r=c>>16&255,g=c>>8&255,b=c&255,m=Math.max(r,Math.max(g,b));float strength=m/17f;
  float white=Math.max(0,block-strength),influence=Math.max(0,Math.min(1,(strength-sky-white)/15f));
  return Math.round(255*(1-influence*(1-r/(float)m)))<<16|Math.round(255*(1-influence*(1-g/(float)m)))<<8|Math.round(255*(1-influence*(1-b/(float)m)));
 }
 public static Map<String,Object> stats(){
  var f=field;Map<String,Object> r=new LinkedHashMap<>();r.put("enabled",enabled);r.put("ready",f!=null);r.put("building",job!=null);r.put("jobs",jobs);r.put("discarded_jobs",discarded);r.put("total_work_ms",totalWorkNs/1e6);r.put("max_slice_ms",maxSliceNs/1e6);r.put("last_latency_ms",lastBuildNs/1e6);r.put("sources",lastSources);r.put("visited_nodes",lastVisits);r.put("bounded_volume_cells",CELLS);r.put("field_bytes",CELLS*9);r.put("job_array_bytes",CELLS*(4+4+1+1+16));r.put("queued_sections",queuedSections);r.put("submitted_sections",submittedSections);r.put("pending_sections",PENDING.size());r.put("renderer",ModList.get().isLoaded("sodium")?"sodium":"vanilla");r.put("sky_darken",f==null?-1:f.skyDarken);r.put("error",error);return r;
 }
 /** Main-thread snapshot/propagation split into time-budgeted slices; immutable result read by mesh workers. */
 private static final class Job {
  final int x,y,z;final long revision,start=System.nanoTime();final int[] rgb=new int[CELLS],queue=new int[CELLS*4],factors=new int[CELLS];final byte[] opacity=new byte[CELLS],light=new byte[CELLS];
  final BlockPos.MutableBlockPos pos=new BlockPos.MutableBlockPos();int scan,head,tail,count,sources,visits,bake;
  Job(ClientLevel level,int x,int y,int z,long revision){this.x=x;this.y=y;this.z=z;this.revision=revision;}
  void push(int i){if(count==queue.length)throw new IllegalStateException("RGB work queue budget exceeded");queue[tail]=i;tail=(tail+1)%queue.length;count++;}
  boolean step(ClientLevel level,long deadline){
   int work=0;
   while(scan<CELLS){
    int i=scan++,dx=i%WIDTH,dz=i/WIDTH%WIDTH,dy=i/(WIDTH*WIDTH);pos.set(x+dx,y+dy,z+dz);
    if(!level.hasChunk(pos.getX()>>4,pos.getZ()>>4)){opacity[i]=15;}else{var state=level.getBlockState(pos);opacity[i]=(byte)state.getLightDampening();light[i]=(byte)(level.getBrightness(LightLayer.SKY,pos)<<4|level.getBrightness(LightLayer.BLOCK,pos));int c=source(state);if(c!=0){rgb[i]=c;push(i);sources++;}}
    if((++work&63)==0&&System.nanoTime()>=deadline)return false;
   }
   while(count>0){
    int i=queue[head];head=(head+1)%queue.length;count--;visits++;int dx=i%WIDTH,dz=i/WIDTH%WIDTH,dy=i/(WIDTH*WIDTH),c=rgb[i];
    if(dx>0)spread(i-1,c);if(dx<WIDTH-1)spread(i+1,c);if(dz>0)spread(i-WIDTH,c);if(dz<WIDTH-1)spread(i+WIDTH,c);if(dy>0)spread(i-WIDTH*WIDTH,c);if(dy<HEIGHT-1)spread(i+WIDTH*WIDTH,c);
    if((++work&63)==0&&System.nanoTime()>=deadline)return false;
   }
   while(bake<CELLS){int i=bake++;factors[i]=cellFactor(rgb[i],Math.max(0,((light[i]&255)>>4)-level.getSkyDarken()),light[i]&15);if((++work&63)==0&&System.nanoTime()>=deadline)return false;}
   return true;
  }
  void spread(int i,int from){
   int decay=Math.max(1,opacity[i]);if(decay>=15)return;
   int fr=from>>16&255,fg=from>>8&255,fb=from&255,m=Math.max(fr,Math.max(fg,fb)),remaining=Math.max(0,m-decay*17);if(remaining==0)return;
   int old=rgb[i],r=Math.max(old>>16&255,(fr*remaining+m/2)/m),g=Math.max(old>>8&255,(fg*remaining+m/2)/m),b=Math.max(old&255,(fb*remaining+m/2)/m),next=r<<16|g<<8|b;
   if(next!=old){rgb[i]=next;push(i);}
  }
 }
}

