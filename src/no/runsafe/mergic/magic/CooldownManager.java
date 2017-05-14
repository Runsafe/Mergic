package no.runsafe.mergic.magic;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager
{
	public CooldownManager(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public boolean canCastSpell(IPlayer player, Spell spell)
	{
		return !cooldowns.containsKey(player.getUniqueId()) || !cooldowns.get(player.getUniqueId()).contains(spell.getType());
	}

	public void applySchoolCooldown(final IPlayer player, Spell spell)
	{
		final SpellType spellType = spell.getType();

		// If we lack a key for the player, create one.
		if (!cooldowns.containsKey(player.getUniqueId()))
			cooldowns.put(player.getUniqueId(), new ArrayList<SpellType>(0));

		cooldowns.get(player.getUniqueId()).add(spellType); // Add the school to the cooldown list.
		scheduler.startAsyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				removeSchoolCooldown(player, spellType);
			}
		}, spell.getCooldown()); // Create a cooldown timer.
	}

	public void removeSchoolCooldown(IPlayer player, SpellType spellType)
	{
		// Remove the school from the players cooldown list if it exists.
		if (cooldowns.containsKey(player.getUniqueId()))
			cooldowns.get(player.getUniqueId()).remove(spellType);
	}

	public void resetCooldowns()
	{
		cooldowns.clear(); // Remove all cooldowns.
	}

	private ConcurrentHashMap<UUID, List<SpellType>> cooldowns = new ConcurrentHashMap<UUID, List<SpellType>>(0);
	private IScheduler scheduler;
}
