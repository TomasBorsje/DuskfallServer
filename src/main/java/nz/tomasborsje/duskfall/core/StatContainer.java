package nz.tomasborsje.duskfall.core;

public class StatContainer {
    private int maxHealth;
    private int currentHealth;
    private int maxMana;
    private int currentMana;
    private int level;
    private int stamina;
    private int intellect;

    public StatContainer(int level) {
        this.level = level;
        recalculateBaseStats();
        fullyHeal();
    }

    public void reset() {
        recalculateBaseStats();
        fullyHeal();
    }

    private void fullyHeal() {
        this.currentHealth = this.maxHealth;
        this.currentMana = this.maxMana;
    }

    private void recalculateBaseStats() {
        this.stamina = level * 3;
        this.intellect = level * 2;

        this.maxHealth = this.stamina * 10;
        this.maxMana = this.intellect * 10;

        // Clamp stats
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
        if (currentMana > maxMana) {
            currentMana = maxMana;
        }
    }

    /**
     * Reduces current health based on a given damage type and amount.
     *
     * @param type   The type of damage to apply
     * @param amount The amount of damage to apply
     * @return The amount of damage applied, after modifiers
     */
    public int takeDamage(MmoDamageType type, int amount) {
        if (!isAlive()) {
            return 0;
        }
        this.currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        return amount;
    }

    public boolean isAlive() {
        return currentHealth > 0;
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
