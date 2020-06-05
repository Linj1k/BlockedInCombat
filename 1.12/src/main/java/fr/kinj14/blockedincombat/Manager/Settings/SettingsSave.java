package fr.kinj14.blockedincombat.Manager.Settings;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class SettingsSave {
    private String name = "ConfigSave";
    private boolean TabHealth = false;
    private boolean BonusChest = true;
    private boolean AutoSmelt = false;
    private boolean AutoSmeltFortune = false;
    private boolean UHCMode = false;
    private boolean FriendlyFire = false;
    private int GameTime = 20;
    private int CombatTime = 2;
    private int GlowingTime = 1;
    private int ExpMultiplier = 1;
    private Map<Material, Boolean> Blocks = new HashMap<>();
    private Map<Material, Boolean> Items = new HashMap<>();

    public SettingsSave(String name, boolean tabHealth, boolean bonusChest, boolean autoSmelt, boolean autoSmeltFortune, boolean UHCMode, boolean friendlyFire, int gameTime, int combatTime, int glowingTime, int expMultiplier, Map<Material, Boolean> blocks, Map<Material, Boolean> items) {
        this.name = name;
        this.TabHealth = tabHealth;
        this.BonusChest = bonusChest;
        this.AutoSmelt = autoSmelt;
        this.AutoSmeltFortune = autoSmeltFortune;
        this.UHCMode = UHCMode;
        this.FriendlyFire = friendlyFire;
        this.GameTime = gameTime;
        this.CombatTime = combatTime;
        this.GlowingTime = glowingTime;
        this.ExpMultiplier = expMultiplier;
        this.Blocks = blocks;
        this.Items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getTabHealth() {
        return TabHealth;
    }

    public boolean getBonusChest() {
        return BonusChest;
    }

    public boolean getAutoSmelt() {
        return AutoSmelt;
    }

    public boolean getAutoSmeltFortune() {
        return AutoSmeltFortune;
    }

    public boolean getUHCMode() {
        return UHCMode;
    }

    public boolean getFriendlyFire() {
        return FriendlyFire;
    }

    public int getGameTime() {
        return GameTime;
    }

    public int getCombatTime() {
        return CombatTime;
    }

    public int getGlowingTime() {
        return GlowingTime;
    }

    public int getExpMultiplier() {
        return ExpMultiplier;
    }

    public Map<Material, Boolean> getBlocks() {
        return Blocks;
    }

    public Map<Material, Boolean> getItems() {
        return Items;
    }

    public void setTabHealth(boolean tabHealth) {
        TabHealth = tabHealth;
    }

    public void setBonusChest(boolean bonusChest) {
        BonusChest = bonusChest;
    }

    public void setAutoSmelt(boolean autoSmelt) {
        AutoSmelt = autoSmelt;
    }

    public void setAutoSmeltFortune(boolean autoSmeltFortune) {
        AutoSmeltFortune = autoSmeltFortune;
    }

    public void setUHCMode(boolean UHCMode) {
        this.UHCMode = UHCMode;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        FriendlyFire = friendlyFire;
    }

    public void setGameTime(int gameTime) {
        if(gameTime <= 0){return;}
        GameTime = gameTime;
    }

    public void setCombatTime(int combatTime) {
        if(combatTime <= 0){return;}
        CombatTime = combatTime;
    }

    public void setGlowingTime(int glowingTime) {
        if(glowingTime <= 0){return;}
        GlowingTime = glowingTime;
    }

    public void setExpMultiplier(int expMultiplier) {
        if(expMultiplier <= 0){return;}
        ExpMultiplier = expMultiplier;
    }

    public void setBlocks(Map<Material, Boolean> blocks) {
        Blocks = blocks;
    }

    public void setItems(Map<Material, Boolean> items) {
        Items = items;
    }

    public static SettingsSave createDefault(){
        Map<Material, Boolean> Blocks = new HashMap<>();
        Map<Material, Boolean> Items = new HashMap<>();

        Blocks.put(Material.COBBLESTONE, true);
        Blocks.put(Material.EMERALD_ORE, true);
        Blocks.put(Material.GRAVEL, true);
        Blocks.put(Material.HAY_BLOCK, true);
        Blocks.put(Material.GOLD_ORE, true);
        Blocks.put(Material.DIRT, true);
        Blocks.put(Material.IRON_ORE, true);
        Blocks.put(Material.WORKBENCH, true);
        Blocks.put(Material.FURNACE, false);
        Blocks.put(Material.COAL_ORE, true);
        Blocks.put(Material.LOG, true);
        Blocks.put(Material.ENCHANTMENT_TABLE, true);
        Blocks.put(Material.GLASS, true);
        Blocks.put(Material.QUARTZ_ORE, true);
        Blocks.put(Material.LAPIS_ORE, true);
        Blocks.put(Material.QUARTZ_BLOCK, true);
        Blocks.put(Material.BOOKSHELF, true);
        Blocks.put(Material.LEAVES, true);
        Blocks.put(Material.DIAMOND_ORE, true);
        Blocks.put(Material.MELON_BLOCK, true);
        Blocks.put(Material.REDSTONE_ORE, true);
        Blocks.put(Material.NETHERRACK, true);
        Blocks.put(Material.OBSIDIAN, false);
        Blocks.put(Material.SLIME_BLOCK, false);

        //Items
        Items.put(Material.BOW, false);
        Items.put(Material.ARROW, true);
        Items.put(Material.WOOD, true);
        Items.put(Material.LOG, true);
        Items.put(Material.SPONGE, true);
        Items.put(Material.STICK, true);
        Items.put(Material.WOOD_PICKAXE, true);
        Items.put(Material.WOOD_AXE, true);
        Items.put(Material.COMPASS, true);
        Items.put(Material.MILK_BUCKET, true);
        Items.put(Material.PAPER, true);
        Items.put(Material.WATER_BUCKET, true);
        Items.put(Material.COAL, true);
        Items.put(Material.LEATHER, true);
        Items.put(Material.FEATHER, true);
        Items.put(Material.APPLE, true);
        Items.put(Material.COOKED_BEEF, true);
        Items.put(Material.SKULL_ITEM, true);
        Items.put(Material.LEATHER_CHESTPLATE, true);
        Items.put(Material.LEATHER_HELMET, true);
        Items.put(Material.TNT, false);
        Items.put(Material.LEVER, false);
        Items.put(Material.WOOD_BUTTON, false);
        Items.put(Material.SHIELD, false);
        Items.put(Material.ENDER_PEARL, false);

        return new SettingsSave("default", true, true, true, false, false, false, 10, 2, 6, 1, Blocks, Items);
    }
}
