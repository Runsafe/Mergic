package no.runsafe.mergic.magic;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.MagicClassHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpellHandler
{
	public SpellHandler(Spell[] spells, IScheduler scheduler, KillManager killManager, MagicClassHandler classHandler)
	{
		// Populate the spell handler with every spell the plug-in has injected.
		for (Spell spell : spells)
			spellList.put(spell.getName().toLowerCase(), spell);

		// Provide a static scheduler for the spells and static kill manager for kill counting.
		SpellHandler.scheduler = scheduler;
		SpellHandler.killManager = killManager;

		this.classHandler = classHandler; // Class handler used for players.
	}

	public void givePlayerSpellBook(IPlayer player, Spell spell)
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
		if (spellList.containsKey(spellName))
			return spellList.get(spellName);

		return null;
	}

	public void givePlayerAllSpells(IPlayer player)
	{
		MagicSchool playerSchool = classHandler.getPlayerSchool(player); // The magic school the player is using.

		// Loop every spell in the handler and give it to the player.
		for (Spell spell : spellList.values())
			if (spell.getSchool() == playerSchool)
				givePlayerSpellBook(player, spell);
	}

	private HashMap<String, Spell> spellList = new HashMap<String, Spell>();
	public static IScheduler scheduler;
	public static KillManager killManager;
	private MagicClassHandler classHandler;
}
