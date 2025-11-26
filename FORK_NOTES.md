# Fork Notes
Still need to test all angles of custom items. This fork is going in the direction of custom boss creation. Changed API version to achieve.
## TODO / WIP

###  Piggy Boss
- [ ] Fix Piggy Axe  
  - Currently drops as a custom-named item with **no effects**  
  - Sometimes drops as a **damaged** item

###  Core Systems
- [ ] Implement Spell Effects
- [ ] Implement Special Attacks
- [ ] Implement Structures
- [ ] Update legacy strings for bosses / boss handling
- [ ] Create new schematics for custom bosses
- [ ] Create an assortment of attack types for bosses
- [ ] Implement WorldGuard support (maybe)
- [ ] Implement spawn command for new custom bosses
- [ ] Implement Custom Boss Bars for custom bosses
- [ ] Add custom egg creation for custom bosses
- [ ] Implement minions  
  - (May move this under Special Attacks system)
- [ ] Implement drop table system for custom items

###  Items & Equipment
- [ ] Create subclasses for general items  
  - Allow classes to be applied to any user-selected item
- [ ] Implement handling for Custom Model Textures
- [ ] Create a base set of Custom Model Textures

### Combat Logic
- [ ] Build a macro-style attack pattern system  
  Example: `Attack1 + delay + (text + attack)` combinations

---

## Possible / Maybe
- [ ] Boss music based on radius of the boss or the schematic structure  
  - Allow selecting custom songs per boss
- [ ] New Command + GUI  
  - `/scheduleboss` → Opens scheduling GUI

---

##  Changes from Main Branch

### Major Updates
- **Upgraded API from 1.20 → 1.21.4**

### GUI Systems
- Created **GUI-Based Custom Boss Creator**
- Created **GUI-Based Custom Boss Spawner**
  - Supports changing:  
    - Size  
    - Aggression  
    - Mob type  
    - Particle auras  
    - Items & equipment  
    - Minions *(WIP)*  
    - WorldGuard extension *(WIP)*  
    - WorldEdit integration *(WIP)*  
    - Custom Textures *(WIP)*  
    - Custom Boss Bars *(WIP)*  

### Behavioral Changes
- Reworked **BowBom handling**  
  - No longer destroys dropped entities  
  - Still destroys blocks around it  

