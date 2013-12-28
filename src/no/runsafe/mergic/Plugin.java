package no.runsafe.mergic;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Events;
import no.runsafe.mergic.commands.CreateSpellBook;
import no.runsafe.mergic.commands.StartGameCommand;
import no.runsafe.mergic.commands.StopGameCommand;
import no.runsafe.mergic.magic.CooldownManager;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.spells.*;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		addComponent(Events.class);
		addComponent(Commands.class);

		// Arena related things.
		this.addComponent(KillManager.class);

		this.addComponent(Arena.class);
		this.addComponent(Lobby.class);
		this.addComponent(Graveyard.class);
		this.addComponent(Game.class);

		this.addComponent(CooldownManager.class);
		this.addComponent(PlayerMonitor.class);

		// Spell related things.
		this.addComponent(SpellHandler.class);

		// Spell list
		this.addComponent(IceBlock.class);
		this.addComponent(BasicHeal.class);
		this.addComponent(Heal.class);
		this.addComponent(Storm.class);
		this.addComponent(HellStorm.class);
		this.addComponent(ConjureArmour.class);

		// Util
		this.addComponent(ControlledEntityCleaner.class);

		// Commands
		Command mergic = new Command("mergic", "A collection of commands to control Wizard PvP", null);
		mergic.addSubCommand(getInstance(StartGameCommand.class));
		mergic.addSubCommand(getInstance(StopGameCommand.class));
		mergic.addSubCommand(getInstance(CreateSpellBook.class));
		this.addComponent(mergic);
	}
}
