package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.PlayerMonitor;

public class DebugMode extends PlayerCommand
{
	public DebugMode(PlayerMonitor playerMonitor)
	{
		super("debug", "Toggle debug mode.", "runsafe.mergic.debug");
		this.playerMonitor = playerMonitor;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		return playerMonitor.toggleDebugging(executor) ? "Debug mode enabled" : "Debug mode disabled";
	}

	private final PlayerMonitor playerMonitor;
}
