package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class IceBlock implements Spell
{
	@Override
	public int getCooldown()
	{
		return 20;
	}

	@Override
	public String getName()
	{
		return "Ice Block";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.WATER;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.WARD;
	}

	@Override
	public String getDescription()
	{
		return "Encase yourself in a shield of ice.";
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		final RunsafeLocation playerLocation = player.getLocation();
		this.spawnIceBlock(playerLocation); // Spawn the ice block around the player;

		// Create a timer to despawn the ice block after five seconds.
		SpellHandler.scheduler.startSyncTask(new Runnable() {
			@Override
			public void run() {
				despawnIceBlock(playerLocation);
			}
		}, 5);

		// Centre the player on the block they are stood on to prevent getting clipped into the ice.
		player.teleport(new RunsafeLocation(
				playerLocation.getWorld(),
				playerLocation.getBlockX() + 0.5D,
				playerLocation.getY(),
				playerLocation.getBlockZ() + 0.5D
		));
	}

	private void spawnIceBlock(RunsafeLocation location)
	{
		this.setFormation(location, Item.Unavailable.Air, Item.BuildingBlock.Ice);
	}

	private void despawnIceBlock(RunsafeLocation location)
	{
		this.setFormation(location, Item.BuildingBlock.Ice, Item.Unavailable.Air);
	}

	private void setFormation(RunsafeLocation location, Item previous, Item next)
	{
		// Loop each relative position of the formation and set it to the item given.
		for (int[] position : this.formation)
		{
			RunsafeBlock block = new RunsafeLocation(
					location.getWorld(),
					location.getBlockX() + position[0],
					location.getBlockY() + position[1],
					location.getBlockZ() + position[2]
			).getBlock();

			// Check if we have the desired previous block, if so, update it to the new one.
			if (block.is(previous))
				block.set(next);
		}
	}

	private int[][] formation = {
			{1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, // First layer
			{1, 1, 0}, {0, 1, 1}, {-1, 1, 0}, {0, 1, -1}, // Second layer
			{0, 2, 0}, {0, -1, 0} // Top and bottom
	};
}
