package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.event.entity.IEntityChangeBlockEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.WorldBlockEffect;
import no.runsafe.framework.minecraft.WorldBlockEffectType;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeFallingBlock;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityChangeBlockEvent;
import no.runsafe.mergic.ControlledEntityCleaner;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.PlayerMonitor;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Bolt implements Spell, IEntityChangeBlockEvent
{
	public Bolt(Item blockType, IScheduler scheduler, KillManager killManager)
	{
		this.blockType = blockType;
		this.scheduler = scheduler;
		this.killManager = killManager;
		effect = new WorldBlockEffect(WorldBlockEffectType.BLOCK_DUST, blockType);
	}

	@Override
	public int getCooldown()
	{
		return 1;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public String getDescription()
	{
		return "Shoot forth a bolt of " + getSchool().name().toLowerCase() + ".";
	}

	@Override
	public void onCast(final IPlayer player)
	{
		IWorld world = player.getWorld();
		ILocation location = player.getEyeLocation();

		if (world == null || location == null) // If either of these are NULL, just stop.
			return;

		location.playSound(Sound.Creature.Ghast.Fireball, 1, 1); // Play a fireball sound.

		Vector direction = location.getDirection(); // Direction the player is facing.

		IEntity block = world.spawnFallingBlock(location.add(direction.multiply(2)), blockType);
		final RunsafeFallingBlock fallingBlock = (RunsafeFallingBlock) block;

		fallingBlock.setDropItem(false); // Prevent block-dropping.
		fallingBlock.setVelocity(direction.multiply(0.8F)); // Fire the block forward.

		int timerID = scheduler.startSyncRepeatingTask(new Runnable()
		{
			@Override
			public void run()
			{
				ILocation location = fallingBlock.getLocation(); // Current location of the block.
				if (location == null) // If we lack a location, stop here.
					return;

				boolean hit = false;

				// Check all the players we have in range.
				for (IPlayer closePlayer : location.getPlayersInRange(3D))
				{
					if (!closePlayer.equals(player)) // Check the player is different to the caster.
					{
						hit = true; // Mark this projectile as hit.
						killManager.attackPlayer(closePlayer, player, 7); // Damage the player.
						location.playEffect(effect, 0.3F, 100, 50); // Create a dust effect using the storm block.
						location.playSound(Sound.Creature.Ghast.Fireball, 1, 0); // Play a slow-thrash sound.
					}
				}

				if (hit)
				{
					removeBolt(fallingBlock.getEntityId()); // Remove thy bolt.
					ControlledEntityCleaner.unregisterEntity(fallingBlock); // Unregister from cleaner.
					fallingBlock.remove(); // Remove the projectile.
				}
			}
		}, 2L, 2L);

		blocks.put(fallingBlock.getEntityId(), timerID); // Monitor the block.
		ControlledEntityCleaner.registerEntity(fallingBlock); // Register for clean-up.
	}

	@Override
	public void OnEntityChangeBlockEvent(RunsafeEntityChangeBlockEvent event)
	{
		RunsafeEntity entity = event.getEntity(); // An entity that changed a block.
		int entityID = entity.getEntityId(); // The ID of the entity.

		if (blocks.containsKey(entityID)) // Check if we are tracking this bolt block.
		{
			ILocation location = entity.getLocation(); // Location the block lands.
			if (location == null) return; // Invalid location, stop here.

			location.playEffect(effect, 0.3F, 100, 50); // Create a dust effect using the storm block.
			location.playSound(Sound.Creature.Ghast.Fireball, 1, 0); // Play a slow-thrash sound.

			removeBolt(entityID); // Remove the bolt from our tracking.
			ControlledEntityCleaner.unregisterEntity(entity); // Unregister from cleaner.
			entity.remove(); // Remove the entity.

			try
			{
				event.cancel(); // Try to cancel the event
			}
			catch (NullPointerException e)
			{
				// Ignore this exception.
			}
		}
	}

	private void removeBolt(int entityID)
	{
		scheduler.cancelTask(blocks.get(entityID)); // Cancel the timer for this block.
		blocks.remove(entityID); // Remove from our bolt tracking.
	}

	private final Item blockType;
	private final ConcurrentHashMap<Integer, Integer> blocks = new ConcurrentHashMap<Integer, Integer>(0);
	private final IScheduler scheduler;
	private final WorldBlockEffect effect;
	private final KillManager killManager;
}
