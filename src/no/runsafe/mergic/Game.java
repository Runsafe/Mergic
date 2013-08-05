package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

public class Game implements IConfigurationChanged
{
	public Game(Lobby lobby, IScheduler scheduler)
	{
		this.lobby = lobby;
		this.scheduler = scheduler;
	}

	public boolean gameInProgress()
	{
		return this.gameInProgress;
	}

	public boolean launchGame()
	{
		if (this.preMatchDelay != -1 && this.preMatchLength != -1)
		{
			this.gameInProgress = true;
			this.currentPreMatchStep = this.preMatchLength;
			this.preMatchStep();
		}
		return this.gameInProgress;
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

		this.lobby.broadcastToLobby(String.format("New match starting in %d seconds.", this.currentPreMatchStep));
		this.scheduler.startAsyncTask(new Runnable() {
			@Override
			public void run() {
				preMatchStep();
			}
		}, this.preMatchDelay);
		this.currentPreMatchStep = this.currentPreMatchStep - this.preMatchDelay;
	}

	public void cancelGame()
	{
		if (this.gameInProgress())
		{
			this.currentPreMatchStep = -1; // Makes sure if we are in pre-match, we cancel.
			this.gameInProgress = false;
		}
	}

	private void startGame()
	{
		this.lobby.broadcastToLobby("This would be the part where you get teleported inside..");
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.preMatchLength = configuration.getConfigValueAsInt("preMatch.length");
		this.preMatchDelay = configuration.getConfigValueAsInt("preMatch.delay");
	}

	private Lobby lobby;
	private boolean gameInProgress = false;
	private int preMatchLength = -1;
	private int preMatchDelay = -1;
	private int currentPreMatchStep = 0;
	private IScheduler scheduler;
}
