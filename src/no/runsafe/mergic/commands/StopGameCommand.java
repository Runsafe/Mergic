package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.mergic.Game;

public class StopGameCommand extends ExecutableCommand
{
	public StopGameCommand(Game game)
	{
		super("stop", "Stops the current match in progress.", "runsafe.mergic.stop");
		this.game = game;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		// Check if the game is running, if not throw an error back.
		if (!game.gameInProgress())
			return "&cThere is no game in progress to stop.";

		game.cancelGame();
		return "&2The current game has been stopped.";
	}

	private final Game game;
}
