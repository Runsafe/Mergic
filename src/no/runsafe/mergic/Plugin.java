package no.runsafe.mergic;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.command.Command;
import no.runsafe.mergic.commands.CreateSpellBook;
import no.runsafe.mergic.commands.StartGameCommand;
import no.runsafe.mergic.commands.StopGameCommand;
import no.runsafe.mergic.magic.spells.*;
import no.runsafe.mergic.magic.CooldownManager;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		addComponent(getFirstPluginAPI(WorldGuardInterface.class));

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
		this.addComponent(BasicFireball.class);
		this.addComponent(Fireball.class);
		this.addComponent(ShadowStrike.class);
		this.addComponent(ArrowBarrage.class);
		this.addComponent(WindJump.class);
		this.addComponent(WindLeap.class);
		this.addComponent(FirePlume.class);
		this.addComponent(IceBlock.class);
		this.addComponent(SkyStrikes.class);
		this.addComponent(MoltenFlurry.class);
		this.addComponent(ArcaneWave.class);
		this.addComponent(BasicHeal.class);
		this.addComponent(Heal.class);
		//this.addComponent(VolatileCreature.class);

		// Commands
		Command mergic = new Command("mergic", "A collection of commands to control Wizard PvP", null);
		mergic.addSubCommand(getInstance(StartGameCommand.class));
		mergic.addSubCommand(getInstance(StopGameCommand.class));
		mergic.addSubCommand(getInstance(CreateSpellBook.class));
		this.addComponent(mergic);


		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("Kruithne", 55);
		test.put("cybersplice", 2);
		test.put("Solonari", 532);
		test.put("kiiaz", 56);
		test.put("JettKuso", 20);
		test.put("Naxanria", 42);

		Map<String, Integer> testSort = MapUtil.sortByValue(test);

		for (Map.Entry<String, Integer> node : testSort.entrySet())
			this.getComponent(IOutput.class).fine("%s - %d", node.getKey(), node.getValue());
	}
}
