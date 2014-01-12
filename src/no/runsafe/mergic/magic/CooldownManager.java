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
		return !cooldowns.containsKey(playerName) || !cooldowns.get(playerName).contains(spell.getSchool());
	}

	public void applySchoolCooldown(IPlayer player, Spell spell)
	{
		final String playerName = player.getName();
		final MagicSchool school = spell.getSchool();

		// If we lack a key for the player, create one.
		if (!cooldowns.containsKey(playerName))
			cooldowns.put(playerName, new ArrayList<MagicSchool>());

		cooldowns.get(playerName).add(school); // Add the school to the cooldown list.
		scheduler.startAsyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				removeSchoolCooldown(playerName, school);
			}
		}, spell.getCooldown()); // Create a cooldown timer.
	}

	public void removeSchoolCooldown(String playerName, MagicSchool school)
	{
		// Remove the school from the players cooldown list if it exists.
		if (cooldowns.containsKey(playerName))
			cooldowns.get(playerName).remove(school);
	}

	public void resetCooldowns()
	{
		cooldowns.clear(); // Remove all cooldowns.
	}

	private ConcurrentHashMap<String, List<MagicSchool>> cooldowns = new ConcurrentHashMap<String, List<MagicSchool>>();
	private IScheduler scheduler;
}
