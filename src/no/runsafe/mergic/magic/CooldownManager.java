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
		return !cooldowns.containsKey(player) || !cooldowns.get(player).contains(spell.getType());
	}

	public void applySchoolCooldown(final IPlayer player, Spell spell)
	{
		final SpellType spellType = spell.getType();

		// If we lack a key for the player, create one.
		if (!cooldowns.containsKey(player))
			cooldowns.put(player, new ArrayList<SpellType>(0));

		cooldowns.get(player).add(spellType); // Add the school to the cooldown list.
		scheduler.startAsyncTask(() -> removeSchoolCooldown(player, spellType), spell.getCooldown()); // Create a cooldown timer.
	}

	public void removeSchoolCooldown(IPlayer player, SpellType spellType)
	{
		// Remove the school from the players cooldown list if it exists.
		if (cooldowns.containsKey(player))
			cooldowns.get(player).remove(spellType);
	}

	public void resetCooldowns()
	{
		cooldowns.clear(); // Remove all cooldowns.
	}

	private ConcurrentHashMap<IPlayer, List<SpellType>> cooldowns = new ConcurrentHashMap<>(0);
	private IScheduler scheduler;
}
