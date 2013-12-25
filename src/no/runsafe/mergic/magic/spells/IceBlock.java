package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.Sound;
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
		return MagicSchool.FROST;
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
	public void onCast(IPlayer player)
	{
		final ILocation playerLocation = player.getLocation();
		this.spawnIceBlock(playerLocation); // Spawn the ice block around the player;

		// Create a timer to despawn the ice block after five seconds.
		SpellHandler.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				despawnIceBlock(playerLocation);
			}
		}, 5);

		// Centre the player on the block they are stood on to prevent getting clipped into the ice.
		player.teleport(playerLocation.getWorld().getLocation(
			playerLocation.getBlockX() + 0.5D,
			playerLocation.getY(),
			playerLocation.getBlockZ() + 0.5D
		));
	}

	private void spawnIceBlock(ILocation location)
	{
		this.setFormation(location, Item.Unavailable.Air, Item.BuildingBlock.Ice);
		location.playSound(Sound.Environment.Swim, 2, -1); // Ice form sound? Kind of.
	}

	private void despawnIceBlock(ILocation location)
	{
		this.setFormation(location, Item.BuildingBlock.Ice, Item.Unavailable.Air);
		location.playSound(Sound.Environment.Glass, 2, -1); // Play ice breaking sound.
	}

	private void setFormation(ILocation location, Item previous, Item next)
	{
		// Loop each relative position of the formation and set it to the item given.
		for (int[] position : this.formation)
		{
			ILocation point = location.clone();
			point.offset(position[0], position[1], position[2]);
			IBlock block = point.getBlock();

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
