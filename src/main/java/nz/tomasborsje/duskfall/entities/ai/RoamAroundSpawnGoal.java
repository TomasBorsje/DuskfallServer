package nz.tomasborsje.duskfall.entities.ai;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI goal that makes an entity roam around a set position.
 */
public class RoamAroundSpawnGoal extends GoalSelector {

    private static final long DELAY = 2500;

    private final List<Vec> roamTargets;
    private final Random random = new Random();
    private final Pos spawn;

    private long lastStroll;

    public RoamAroundSpawnGoal(@NotNull EntityCreature entityCreature, Pos spawn, int radius) {
        super(entityCreature);
        this.spawn = spawn;
        this.roamTargets = getRoamTargets(radius);
    }

    @Override
    public boolean shouldStart() {
        return System.currentTimeMillis() - lastStroll >= DELAY;
    }

    @Override
    public void start() {
        int remainingAttempt = roamTargets.size();
        while (remainingAttempt-- > 0) {
            final int index = random.nextInt(roamTargets.size());
            final Vec target = roamTargets.get(index);
            final boolean result = entityCreature.getNavigator().setPathTo(target);
            if (result) {
                break;
            }
        }
    }

    @Override
    public void tick(long time) { }

    @Override
    public boolean shouldEnd() {
        return true;
    }

    @Override
    public void end() {
        this.lastStroll = System.currentTimeMillis();
    }

    private @NotNull List<Vec> getRoamTargets(int radius) {
        List<Vec> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(new Vec(spawn.blockX() + x, spawn.blockY()+y, spawn.blockZ()+z));
                }
            }
        }
        return blocks;
    }
}
