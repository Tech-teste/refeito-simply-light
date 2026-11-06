"""Dedicated item previews: world geometry/state/collision are not touched."""
from pathlib import Path
import copy,json

def generate(root):
 root=Path(root)
 def read(p):return json.loads((root/p).read_text(encoding='utf-8'))
 def write(p,v):
  p=root/p;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 colors=[p.stem[:-len('_bulb')] for p in (root/'assets/lumaworks/items').glob('*_bulb.json')]
 assert len(colors)==16
 for color in colors:
  for form in ['fixture','bulb','strip_low','strip_high']:
   base=color+'_'+form;model=read('assets/lumaworks/models/block/'+base+'.json')
   if form.startswith('strip_'):
    # A full selectable outline illustrates the four-edge item; selection in the world still depends on placement/editor.
    source=read('assets/lumaworks/models/block/'+color+'_strip_low.json')['elements'];elements=[]
    for turn in range(4):
     for original in source:
      e=copy.deepcopy(original)
      if turn%2:e['from'][0]=1.4;e['to'][0]=14.6
      for endpoint in ['from','to']:e[endpoint][1]+=7.475
      for _ in range(turn):
       a,b=e['from'],e['to'];e['from']=[16-b[2],a[1],a[0]];e['to']=[16-a[2],b[1],b[0]]
      elements.append(e)
    model['elements']=elements;scale=.70;rotation=[60,225,0]
   else:
    lo=min(e['from'][1] for e in model['elements']);hi=max(e['to'][1] for e in model['elements']);shift=8-(lo+hi)/2
    for e in model['elements']:
     e['from'][1]+=shift;e['to'][1]+=shift
    scale=1.65 if form=='fixture' else 1.65;rotation=[50,225,0] if form=='fixture' else [25,225,0]
   model['display']={'gui':{'rotation':rotation,'translation':[0,0,0],'scale':[scale]*3}}
   write('assets/lumaworks/models/item/'+base+'.json',model)
   for suffix in ['','_redstone','_redstone_inverted']:
    path='assets/lumaworks/items/'+base+suffix+'.json';old=read(path)['model']
    write(path,{'model':{'type':'minecraft:select','property':'minecraft:display_context',
      'cases':[{'when':'gui','model':{'type':'minecraft:model','model':'lumaworks:item/'+base}}],
      'fallback':old}})
