package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graveyard implements IConfigurationChanged
{
	public Graveyard(Arena arena, IScheduler scheduler, IOutput output, WorldGuardInterface worldGuard)
	{
		this.arena = arena;
		this.scheduler = scheduler;
		this.output = output;
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the graveyard as not set-up, providing a fall-back.
		this.isSetup = false;

		// Grab the deadTime from configuration, throw an error and return if we failed.
		this.deadTime = configuration.getConfigValueAsInt("graveyard.deadTime");
		if (this.deadTime < 0)
		{
			this.output.logError("Graveyard deadTime invalid or missing in configuration.");
			return;
		}

		// Grab the world from configuration, throw an error and return if we failed.
		this.world = configuration.getConfigValueAsWorld("graveyard.world");
		if (this.world == null)
		{
			this.output.logError("Graveyard world invalid or missing in configuration.");
			return;
		}

		// Grab the region from the configuration, throw an error and return if we failed.
		this.region = configuration.getConfigValueAsString("graveyard.region");
		if (this.region == null)
		{
			this.output.logError("Graveyard region missing in configuration.");
			return;
		}

		// Check the region exists, if not throw an error and return.
		if (this.worldGuard.getRegion(this.world, this.region) == null)
		{
			this.output.logError("Graveyard region invalid in configuration.");
			return;
		}

		// Grab the teleport location from configuration, throw an error and return if we failed.
		this.location = configuration.getConfigValueAsLocation("graveyard.location");
		if (this.location == null)
		{
			this.output.logError("Graveyard location missing or invalid in configuration.");
			return;
		}

		// Flag the graveyard as set-up.
		this.isSetup = true;
	}

	public boolean playerIsInGraveyard(RunsafePlayer player)
	{
		return this.deadTimers.containsKey(player.getName());
	}

	public void removePlayer(RunsafePlayer player)
	{
		String playerName = player.getName();

		// Check if we have a timer for the player in the graveyard.
		if (this.deadTimers.containsKey(playerName))
		{
			this.scheduler.cancelTask(this.deadTimers.get(playerName)); // Cancel the timer.
			this.deadTimers.remove(playerName); // Remove the players timer.
			this.arena.teleportPlayerIntoArena(player); // Teleport the player back into the arena.
		}
	}

	public List<RunsafePlayer> getPlayers()
	{
		return this.worldGuard.getPlayersInRegion(this.world, this.region);
	}

	public void removeAllTimers()
	{
		// Loop every timer, cancel it and remove it.
		for (Map.Entry<String, Integer> node : this.deadTimers.entrySet())
		{
			this.scheduler.cancelTask(node.getValue()); // Cancel the timer.
			this.deadTimers.remove(node.getKey()); // Remove the timer node.
		}
	}

	public void teleportPlayerToGraveyard(final RunsafePlayer player)
	{
		// Teleport the player to the location stored in this instance.
		player.teleport(this.location);

		// Store a new timer for the player to respawn them.
		this.deadTimers.put(player.getName(), this.scheduler.startSyncTask(new Runnable() {
			@Override
			public void run() {
				removePlayer(player);
			}
		}, this.deadTime));
	}

	public boolean isAvailable()
	{
		return this.isSetup;
	}

	private Arena arena;
	private IScheduler scheduler;
	private HashMap<String, Integer> deadTimers = new HashMap<String, Integer>();
	private int deadTime = -1;
	private RunsafeWorld world;
	private String region;
	private RunsafeLocation location;
	private boolean isSetup;
	private IOutput output;
	private WorldGuardInterface worldGuard;
}
