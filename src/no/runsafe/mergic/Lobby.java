package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements IConfigurationChanged
{
	public Lobby(WorldGuardInterface worldGuard, IOutput output)
	{
		this.worldGuard = worldGuard;
		this.output = output;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the lobby as not set-up to begin with.
		this.lobbySetup = false;

		// Grab the location from configuration, throw an error if we can't.
		this.location = configuration.getConfigValueAsLocation("lobby.location");
		if (this.location == null)
		{
			this.output.logError("Lobby location missing or invalid in configuration.");
			return;
		}

		// Grab the world from configuration, throw an error if we can't.
		this.lobbyWorld = configuration.getConfigValueAsWorld("lobby.world");
		if (this.lobbyWorld == null)
		{
			this.output.logError("Lobby world missing or invalid in configuration.");
			return;
		}

		// Grab the region from configuration, throw an error if we can't.
		this.lobbyRegion = configuration.getConfigValueAsString("lobby.region");
		if (this.lobbyRegion == null)
		{
			this.output.logError("Lobby region missing in configuration.");
			return;
		}

		// Check that the region exists, if not, throw an error.
		if (this.worldGuard.getRegion(this.lobbyWorld, this.lobbyRegion) == null)
		{
			this.output.logError("Lobby region invalid in configuration.");
			return;
		}

		// Everything went fine, flag the lobby as set-up correctly.
		this.lobbySetup = true;
	}

	public boolean isAvailable()
	{
		return this.lobbySetup;
	}

	public List<RunsafePlayer> getPlayersInLobby()
	{
		// Check if the lobby is set-up, if so return a list of players inside the region.
		if (this.isAvailable())
			return this.worldGuard.getPlayersInRegion(this.lobbyWorld, this.lobbyRegion);

		// We are not set-up correctly, throw an empty list to prevent errors.
		return new ArrayList<RunsafePlayer>();
	}

	public void broadcastToLobby(String message)
	{
		// Loop every player in the lobby and send them the message.
		for (RunsafePlayer player : this.getPlayersInLobby())
			player.sendColouredMessage(message);
	}

	public void teleportPlayersToLobby(List<RunsafePlayer> playerList)
	{
		// Loop every player in the list given and teleport them into the lobby.
		for (RunsafePlayer player : playerList)
			this.teleportPlayerToLobby(player);
	}

	public void teleportPlayerToLobby(RunsafePlayer player)
	{
		// Teleport the player into the lobby, simple!
		player.teleport(this.location);
	}

	public void teleportLobbyPlayers(RunsafeLocation location)
	{
		// Loop every player in the lobby and teleport them to the given location.
		for (RunsafePlayer player : this.getPlayersInLobby())
			player.teleport(location);
	}

	public String getLobbyRegionString()
	{
		return String.format("%s-%s", this.lobbyWorld.getName(), this.lobbyRegion);
	}

	public void playStartSound()
	{
		this.location.Play(Sound.Creature.Wither.Spawn, 1000, 0); // Play a gong like sound when the match starts.
	}

	private boolean lobbySetup;
	private RunsafeWorld lobbyWorld;
	private String lobbyRegion;
	private WorldGuardInterface worldGuard;
	private IOutput output;
	private RunsafeLocation location;
}
