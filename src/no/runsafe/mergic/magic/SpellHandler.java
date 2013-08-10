package no.runsafe.mergic.magic;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.KillManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SpellHandler
{
	public SpellHandler(Spell[] spells, IScheduler scheduler, KillManager killManager)
	{
		// Populate the spell handler with every spell the plug-in has injected.
		for (Spell spell : spells)
			this.spellList.put(spell.getName().toLowerCase(), spell);

		// Provide a static scheduler for the spells and static kill manager for kill counting.
		SpellHandler.scheduler = scheduler;
		SpellHandler.killManager = killManager;
	}

	public void givePlayerSpellBook(RunsafePlayer player, Spell spell)
	{
		SpellType spellType = spell.getType();
		RunsafeMeta item = spellType.getCastItem().getItem(); // Create whatever item we cast with.
		item.setDisplayName(spell.getName()); // Rename the book to match the spell.

		List<String> lore = new ArrayList<String>(); // Create en empty holder for the lore text.
		lore.add("§7" + spell.getDescription()); // Add a short description to the item.
		lore.add("§8School:§7 " + spell.getSchool().name()); // Add the magic school.
		lore.add(String.format("§8Cooldown:§7 %d seconds", spell.getCooldown())); // Add the cooldown.
		lore.add("§7" + spellType.getText()); // Add information on the spell.
		item.setLore(lore);

		player.give(item); // Give the spell book to the player.
	}

	public Spell getSpellByName(String spellName)
	{
		if (spellName == null)
			return null;

		spellName = spellName.toLowerCase(); // Convert provided name to lower-case.

		// Check if we have a spell with the requested name.
		if (this.spellList.containsKey(spellName))
			return this.spellList.get(spellName);

		return null;
	}

	public void givePlayerAllSpells(RunsafePlayer player)
	{
		// Loop every spell in the handler and give it to the player.
		for (Spell spell : this.spellList.values())
			this.givePlayerSpellBook(player, spell);
	}

	private HashMap<String, Spell> spellList = new HashMap<String, Spell>();
	public static IScheduler scheduler;
	public static KillManager killManager;
}
