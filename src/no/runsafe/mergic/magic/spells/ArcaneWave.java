package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.WorldEffect;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;
import org.bukkit.util.Vector;

public class ArcaneWave implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Arcane Wave";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.ARCANE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public void onCast(final RunsafePlayer player)
	{
		final RunsafeLocation location = player.getLocation();
		if (location == null)
			return;

		int current = 1;
		while (current < 11)
		{
			final int number = current;
			SpellHandler.scheduler.startSyncTask(new Runnable() {
				@Override
				public void run() {
					createSpellLine(player, location, number);
				}
			}, (long) (5 * current));
			current++;
		}
	}

	private void createSpellLine(RunsafePlayer player, RunsafeLocation location, int step)
	{
		RunsafeWorld world = location.getWorld();
		for (int[] node : this.offsets)
		{
			RunsafeLocation position = new RunsafeLocation(
					location.getDirection().add(
							new Vector(node[0] * step, 0, node[1] * step)
					).toLocation(world.getRaw())
			);
			position.offset(0.5D, 0.5D, 0.5D); // Offset to the center of the block.
			location.playEffect(WorldEffect.CRIT, 1, 10, 30); // Play a sparkle at that location.

			for (RunsafePlayer victim : position.getPlayersInRange(1))
			{
				victim.damage(8D); // Hit the player for 4 hearts (8 halves)
				SpellHandler.killManager.registerAttack(victim, player); // Register the hit for the attacker.
			}
		}
	}

	private int[][] offsets = {
			{1, 0}, {0, 1}, {0, 1}
	};
}
