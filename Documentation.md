# MoreBosses Plugin - Complete Documentation 📚

## Table of Contents
1. [Overview](#overview)
2. [Pattern System](#pattern-system)
3. [Mechanics System](#mechanics-system)
4. [Boss AI System](#boss-ai-system)
5. [Custom Attacks](#custom-attacks)
7. [YAML Configuration](#yaml-configuration)

---

## Overview

The MoreBosses plugin provides a comprehensive boss creation system with:
- **Custom Attacks** - YAML-configurable boss abilities
- **Projectile Patterns** - Geometric patterns (Circle, Rain, Spiral, Scatter)
- **Mechanics System** - Modular ability components (10+ mechanics)
- **Boss AI** - Intelligent attack rotation and targeting
- **Loop/Random Modes** - Predictable sequences or random abilities

---

## Pattern System

### What Are Patterns?

Patterns control how projectiles spawn in custom attacks. Instead of simply shooting at the target, projectiles can form geometric shapes.

### Available Patterns

#### 1. CIRCLE ⭕
Perfect ring around target's position when attack is cast.

**Use Cases:**
- Meteor strikes falling in a ring
- Arrow barrages creating escape zones
- Ice shards forming barriers

**Configuration:**
```yaml
pattern:
  type: CIRCLE
  radius: 6.0              # Distance from target center
  heightOffset: 15.0       # How high above target
  waves: 3                 # Number of volleys
  meteorsPerWave: 8        # Projectiles per circle
  intervalTicks: 10        # Delay between waves (20 ticks = 1 sec)
```

**Example Visual:**
```
        🔥
      🔥   🔥
    🔥   🎯   🔥    (8 fireballs in perfect ring)
      🔥   🔥
        🔥
```

#### 2. RAIN 🌧️
Random projectiles falling within radius.

**Use Cases:**
- Chaotic arrow storms
- Overwhelming bombardments
- Area saturation

**Configuration:**
```yaml
pattern:
  type: RAIN
  radius: 10.0             # Spread area
  heightOffset: 20.0       # Spawn height
  waves: 5                 # Number of waves
  meteorsPerWave: 12       # Projectiles per wave
  intervalTicks: 12        # Wave frequency
```

#### 3. SPIRAL 🌀
Expanding spiral pattern from center.

**Use Cases:**
- Hypnotic attacks
- Expanding threats
- Visual spectacle

**Configuration:**
```yaml
pattern:
  type: SPIRAL
  radius: 8.0              # Maximum spiral radius
  heightOffset: 15.0       # Spawn height
  waves: 1                 # Usually 1 for continuous spiral
  meteorsPerWave: 24       # Total projectiles in spiral
  intervalTicks: 2         # Spacing between projectiles
```

#### 4. SCATTER 💥
Completely random placement around target.

**Use Cases:**
- Chaos bombardments
- Unpredictable attacks
- Maximum coverage

**Configuration:**
```yaml
pattern:
  type: SCATTER
  radius: 15.0             # Scatter area
  heightOffset: 25.0       # High drop point
  waves: 1                 # Single bombardment
  meteorsPerWave: 20       # Total projectiles
  intervalTicks: 1         # Nearly instant
```

### Pattern Parameters Reference

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `type` | String | CIRCLE, RAIN, SPIRAL, or SCATTER | CIRCLE |
| `radius` | Double | Pattern size in blocks | 6.0 |
| `heightOffset` | Double | Spawn height above target | 15.0 |
| `waves` | Integer | Number of pattern repetitions | 1 |
| `meteorsPerWave` | Integer | Projectiles per wave | 8 |
| `intervalTicks` | Integer | Delay between waves | 10 |

### Compatible Projectiles

All patterns work with:
- FIREBALL
- ARROW
- SNOWBALL
- EGG
- ENDER_PEARL
- WITHER_SKULL
- DRAGON_FIREBALL

### Important Notes

⚠️ **Target Position Locking**
- Pattern locks target position when attack is **cast**
- Target can dodge by moving after attack starts
- Creates fair, skill-based gameplay

✨ **Visual Feedback**
- Flame particles spawn at projectile spawn points
- Gives players brief warning of incoming danger

---

## Mechanics System

### What Are Mechanics?

Mechanics are modular ability components that can be combined in YAML to create complex boss attacks. Each mechanic is self-contained and reusable.

### Available Mechanics (10 Total)

#### 1. ArrowRain
Rains arrows from the sky above target.

**Parameters:**
```yaml
- id: arrowrain
  parameters:
    amount: 20            # Number of arrows
    spread: 4.5           # Spread radius in blocks
    velocity: 2.0         # Arrow speed
    fireticks: 0          # Fire duration (0 = no fire)
    removedelay: 200      # Ticks before despawn (200 = 10 sec)
    canpickup: false      # Can players pick up arrows?
    follows: false        # Follow target movement?
```

**Example:**
```yaml
mechanics:
  - id: arrowrain
    parameters:
      amount: 30
      spread: 6.0
      follows: true
      fireticks: 100
```

#### 2. Damage
Deals custom damage with optional elements.

**Parameters:**
```yaml
- id: damage
  parameters:
    amount: 5.0                # Damage amount
    ignorearmor: false         # Bypass armor?
    preventknockback: false    # No knockback?
    preventimmunity: false     # Reset damage cooldown?
    element: null              # FIRE, ICE, or LIGHTNING
    tags: ""                   # Comma-separated tags
```

**Elements:**
- `FIRE` - Sets target on fire (100 ticks)
- `ICE` - No built-in effect (use with potioneffect)
- `LIGHTNING` - Visual lightning strike effect

**Example:**
```yaml
mechanics:
  - id: damage
    parameters:
      amount: 15.0
      element: FIRE
      preventknockback: true
      tags: "BURNING,MAGIC"
```

#### 3. BlackScreen
Blacks out player's screen using blindness.

**Parameters:**
```yaml
- id: blackscreen
  parameters:
    duration: 40          # Duration in ticks (40 = 2 sec)
```

**Example:**
```yaml
mechanics:
  - id: blackscreen
    parameters:
      duration: 60
```

#### 4. Command
Executes a server command.

**Parameters:**
```yaml
- id: command
  parameters:
    command: ""           # Command to execute (required)
    ascaster: false       # Execute as caster entity?
    asop: false          # Execute with OP permissions?
    astarget: false      # Execute as target player?
```

**Placeholders:**
- `<caster.name>` - Caster's name
- `<caster.uuid>` - Caster's UUID
- `<target.name>` - Target's name
- `<target.uuid>` - Target's UUID

**Example:**
```yaml
mechanics:
  - id: command
    parameters:
      command: "give <target.name> diamond 5"
      asop: true
```

#### 5. PotionEffect
Applies potion effects to target.

**Parameters:**
```yaml
- id: potioneffect
  parameters:
    effects: []           # List of "EFFECT:duration:amplifier"
```

**Effect Format:** `EFFECT_NAME:duration_in_ticks:amplifier_level`

**Common Effects:**
- SLOWNESS
- WEAKNESS
- BLINDNESS
- POISON
- WITHER
- MINING_FATIGUE
- LEVITATION

**Example:**
```yaml
mechanics:
  - id: potioneffect
    parameters:
      effects:
        - "SLOWNESS:100:3"
        - "WEAKNESS:80:1"
        - "BLINDNESS:60:0"
```

#### 6. Sound
Plays a sound at target location.

**Parameters:**
```yaml
- id: sound
  parameters:
    sound: ""             # Sound name (required)
    volume: 1.0           # Volume (0.0 to 1.0+)
    pitch: 1.0            # Pitch (0.5 to 2.0)
```

**Common Sounds:**
- ENTITY_ENDER_DRAGON_GROWL
- ENTITY_BLAZE_SHOOT
- ENTITY_GENERIC_EXPLODE
- ENTITY_ARROW_SHOOT
- BLOCK_GLASS_BREAK

**Example:**
```yaml
mechanics:
  - id: sound
    parameters:
      sound: ENTITY_ENDER_DRAGON_GROWL
      volume: 2.0
      pitch: 0.5
```

#### 7. Particle
Spawns particles at target location.

**Parameters:**
```yaml
- id: particle
  parameters:
    particle: ""          # Particle type (required)
    amount: 10            # Number of particles
    offsetx: 0.5          # X spread
    offsety: 0.5          # Y spread
    offsetz: 0.5          # Z spread
    speed: 0.1            # Particle speed
```

**Common Particles:**
- FLAME
- EXPLOSION
- LAVA
- SNOWFLAKE
- PORTAL
- DRAGON_BREATH

**Example:**
```yaml
mechanics:
  - id: particle
    parameters:
      particle: FLAME
      amount: 50
      speed: 0.5
```

#### 8. Summon
Summons entities around the caster.

**Parameters:**
```yaml
- id: summon
  parameters:
    entity: ""            # Entity type (required)
    amount: 1             # Number to summon
    spread: 3.0           # Spread radius
```

**Common Entities:**
- ZOMBIE
- SKELETON
- ENDERMITE
- SILVERFISH
- BLAZE

**Example:**
```yaml
mechanics:
  - id: summon
    parameters:
      entity: ZOMBIE
      amount: 5
      spread: 5.0
```

#### 9. Explosion
Creates an explosion at target location.

**Parameters:**
```yaml
- id: explosion
  parameters:
    power: 2.0            # Explosion power
    setfire: false        # Create fire?
    breakblocks: false    # Break blocks?
```

**Example:**
```yaml
mechanics:
  - id: explosion
    parameters:
      power: 4.0
      setfire: true
      breakblocks: false
```

#### 10. Teleport
Teleports caster behind target.

**Parameters:**
```yaml
- id: teleport
  parameters:
    behind: false         # Teleport behind target?
    distance: 2.0         # Distance behind target
```

**Example:**
```yaml
mechanics:
  - id: teleport
    parameters:
      behind: true
      distance: 3.0
```

---

## Boss AI System

### Overview

The Boss AI Controller manages attack execution, cooldowns, and targeting for custom bosses.

### Attack Modes

#### Loop Mode (Default)
Attacks execute in sequential order with delays.

**Characteristics:**
- Predictable attack patterns
- 2 seconds between attacks in sequence
- 8 seconds delay after completing full loop
- Loops continuously until boss dies
- Best for designed encounters

**Timeline Example:**
```
Boss with: FIREBALL, METEOR_STRIKE, ICE_PRISON

0:00 - FIREBALL
0:02 - METEOR_STRIKE
0:04 - ICE_PRISON
0:06 - (Waiting - 8 second loop delay)
0:14 - FIREBALL (loop restarts)
0:16 - METEOR_STRIKE
0:18 - ICE_PRISON
```

#### Random Mode
Selects available attacks randomly with cooldowns.

**Characteristics:**
- Unpredictable attack patterns
- Category-based cooldowns (3-15 seconds)
- 2 second global cooldown between any attacks
- More challenging for players
- Best for chaotic fights

**Cooldowns by Category:**
- MELEE: 3 seconds
- RANGED: 4 seconds
- AOE: 6 seconds
- BUFF/DEBUFF: 10 seconds
- SPECIAL: 12 seconds
- SUMMON: 15 seconds

### Aggression Types

#### PASSIVE
Boss doesn't attack until hit.

**Use Cases:**
- Friendly NPCs that fight back
- Optional bosses
- Punish-on-attack mechanics

**Behavior:**
- Won't target players initially
- Becomes aggressive when damaged
- Stays aggressive after provoked

#### AGGRESSIVE
Boss attacks players on sight.

**Use Cases:**
- Standard boss encounters
- Hostile enemies
- Territory defenders

**Behavior:**
- Targets nearest player in range
- Uses special attacks on cooldown
- Pursues targets

#### NO_AI
Boss doesn't move or attack.

**Use Cases:**
- Stationary targets
- Dummy bosses
- Testing purposes

---

## Custom Attacks

### Attack Structure

```yaml
attacks:
  ATTACK_ID:
    displayName: "&cAttack Name"
    description: "What the attack does"
    category: AOE                    # MELEE, RANGED, AOE, SUMMON, BUFF, DEBUFF, SPECIAL
    icon: FIRE_CHARGE               # Material for GUI
    damage: 15.0                    # Base damage (can be 0 if mechanics handle it)
    range: 20.0                     # Attack range
    
    # Optional: Pattern system (for projectile attacks)
    effects:
      projectileType: FIREBALL
      pattern:
        type: CIRCLE
        radius: 6.0
        # ... pattern parameters
    
    # Optional: Mechanics system (modular abilities)
    mechanics:
      - id: mechanic_id
        parameters:
          param1: value1
          param2: value2
```

### Categories Explained

**MELEE** - Close-range attacks
- Short cooldown (3 seconds)
- High frequency
- Direct damage

**RANGED** - Projectile attacks
- Medium cooldown (4 seconds)
- Distance damage
- Can use patterns

**AOE** - Area of Effect
- Medium cooldown (6 seconds)
- Multi-target
- Zone control

**SUMMON** - Spawn entities
- Long cooldown (15 seconds)
- Adds reinforcements
- Strategic advantage

**BUFF** - Self-enhancement
- Long cooldown (10 seconds)
- Boss strengthening
- Temporary boosts

**DEBUFF** - Enemy weakening
- Long cooldown (10 seconds)
- Player hindrance
- Crowd control

**SPECIAL** - Unique abilities
- Long cooldown (12 seconds)
- Mixed effects
- Complex mechanics

---


---

## YAML Configuration

### File Structure

```
plugins/MoreBosses/
├── config.yml
├── Custom Bosses/
│   ├── FireLord.yml
│   └── IceMage.yml
└── Custom Attacks/
    ├── example_attacks.yml
    ├── fire_attacks.yml
    └── ice_attacks.yml
```

### Example Attack Files

#### Simple Attack (Damage Only)

```yaml
attacks:
  POWER_STRIKE:
    displayName: "&e&lPower Strike"
    description: "Powerful melee attack"
    category: MELEE
    icon: DIAMOND_SWORD
    damage: 20.0
    range: 5.0
```

#### Pattern Attack (Circle Meteors)

```yaml
attacks:
  METEOR_CIRCLE:
    displayName: "&c&lMeteor Circle"
    description: "Ring of meteors falls around target"
    category: AOE
    icon: FIRE_CHARGE
    damage: 15.0
    range: 20.0
    effects:
      projectileType: FIREBALL
      setFire: true
      fireTicks: 100
      pattern:
        type: CIRCLE
        radius: 6.0
        heightOffset: 15.0
        waves: 3
        meteorsPerWave: 8
        intervalTicks: 10
```

#### Mechanics-Based Attack

```yaml
attacks:
  ARROW_STORM:
    displayName: "&7&lArrow Storm"
    description: "Arrows rain from the sky"
    category: RANGED
    icon: ARROW
    damage: 0
    range: 30.0
    mechanics:
      - id: arrowrain
        parameters:
          amount: 30
          spread: 6.0
          follows: true
      - id: sound
        parameters:
          sound: ENTITY_ARROW_SHOOT
          volume: 1.0
```

#### Complex Multi-Mechanic Attack

```yaml
attacks:
  ULTIMATE_DESTRUCTION:
    displayName: "&c&l&nUltimate Destruction"
    description: "The boss's ultimate ability"
    category: SPECIAL
    icon: NETHER_STAR
    damage: 0
    range: 40.0
    mechanics:
      # Warning phase
      - id: sound
        parameters:
          sound: ENTITY_ENDER_DRAGON_GROWL
          volume: 2.0
          pitch: 0.5
      
      - id: particle
        parameters:
          particle: LAVA
          amount: 100
          offsetx: 5.0
          offsetz: 5.0
      
      # Blind players
      - id: blackscreen
        parameters:
          duration: 40
      
      # Rain arrows
      - id: arrowrain
        parameters:
          amount: 50
          spread: 8.0
          fireticks: 200
          follows: true
      
      # Apply debuffs
      - id: potioneffect
        parameters:
          effects:
            - "SLOWNESS:200:3"
            - "WEAKNESS:200:2"
      
      # Deal damage
      - id: damage
        parameters:
          amount: 30.0
          element: FIRE
          preventimmunity: true
      
      # Create explosion
      - id: explosion
        parameters:
          power: 4.0
          setfire: true
```

#### Pattern + Mechanics Combined

```yaml
attacks:
  FIRE_SPIRAL:
    displayName: "&6&lFire Spiral"
    description: "Spiraling fire attack with debuffs"
    category: AOE
    icon: BLAZE_POWDER
    damage: 10.0
    range: 25.0
    effects:
      projectileType: FIREBALL
      setFire: true
      pattern:
        type: SPIRAL
        radius: 10.0
        heightOffset: 15.0
        waves: 1
        meteorsPerWave: 24
        intervalTicks: 2
    mechanics:
      - id: potioneffect
        parameters:
          effects:
            - "BLINDNESS:100:0"
      - id: sound
        parameters:
          sound: ENTITY_BLAZE_SHOOT
```

### Example 1: Simple Fire Boss

**Attacks:**
```yaml
attacks:
  FIREBALL_BLAST:
    displayName: "&c&lFireball Blast"
    description: "Shoots a powerful fireball"
    category: RANGED
    icon: FIRE_CHARGE
    damage: 12.0
    range: 25.0
    effects:
      projectileType: FIREBALL
      explosionPower: 2.0
      setFire: true
  
  FLAME_AURA:
    displayName: "&6&lFlame Aura"
    description: "Burns nearby enemies"
    category: AOE
    icon: BLAZE_POWDER
    damage: 0
    range: 10.0
    mechanics:
      - id: damage
        parameters:
          amount: 8.0
          element: FIRE
      - id: particle
        parameters:
          particle: FLAME
          amount: 50
```

**Boss Setup:**
- Type: BLAZE
- Health: 300
- Attacks: FIREBALL_BLAST, FLAME_AURA
- Mode: Loop (predictable)

---

### Example 2: Ice Mage

**Attacks:**
```yaml
attacks:
  ICE_PRISON:
    displayName: "&b&lIce Prison"
    description: "Freezes enemies in place"
    category: DEBUFF
    icon: PACKED_ICE
    damage: 5.0
    range: 15.0
    mechanics:
      - id: potioneffect
        parameters:
          effects:
            - "SLOWNESS:200:4"
            - "MINING_FATIGUE:200:2"
      - id: particle
        parameters:
          particle: SNOWFLAKE
          amount: 30
      - id: sound
        parameters:
          sound: BLOCK_GLASS_BREAK
  
  BLIZZARD:
    displayName: "&f&lBlizzard"
    description: "Snowballs spiral outward"
    category: AOE
    icon: SNOWBALL
    damage: 3.0
    range: 20.0
    effects:
      projectileType: SNOWBALL
      pattern:
        type: SPIRAL
        radius: 8.0
        heightOffset: 12.0
        waves: 1
        meteorsPerWave: 24
        intervalTicks: 2
    mechanics:
      - id: potioneffect
        parameters:
          effects:
            - "SLOWNESS:100:2"
```

---

### Example 3: Shadow Assassin

**Attacks:**
```yaml
attacks:
  SHADOW_STRIKE:
    displayName: "&5&lShadow Strike"
    description: "Teleports behind and strikes"
    category: SPECIAL
    icon: ENDER_PEARL
    damage: 18.0
    range: 15.0
    mechanics:
      - id: teleport
        parameters:
          behind: true
          distance: 2.0
      - id: blackscreen
        parameters:
          duration: 30
      - id: damage
        parameters:
          amount: 18.0
      - id: particle
        parameters:
          particle: PORTAL
          amount: 50
  
  POISON_RAIN:
    displayName: "&2&lPoison Rain"
    description: "Toxic projectiles rain down"
    category: RANGED
    icon: SPIDER_EYE
    damage: 0
    range: 25.0
    mechanics:
      - id: arrowrain
        parameters:
          amount: 20
          spread: 6.0
          follows: false
      - id: potioneffect
        parameters:
          effects:
            - "POISON:200:2"
            - "NAUSEA:100:0"
```

---

### Example 4: Storm Titan

**Attacks:**
```yaml
attacks:
  LIGHTNING_BARRAGE:
    displayName: "&e&l⚡ Lightning Barrage"
    description: "Multiple lightning strikes"
    category: AOE
    icon: LIGHTNING_ROD
    damage: 0
    range: 30.0
    mechanics:
      - id: damage
        parameters:
          amount: 15.0
          element: LIGHTNING
      - id: sound
        parameters:
          sound: ENTITY_LIGHTNING_BOLT_THUNDER
          volume: 2.0
      - id: particle
        parameters:
          particle: ELECTRIC_SPARK
          amount: 100
  
  METEOR_APOCALYPSE:
    displayName: "&c&l☄ Meteor Apocalypse"
    description: "Ultimate meteor strike"
    category: SPECIAL
    icon: FIRE_CHARGE
    damage: 25.0
    range: 40.0
    effects:
      projectileType: FIREBALL
      explosionPower: 3.0
      setFire: true
      pattern:
        type: RAIN
        radius: 15.0
        heightOffset: 25.0
        waves: 3
        meteorsPerWave: 15
        intervalTicks: 10
    mechanics:
      - id: blackscreen
        parameters:
          duration: 60
      - id: sound
        parameters:
          sound: ENTITY_ENDER_DRAGON_GROWL
          volume: 2.0
          pitch: 0.5
```

---

## Quick Reference

### Pattern Types
- `CIRCLE` - Ring around target
- `RAIN` - Random within radius
- `SPIRAL` - Expanding spiral
- `SCATTER` - Complete randomness

### Mechanic IDs
1. `arrowrain` - Arrow bombardment
2. `damage` - Custom damage
3. `blackscreen` - Blindness effect
4. `command` - Execute command
5. `potioneffect` - Apply effects
6. `sound` - Play sound
7. `particle` - Spawn particles
8. `summon` - Summon entities
9. `explosion` - Create explosion
10. `teleport` - Teleport caster

### Attack Categories
- `MELEE` - Close combat (3s cooldown)
- `RANGED` - Projectiles (4s cooldown)
- `AOE` - Area effect (6s cooldown)
- `SUMMON` - Spawn mobs (15s cooldown)
- `BUFF` - Self-enhancement (10s cooldown)
- `DEBUFF` - Weaken enemies (10s cooldown)
- `SPECIAL` - Unique abilities (12s cooldown)

### File Locations
- Config: `plugins/MoreBosses/config.yml`
- Attacks: `plugins/MoreBosses/Custom Attacks/*.yml`
- Bosses: `plugins/MoreBosses/Custom Bosses/*.yml`

---

## Support & Credits

**Plugin:** MoreBosses
**Original:** s5y-ux

---
