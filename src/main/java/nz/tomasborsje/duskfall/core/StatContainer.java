package nz.tomasborsje.duskfall.core;

/**
 * Container class that holds all stats an MMO entity can have.
 * Note that this class is the low-level stat values, and modifiers like damage increases aren't applied here.
 * They should be applied in the MmoEntity implementing class's heal() and hurt() functions.
 */
public class StatContainer {
    private int maxHealth;
    private int currentHealth;
    private int maxMana;
    private int currentMana;
    private final MmoEntity owner;
    private int level;
    private int stamina;
    private int strength;
    private int intellect;

    public StatContainer(MmoEntity owner, int level) {
        this.owner = owner;
        this.level = level;
        recalculateStats();
        healToFull();
    }

    public void recalculateStats() {
        setToBaseStats();
        // Apply all stat modifiers
        for(StatModifier statModifier : owner.getStatModifiers()) {
            applyStatModifier(statModifier);
        }
        // Calculate max hp, etc. that relies on previous stats
        calculateDependentStats();
        // Clamp transient stats
        clampHealthAndMana();
    }

    private void clampHealthAndMana() {
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
        if (currentMana > maxMana) {
            currentMana = maxMana;
        }
    }

    private void applyStatModifier(StatModifier modifier) {
        this.stamina += modifier.getStaminaMod();
        this.strength += modifier.getStrengthMod();
        this.intellect += modifier.getIntellectMod();
    }

    public void healToFull() {
        this.currentHealth = this.maxHealth;
        this.currentMana = this.maxMana;
    }

    private void setToBaseStats() {
        this.stamina = level * 3;
        this.intellect = level * 2;
    }

    private void calculateDependentStats() {
        this.maxHealth = this.stamina * 10;
        this.maxMana = this.intellect * 10;
    }

    /**
     * Reduces current health based on a given damage type and amount.
     *
     * @param type   The type of damage to apply
     * @param amount The amount of damage to apply
     * @return The amount of damage applied, after modifiers
     */
    public int takeDamage(MmoDamageType type, int amount) {
        if (isDead()) {
            return 0;
        }
        // TODO: Calculate armor damage reduction, etc. based on damage type
        this.currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        return amount;
    }

    /**
     * Increases current health based on the given value.
     * Current health will be clamped to max health if it exceeds it.
     * @param health The amount to increase current health by
     */
    public void gainHealth(int health) {
        if(isDead()) {
            return;
        }
        this.currentHealth += health;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getCurrentMana() {
        return currentMana;
    }
}
