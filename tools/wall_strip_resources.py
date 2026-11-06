"""Original wall-frame geometry. Adds IDs/resources without rewriting the old families."""
from pathlib import Path
import json

def generate(root):
 from generate_resources import COLORS,PT_COLORS
 def write(path,data):
  p=root/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 def read(path):return json.loads((root/path).read_text(encoding='utf-8'))
 def recipe(name,result,count=1,**data):write('data/lumaworks/recipe/'+name+'.json',{'type':'minecraft:crafting_shaped' if 'pattern' in data else 'minecraft:crafting_shapeless','category':'building',**data,'result':{'id':'lumaworks:'+result,'count':count}})
 def box(a,b,tex):return {'from':a,'to':b,'faces':{d:{'texture':'#'+tex} for d in ['up','down','north','south','east','west']}}
 def elements(bit):
  # Viewed from north: screen-right is west; each bar has a separate front diffuser.
  if bit==0:a,b=[1.4,14.6,14.5],[14.6,16,16];la,lb=[1.6,14.85,14.4375],[14.4,15.75,14.5]
  elif bit==1:a,b=[0,0,14.5],[1.4,16,16];la,lb=[.25,.25,14.4375],[1.15,15.75,14.5]
  elif bit==2:a,b=[1.4,0,14.5],[14.6,1.4,16];la,lb=[1.6,.25,14.4375],[14.4,1.15,14.5]
  else:a,b=[14.6,0,14.5],[16,16,16];la,lb=[14.85,.25,14.4375],[15.75,15.75,14.5]
  return [box(a,b,'metal'),box(la,lb,'light')]
 mine=read('data/minecraft/tags/block/mineable/pickaxe.json');lights=read('data/lumaworks/tags/block/lights.json') if (root/'data/lumaworks/tags/block/lights.json').exists() else None
 for ci,(color,_) in enumerate(COLORS.items()):
  name=color+'_strip_wall';base='lumaworks:block/'+name
  for bit in range(4):
   for off in [False,True]:
    write('assets/lumaworks/models/block/'+name+'_'+str(bit)+('_off' if off else '')+'.json',{'parent':'minecraft:block/block','textures':{'metal':'lumaworks:block/housing_graphite','light':'lumaworks:block/'+('unlit_smooth' if off else color+'_smooth'),'particle':'lumaworks:block/'+color+'_smooth'},'elements':elements(bit)})
  write('assets/lumaworks/models/block/'+name+'_item.json',{'parent':'minecraft:block/block','textures':{'metal':'lumaworks:block/housing_graphite','light':'lumaworks:block/'+color+'_smooth','particle':'lumaworks:block/'+color+'_smooth'},'elements':[e for bit in range(4) for e in elements(bit)]})
  for mode,suffix in enumerate(['','_redstone','_redstone_inverted']):
   rid=name+suffix;parts=[]
   for face,rotation in [('north',0),('east',90),('south',180),('west',270)]:
    for bit in range(4):
     masks='|'.join(str(m) for m in range(16) if (m or 15)&(1<<bit))
     for lit in (['true','false'] if mode else [None]):
      when={'facing':face,'edges':masks}
      if lit is not None:when['lit']=lit
      parts.append({'when':when,'apply':{'model':base+'_'+str(bit)+('_off' if lit=='false' else ''),'y':rotation}})
   write('assets/lumaworks/blockstates/'+rid+'.json',{'multipart':parts})
   write('assets/lumaworks/items/'+rid+'.json',{'model':{'type':'minecraft:model','model':base+'_item'}})
   write('data/lumaworks/loot_table/blocks/'+rid+'.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'lumaworks:'+rid}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
   tag='strip_wall'+suffix
   recipe(rid+'_recolor',rid,8,pattern=['LLL','LDL','LLL'],key={'L':'#lumaworks:'+tag,'D':'minecraft:'+color+'_dye'})
   if mode:
    recipe(rid+'_toggle',name+('_redstone_inverted' if mode==1 else '_redstone'),ingredients=['lumaworks:'+rid])
    if mode==1:recipe(rid+'_upgrade',rid,ingredients=['lumaworks:'+name,'minecraft:redstone'])
   if color=='white' and mode==0:recipe(rid,rid,4,pattern=['IGI','D D','IGI'],key={'I':'minecraft:iron_nugget','G':'minecraft:glass','D':'minecraft:glowstone_dust'})
   for lang in ['en_us','pt_br']:
    p='assets/lumaworks/lang/'+lang+'.json';strings=read(p)
    strings['block.lumaworks.'+rid]=(color.replace('_',' ').title()+' Wall Edge Light'+(['',' — Redstone',' — Inverted Redstone'][mode]) if lang=='en_us' else 'Fita de parede '+PT_COLORS[ci].lower()+(['',' — Redstone',' — Redstone invertida'][mode]))
    strings['tooltip.lumaworks.strip_wall']=('Wall outline: top, right, bottom, left. Sneak + empty-hand right-click to choose edges.','Contorno de parede: cima, direita, baixo e esquerda. Agache e clique com a mão vazia para ajustar.')[lang=='pt_br']
    for key,en,pt in [('top','Top','Cima'),('right','Right','Direita'),('bottom','Bottom','Baixo'),('left','Left','Esquerda')]:strings['gui.lumaworks.'+key]=pt if lang=='pt_br' else en
    write(p,strings)
   mine['values'].append('lumaworks:'+rid)
   if lights is not None:lights['values'].append('lumaworks:'+rid)
 for suffix in ['','_redstone','_redstone_inverted']:write('data/lumaworks/tags/item/strip_wall'+suffix+'.json',{'values':['lumaworks:'+c+'_strip_wall'+suffix for c in COLORS]})
 mine['values']=sorted(set(mine['values']));write('data/minecraft/tags/block/mineable/pickaxe.json',mine)
 if lights is not None:lights['values']=sorted(set(lights['values']));write('data/lumaworks/tags/block/lights.json',lights)
