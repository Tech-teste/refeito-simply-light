"""Original, deterministic resources for the Luma Works prototype. Python standard library only."""
from pathlib import Path
import json
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources'
COLORS = dict(white='e8f3f5', orange='f7a345', magenta='d266d7', light_blue='7ec9ed',
              yellow='f5df6a', lime='a7d564', pink='f5a4c0', gray='646d79', light_gray='b6c1c9',
              cyan='43b9bd', purple='9869d6', blue='658bea', brown='ae8264', green='6aaf71',
              red='e56d70', black='414953')
FORMS = ['signal', 'inverse', 'slab', 'panel', 'fixture', 'bulb', 'strip_low', 'strip_high', 'column', 'tube']
PT_COLORS = ['Branca', 'Laranja', 'Magenta', 'Azul-clara', 'Amarela', 'Verde-lima', 'Rosa', 'Cinza',
             'Cinza-clara', 'Ciano', 'Roxa', 'Azul', 'Marrom', 'Verde', 'Vermelha', 'Preta']
PT_FORMS = ['Luz de sinal', 'Luz invertida', 'Laje luminosa', 'Placa luminosa', 'Arandela',
            'Lâmpada', 'Fita inferior', 'Fita superior', 'Coluna luminosa', 'Barra luminosa']


def write(path, data):
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def png(path, color, seamless=False):
    def chunk(kind, data):
        return struct.pack('>I', len(data)) + kind + data + struct.pack('>I', zlib.crc32(kind + data))
    rgb = tuple(bytes.fromhex(color))
    rows = b''
    for y in range(32):
        rows += b'\0'
        for x in range(32):
            # Own 32px graphite frame and beveled diffuser. Never sampled from reference assets.
            edge = min(x, y, 31-x, 31-y)
            corner = min(x,31-x) + min(y,31-y)
            if seamless:
                pixel = rgb
            elif edge < 2 or corner < 6:
                pixel = (47, 51, 57) if edge == 0 else (66, 71, 78)
            elif edge == 2 or corner == 6:
                pixel = tuple(round(v * .83) for v in rgb)
            else:
                pixel = rgb
            rows += bytes(pixel)
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_bytes(b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', 32,32,8,2,0,0,0)) + chunk(b'IDAT', zlib.compress(rows)) + chunk(b'IEND', b''))


def model(name, color, boxes, dark=False, seamless=False):
    texture = 'lumaworks:block/unlit' if dark else f'lumaworks:block/{color}'
    if seamless: texture += '_smooth'
    elements = []
    for start, end in boxes:
        elements.append({'from': start, 'to': end, 'shade': False,
                         'faces': {d: dict(texture='#light', **({'cullface':d} if start == [0,0,0] and end == [16,16,16] else {})) for d in ['up','down','north','south','east','west']}})
    write(f'assets/lumaworks/models/block/{name}.json',
          {'parent': 'minecraft:block/block', 'ambientocclusion': False,
           'textures': {'light': texture, 'particle': texture}, 'elements': elements})


def small_fixture_model(name, color, form):
    """Separate the housing from the diffuser; small lights do not reuse cube-face borders."""
    if form == 'bulb':
        parts = [([5.5,0,5.5],[10.5,.75,10.5],'metal'),
                 ([6.5,.75,6.5],[9.5,2,9.5],'socket'),
                 ([6.5,2,6.5],[9.5,6.5,9.5],'light'),
                 ([7,6.5,7],[9,7,9],'light')]
    elif form == 'fixture':
        parts = [([4,0,4],[12,.8,12],'metal'),
                 ([4.5,.8,4.5],[11.5,1.2,11.5],'socket'),
                 ([5,1.2,5],[11,2,11],'light')]
    elif form == 'strip_low':
        parts = [([0,0,0],[16,.7,1.4],'metal'),
                 ([0,.7,.2],[16,1.05,1.2],'light')]
    elif form == 'strip_high':
        parts = [([0,15.3,0],[16,16,1.4],'metal'),
                 ([0,15,.2],[16,15.3,1.2],'light')]
    else:
        raise ValueError(form)
    elements = [{'from':start, 'to':end, 'shade':material!='light',
                 'faces':{d:{'texture':'#'+material} for d in ['up','down','north','south','east','west']}}
                for start,end,material in parts]
    write(f'assets/lumaworks/models/block/{name}.json',
          {'parent':'minecraft:block/block','ambientocclusion':False,
           'textures':{'metal':'lumaworks:block/housing_graphite',
                       'socket':'lumaworks:block/socket_graphite',
                       'light':f'lumaworks:block/{color}_smooth',
                       'particle':f'lumaworks:block/{color}_smooth'},
           'elements':elements})


def recipe(name, result, count=1, ingredients=None, pattern=None, key=None):
    data = {'type': 'minecraft:crafting_shaped' if pattern else 'minecraft:crafting_shapeless',
            'category': 'building', 'result': {'id': f'lumaworks:{result}', 'count': count}}
    if pattern:
        data.update(pattern=pattern, key=key)
    else:
        data['ingredients'] = ingredients
    write(f'data/lumaworks/recipe/{name}.json', data)


def generate():
    en = {'itemGroup.lumaworks': 'Refeito: Simply Light'}
    pt = {'itemGroup.lumaworks': 'Refeito: Simply Light'}
    en.update({'message.lumaworks.framed':'Graphite frame', 'message.lumaworks.seamless':'Seamless surface — place matching lights together'})
    pt.update({'message.lumaworks.framed':'Moldura grafite', 'message.lumaworks.seamless':'Superfície contínua — junte peças da mesma cor'})
    en.update({'gui.lumaworks.edge_editor':'Light strip directions', 'gui.lumaworks.north':'North',
               'gui.lumaworks.east':'East', 'gui.lumaworks.south':'South', 'gui.lumaworks.west':'West',
               'gui.lumaworks.all':'All sides', 'gui.lumaworks.done':'Done',
               'gui.lumaworks.hint':'Choose sides. Changes save instantly.', 'gui.lumaworks.facing':'You face: %s'})
    pt.update({'gui.lumaworks.edge_editor':'Direções da fita luminosa', 'gui.lumaworks.north':'Norte',
               'gui.lumaworks.east':'Leste', 'gui.lumaworks.south':'Sul', 'gui.lumaworks.west':'Oeste',
               'gui.lumaworks.all':'Todos os lados', 'gui.lumaworks.done':'Concluir',
               'gui.lumaworks.hint':'Escolha os lados. Salvo na hora.', 'gui.lumaworks.facing':'Você olha para: %s'})
    png('assets/lumaworks/textures/block/unlit.png', '333c49')
    png('assets/lumaworks/textures/block/unlit_smooth.png', '333c49', seamless=True)
    png('assets/lumaworks/textures/block/housing_graphite.png', '30353c', seamless=True)
    png('assets/lumaworks/textures/block/socket_graphite.png', '505861', seamless=True)
    names = []
    for ci, (color, hexcolor) in enumerate(COLORS.items()):
        png(f'assets/lumaworks/textures/block/{color}.png', hexcolor)
        png(f'assets/lumaworks/textures/block/{color}_smooth.png', hexcolor, seamless=True)
        for fi, form in enumerate(FORMS):
            name = f'{color}_{form}'
            names.append(name)
            en[f'block.lumaworks.{name}'] = f'{color.replace("_", " ").title()} {form.replace("_", " ").title()}'
            pt[f'block.lumaworks.{name}'] = f'{PT_FORMS[fi]} {PT_COLORS[ci].lower()}'
            boxes = {
                'signal': [([0,0,0],[16,16,16])], 'inverse': [([0,0,0],[16,16,16])],
                'slab': [([0,0,0],[16,8,16])], 'panel': [([0,0,0],[16,4,16])],
                'fixture': [([4,0,4],[12,2,12])], 'bulb': [([5.5,0,5.5],[10.5,7,10.5])],
                'tube': [([6.5,0,6.5],[9.5,16,9.5])],
                'strip_low': [([0,0,0],[16,2,2])], 'strip_high': [([0,14,0],[16,16,2])],
                'column': [([3,0,3],[13,3,13]),([6,3,6],[10,16,10])]
            }[form]
            model(name, color, boxes)
            if form in ('bulb','fixture','strip_low','strip_high'):
                small_fixture_model(name, color, form)
            if form in ('signal','inverse'):
                model(name+'_off', color, boxes, dark=True)
                model(name+'_smooth', color, boxes, seamless=True)
                model(name+'_off_smooth', color, boxes, dark=True, seamless=True)
                state = {'variants': {f'lit={lit},framed={framed}': {'model': f'lumaworks:block/{name}'+('' if lit=='true' else '_off')+('' if framed=='true' else '_smooth')}
                    for lit in ['true','false'] for framed in ['true','false']}}
            elif form == 'column':
                model(name+'_mid', color, [([6,0,6],[10,16,10])], dark=True)
                model(name+'_top', color, [([6,0,6],[10,5,10]),([4,5,4],[12,15,12])])
                state = {'variants': {'part=0': {'model':f'lumaworks:block/{name}'}, 'part=1': {'model':f'lumaworks:block/{name}_mid'}, 'part=2': {'model':f'lumaworks:block/{name}_top'}}}
            elif form.startswith('strip'):
                parts = []
                for mask in range(16):
                    for bit in range(4):
                        if ((mask or 15) & 1 << bit):
                            parts.append({'when': {'edges': str(mask)}, 'apply': {'model':f'lumaworks:block/{name}', 'y': bit*90}})
                state = {'multipart': parts}
            else:
                transforms = {'up':(0,0),'down':(180,0),'north':(90,0),'south':(90,180),'west':(90,270),'east':(90,90)}
                state = {'variants': {f'facing={d}': {'model':f'lumaworks:block/{name}', 'x':x, 'y':y} for d,(x,y) in transforms.items()}}
                if form in ('slab','panel'):
                    model(name+'_smooth', color, boxes, seamless=True)
                    state = {'variants': {f'facing={d},framed={framed}': {'model':f'lumaworks:block/{name}'+('' if framed=='true' else '_smooth'), 'x':x, 'y':y}
                        for d,(x,y) in transforms.items() for framed in ['true','false']}}
            write(f'assets/lumaworks/blockstates/{name}.json', state)
            write(f'assets/lumaworks/items/{name}.json', {'model':{'type':'minecraft:model','model':f'lumaworks:block/{name}'}})
            pool = {'rolls':1,'entries':[{'type':'minecraft:item','name':f'lumaworks:{name}'}], 'conditions':[{'condition':'minecraft:survives_explosion'}]}
            if form in ('signal','inverse','slab','panel'):
                pool['entries'][0]['functions'] = [{'function':'minecraft:copy_state', 'block':f'lumaworks:{name}', 'properties':['framed']}]
            write(f'data/lumaworks/loot_table/blocks/{name}.json', {'type':'minecraft:block','pools':[pool]})
            # Original recipes: glass + glowstone dust; not the reference mod's stone/glowstone layout.
            patterns = {'signal':['GGG','DRD','GGG'], 'slab':['GGG','DDD'],
                        'fixture':['G G','DID'], 'bulb':[' G ',' D ',' I '],
                        'strip_low':['GGG','IDI'], 'column':[' G ',' I ','III'],
                        'tube':['IGI',' D ','IGI']}
            if color == 'white' and form in patterns:
                pattern = patterns[form]
                ingredients = {'G':'minecraft:glass','D':'minecraft:glowstone_dust',
                               'I':'minecraft:iron_nugget','R':'minecraft:redstone'}
                if form == 'column': ingredients['G'] = 'lumaworks:white_signal'
                recipe(name, name, 4 if form != 'column' else 1, pattern=pattern,
                       key={k:v for k,v in ingredients.items() if any(k in row for row in pattern)})
            recipe(name+'_recolor', name, 8, pattern=['LLL','LDL','LLL'], key={'L':f'#lumaworks:{form}', 'D':f'minecraft:{color}_dye'})
            if form == 'signal': recipe(name+'_invert', f'{color}_inverse', ingredients=[f'lumaworks:{name}'])
            if form == 'inverse': recipe(name+'_normalize', f'{color}_signal', ingredients=[f'lumaworks:{name}'])
            if form == 'strip_low': recipe(name+'_raise', f'{color}_strip_high', ingredients=[f'lumaworks:{name}'])
            if form == 'strip_high': recipe(name+'_lower', f'{color}_strip_low', ingredients=[f'lumaworks:{name}'])
            if form == 'slab': recipe(name+'_split', f'{color}_panel', 2, ingredients=[f'lumaworks:{name}'])
            if form == 'panel': recipe(name+'_combine', f'{color}_slab', ingredients=[f'lumaworks:{name}',f'lumaworks:{name}'])
    for form in FORMS:
        write(f'data/lumaworks/tags/item/{form}.json', {'replace':False, 'values':[f'lumaworks:{c}_{form}' for c in COLORS]})
    write('data/minecraft/tags/block/mineable/pickaxe.json', {'replace':False,'values':[f'lumaworks:{n}' for n in names]})
    write('assets/lumaworks/lang/en_us.json', en)
    write('assets/lumaworks/lang/pt_br.json', pt)
    from draw_finishing_key import generate as draw_finishing_key
    draw_finishing_key(ROOT / 'assets/lumaworks/textures/item/finishing_key.png')
    # Finishing variants and editor translations are maintained as original data resources.
    for path, data in json.loads((Path(__file__).parent / 'finishing_resources.json').read_text(encoding='utf-8')).items():
        write(path, data)
    from recipe_book_resources import generate as recipe_book
    recipe_book(ROOT)
    print('Generated 224 independent lights, finishing tool and editor translations.')


if __name__ == '__main__':
    generate()
