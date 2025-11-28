## Pattern Types Implemented

### **CIRCLE** ⭕
- Projectiles spawn in perfect circle around target
- Fall straight down from above
- **Perfect for:** Meteor strikes, arrow rings, ice shards
- **Example:** 8 fireballs in a circle, 6 blocks radius

### **RAIN** 🌧️
- Random projectiles within radius
- Chaotic and unpredictable
- **Perfect for:** Arrow storms, overwhelming barrages
- **Example:** 12 arrows scattered randomly overhead

### **SPIRAL** 🌀
- Expanding spiral pattern from target
- Mesmerizing visual effect
- **Perfect for:** Snowball spirals, expanding threats
- **Example:** 24 snowballs spiraling outward

### **SCATTER** 💥
- Completely random placement
- Maximum chaos
- **Perfect for:** Bombardments, explosive attacks
- **Example:** 20 fireballs randomly scattered

---

## 📝 How to Use Patterns

### Basic YAML Structure:
```yaml
attacks:
  MY_ATTACK:
    displayName: "&cMy Attack"
    description: "Description here"
    category: AOE
    icon: FIRE_CHARGE
    damage: 10.0
    range: 20.0
    effects:
      projectileType: FIREBALL    # Required for patterns
      pattern:                     # Add this section!
        type: CIRCLE               # CIRCLE, RAIN, SPIRAL, or SCATTER
        radius: 6.0                # Size of pattern area
        heightOffset: 15.0         # How high above target
        waves: 3                   # Number of volleys
        meteorsPerWave: 8          # Projectiles per wave
        intervalTicks: 10          # Delay between waves
```

### Meteor Strike Example (Already in default file):
```yaml
METEOR_STRIKE:
  displayName: "&c&lMeteor Strike"
  effects:
    projectileType: FIREBALL
    pattern:
      type: CIRCLE
      radius: 6.0
      heightOffset: 15.0
      waves: 3
      meteorsPerWave: 8
      intervalTicks: 10
```

**Result:** 3 waves of 8 fireballs each, forming perfect circles around the target!

---

## 🔧 Configuration Options

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `type` | String | CIRCLE | Pattern shape: CIRCLE, RAIN, SPIRAL, SCATTER |
| `radius` | Double | 6.0 | Size of pattern area (blocks) |
| `heightOffset` | Double | 15.0 | Spawn height above target (blocks) |
| `waves` | Integer | 1 | Number of pattern repetitions |
| `meteorsPerWave` | Integer | 8 | Projectiles per wave |
| `intervalTicks` | Integer | 10 | Delay between waves (20 ticks = 1 second) |

---

## ✨ Key Features

### Target Position Locking
- Pattern locks onto target's position **when attack is cast**
- Target can dodge by moving after attack starts
- Creates fair, skill-based gameplay

### All Projectile Types Supported
✅ FIREBALL  
✅ ARROW  
✅ SNOWBALL  
✅ EGG  
✅ ENDER_PEARL  
✅ WITHER_SKULL  
✅ DRAGON_FIREBALL  

### Visual Feedback
- Flame particles spawn at projectile spawn points
- Gives players brief warning of incoming danger
- Makes patterns clearly visible

---

## 📊 Pattern Behavior

### Pattern Comparison

**CIRCLE:**
- Most predictable
- Easy to understand
- Fair to dodge
- Great for learning

**RAIN:**
- Medium difficulty
- Some randomness
- Covers area well
- Good for pressure

**SPIRAL:**
- Visually impressive
- Moderate challenge
- Unique mechanic
- Memorable fights

**SCATTER:**
- Maximum chaos
- Very challenging
- Overwhelming effect
- Epic boss finale

---

## 🎯 Example Boss Attacks

### Fire Lord - Meteor Circle
```yaml
type: CIRCLE
radius: 6.0
waves: 3
meteorsPerWave: 8
intervalTicks: 10
```
**Effect:** Classic meteor strike in rings

### Ice Mage - Blizzard Rain
```yaml
type: RAIN
radius: 10.0
waves: 5
meteorsPerWave: 12
intervalTicks: 12
```
**Effect:** Overwhelming snowball storm

### Void Summoner - Spiral Doom
```yaml
type: SPIRAL
radius: 8.0
waves: 1
meteorsPerWave: 24
intervalTicks: 2
```
**Effect:** Hypnotic expanding spiral

### Chaos Dragon - Scatter Blast
```yaml
type: SCATTER
radius: 15.0
waves: 1
meteorsPerWave: 20
intervalTicks: 1
```
**Effect:** Explosive random bombardment

## 🎨 Visual Examples

### Circle Pattern:
```
        X
      X   X
    X   T   X    T = Target position
      X   X      X = Projectile spawn
        X
```

### Rain Pattern:
```
   X  X    X
  X    X  X   X
    X   T   X    T = Target position
  X   X    X      X = Random projectile
    X    X
```

### Spiral Pattern:
```
      X X X X
     X       X
    X    T    X   T = Target position
     X       X    X = Spiraling outward
      X X X X
```

### Scatter Pattern:
```
  X     X    X
    X  X   X
  X   T    X  X   T = Target position
   X    X    X     X = Random scatter
     X    X
```

---

## ⚙️ Performance Notes

**Recommended Limits:**
- Max projectiles per wave: 20-25
- Max total projectiles: 50-75
- Test with multiple players present

**Optimization:**
- Fewer waves with more projectiles is better than many small waves
- Increase `intervalTicks` if server lags
- Circle pattern is most performant

