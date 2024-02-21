package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.api.server.IBroadcast;
import no.runsafe.mergic.achievements.ApprenticeWizard;
import no.runsafe.mergic.achievements.MasterOfMagic;
import no.runsafe.mergic.magic.CooldownManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements IConfigurationChanged
{
	public Game(Graveyard graveyard, Lobby lobby, Arena arena, IScheduler scheduler, CooldownManager cooldownManager, KillManager killManager, IBroadcast broadcaster)
	{
		this.graveyard = graveyard;
		this.lobby = lobby;
		this.arena = arena;
		this.scheduler = scheduler;
		this.cooldownManager = cooldownManager;
		this.killManager = killManager;
		this.broadcaster = broadcaster;
	}

	public boolean gameInProgress()
	{
		return gameInProgress;
	}

	public boolean gameHasStarted()
	{
		return gameHasStarted;
	}

	public void launchGame() throws GameException
	{
		// Check if the lobby has been set-up without problems.
		if (!lobby.isAvailable())
			throw new GameException("Lobby is not available. Check errors on startup.");

		// Check if the arena has been set-up without problems.
		if (arena.isUnavailable())
			throw new GameException("Arena is not available. Check errors on startup.");

		// Check if the graveyard has been set-up without problems.
		if (!graveyard.isAvailable())
			throw new GameException("Graveyard is not available. Check errors on startup");

		// Check if the pre-match timings were defined correctly.
		if (preMatchDelay == -1 || preMatchLength == -1)
			throw new GameException("Pre-match timers not set in configuration.");

		gameInProgress = true; // Flag the game as in progress.
		currentPreMatchStep = preMatchLength; // Set the pre-match timer to it's full length.
		preMatchStep(); // Begin the pre-match timer.
	}

	private void preMatchStep()
	{
		// If we are lower than zero, the pre-match was cancelled.
		if (currentPreMatchStep < 0)
			return;

		// If we are at zero, the pre-match expired, we can push into a match.
		if (currentPreMatchStep == 0)
		{
			if (lobby.getPlayersInLobby().size() < 2)
			{
				for (IPlayer player : lobby.getPlayersInLobby()) // send message to our one player if exists
					player.sendColouredMessage("&3Not enough players. &cMatch cancelled&3.");
				gameInProgress = false;
			}
			else startGame();
			return;
		}

		// Send a message to all the players, be like, yo.. match starting.
		lobby.broadcastToLobby(String.format("&3New match starting in %d seconds.", currentPreMatchStep));

		// Start a timer for the next pre-match step. This could be a repeating timer, but it's not for now.
		scheduler.startAsyncTask(this::preMatchStep, preMatchDelay);

		// Lower the current step by the delay amount.
		currentPreMatchStep = currentPreMatchStep - preMatchDelay;
	}

	public void removePlayerFromGame(IPlayer player)
	{
		lobby.teleportPlayerToLobby(player); // Teleport them to the lobby.
		arena.removePlayer(player); // Remove them from the arena list.
		killManager.wipePlayerData(player); // Wipe player data.
		player.sendColouredMessage("You have been removed from the match."); // Send them a message explaining.
	}

	public void cancelGame()
	{
		// Do we have a game running?
		if (!gameInProgress())
			return;

		ConcurrentHashMap<IPlayer, Integer> scores = killManager.getScoreList(); // Grab the score list for the match.
		currentPreMatchStep = -1; // Signal for the pre-match countdown (if running) to cancel.
		gameInProgress = false; // Flag the game as not running.
		gameHasStarted = false; // Flag the game as not started;
		arena.removeAllPlayers(); // Remove all players from the game.
		lobby.teleportPlayersToLobby(arena.getPlayers()); // Move players from arena to lobby.
		graveyard.removeAllTimers(); // Cancel any outstanding graveyard timers.
		cooldownManager.resetCooldowns(); // Reset all cooldowns.
		lobby.playEndSound(); // Play the end-of-game sound.

		// Everything reset, let's give the players now in the lobby a list of what just went down.

		// Generate the score list.
		List<Map.Entry<IPlayer, Integer>> top = new ArrayList<>(scores.size());

		for (Map.Entry<IPlayer, Integer> node : scores.entrySet())
		{
			if (top.isEmpty())
			{
				top.add(0, node);
				continue;
			}

			int index = 0;
			for (Map.Entry<IPlayer, Integer> compareNode : top)
			{
				if (node.getValue() > compareNode.getValue())
				{
					top.add(index, node);
					break;
				}
				index++;
			}
		}

		List<String> output = new ArrayList<>(2 + top.size());
		output.add("&cThe match has ended!");

		int pos = 1;
		for (Map.Entry<IPlayer, Integer> node : top)
		{
			IPlayer player = node.getKey();
			if (player == null)
			{
				pos++;
				continue;
			}

			if (pos == 6)
				break;

			if (pos < 4)
				new ApprenticeWizard(player).Fire();

			if (pos == 1)
			{
				new MasterOfMagic(player).Fire();

				broadcaster.broadcastMessage("&b" + player.getName() + " has triumphed at Wizard PvP!");
				player.sendTitle("§AYou Win!", "Score: §C"+node.getValue().toString());
			}
			output.add(String.format("%d. &b%s &f- &a%d&f kills.", pos, player.getName(), node.getValue()));
			pos++;
		}

		// Loop every player now in the lobby and give them their score and the top 5.
		for (IPlayer player : lobby.getPlayersInLobby())
		{
			for (String line : output)
				player.sendColouredMessage(line);

			player.sendColouredMessage("You are currently at &a%d&f kills.", killManager.getPlayerKills(player));
		}

		killManager.wipeAllData(); // Wipe all of the data before the next match.
	}

	private void startGame()
	{
		List<IPlayer> lobbyPlayers = lobby.getPlayersInLobby();
		// Teleport all the players from the lobby into the arena.
		arena.teleportPlayersIntoArena(lobbyPlayers);
		lobby.playStartSound(); // Play that funky gong, white boy!
		gameHasStarted = true;

		for (IPlayer player : lobbyPlayers)
			player.sendColouredMessage("&3The game has started! get &4%d&3 kills to win!", winScore);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		preMatchLength = configuration.getConfigValueAsInt("preMatch.length");
		preMatchDelay = configuration.getConfigValueAsInt("preMatch.delay");
		winScore = configuration.getConfigValueAsInt("winScore");
	}

	private final Graveyard graveyard;
	private final Lobby lobby;
	private final Arena arena;
	private boolean gameInProgress = false;
	private boolean gameHasStarted = false;
	private int preMatchLength = -1;
	private int preMatchDelay = -1;
	private int currentPreMatchStep = 0;
	private int winScore;
	private final IScheduler scheduler;
	private final CooldownManager cooldownManager;
	private final KillManager killManager;
	private final IBroadcast broadcaster;
}
