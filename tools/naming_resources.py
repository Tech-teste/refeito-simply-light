"""Single final naming pass and additive semantic tags; legacy recipe tags stay intact."""
from pathlib import Path
import json
COLORS=['white','orange','magenta','light_blue','yellow','lime','pink','gray','light_gray','cyan','purple','blue','brown','green','red','black']
PT_M=['branco','laranja','magenta','azul-claro','amarelo','verde-lima','rosa','cinza','cinza-claro','ciano','roxo','azul','marrom','verde','vermelho','preto']
PT_F=['branca','laranja','magenta','azul-clara','amarela','verde-lima','rosa','cinza','cinza-clara','ciano','roxa','azul','marrom','verde','vermelha','preta']
# Names follow the original families; wall strips are explicitly manual, not dynamic.
FAMILIES={
 'signal':('Illuminant Block','Bloco luminoso','m','blocks'),
 'inverse':('Illuminant Block','Bloco luminoso','m','blocks'),
 'slab':('Illuminant Slab','Laje luminosa','f','slabs'),
 'panel':('Illuminant Panel','Painel luminoso','m','panels'),
 'fixture':('Illuminant Fixture','Luminária','f','fixtures'),
 'bulb':('Simple Light Bulb','Lâmpada simples','f','bulbs'),
 'tube':('Illuminant Rod','Barra luminosa','f','rods'),
 'column':('Illuminant Column','Coluna luminosa','f','columns'),
 'strip_low':('Dynamic Edge Light','Luz de borda dinâmica','f','edge_lights'),
 'strip_high':('Dynamic Edge Light','Luz de borda dinâmica','f','edge_lights'),
 'strip_wall':('Wall Edge Light','Luz de borda de parede','f','edge_lights')}

def describe(name):
 color=next(c for c in sorted(COLORS,key=len,reverse=True) if name.startswith(c+'_'))
 form=name[len(color)+1:];frameless=form.endswith('_seamless')
 if frameless:form=form[:-9]
 mode='always_on'
 if form.endswith('_redstone_inverted'):form=form[:-18];mode='inverted'
 elif form.endswith('_redstone'):form=form[:-9];mode='normal'
 if form=='signal':mode='normal'
 if form=='inverse':mode='inverted'
 return color,form,frameless,mode

def generate(root):
 root=Path(root);langs={l:json.loads((root/f'assets/lumaworks/lang/{l}.json').read_text(encoding='utf-8')) for l in ['en_us','pt_br']};tags={}
 for p in sorted((root/'assets/lumaworks/blockstates').glob('*.json')):
  name=p.stem;color,form,frameless,mode=describe(name);en,pt,gender,family=FAMILIES[form];ci=COLORS.index(color)
  english=color.replace('_',' ').title()+' '+en
  portuguese=pt+' '+(PT_M if gender=='m' else PT_F)[ci]
  qualifiers={'strip_low':('Bottom','inferior'),'strip_high':('Top','superior')}
  if form in qualifiers:
   a,b=qualifiers[form];english+=' ('+a+')';portuguese+=' ('+b+')'
  if mode!='always_on':
   english+=' — '+('Redstone' if mode=='normal' else 'Inverted Redstone')
   portuguese+=' — '+('Redstone' if mode=='normal' else 'Redstone invertida')
  if frameless:english+=' — Frameless';portuguese+=' — Sem moldura'
  langs['en_us']['block.lumaworks.'+name]=english;langs['pt_br']['block.lumaworks.'+name]=portuguese
  for tag in ['lights','families/'+family,'colors/'+color,'redstone/'+mode,'finishes/'+('frameless' if frameless else 'framed')]:tags.setdefault(tag,[]).append('lumaworks:'+name)
 for lang,data in langs.items():(root/f'assets/lumaworks/lang/{lang}.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 for kind in ['block','item']:
  for tag,values in tags.items():
   p=root/f'data/lumaworks/tags/{kind}/{tag}.json';p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps({'replace':False,'values':sorted(values)},indent=2)+'\n')
 p=root/'data/lumaworks/tags/item/tools.json';p.write_text(json.dumps({'replace':False,'values':['lumaworks:finishing_key']},indent=2)+'\n')
