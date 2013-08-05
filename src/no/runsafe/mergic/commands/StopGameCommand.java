package no.runsafe.mergic.commands;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.mergic.Game;

import java.util.Map;

public class StopGameCommand extends ExecutableCommand
{
	public StopGameCommand(Game game)
	{
		super("stop", "Stops the current match in progress.", "runsafe.mergic.stop");
		this.game = game;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, Map<String, String> parameters)
	{
		if (!this.game.gameInProgress())
			return "&cThere is no game in progress to stop.";

		this.game.cancelGame();
		return "&2The current game has been stopped.";
	}

	private Game game;
}
