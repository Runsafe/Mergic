package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
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
		this.lobbySetup = false;

		this.location = configuration.getConfigValueAsLocation("lobby.location");
		if (this.location == null)
		{
			this.output.logError("Lobby location missing or invalid in configuration.");
			return;
		}

		this.lobbyWorld = configuration.getConfigValueAsWorld("lobby.world");
		if (this.lobbyWorld == null)
		{
			this.output.logError("Lobby world missing or invalid in configuration.");
			return;
		}

		this.lobbyRegion = configuration.getConfigValueAsString("lobby.region");

		if (this.lobbyRegion == null)
		{
			this.output.logError("Lobby region missing in configuration.");
			return;
		}

		if (this.worldGuard.getRegion(this.lobbyWorld, this.lobbyRegion) == null)
			this.output.logError("Lobby region invalid in configuration.");
		else
			this.lobbySetup = true;
	}

	public boolean isAvailable()
	{
		return this.lobbySetup;
	}

	private List<RunsafePlayer> getPlayersInLobby()
	{
		if (this.isAvailable())
			return this.worldGuard.getPlayersInRegion(this.lobbyWorld, this.lobbyRegion);

		return new ArrayList<RunsafePlayer>();
	}

	public void broadcastToLobby(String message)
	{
		for (RunsafePlayer player : this.getPlayersInLobby())
			player.sendColouredMessage(message);
	}

	public void teleportPlayersToLobby(List<RunsafePlayer> playerList)
	{
		for (RunsafePlayer player : playerList)
			player.teleport(this.location);
	}

	public void teleportLobbyPlayers(RunsafeLocation location)
	{
		for (RunsafePlayer player : this.getPlayersInLobby())
			player.teleport(location);
	}

	private boolean lobbySetup;
	private RunsafeWorld lobbyWorld;
	private String lobbyRegion;
	private WorldGuardInterface worldGuard;
	private IOutput output;
	private RunsafeLocation location;
}
