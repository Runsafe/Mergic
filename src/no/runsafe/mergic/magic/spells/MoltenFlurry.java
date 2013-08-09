package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;
import org.bukkit.util.Vector;

public class MoltenFlurry implements Spell
{
	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public String getName()
	{
		return "Molten Flurry";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FIRE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public void onCast(final RunsafePlayer player)
	{
		RunsafeLocation location = player.getLocation();
		if (location == null)
			return; // We don't want to continue if our location is borked.

		location.incrementY(2); // Go up one blocks from the players head.
		final RunsafeLocation loc = location; // Make a final version for the timers.

		// Loop four times, triggering fireBalls() once per second.
		int current = 0;
		while (current < 4)
		{
			SpellHandler.scheduler.startSyncTask(new Runnable() {
				@Override
				public void run() {
					fireBalls(player, loc);
				}
			}, current + 1);
			current++;
		}
	}

	private void fireBalls(RunsafePlayer player, RunsafeLocation location)
	{
		int current = 0;
		while (current < 4)
		{
			RunsafeEntity entity = player.Launch(ProjectileEntity.SmallFireball);
			entity.teleport(location);
			entity.setVelocity(this.fireVectors[current]);
			current++;
		}
	}

	private Vector[] fireVectors = {
			new Vector(0.2, 0, 0),
			new Vector(0, 0, 0.2),
			new Vector(-0.2, 0, 0),
			new Vector(0, 0, -0.2)
	};
}
