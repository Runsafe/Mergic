package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class SpellHandler
{
	public SpellHandler(Spell[] spells)
	{
		// Populate the spell handler with every spell the plug-in has injected.
		for (Spell spell : spells)
			this.spellList.put(spell.getName().toLowerCase(), spell);
	}

	public void givePlayerSpellBook(RunsafePlayer player, Spell spell)
	{
		RunsafeMeta item = Item.Special.Crafted.EnchantedBook.getItem(); // Create a new, blank book.
		item.setDisplayName(spell.getName()); // Rename the book to match the spell.
		player.give(item); // Give the spell book to the player.
	}

	public Spell getSpellByName(String spellName)
	{
		spellName = spellName.toLowerCase(); // Convert provided name to lower-case.

		// Check if we have a spell with the requested name.
		if (this.spellList.containsKey(spellName))
			return this.spellList.get(spellName);

		return null;
	}

	private HashMap<String, Spell> spellList = new HashMap<String, Spell>();
}
