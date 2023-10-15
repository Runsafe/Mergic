package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;

public class CreateSpellBook extends PlayerCommand
{
	public CreateSpellBook(SpellHandler spellHandler)
	{
		super("createbook", "Creates a spell book", "runsafe.mergic.books.create", new TrailingArgument("spell"));
		this.spellHandler = spellHandler;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		Spell spell = spellHandler.getSpellByName(parameters.getValue("spell"));
		if (spell == null)
			return "&cUnable to find spell with that name.";

		spellHandler.givePlayerSpellBook(executor, spell);
		return "&2Created spell-book: " + spell.getName();
	}

	private final SpellHandler spellHandler;
}
