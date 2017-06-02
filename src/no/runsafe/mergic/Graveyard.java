package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Graveyard implements IConfigurationChanged
{
	public Graveyard(Arena arena, IScheduler scheduler, IConsole output, IRegionControl worldGuard)
	{
		this.arena = arena;
		this.scheduler = scheduler;
		this.console = output;
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the graveyard as not set-up, providing a fall-back.
		isSetup = false;

		// Grab the deadTime from configuration, throw an error and return if we failed.
		deadTime = configuration.getConfigValueAsInt("graveyard.deadTime");
		if (deadTime < 0)
		{
			console.logError("Graveyard deadTime invalid or missing in configuration.");
			return;
		}

		// Grab the world from configuration, throw an error and return if we failed.
		world = configuration.getConfigValueAsWorld("graveyard.world");
		if (world == null)
		{
			console.logError("Graveyard world invalid or missing in configuration.");
			return;
		}

		// Grab the region from the configuration, throw an error and return if we failed.
		region = configuration.getConfigValueAsString("graveyard.region");
		if (region == null)
		{
			console.logError("Graveyard region missing in configuration.");
			return;
		}

		// Check the region exists, if not throw an error and return.
		if (worldGuard.getRectangle(this.world, this.region) == null)
		{
			console.logError("Graveyard region invalid in configuration.");
			return;
		}

		// Grab the teleport location from configuration, throw an error and return if we failed.
		location = configuration.getConfigValueAsLocation("graveyard.location");
		if (location == null)
		{
			console.logError("Graveyard location missing or invalid in configuration.");
			return;
		}

		// Flag the graveyard as set-up.
		isSetup = true;
	}

	public boolean playerIsInGraveyard(IPlayer player)
	{
		return deadTimers.containsKey(player);
	}

	public void removePlayer(IPlayer player)
	{
		// Check if we have a timer for the player in the graveyard.
		if (deadTimers.containsKey(player))
		{
			scheduler.cancelTask(deadTimers.get(player)); // Cancel the timer.
			deadTimers.remove(player); // Remove the players timer.
			arena.teleportPlayerIntoArena(player); // Teleport the player back into the arena.
		}
	}

	public List<IPlayer> getPlayers()
	{
		return worldGuard.getPlayersInRegion(world, region);
	}

	public void removeAllTimers()
	{
		// Loop every timer, cancel it and remove it.
		for (Map.Entry<IPlayer, Integer> node : deadTimers.entrySet())
		{
			scheduler.cancelTask(node.getValue()); // Cancel the timer.
			deadTimers.remove(node.getKey()); // Remove the timer node.
		}
	}

	public void teleportPlayerToGraveyard(final IPlayer player)
	{
		// Teleport the player to the location stored in this instance.
		player.teleport(location);

		// Store a new timer for the player to respawn them.
		deadTimers.put(player, scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				removePlayer(player);
			}
		}, deadTime));
	}

	public boolean isAvailable()
	{
		return isSetup;
	}

	private Arena arena;
	private IScheduler scheduler;
	private ConcurrentHashMap<IPlayer, Integer> deadTimers = new ConcurrentHashMap<>();
	private int deadTime = -1;
	private IWorld world;
	private String region;
	private ILocation location;
	private boolean isSetup;
	private IConsole console;
	private IRegionControl worldGuard;
}
