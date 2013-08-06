package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

public class Game implements IConfigurationChanged
{
	public Game(Lobby lobby, Arena arena, IScheduler scheduler)
	{
		this.lobby = lobby;
		this.arena = arena;
		this.scheduler = scheduler;
	}

	public boolean gameInProgress()
	{
		return this.gameInProgress;
	}

	public void launchGame() throws GameException
	{
		// Check if the lobby has been set-up without problems.
		if (!this.lobby.isAvailable())
			throw new GameException("Lobby is not available. Check errors on startup.");

		// Check if the arena has been set-up without problems.
		if (!this.arena.isAvailable())
			throw new GameException("Arena is not available. Check errors on startup.");

		// Check if the pre-match timings were defined correctly.
		if (this.preMatchDelay == -1 || this.preMatchLength == -1)
			throw new GameException("Pre-match timers not set in configuration.");

		this.gameInProgress = true; // Flag the game as in progress.
		this.currentPreMatchStep = this.preMatchLength; // Set the pre-match timer to it's full length.
		this.preMatchStep(); // Begin the pre-match timer.
	}

	private void preMatchStep()
	{
		// If we are lower than zero, the pre-match was cancelled.
		if (this.currentPreMatchStep < 0)
			return;

		// If we are at zero, the pre-match expired, we can push into a match.
		if (this.currentPreMatchStep == 0)
		{
			this.startGame();
			return;
		}

		// Send a message to all the players, be like, yo.. match starting.
		this.lobby.broadcastToLobby(String.format("New match starting in %d seconds.", this.currentPreMatchStep));

		// Start a timer for the next pre-match step. This could be a repeating timer, but it's not for now.
		this.scheduler.startAsyncTask(new Runnable() {
			@Override
			public void run() {
				preMatchStep();
			}
		}, this.preMatchDelay);

		// Lower the current step by the delay amount.
		this.currentPreMatchStep = this.currentPreMatchStep - this.preMatchDelay;
	}

	public void cancelGame()
	{
		// Do we have a game running?
		if (this.gameInProgress())
		{
			this.currentPreMatchStep = -1; // Signal for the pre-match countdown (if running) to cancel.
			this.gameInProgress = false; // Flag the game as not running.
		}
	}

	private void startGame()
	{
		// Teleport all the players from the lobby into the arena.
		this.arena.teleportPlayersIntoArena(this.lobby.getPlayersInLobby());
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.preMatchLength = configuration.getConfigValueAsInt("preMatch.length");
		this.preMatchDelay = configuration.getConfigValueAsInt("preMatch.delay");
	}

	private Lobby lobby;
	private Arena arena;
	private boolean gameInProgress = false;
	private int preMatchLength = -1;
	private int preMatchDelay = -1;
	private int currentPreMatchStep = 0;
	private IScheduler scheduler;
}
