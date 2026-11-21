"""Generates scene.rml for the Nippia league tier badge (Perunggu, Perak, Emas, Safir, Berlian).

State machine `TierState`:
  - tier (number)        0=Perunggu, 1=Perak, 2=Emas, 3=Safir, 4=Berlian
  - isLocked (bool)      dims the badge and shows a lock glyph (tier not reached yet)
  - promoteTrigger       one-shot pop + glow ring (promotion celebration)

Run: python generate_scene.py  (writes scene.rml next to this file)
"""
from pathlib import Path

# ---------------------------------------------------------------- palette
TIERS = [
    # name,      rim,        inner,      ledge,      sparkles
    ("Perunggu", "FFE7A977", "FFCD7F32", "FF8A4B20", 0.0),
    ("Perak",    "FFF1F5F9", "FF94A3B8", "FF64748B", 0.0),
    ("Emas",     "FFFDE68A", "FFF59E0B", "FFB45309", 1.0),
    ("Safir",    "FF93C5FD", "FF2563EB", "FF1E3A8A", 1.0),
    ("Berlian",  "FFE0F2FE", "FF22D3EE", "FF0E7490", 1.0),
]
GLYPH_IDS = ["0:300", "0:310", "0:320", "0:330", "0:340"]

RIM_ID, INNER_ID, LEDGE_ID = "0:201", "0:211", "0:221"
SPARKLES_ID = "0:400"
BOB_ID, PROMOTE_ROOT_ID, RING_ID, SHINE_ID = "0:120", "0:110", "0:130", "0:240"
LOCK_ID = "0:500"
SPARKLE_STAR_IDS = ["0:410", "0:420", "0:430"]

TIER_ANIM_BASE = 30  # 0:30..0:34


def shield_path(s: float) -> str:
    pts = [(-66, -72, 18), (66, -72, 18), (64, 12, 34), (0, 86, 12), (-64, 12, 34)]
    verts = "\n".join(
        f'                        <StraightVertex x="{x * s:.1f}" y="{y * s:.1f}" radius="{r * s:.1f}"/>'
        for x, y, r in pts
    )
    return f'<PointsPath isClosed="true" name="Path">\n{verts}\n                    </PointsPath>'


def key(obj: str, prop: int, frames: str) -> str:
    return f'            <KeyedObject objectId="{obj}"><KeyedProperty propertyKey="{prop}">{frames}</KeyedProperty></KeyedObject>'


def hold_color(v: str) -> str:
    return f'<KeyFrameColor value="{v}" frame="0" interpolationType="hold"/>'


def hold_double(v: float) -> str:
    return f'<KeyFrameDouble value="{v}" frame="0" interpolationType="hold"/>'


EASE = '<CubicEaseInterpolator x1="0.42" y1="0" x2="0.58" y2="1"/>'


def ease_frames(values) -> str:
    out = []
    for i, (frame, v) in enumerate(values):
        last = i == len(values) - 1
        if last:
            out.append(f'<KeyFrameDouble value="{v}" frame="{frame}" interpolationType="linear"/>')
        else:
            out.append(f'<KeyFrameDouble value="{v}" frame="{frame}" interpolationType="cubic">{EASE}</KeyFrameDouble>')
    return "".join(out)


# ---------------------------------------------------------------- state machine
def tier_layer() -> str:
    states = []
    for k in range(5):
        trans = []
        for j in range(5):
            if j == k:
                continue
            conds = []
            if j > 0:
                conds.append(f'<TransitionNumberCondition inputId="0:11" opValue="greaterThanOrEqual" value="{j}"/>')
            if j < 4:
                conds.append(f'<TransitionNumberCondition inputId="0:11" opValue="lessThan" value="{j + 1}"/>')
            trans.append(
                f'                    <StateTransition stateToId="0:{21 + j}" duration="220">\n'
                + "\n".join(f"                        {c}" for c in conds)
                + "\n                    </StateTransition>"
            )
        states.append(
            f'                <!-- {k}: {TIERS[k][0]} -->\n'
            f'                <AnimationState x="{160 + k * 180}" y="40" animationId="0:{TIER_ANIM_BASE + k}" id="0:{21 + k}">\n'
            + "\n".join(trans)
            + "\n                </AnimationState>"
        )
    return f"""            <StateMachineLayer name="TierLayer" id="0:20">
                <AnyState x="200" y="-120"/>
                <ExitState x="900" y="-120"/>
                <EntryState>
                    <StateTransition stateToId="0:21"/>
                </EntryState>
{chr(10).join(states)}
            </StateMachineLayer>"""


STATE_MACHINE = f"""        <StateMachine name="TierState" id="0:10">
            <StateMachineNumber value="0" name="tier" id="0:11"/>
            <StateMachineBool value="false" name="isLocked" id="0:12"/>
            <StateMachineTrigger name="promoteTrigger" id="0:13"/>

            <!-- Layer 1: Tier palette + emblem -->
{tier_layer()}

            <!-- Layer 2: Idle float, shine and sparkle twinkle -->
            <StateMachineLayer name="IdleLayer" id="0:40">
                <AnyState x="200" y="-120"/>
                <ExitState x="600" y="-120"/>
                <EntryState>
                    <StateTransition stateToId="0:41"/>
                </EntryState>
                <AnimationState x="200" y="40" animationId="0:50" id="0:41"/>
            </StateMachineLayer>

            <!-- Layer 3: Promotion celebration -->
            <StateMachineLayer name="PromoteLayer" id="0:60">
                <AnyState x="200" y="-120"/>
                <ExitState x="600" y="-120"/>
                <EntryState>
                    <StateTransition stateToId="0:61"/>
                </EntryState>
                <AnimationState x="200" y="40" animationId="0:51" id="0:61">
                    <StateTransition stateToId="0:62" duration="0">
                        <TransitionTriggerCondition inputId="0:13"/>
                    </StateTransition>
                </AnimationState>
                <AnimationState x="420" y="40" animationId="0:52" reset="true" id="0:62">
                    <StateTransition stateToId="0:61" duration="100" enableExitTime="true" exitTime="900"/>
                </AnimationState>
            </StateMachineLayer>

            <!-- Layer 4: Locked overlay -->
            <StateMachineLayer name="LockLayer" id="0:70">
                <AnyState x="200" y="-120"/>
                <ExitState x="600" y="-120"/>
                <EntryState>
                    <StateTransition stateToId="0:71"/>
                </EntryState>
                <AnimationState x="200" y="40" animationId="0:53" id="0:71">
                    <StateTransition stateToId="0:72" duration="180">
                        <TransitionBoolCondition inputId="0:12" opValue="equal"/>
                    </StateTransition>
                </AnimationState>
                <AnimationState x="420" y="40" animationId="0:54" id="0:72">
                    <StateTransition stateToId="0:71" duration="180">
                        <TransitionBoolCondition inputId="0:12" opValue="notEqual"/>
                    </StateTransition>
                </AnimationState>
            </StateMachineLayer>
        </StateMachine>"""


# ---------------------------------------------------------------- animations
def tier_anims() -> str:
    out = []
    for k, (name, rim, inner, ledge, sparkles) in enumerate(TIERS):
        keys = [
            key(RIM_ID, 37, hold_color(rim)),
            key(INNER_ID, 37, hold_color(inner)),
            key(LEDGE_ID, 37, hold_color(ledge)),
            key(SPARKLES_ID, 18, hold_double(sparkles)),
        ] + [key(gid, 18, hold_double(1.0 if i == k else 0.0)) for i, gid in enumerate(GLYPH_IDS)]
        out.append(
            f'        <LinearAnimation duration="10" loopValue="oneShot" name="Tier{name}" id="0:{TIER_ANIM_BASE + k}">\n'
            + "\n".join(keys)
            + "\n        </LinearAnimation>"
        )
    return "\n\n".join(out)


def idle_anim() -> str:
    keys = [
        key(BOB_ID, 14, ease_frames([(0, 0), (60, -6), (120, 0)])),
        key(SHINE_ID, 18, ease_frames([(0, 0.35), (60, 0.7), (120, 0.35)])),
    ]
    for i, sid in enumerate(SPARKLE_STAR_IDS):
        off = i * 40
        frames = [(0, 0.5), (off % 120, 0.5), ((off + 30) % 120 or 119, 1.15), ((off + 60) % 120 or 119, 0.5), (120, 0.5)]
        frames = sorted({f: v for f, v in frames}.items())
        keys.append(key(sid, 16, ease_frames(frames)))
        keys.append(key(sid, 17, ease_frames(frames)))
    return (
        '        <LinearAnimation duration="120" loopValue="loop" name="IdleFloat" id="0:50">\n'
        + "\n".join(keys)
        + "\n        </LinearAnimation>"
    )


PROMOTE_ANIMS = f"""        <LinearAnimation duration="10" loopValue="oneShot" name="PromoteIdle" id="0:51">
{key(PROMOTE_ROOT_ID, 16, hold_double(1.0))}
{key(PROMOTE_ROOT_ID, 17, hold_double(1.0))}
{key(RING_ID, 18, hold_double(0.0))}
        </LinearAnimation>

        <LinearAnimation duration="54" loopValue="oneShot" name="PromotePop" id="0:52">
{key(PROMOTE_ROOT_ID, 16, ease_frames([(0, 1.0), (12, 1.28), (26, 0.94), (40, 1.04), (54, 1.0)]))}
{key(PROMOTE_ROOT_ID, 17, ease_frames([(0, 1.0), (12, 1.28), (26, 0.94), (40, 1.04), (54, 1.0)]))}
{key(PROMOTE_ROOT_ID, 15, ease_frames([(0, 0), (12, -0.12), (26, 0.08), (40, 0), (54, 0)]))}
{key(RING_ID, 18, ease_frames([(0, 0.0), (8, 1.0), (54, 0.0)]))}
{key(RING_ID, 16, ease_frames([(0, 0.7), (54, 1.45)]))}
{key(RING_ID, 17, ease_frames([(0, 0.7), (54, 1.45)]))}
        </LinearAnimation>"""

LOCK_ANIMS = f"""        <LinearAnimation duration="10" loopValue="oneShot" name="Unlocked" id="0:53">
{key(LOCK_ID, 18, hold_double(0.0))}
        </LinearAnimation>

        <LinearAnimation duration="10" loopValue="oneShot" name="Locked" id="0:54">
{key(LOCK_ID, 18, hold_double(1.0))}
        </LinearAnimation>"""


# ---------------------------------------------------------------- artwork (front-to-back)
def star(x, y, size, color, name, points=5, inner=0.48, node_id=None) -> str:
    id_attr = f' id="{node_id}"' if node_id else ""
    return f"""                    <Shape x="{x}" y="{y}" name="{name}"{id_attr}>
                        <Star width="{size}" height="{size}" points="{points}" innerRadius="{inner}" cornerRadius="3" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="{color}" name="Color"/></Fill>
                    </Shape>"""


GLYPHS = f"""                <!-- Perunggu: 1 bintang -->
                <Node x="0" y="2" name="GlyphPerunggu" id="{GLYPH_IDS[0]}">
{star(0, 0, 64, "FFFFF1E0", "Star")}
                </Node>

                <!-- Perak: 2 bintang -->
                <Node x="0" y="2" opacity="0" name="GlyphPerak" id="{GLYPH_IDS[1]}">
{star(-24, 0, 46, "FFFFFFFF", "StarL")}
{star(24, 0, 46, "FFFFFFFF", "StarR")}
                </Node>

                <!-- Emas: mahkota 3 bintang -->
                <Node x="0" y="4" opacity="0" name="GlyphEmas" id="{GLYPH_IDS[2]}">
{star(0, -10, 54, "FFFFFBEB", "StarC")}
{star(-36, 8, 34, "FFFFFBEB", "StarL")}
{star(36, 8, 34, "FFFFFBEB", "StarR")}
                    <Shape x="0" y="30" name="CrownBase">
                        <Rectangle width="76" height="10" cornerRadiusTL="5" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="FFFFFBEB" name="Color"/></Fill>
                    </Shape>
                </Node>

                <!-- Safir: permata heksagon -->
                <Node x="0" y="2" opacity="0" name="GlyphSafir" id="{GLYPH_IDS[3]}">
                    <Shape x="-8" y="-12" name="Facet">
                        <Polygon width="26" height="30" points="6" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="88FFFFFF" name="Color"/></Fill>
                    </Shape>
                    <Shape x="0" y="0" name="GemCore">
                        <Polygon width="52" height="60" points="6" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="FF60A5FA" name="Color"/></Fill>
                    </Shape>
                    <Shape x="0" y="0" name="GemRim">
                        <Polygon width="74" height="84" points="6" cornerRadius="6" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="FFDBEAFE" name="Color"/></Fill>
                    </Shape>
                </Node>

                <!-- Berlian: permata berlian -->
                <Node x="0" y="4" opacity="0" name="GlyphBerlian" id="{GLYPH_IDS[4]}">
                    <Shape x="0" y="0" name="Facets">
                        <PointsPath isClosed="false" name="Path">
                            <StraightVertex x="-38" y="-14"/>
                            <StraightVertex x="38" y="-14"/>
                        </PointsPath>
                        <Stroke thickness="4" join="round" name="Line"><SolidColor colorValue="FF67E8F9" name="Color"/></Stroke>
                    </Shape>
                    <Shape x="0" y="0" name="FacetV">
                        <PointsPath isClosed="false" name="Path">
                            <StraightVertex x="-14" y="-14"/>
                            <StraightVertex x="0" y="36"/>
                            <StraightVertex x="14" y="-14"/>
                        </PointsPath>
                        <Stroke thickness="4" join="round" name="Line"><SolidColor colorValue="FF67E8F9" name="Color"/></Stroke>
                    </Shape>
                    <Shape x="0" y="0" name="Gem">
                        <PointsPath isClosed="true" name="Path">
                            <StraightVertex x="-38" y="-14" radius="3"/>
                            <StraightVertex x="-20" y="-34" radius="3"/>
                            <StraightVertex x="20" y="-34" radius="3"/>
                            <StraightVertex x="38" y="-14" radius="3"/>
                            <StraightVertex x="0" y="38" radius="3"/>
                        </PointsPath>
                        <Fill name="Fill"><SolidColor colorValue="FFFFFFFF" name="Color"/></Fill>
                    </Shape>
                </Node>"""


ARTWORK = f"""        <!-- Promotion glow ring (front) -->
        <Shape x="128" y="132" opacity="0" name="PromoteRing" id="{RING_ID}">
            <Ellipse width="200" height="200" originX="0.5" originY="0.5" name="Path"/>
            <Stroke thickness="8" name="Ring"><SolidColor colorValue="FFFDE047" name="Glow"/></Stroke>
        </Shape>

        <Node x="128" y="124" name="PromoteRoot" id="{PROMOTE_ROOT_ID}">
            <Node x="0" y="0" name="BobGroup" id="{BOB_ID}">
                <!-- Lock overlay (isLocked) -->
                <Node x="0" y="0" opacity="0" name="LockOverlay" id="{LOCK_ID}">
                    <Shape x="0" y="-10" name="Shackle">
                        <Rectangle width="30" height="30" cornerRadiusTL="15" originX="0.5" originY="0.5" name="Path"/>
                        <Stroke thickness="7" name="Stroke"><SolidColor colorValue="FFFFFFFF" name="White"/></Stroke>
                    </Shape>
                    <Shape x="0" y="14" name="LockBody">
                        <Rectangle width="48" height="36" cornerRadiusTL="8" originX="0.5" originY="0.5" name="Path"/>
                        <Fill name="Fill"><SolidColor colorValue="FFFFFFFF" name="White"/></Fill>
                    </Shape>
                    <Shape x="0" y="0" name="Dim">
                        {shield_path(1.0)}
                        <Fill name="Fill"><SolidColor colorValue="C8475569" name="Slate"/></Fill>
                    </Shape>
                    <Shape x="0" y="8" name="DimLedge">
                        {shield_path(1.0)}
                        <Fill name="Fill"><SolidColor colorValue="C8334155" name="Slate"/></Fill>
                    </Shape>
                </Node>

                <!-- Sparkles (Emas, Safir, Berlian) -->
                <Node x="0" y="0" opacity="0" name="Sparkles" id="{SPARKLES_ID}">
{star(-86, -66, 26, "FFFFFFFF", "SparkleA", points=4, inner=0.28, node_id=SPARKLE_STAR_IDS[0])}
{star(84, -52, 20, "FFFEF3C7", "SparkleB", points=4, inner=0.28, node_id=SPARKLE_STAR_IDS[1])}
{star(78, 62, 22, "FFFFFFFF", "SparkleC", points=4, inner=0.28, node_id=SPARKLE_STAR_IDS[2])}
                </Node>

{GLYPHS}

                <!-- Specular shine -->
                <Shape x="-30" y="-50" rotation="-0.5" name="Shine" id="{SHINE_ID}">
                    <Ellipse width="36" height="14" originX="0.5" originY="0.5" name="Path"/>
                    <Fill name="Fill"><SolidColor colorValue="FFFFFFFF" name="Shine"/></Fill>
                </Shape>

                <!-- Inner shield -->
                <Shape x="0" y="2" name="ShieldInner">
                    {shield_path(0.8)}
                    <Fill name="Fill"><SolidColor colorValue="FFCD7F32" name="InnerColor" id="{INNER_ID}"/></Fill>
                </Shape>

                <!-- Outer rim -->
                <Shape x="0" y="0" name="ShieldRim">
                    {shield_path(1.0)}
                    <Fill name="Fill"><SolidColor colorValue="FFE7A977" name="RimColor" id="{RIM_ID}"/></Fill>
                </Shape>

                <!-- 3D ledge -->
                <Shape x="0" y="8" name="ShieldLedge">
                    {shield_path(1.0)}
                    <Fill name="Fill"><SolidColor colorValue="FF8A4B20" name="LedgeColor" id="{LEDGE_ID}"/></Fill>
                </Shape>
            </Node>
        </Node>"""


SCENE = f"""<Rive version="1" kind="fragment">
    <Artboard defaultStateMachineId="0:10" styleId="0:5" width="256" height="256" name="LeagueTierArtboard" id="0:2">
        <LayoutComponentStyle name="Artboard Style" id="0:5"/>

        <Fill name="Background">
            <SolidColor colorValue="00000000" name="Transparent"/>
        </Fill>

{STATE_MACHINE}

{tier_anims()}

{idle_anim()}

{PROMOTE_ANIMS}

{LOCK_ANIMS}

{ARTWORK}
    </Artboard>
</Rive>
"""

if __name__ == "__main__":
    out = Path(__file__).with_name("scene.rml")
    out.write_text(SCENE, encoding="utf-8")
    print(f"wrote {out} ({len(SCENE)} chars)")
