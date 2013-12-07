package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.CooldownManager;

import java.util.HashMap;
import java.util.Map;

public class Game implements IConfigurationChanged
{
	public Game(Graveyard graveyard, Lobby lobby, Arena arena, IScheduler scheduler, CooldownManager cooldownManager, KillManager killManager)
	{
		this.graveyard = graveyard;
		this.lobby = lobby;
		this.arena = arena;
		this.scheduler = scheduler;
		this.cooldownManager = cooldownManager;
		this.killManager = killManager;
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

		// Check if the graveyard has been set-up without problems.
		if (!this.graveyard.isAvailable())
			throw new GameException("Graveyard is not available. Check errors on startup");

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
		this.lobby.broadcastToLobby(String.format("&3New match starting in %d seconds.", this.currentPreMatchStep));

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

	public void removePlayerFromGame(IPlayer player)
	{
		this.lobby.teleportPlayerToLobby(player); // Teleport them to the lobby.
		this.arena.removePlayer(player); // Remove them from the arena list.
		player.sendColouredMessage("You have been removed from the match."); // Send them a message explaining.
	}

	public void cancelGame()
	{
		// Do we have a game running?
		if (this.gameInProgress())
		{
			this.currentPreMatchStep = -1; // Signal for the pre-match countdown (if running) to cancel.
			this.gameInProgress = false; // Flag the game as not running.
			this.arena.removeAllPlayers(); // Remove all players from the game.
			this.lobby.teleportPlayersToLobby(this.arena.getPlayers()); // Move players from arena to lobby.
			this.graveyard.removeAllTimers(); // Cancel any outstanding graveyard timers.
			this.cooldownManager.resetCooldowns(); // Reset all cooldowns.

			// We don't want to reset data for the tournament.
			//this.killManager.wipeAllData();

			// Everything reset, let's give the players now in the lobby a list of what just went down.

			// Generate the score list.
			Map<String, Integer> scores = this.killManager.getScoreList();

			Map<String, Integer> top = new HashMap<String, Integer>();
			int current = 1;
			for (Map.Entry<String, Integer> node : scores.entrySet())
			{
				if (current == 6)
					break;

				top.put(node.getKey(), node.getValue());
				current++;
			}

			// Loop every player now in the lobby and give them their score and the top 5.
			for (IPlayer player : this.lobby.getPlayersInLobby())
			{
				player.sendColouredMessage("&cThe match has ended!");
				for (Map.Entry<String, Integer> node : top.entrySet())
					player.sendColouredMessage("%d. &b%s &f- &a%d&f kills.", current, node.getKey(), node.getValue());

				player.sendColouredMessage("You are currently at &a%d&f kills.", this.killManager.getPlayerKills(player));
			}
		}
	}

	private void startGame()
	{
		// Teleport all the players from the lobby into the arena.
		this.arena.teleportPlayersIntoArena(this.lobby.getPlayersInLobby());
		this.lobby.playStartSound(); // Play that funky gong, white boy!
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.preMatchLength = configuration.getConfigValueAsInt("preMatch.length");
		this.preMatchDelay = configuration.getConfigValueAsInt("preMatch.delay");
	}

	private Graveyard graveyard;
	private Lobby lobby;
	private Arena arena;
	private boolean gameInProgress = false;
	private int preMatchLength = -1;
	private int preMatchDelay = -1;
	private int currentPreMatchStep = 0;
	private IScheduler scheduler;
	private CooldownManager cooldownManager;
	private KillManager killManager;
}
