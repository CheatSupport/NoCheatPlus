/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.neatmonster.nocheatplus.checks.fight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.actions.ActionList;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.ACheckConfig;
import fr.neatmonster.nocheatplus.checks.access.CheckConfigFactory;
import fr.neatmonster.nocheatplus.checks.access.ICheckConfig;
import fr.neatmonster.nocheatplus.compat.AlmostBoolean;
import fr.neatmonster.nocheatplus.compat.versions.Bugs;
import fr.neatmonster.nocheatplus.compat.versions.ServerVersion;
import fr.neatmonster.nocheatplus.config.ConfPaths;
import fr.neatmonster.nocheatplus.config.ConfigFile;
import fr.neatmonster.nocheatplus.config.ConfigManager;
import fr.neatmonster.nocheatplus.permissions.Permissions;

/**
 * Configurations specific for the "fight" checks. Every world gets one of these assigned to it, or if a world doesn't
 * get it's own, it will use the "global" version.
 */
public class FightConfig extends ACheckConfig {

    /** The factory creating configurations. */
    public static final CheckConfigFactory factory = new CheckConfigFactory() {
        @Override
        public final ICheckConfig getConfig(final Player player) {
            return FightConfig.getConfig(player);
        }

        @Override
        public void removeAllConfigs() {
            clear(); // Band-aid.
        }
    };

    /** The map containing the configurations per world. */
    private static final Map<String, FightConfig> worldsMap = new HashMap<String, FightConfig>();

    /**
     * Clear all the configurations.
     */
    public static void clear() {
        worldsMap.clear();
    }

    /**
     * Gets the configuration for a specified player.
     * 
     * @param player
     *            the player
     * @return the configuration
     */
    public static FightConfig getConfig(final Player player) {
        if (!worldsMap.containsKey(player.getWorld().getName()))
            worldsMap.put(player.getWorld().getName(),
                    new FightConfig(ConfigManager.getConfigFile(player.getWorld().getName())));
        return worldsMap.get(player.getWorld().getName());
    }

    public final boolean    angleCheck;
    public final double     angleThreshold;
    public final ActionList angleActions;

    public final long		toolChangeAttackPenalty;

    public final boolean    criticalCheck;
    public final double     criticalFallDistance;
    public final ActionList criticalActions;

    public final boolean    directionCheck;
    public final boolean	directionStrict;
    public final long       directionPenalty;
    public final ActionList directionActions;

    public final boolean	fastHealCheck;
    public final long		fastHealInterval;
    public final long		fastHealBuffer;
    public final ActionList fastHealActions;

    public final boolean    godModeCheck;
    public final long 		godModeLagMinAge;
    public final long 		godModeLagMaxAge;
    public final ActionList godModeActions;

    public final boolean    noSwingCheck;
    public final ActionList noSwingActions;

    public final boolean    reachCheck;
    public final long       reachPenalty;
    public final boolean    reachPrecision;
    public final boolean    reachReduce;
    public final double		reachSurvivalDistance;
    public final double		reachReduceDistance;
    public final double		reachReduceStep;

    public final ActionList reachActions;

    public final boolean    selfHitCheck;
    public final ActionList selfHitActions;

    public final boolean    speedCheck;
    public final int        speedLimit;
    public final int        speedBuckets;
    public final long       speedBucketDur;
    public final float      speedBucketFactor;  

    public final int        speedShortTermLimit;
    public final int        speedShortTermTicks;
    public final ActionList speedActions;

    // Special flags:
    public final boolean    yawRateCheck;
    public final boolean    cancelDead;
    public final boolean    knockBackVelocityPvP;

    /** Maximum latency counted in ticks for the loop checks (reach, direction). */
    public final long       loopMaxLatencyTicks = 15; // TODO: Configurable,  sections for players and entities.

    /**
     * Instantiates a new fight configuration.
     * 
     * @param data
     *            the data
     */
    public FightConfig(final ConfigFile data) {
        super(data, ConfPaths.FIGHT);
        angleCheck = data.getBoolean(ConfPaths.FIGHT_ANGLE_CHECK);
        angleThreshold = data.getDouble(ConfPaths.FIGHT_ANGLE_THRESHOLD);
        angleActions = data.getOptimizedActionList(ConfPaths.FIGHT_ANGLE_ACTIONS, Permissions.FIGHT_ANGLE);

        toolChangeAttackPenalty = data.getLong(ConfPaths.FIGHT_TOOLCHANGEPENALTY);

        criticalCheck = data.getBoolean(ConfPaths.FIGHT_CRITICAL_CHECK);
        criticalFallDistance = data.getDouble(ConfPaths.FIGHT_CRITICAL_FALLDISTANCE);
        criticalActions = data.getOptimizedActionList(ConfPaths.FIGHT_CRITICAL_ACTIONS, Permissions.FIGHT_CRITICAL);

        directionCheck = data.getBoolean(ConfPaths.FIGHT_DIRECTION_CHECK);
        directionStrict = data.getBoolean(ConfPaths.FIGHT_DIRECTION_STRICT);
        directionPenalty = data.getLong(ConfPaths.FIGHT_DIRECTION_PENALTY);
        directionActions = data.getOptimizedActionList(ConfPaths.FIGHT_DIRECTION_ACTIONS, Permissions.FIGHT_DIRECTION);

        fastHealCheck = ServerVersion.compareMinecraftVersion("1.9") < 0 ? data.getBoolean(ConfPaths.FIGHT_FASTHEAL_CHECK) : false;
        fastHealInterval = data.getLong(ConfPaths.FIGHT_FASTHEAL_INTERVAL);
        fastHealBuffer = data.getLong(ConfPaths.FIGHT_FASTHEAL_BUFFER);
        fastHealActions = data.getOptimizedActionList(ConfPaths.FIGHT_FASTHEAL_ACTIONS, Permissions.FIGHT_FASTHEAL);

        godModeCheck = data.getBoolean(ConfPaths.FIGHT_GODMODE_CHECK);
        godModeLagMinAge = data.getLong(ConfPaths.FIGHT_GODMODE_LAGMINAGE);
        godModeLagMaxAge = data.getLong(ConfPaths.FIGHT_GODMODE_LAGMAXAGE);
        godModeActions = data.getOptimizedActionList(ConfPaths.FIGHT_GODMODE_ACTIONS, Permissions.FIGHT_GODMODE);

        noSwingCheck = data.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK);
        noSwingActions = data.getOptimizedActionList(ConfPaths.FIGHT_NOSWING_ACTIONS, Permissions.FIGHT_NOSWING);

        reachCheck = data.getBoolean(ConfPaths.FIGHT_REACH_CHECK);
        reachSurvivalDistance = data.getDouble(ConfPaths.FIGHT_REACH_SURVIVALDISTANCE, 3.5, 6.0, 4.4);
        reachPenalty = data.getLong(ConfPaths.FIGHT_REACH_PENALTY);
        reachPrecision = data.getBoolean(ConfPaths.FIGHT_REACH_PRECISION);
        reachReduce = data.getBoolean(ConfPaths.FIGHT_REACH_REDUCE);
        reachReduceDistance = data.getDouble(ConfPaths.FIGHT_REACH_REDUCEDISTANCE, 0, reachSurvivalDistance, 0.9);
        reachReduceStep = data.getDouble(ConfPaths.FIGHT_REACH_REDUCESTEP, 0, reachReduceDistance, 0.15);
        reachActions = data.getOptimizedActionList(ConfPaths.FIGHT_REACH_ACTIONS, Permissions.FIGHT_REACH);

        selfHitCheck = data.getBoolean(ConfPaths.FIGHT_SELFHIT_CHECK);
        selfHitActions = data.getOptimizedActionList(ConfPaths.FIGHT_SELFHIT_ACTIONS, Permissions.FIGHT_SELFHIT);

        speedCheck = data.getBoolean(ConfPaths.FIGHT_SPEED_CHECK);
        speedLimit = data.getInt(ConfPaths.FIGHT_SPEED_LIMIT);
        speedBuckets = data.getInt(ConfPaths.FIGHT_SPEED_BUCKETS_N, 6);
        speedBucketDur = data.getLong(ConfPaths.FIGHT_SPEED_BUCKETS_DUR, 333);
        speedBucketFactor = (float) data.getDouble(ConfPaths.FIGHT_SPEED_BUCKETS_FACTOR, 1f);
        speedShortTermLimit = data.getInt(ConfPaths.FIGHT_SPEED_SHORTTERM_LIMIT);
        speedShortTermTicks = data.getInt(ConfPaths.FIGHT_SPEED_SHORTTERM_TICKS);
        speedActions = data.getOptimizedActionList(ConfPaths.FIGHT_SPEED_ACTIONS, Permissions.FIGHT_SPEED);


        yawRateCheck = data.getBoolean(ConfPaths.FIGHT_YAWRATE_CHECK, true);
        cancelDead = data.getBoolean(ConfPaths.FIGHT_CANCELDEAD);
        AlmostBoolean ref = data.getAlmostBoolean(ConfPaths.FIGHT_PVP_KNOCKBACKVELOCITY, AlmostBoolean.MAYBE);
        knockBackVelocityPvP = ref == AlmostBoolean.MAYBE ? Bugs.shouldPvpKnockBackVelocity() : ref.decide();
    }

    /* (non-Javadoc)
     * @see fr.neatmonster.nocheatplus.checks.ICheckConfig#isEnabled(fr.neatmonster.nocheatplus.checks.CheckType)
     */
    @Override
    public final boolean isEnabled(final CheckType checkType) {
        switch (checkType) {
            case FIGHT_ANGLE:
                return angleCheck;
            case FIGHT_CRITICAL:
                return criticalCheck;
            case FIGHT_DIRECTION:
                return directionCheck;
            case FIGHT_GODMODE:
                return godModeCheck;
            case FIGHT_NOSWING:
                return noSwingCheck;
            case FIGHT_REACH:
                return reachCheck;
            case FIGHT_SPEED:
                return speedCheck;
            case FIGHT_SELFHIT:
                return selfHitCheck;
            case FIGHT_FASTHEAL:
                return fastHealCheck;
            default:
                return true;
        }
    }
}
