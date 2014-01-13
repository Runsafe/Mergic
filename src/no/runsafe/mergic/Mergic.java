package no.runsafe.mergic;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Events;
import no.runsafe.mergic.commands.CreateSpellBook;
import no.runsafe.mergic.commands.DebugMode;
import no.runsafe.mergic.commands.StartGameCommand;
import no.runsafe.mergic.commands.StopGameCommand;
import no.runsafe.mergic.magic.CooldownManager;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.spells.*;

public class Mergic extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		addComponent(Events.class);
		addComponent(Commands.class);

		// Arena related things.
		addComponent(KillManager.class);

		addComponent(Arena.class);
		addComponent(Lobby.class);
		addComponent(Graveyard.class);
		addComponent(Game.class);

		addComponent(CooldownManager.class);
		addComponent(PlayerMonitor.class);
		addComponent(GameMonitor.class);

		// Spell related things.
		addComponent(SpellHandler.class);
		addComponent(MagicClassHandler.class);

		// Storm spells
		addComponent(FireStorm.class);
		addComponent(IceStorm.class);
		addComponent(NatureStorm.class);
		addComponent(ShadowStorm.class);

		// Bolt spells
		addComponent(FireBolt.class);
		addComponent(IceBolt.class);
		addComponent(NatureBolt.class);
		addComponent(ShadowBolt.class);

		// Healing spells
		addComponent(Heal.class);
		addComponent(BasicHeal.class);

		// Util
		addComponent(ControlledEntityCleaner.class);

		// Commands
		Command mergic = new Command("mergic", "A collection of commands to control Wizard PvP", null);
		mergic.addSubCommand(getInstance(StartGameCommand.class));
		mergic.addSubCommand(getInstance(StopGameCommand.class));
		mergic.addSubCommand(getInstance(CreateSpellBook.class));
		mergic.addSubCommand(getInstance(DebugMode.class));
		addComponent(mergic);
	}
}
