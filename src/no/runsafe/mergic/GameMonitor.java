package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.log.IConsole;

import java.util.Map;

public class GameMonitor implements IConfigurationChanged
{
	public GameMonitor(IScheduler scheduler, Game game, Lobby lobby, KillManager killManager, IConsole console)
	{
		this.scheduler = scheduler;
		this.game = game;
		this.lobby = lobby;
		this.killManager = killManager;
		this.console = console;

		this.scheduler.startSyncRepeatingTask(new Runnable()
		{
			@Override
			public void run()
			{
				runCycle();
			}
		}, 5, 5);
	}

	private void runCycle()
	{
		if (!game.gameInProgress()) // Is there already a game in progress?
		{
			if (lobby.getPlayersInLobby().size() > 1) // Do we have at least two players?
			{
				try
				{
					game.launchGame(); // Launch the game!
					endTimer = scheduler.startSyncTask(new Runnable()
					{
						@Override
						public void run()
						{
							cancelGame();
						}
					}, matchLength * 60);
				}
				catch (GameException exception)
				{
					console.logException(exception); // Log the exception to the console.
				}
			}
		}
		else
		{
			for (Map.Entry<String, Integer> score : killManager.getScoreList().entrySet())
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
	}

	private final IScheduler scheduler;
	private final Game game;
	private final Lobby lobby;
	private final KillManager killManager;
	private final IConsole console;
	private int endTimer;
	private int winScore;
	private int matchLength;
}
