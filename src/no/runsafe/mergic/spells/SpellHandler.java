package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellHandler
{
	public SpellHandler(Spell[] spells)
	{
		this.spellList = new ArrayList<Spell>();

		// Add all the spells the plug-in has loaded into the handler.
		this.spellList.addAll(Arrays.asList(spells));
	}

	public void givePlayerSpellBook(RunsafePlayer player, Spell spell)
	{
		RunsafeMeta item = Item.Special.Crafted.EnchantedBook.getItem(); // Create a new, blank book.
		item.setDisplayName(spell.getName()); // Rename the book to match the spell.
		player.give(item); // Give the spell book to the player.
	}

	public Spell getSpellByName(String spellName)
	{
		// Loop every spell we know of, if we find one with a matching name, return it.
		for (Spell spell : this.spellList)
			if (spellName.equalsIgnoreCase(spell.getName()))
				return spell;

		return null;
	}

	private List<Spell> spellList;
}
