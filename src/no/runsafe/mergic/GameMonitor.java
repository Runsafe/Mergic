package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;

import java.util.Map;

public class GameMonitor implements IConfigurationChanged
{
	public GameMonitor(IScheduler scheduler, Game game, Lobby lobby, Arena arena, KillManager killManager, IConsole console)
	{
		this.scheduler = scheduler;
		this.game = game;
		this.lobby = lobby;
		this.arena = arena;
		this.killManager = killManager;
		this.console = console;

		this.scheduler.startSyncRepeatingTask(this::runCycle, 5, 5);
	}

	private void runCycle()
	{
		// Is there already a game in progress and do we have at least two players?
		if (!game.gameInProgress() && !game.gameHasStarted() && lobby.getPlayersInLobby().size() > 1)
		{
			try
			{
				game.launchGame(); // Launch the game!
				endTimer = scheduler.startSyncTask(this::cancelGame, matchLength * 60);
			}
			catch (GameException exception)
			{
				if (!hasThrown)
				{
					console.logException(exception); // Log the exception to the console.
					hasThrown = true;
				}
			}
		}
		else if (game.gameHasStarted())
		{
			if (arena.getPlayers().size() < 2)
			{
				cancelGame(); // Stop the game!
				return;
			}

			for (Map.Entry<IPlayer, Integer> score : killManager.getScoreList().entrySet())
				if (score.getValue() >= winScore)
					cancelGame(); // Stop the game!
		}
	}

	private void cancelGame()
	{
		game.cancelGame(); // Cancel the game.
		scheduler.cancelTask(endTimer); // Remove the timer.
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		winScore = configuration.getConfigValueAsInt("winScore");
		matchLength = configuration.getConfigValueAsInt("matchLength");
		hasThrown = false;
	}

	private final IScheduler scheduler;
	private final Game game;
	private final Lobby lobby;
	private final Arena arena;
	private final KillManager killManager;
	private final IConsole console;
	private boolean hasThrown = false;
	private int endTimer;
	private int winScore;
	private int matchLength;
}
