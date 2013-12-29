package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.IWorldEffect;
import no.runsafe.framework.api.event.entity.IEntityChangeBlockEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.*;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeFallingBlock;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityChangeBlockEvent;
import no.runsafe.mergic.ControlledEntityCleaner;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Storm implements Spell, IEntityChangeBlockEvent
{
	public Storm(IServer server, Item blockType)
	{
		this.server = server;
		this.blockType = blockType;
		this.effect = new WorldBlockEffect(WorldBlockEffectType.BLOCK_DUST, blockType);
	}

	@Override
	public int getCooldown()
	{
		return 20;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.STORM;
	}

	@Override
	public String getDescription()
	{
		return "Conjure a " + getSchool().name().toLowerCase() + " storm which will rain down on the area.";
	}

	@Override
	public void onCast(IPlayer player)
	{
		int radius = 6; // Will be doubled in a square radius.
		ILocation location = player.getLocation();
		final IWorld world = player.getWorld();

		if (location == null || world == null)
			return; // If we've got an invalid location, cancel.

		final String playerName = player.getName();

		final double highX = location.getBlockX() + radius;
		final double highZ = location.getBlockZ() + radius;
		final double lowX = location.getBlockX() - radius;
		final double lowZ = location.getBlockZ() - radius;
		final double high = location.getBlockY() + 20;

		final int ticker = SpellHandler.scheduler.startSyncRepeatingTask(new Runnable()
		{
			@Override
			public void run()
			{
				double x = lowX + (int) (Math.random() * ((highX - lowX) + 1));
				double z = lowZ + (int) (Math.random() * ((highZ - lowZ) + 1));

				// Spawn a falling ice block randomly within the radius.
				RunsafeFallingBlock block = ((RunsafeWorld) world).spawnFallingBlock(
						world.getLocation(x, high, z),
						blockType.getType(),
						blockType.getData()
				);
				block.setDropItem(false);

				blocks.put(block.getEntityId(), playerName); // Track the block.
				ControlledEntityCleaner.registerEntity(block); // Register for clean-up.
			}
		}, 3L, 3L);

		SpellHandler.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				SpellHandler.scheduler.cancelTask(ticker); // Cancel the blizzard.
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
			ILocation location = entity.getLocation();

			if (location != null)
			{
				IPlayer player = server.getPlayerExact(blocks.get(entityID));

				location.playEffect(effect, 0.3F, 100, 50); // Create a dust effect using the storm block.
				//location.playSound(Sound.Environment.Glass, 2, -1); // Play ice breaking sound.

				for (IPlayer victim : location.getPlayersInRange(4))
				{
					if (player != null && player.getName().equals(victim.getName()))
						continue;

					victim.damage(3D, player); // Three hearts of damage.
				}
				blocks.remove(entityID); // Remove the entity ID from our tracker.
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

	private final Item blockType;
	private final IWorldEffect effect;
	private final IServer server;
	private final ConcurrentHashMap<Integer, String> blocks = new ConcurrentHashMap<Integer, String>();
}
