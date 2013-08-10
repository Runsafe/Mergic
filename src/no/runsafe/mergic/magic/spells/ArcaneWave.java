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
			}, (long) (2 * current));
			current++;
		}
	}

	private void createSpellLine(RunsafePlayer player, RunsafeLocation location, int step)
	{
		Vector viewVector = location.getDirection();
		for (int[] node : this.offsets)
		{
			RunsafeLocation position = new RunsafeLocation(
					location.getWorld(),
					location.getX() + ((viewVector.getX() * step) * node[0]),
					location.getY(),
					location.getZ()+ ((viewVector.getZ() * step) * node[1])
			); // Get the relative position, hopefully.

			position.offset(0.5D, 0, 0.5D); // Offset to centre of the block.
			position.playEffect(WorldEffect.CRIT, 1, 30, 50); // Play a sparkle at the location.
		}
	}

	private int[][] offsets = {
			{1, 0}, {0, 1}, {0, -1}, {-1, 0}
	};
}
