package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.IWorldEffect;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.event.entity.IEntityChangeBlockEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.*;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeFallingBlock;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityChangeBlockEvent;
import no.runsafe.mergic.ControlledEntityCleaner;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Storm implements Spell, IEntityChangeBlockEvent
{
	public Storm(IServer server, Item blockType, KillManager killManager)
	{
		this.server = server;
		this.blockType = blockType;
		this.killManager = killManager;
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
	public void onCast(final IPlayer player)
	{
		int radius = 6; // Will be doubled in a square radius.
		ILocation location = player.getLocation();
		final IWorld world = player.getWorld();

		if (location == null || world == null)
			return; // If we've got an invalid location, cancel.

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
				IEntity block = world.spawnFallingBlock(world.getLocation(x, high, z), blockType);
				((RunsafeFallingBlock)block).setDropItem(false);

				blocks.put(block.getEntityId(), player); // Track the block.
				ControlledEntityCleaner.registerEntity(block); // Register for clean-up.
			}
		}, 10L, 10L);

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
				IPlayer player = blocks.get(entityID);

				location.playEffect(effect, 0.3F, 100, 50); // Create a dust effect using the storm block.
				location.playSound(Sound.Environment.Explode, 1, 1); // Play a slow-thrash sound.

				for (IPlayer victim : location.getPlayersInRange(4))
				{
					if (player != null && player.equals(victim))
						continue;

					killManager.attackPlayer(victim, player, 4);
				}
			}

			blocks.remove(entityID); // Remove the entity ID from our tracker.
			ControlledEntityCleaner.unregisterEntity(entity); // Remove entity from cleaner
			entity.remove(); // Remove the entity.

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
	private final KillManager killManager;
	private final ConcurrentHashMap<Integer, IPlayer> blocks = new ConcurrentHashMap<Integer, IPlayer>();
}
