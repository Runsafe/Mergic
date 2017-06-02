package no.runsafe.mergic;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Arena implements IConfigurationChanged
{
	public Arena(IConsole output, IRegionControl worldGuard)
	{
		this.console = output;
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the arena as not set-up, as a fall-back.
		isSetup = false;

		// Grab the world from configuration, throw an error and return if we fail.
		world = configuration.getConfigValueAsWorld("arena.world");
		if (world == null)
		{
			console.logError("Arena world missing or invalid in configuration.");
			return;
		}

		// Grab the arena region from configuration, throw an error and return if we fail.
		region = configuration.getConfigValueAsString("arena.region");
		if (region == null)
		{
			console.logError("Arena region missing from configuration.");
			return;
		}

		// Grab the arena teleport region from configuration, throw an error and return if we fail.
		String teleportRegion = configuration.getConfigValueAsString("arena.teleportRegion");
		if (teleportRegion == null)
		{
			console.logError("Arena teleport region missing from configuration.");
			return;
		}

		teleportY = configuration.getConfigValueAsInt("arena.teleportY");
		if (teleportY == -1)
		{
			console.logError("Arena teleportY missing from configuration.");
			return;
		}

		// Grab the teleport region, throw an error and return if we fail like idiots.
		this.teleportRegion = worldGuard.getRectangle(world, teleportRegion);
		if (this.teleportRegion == null)
		{
			this.console.logError("Arena teleport region from configuration does not exist.");
			return;
		}

		// Flag the arena as set-up, woo!
		isSetup = true;
	}

	public void teleportPlayersIntoArena(List<IPlayer> playerList)
	{
		for (IPlayer player : playerList)
			teleportPlayerIntoArena(player);
	}

	public void teleportPlayerIntoArena(IPlayer player)
	{
		// Register the player as in-game in the arena instance.
		players.add(player);

		// Create random X and Z co-ordinates within the region.
		double randomX = getRandomBetween(((Number) teleportRegion.getMinX()).intValue(), ((Number) teleportRegion.getMaxX()).intValue());
		double randomZ = getRandomBetween(((Number) teleportRegion.getMinY()).intValue(), ((Number) teleportRegion.getMaxY()).intValue());


		// Get a safe spot at the co-ordinates we generated.
		ILocation loc = getSafeSpot(world.getLocation(randomX, teleportY, randomZ));

		// Adjust the location to be at the centre of the block.
		loc.incrementX(0.5D);
		loc.incrementZ(0.5D);

		// Teleport the player to the location.
		player.teleport(loc);
	}

	private ILocation getSafeSpot(ILocation location)
	{
		while (!location.getBlock().isAir()) // Loop until we find air.
		{
			location.incrementY(1); // Increment the Y co-ordinate.
		}
		return location;
	}

	public List<IPlayer> getPlayers()
	{
		return worldGuard.getPlayersInRegion(world, region);
	}

	public boolean playerIsInPhysicalArena(IPlayer player)
	{
		if (!isAvailable())
			return false;

		// Note: If a player is in the graveyard, they will also be in the arena as
		// the graveyard should be a sub-region of the arena if set-up right.
		// Check if the player is in the correct world, if not we can be sure they are not in the arena.
		if (!player.getWorld().getName().equals(world.getName()))
			return false;

		// Grab a list of the regions the player is currently in.
		List<String> playerRegions = worldGuard.getApplicableRegions(player);

		// If the list is null, there are no regions, so it's false!
		if (playerRegions == null)
			return false;

		// Return if we have the player in the correct region.
		return playerRegions.contains(region);
	}

	public boolean playerIsInGame(IPlayer player)
	{
		return players.contains(player);
	}

	public void removePlayer(IPlayer player)
	{
		players.remove(player);
	}

	public void removeAllPlayers()
	{
		players.clear();
	}

	public String getArenaRegionString()
	{
		return String.format("%s-%s", world.getName(), region);
	}

	private int getRandomBetween(int low, int high)
	{
		return low + (int) (Math.random() * ((high - low) + 1));
	}

	public boolean isAvailable()
	{
		return isSetup;
	}

	private boolean isSetup;
	private IWorld world;
	private double teleportY;
	private IConsole console;
	private IRegionControl worldGuard;
	private Rectangle2D teleportRegion;
	private String region;
	private List<IPlayer> players = new ArrayList<>();
}
