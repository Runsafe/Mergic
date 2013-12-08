package no.runsafe.mergic;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.List;

public class Arena implements IConfigurationChanged
{
	public Arena(IOutput output, WorldGuardInterface worldGuard)
	{
		this.output = output;
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		// Flag the arena as not set-up, as a fall-back.
		this.isSetup = false;

		// Grab the world from configuration, throw an error and return if we fail.
		this.world = configuration.getConfigValueAsWorld("arena.world");
		if (this.world == null)
		{
			this.output.logError("Arena world missing or invalid in configuration.");
			return;
		}

		// Grab the arena region from configuration, throw an error and return if we fail.
		this.region = configuration.getConfigValueAsString("arena.region");
		if (this.region == null)
		{
			this.output.logError("Arena region missing from configuration.");
			return;
		}

		// Grab the arena teleport region from configuration, throw an error and return if we fail.
		String teleportRegion = configuration.getConfigValueAsString("arena.teleportRegion");
		if (teleportRegion == null)
		{
			this.output.logError("Arena teleport region missing from configuration.");
			return;
		}

		this.teleportY = configuration.getConfigValueAsInt("arena.teleportY");
		if (this.teleportY == -1)
		{
			this.output.logError("Arena teleportY missing from configuration.");
			return;
		}

		// Grab the teleport region, throw an error and return if we fail like idiots.
		this.teleportRegion = this.worldGuard.getRegion(world, teleportRegion);
		if (this.teleportRegion == null)
		{
			this.output.logError("Arena teleport region from configuration does not exist.");
			return;
		}

		// Flag the arena as set-up, woo!
		this.isSetup = true;
	}

	public void teleportPlayersIntoArena(List<IPlayer> playerList)
	{
		for (IPlayer player : playerList)
			this.teleportPlayerIntoArena(player);
	}

	public void teleportPlayerIntoArena(IPlayer player)
	{
		// Register the player as in-game in the arena instance.
		this.players.add(player.getName());

		// Create random X and Z co-ordinates within the region.
		int randomX = getRandomBetween(teleportRegion.getMinimumPoint().getBlockX(), teleportRegion.getMaximumPoint().getBlockX());
		int randomZ = getRandomBetween(teleportRegion.getMinimumPoint().getBlockZ(), teleportRegion.getMaximumPoint().getBlockZ());


		// Get a safe spot at the co-ordinates we generated.
		ILocation loc = getSafeSpot(new RunsafeLocation(this.world, randomX, this.teleportY, randomZ));

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
		return this.worldGuard.getPlayersInRegion(this.world, this.region);
	}

	public boolean playerIsInPhysicalArena(IPlayer player)
	{
		if (!this.isAvailable())
			return false;

		// Note: If a player is in the graveyard, they will also be in the arena as
		// the graveyard should be a sub-region of the arena if set-up right.
		// Check if the player is in the correct world, if not we can be sure they are not in the arena.
		if (!player.getWorld().getName().equals(this.world.getName()))
			return false;

		// Grab a list of the regions the player is currently in.
		List<String> playerRegions = this.worldGuard.getApplicableRegions(player);

		// If the list is null, there are no regions, so it's false!
		if (playerRegions == null)
			return false;

		// Return if we have the player in the correct region.
		return playerRegions.contains(this.region);
	}

	public boolean playerIsInGame(IPlayer player)
	{
		return this.players.contains(player.getName());
	}

	public void removePlayer(IPlayer player)
	{
		this.players.remove(player.getName());
	}

	public void removeAllPlayers()
	{
		this.players.clear();
	}

	public String getArenaRegionString()
	{
		return String.format("%s-%s", this.world.getName(), this.region);
	}

	private int getRandomBetween(int low, int high)
	{
		return low + (int) (Math.random() * ((high - low) + 1));
	}

	public boolean isAvailable()
	{
		return this.isSetup;
	}

	private boolean isSetup;
	private IWorld world;
	private int teleportY;
	private IOutput output;
	private WorldGuardInterface worldGuard;
	private ProtectedRegion teleportRegion;
	private String region;
	private List<String> players = new ArrayList<String>();
}
