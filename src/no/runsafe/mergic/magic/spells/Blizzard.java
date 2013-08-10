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

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Blizzard implements Spell, IEntityChangeBlockEvent
{
	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public String getName()
	{
		return "Blizzard";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FROST;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public String getDescription()
	{
		return "Conjures up a blizzard. Chilly!";
	}

	@Override
	public void onCast(final RunsafePlayer player)
	{
		int radius = 5; // Will be doubled in a square radius.
		RunsafeLocation location = player.getLocation();
		final RunsafeWorld world = player.getWorld();

		if (location == null || world == null)
			return; // If we've got an invalid location, cancel.

		player.sendColouredMessage("We have valid location objects.");

		final String playerName = player.getName();

		final int highX = location.getBlockX() + radius;
		final int highZ = location.getBlockX() + radius;
		final int lowX = location.getBlockZ() - radius;
		final int lowZ = location.getBlockZ() - radius;
		final int high = location.getBlockY() + 20;

		final int ticker = SpellHandler.scheduler.startSyncRepeatingTask(new Runnable() {
			@Override
			public void run() {
				int x = random.nextInt(lowX - highX + 1) + lowX;
				int z = random.nextInt(lowZ - highZ + 1) + lowZ;

				// Spawn a falling ice block randomly within the radius.
				RunsafeFallingBlock block = world.spawnFallingBlock(
						new RunsafeLocation(world, x, high, z),
						Item.BuildingBlock.Ice.getType(),
						(byte) 0
				);
				block.setDropItem(false);

				Blizzard.blocks.put(block.getEntityId(), playerName); // Track the block.
				ControlledEntityCleaner.registerEntity(block); // Register for clean-up.
				player.sendColouredMessage("Tick!");
				player.sendColouredMessage(new RunsafeLocation(world, x, high, z).toString());
			}
		}, 10L, 10L);

		player.sendColouredMessage("Repeating ticker registered: " + ticker);

		SpellHandler.scheduler.startSyncTask(new Runnable() {
			@Override
			public void run() {
				SpellHandler.scheduler.cancelTask(ticker); // Cancel the blizzard.
				player.sendColouredMessage("Cancelling ticker now.");
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

				location.playEffect(WorldEffect.SPLASH, 1, 20, 50); // Play a splash.
				for (RunsafePlayer victim : location.getPlayersInRange(2))
				{
					Buff.Utility.Movement.DecreaseSpeed.duration(5).applyTo(victim); // Slow the player.
					victim.damage(6D); // Three hearts of damage.

					if (player != null)
						SpellHandler.killManager.registerAttack(victim, player); // Register the hit for a player.
				}
			}

			ControlledEntityCleaner.unregisterEntity(entity); // Remove entity from cleaner.
			event.cancel(); // Cancel the event, we don't want the block to turn to ice.
		}
	}

	private static ConcurrentHashMap<Integer, String> blocks = new ConcurrentHashMap<Integer, String>();
	final private Random random = new Random();
}
