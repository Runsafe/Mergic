package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class FirePlume implements Spell
{
	@Override
	public int getCooldown()
	{
		return 6;
	}

	@Override
	public String getName()
	{
		return "Fire Plume";
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
	public void onCast(RunsafePlayer player)
	{
		final RunsafeLocation location = player.getLocation();
		int currentStep = 1;

		// Loop three times, each time spawning a timer to construct the next plume stage in 1 second intervals.
		while (currentStep < 4)
		{
			final int step = currentStep - 1;
			SpellHandler.scheduler.startSyncTask(new Runnable() {
				@Override
				public void run() {
					createPlume(location, step);
				}
			}, currentStep);
			currentStep++;
		}

		// Teleport the player to the middle of the block they are stood on to prevent them catching fire.
		player.teleport(new RunsafeLocation(
				location.getWorld(),
				location.getBlockX() + 0.5D,
				location.getY(),
				location.getBlockZ() + 0.5D
		));
	}

	private void createPlume(RunsafeLocation loc, int stage)
	{
		// Grab the current plume stage.
		int[][] plumeStages = this.plume[stage];

		for (int[] plumeStage : plumeStages)
		{
			// Work out a relative location for this plume stage.
			RunsafeBlock block = new RunsafeLocation(
					loc.getWorld(),
					loc.getX() + plumeStage[0],
					loc.getY(),
					loc.getZ() + plumeStage[1]
			).getBlock();

			// Check if the block is air.
			if (block.is(Item.Unavailable.Air))
				block.set(Item.Unavailable.Fire); // and then set the world on fiyyyaaaa!
		}
	}

	// The relative co-ordinates for constructing a fire plume.
	private int[][][] plume = {
			{{1, 0}, {0, 1}, {-1, 0}, {0, -1}},
			{{2, 0}, {1, 1}, {0, 2}, {-1, -1}, {-2, 0}, {-1, 1}, {0, -2}, {1, -1}},
			{{3, 0}, {0, 3}, {-3, 0}, {0, -3}, {2, 1}, {1, 2}, {-1, -2}, {-2, -1}, {1, -2}, {-1, 2}, {-2, 1}, {2, -1}}
	};
}
