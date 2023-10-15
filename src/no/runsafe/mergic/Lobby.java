package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.worldguardbridge.IRegionControl;

import java.util.Collections;
import java.util.List;

public class Lobby implements IConfigurationChanged
{
	public Lobby(IRegionControl worldGuard, IConsole output)
	{
		this.worldGuard = worldGuard;
		this.output = output;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the lobby as not set-up to begin with.
		lobbySetup = false;

		// Grab the location from configuration, throw an error if we can't.
		location = configuration.getConfigValueAsLocation("lobby.location");
		if (location == null)
		{
			output.logError("Lobby location missing or invalid in configuration.");
			return;
		}

		// Grab the world from configuration, throw an error if we can't.
		lobbyWorld = configuration.getConfigValueAsWorld("lobby.world");
		if (lobbyWorld == null)
		{
			output.logError("Lobby world missing or invalid in configuration.");
			return;
		}

		// Grab the region from configuration, throw an error if we can't.
		lobbyRegion = configuration.getConfigValueAsString("lobby.region");
		if (lobbyRegion == null)
		{
			output.logError("Lobby region missing in configuration.");
			return;
		}

		// Check that the region exists, if not, throw an error.
		if (worldGuard.getRectangle(lobbyWorld, lobbyRegion) == null)
		{
			output.logError("Lobby region invalid in configuration.");
			return;
		}

		// Everything went fine, flag the lobby as set-up correctly.
		lobbySetup = true;
	}

	public boolean isAvailable()
	{
		return lobbySetup;
	}

	public List<IPlayer> getPlayersInLobby()
	{
		// Check if the lobby is set-up, if so return a list of players inside the region.
		if (isAvailable())
			return worldGuard.getPlayersInRegion(lobbyWorld, lobbyRegion);

		// We are not set-up correctly, throw an empty list to prevent errors.
		return Collections.emptyList();
	}

	public void broadcastToLobby(String message)
	{
		// Loop every player in the lobby and send them the message.
		for (IPlayer player : getPlayersInLobby())
			player.sendColouredMessage(message);
	}

	public void teleportPlayersToLobby(List<IPlayer> playerList)
	{
		// Loop every player in the list given and teleport them into the lobby.
		for (IPlayer player : playerList)
			teleportPlayerToLobby(player);
	}

	public void teleportPlayerToLobby(IPlayer player)
	{
		// Teleport the player into the lobby, simple!
		player.teleport(location);
	}

	public void teleportLobbyPlayers(ILocation location)
	{
		// Loop every player in the lobby and teleport them to the given location.
		for (IPlayer player : getPlayersInLobby())
			player.teleport(location);
	}

	public String getLobbyRegionString()
	{
		return String.format("%s-%s", lobbyWorld.getName(), lobbyRegion);
	}

	public void playStartSound()
	{
		location.playSound(Sound.Creature.Wither.Spawn, 1000, 0); // Play a gong like sound when the match starts.
	}

	public void playEndSound()
	{
		location.playSound(Sound.Player.LevelUp, 1000, 0); // Play a ding when the match ends.
	}

	public boolean playerIsInLobby(IPlayer player)
	{
		if (!isAvailable())
			return false;

		IWorld playerWorld = player.getWorld(); // The world of the player.
		if (playerWorld == null || !playerWorld.isWorld(lobbyWorld))
			return false;

		List<String> playerRegions = worldGuard.getApplicableRegions(player);
		return playerRegions != null && playerRegions.contains(lobbyRegion);
	}

	private boolean lobbySetup;
	private IWorld lobbyWorld;
	private String lobbyRegion;
	private final IRegionControl worldGuard;
	private final IConsole output;
	private ILocation location;
}
