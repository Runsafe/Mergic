package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.event.entity.IEntityChangeBlockEvent;
import no.runsafe.framework.minecraft.*;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeFallingBlock;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityChangeBlockEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.ControlledEntityCleaner;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

import java.util.concurrent.ConcurrentHashMap;

public class HellStorm implements Spell, IEntityChangeBlockEvent
{
	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public String getName()
	{
		return "Hell Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.SHADOW;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public String getDescription()
	{
		return "Let the fury of hell rain from the sky!";
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		int radius = 5; // Will be doubled in a square radius.
		RunsafeLocation location = player.getLocation();
		final RunsafeWorld world = player.getWorld();

		if (location == null || world == null)
			return; // If we've got an invalid location, cancel.

		final String playerName = player.getName();

		final int highX = location.getBlockX() + radius;
		final int highZ = location.getBlockZ() + radius;
		final int lowX = location.getBlockX() - radius;
		final int lowZ = location.getBlockZ() - radius;
		final int high = location.getBlockY() + 20;

		final int ticker = SpellHandler.scheduler.startSyncRepeatingTask(new Runnable() {
			@Override
			public void run() {
				int x = lowX + (int)(Math.random() * ((highX - lowX) + 1));
				int z = lowZ + (int)(Math.random() * ((highZ - lowZ) + 1));

				// Spawn a falling coal block randomly within the radius.
				RunsafeFallingBlock block = world.spawnFallingBlock(
						new RunsafeLocation(world, x, high, z),
						Item.BuildingBlock.CoalBlock.getType(),
						(byte) 0
				);
				block.setDropItem(false);
				block.setFireTicks(10 * 20); // SET IT ON FIREEEEEE!

				no.runsafe.mergic.magic.spells.HellStorm.blocks.put(block.getEntityId(), playerName); // Track the block.
				ControlledEntityCleaner.registerEntity(block); // Register for clean-up.
			}
		}, 5L, 5L);

		SpellHandler.scheduler.startSyncTask(new Runnable() {
			@Override
			public void run() {
				SpellHandler.scheduler.cancelTask(ticker); // Cancel the hell storm.
			}
		}, 10);
	}

	@Override
	public void OnEntityChangeBlockEvent(RunsafeEntityChangeBlockEvent event)
	{
		RunsafeEntity entity = event.getEntity();
		int entityID = entity.getEntityId();

		// Are we tracking this entity?
		if (blocks.containsKey(entityID))
		{
			RunsafeLocation location = entity.getLocation();

			if (location != null)
			{
				RunsafePlayer player = RunsafeServer.Instance.getPlayerExact(blocks.get(entityID));

				location.playEffect(WorldEffect.LAVA, 1, 20, 50); // Play a splash.
				location.Play(Sound.Environment.Explode, 2, 1);
				for (RunsafePlayer victim : location.getPlayersInRange(4))
				{
					if (player != null && player.getName().equals(victim.getName()))
						continue;

					victim.setFireTicks(5 * 20); // Set the player on fire for 5 seconds
					victim.damage(3D, player); // Three hearts of damage.
				}
			}

			ControlledEntityCleaner.unregisterEntity(entity); // Remove entity from cleaner

			try
			{
				event.cancel(); // Try to cancel the event
			}
			catch (NullPointerException e)
			{
				// Can we just ignore this?
			}
		}
	}

	private static ConcurrentHashMap<Integer, String> blocks = new ConcurrentHashMap<Integer, String>();
}