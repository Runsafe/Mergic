package no.runsafe.mergic.magic;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager
{
	public CooldownManager(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public boolean canCastSpell(IPlayer player, Spell spell)
	{
		String playerName = player.getName();
		return !cooldowns.containsKey(playerName) || !cooldowns.get(playerName).contains(spell.getType());
	}

	public void applySchoolCooldown(IPlayer player, Spell spell)
	{
		final String playerName = player.getName();
		final SpellType spellType = spell.getType();

		// If we lack a key for the player, create one.
		if (!cooldowns.containsKey(playerName))
			cooldowns.put(playerName, new ArrayList<SpellType>(0));

		cooldowns.get(playerName).add(spellType); // Add the school to the cooldown list.
		scheduler.startAsyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				removeSchoolCooldown(playerName, spellType);
			}
		}, spell.getCooldown()); // Create a cooldown timer.
	}

	public void removeSchoolCooldown(String playerName, SpellType spellType)
	{
		// Remove the school from the players cooldown list if it exists.
		if (cooldowns.containsKey(playerName))
			cooldowns.get(playerName).remove(spellType);
	}

	public void resetCooldowns()
	{
		cooldowns.clear(); // Remove all cooldowns.
	}

	private ConcurrentHashMap<String, List<SpellType>> cooldowns = new ConcurrentHashMap<String, List<SpellType>>(0);
	private IScheduler scheduler;
}
