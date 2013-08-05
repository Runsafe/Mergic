package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.mergic.Game;

import java.util.Map;

public class StartGameCommand extends ExecutableCommand
{
	public StartGameCommand(Game game)
	{
		super("start", "Starts the Wizard PvP game.", "runsafe.mergic.start");
		this.game = game;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, Map<String, String> parameters)
	{
		if (this.game.gameInProgress())
			return "&cThe game is already in progress.";

		this.game.launchGame();
		return "&2The game has been started.";
	}

	private Game game;
}
