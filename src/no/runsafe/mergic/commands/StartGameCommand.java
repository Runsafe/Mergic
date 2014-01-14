package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.mergic.Game;
import no.runsafe.mergic.GameException;

public class StartGameCommand extends ExecutableCommand
{
	public StartGameCommand(Game game)
	{
		super("start", "Starts a match.", "runsafe.mergic.start");
		this.game = game;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		// Check if the game is running, if it is, throw an error back.
		if (game.gameInProgress())
			return "&cThe game is already in progress.";

		// Attempt to launch the game.
		try
		{
			game.launchGame();
			return "&2The game has been launched.";
		}
		catch (GameException exception)
		{
			// We failed, throw the exception message to console.
			return "&cFailed to launch game: " + exception.getMessage();
		}
	}

	private Game game;
}
