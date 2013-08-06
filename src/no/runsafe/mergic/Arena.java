package no.runsafe.mergic;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
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
		String region = configuration.getConfigValueAsString("arena.region");
		if (region == null)
		{
			this.output.logError("Arena region missing from configuration.");
			return;
		}

		this.teleportY = configuration.getConfigValueAsInt("arena.teleportY");
		if (this.teleportY == -1)
		{
			this.output.logError("Arena teleportY missing from configuration.");
			return;
		}

		// Grab the region, throw an error and return if we fail like idiots.
		this.region = this.worldGuard.getRegion(world, region);
		if (this.region == null)
		{
			this.output.logError("Arena region from configuration does not exist.");
			return;
		}

		// Flag the arena as set-up, woo!
		this.isSetup = true;
	}

	public void teleportPlayersIntoArena(List<RunsafePlayer> playerList)
	{
		for (RunsafePlayer player : playerList)
			this.teleportPlayerIntoArena(player);
	}

	public void teleportPlayerIntoArena(RunsafePlayer player)
	{
		// Create random X and Z co-ordinates within the region.
		int randomX = getRandomBetween(region.getMinimumPoint().getBlockX(), region.getMaximumPoint().getBlockX());
		int randomZ = getRandomBetween(region.getMinimumPoint().getBlockZ(), region.getMaximumPoint().getBlockZ());


		// Get a safe spot at the co-ordinates we generated.
		RunsafeLocation loc = getSafeSpot(new RunsafeLocation(this.world, randomX, this.teleportY, randomZ));

		// Adjust the location to be at the centre of the block.
		loc.incrementX(0.5D);
		loc.incrementZ(0.5D);

		// Teleport the player to the location.
		player.teleport(loc);
	}

	private RunsafeLocation getSafeSpot(RunsafeLocation location)
	{
		int blockID = location.getBlock().getTypeId(); // The ID of the block.
		while (blockID != 0) // Loop until we find air.
		{
			location.incrementY(1); // Increment the Y co-ordinate.
			blockID = location.getBlock().getTypeId(); // Reset the blockID to the block we're at now.
		}
		return location;
	}

	public boolean playerIsInGame(RunsafePlayer player)
	{
		return this.players.contains(player.getName());
	}

	public void removePlayer(RunsafePlayer player)
	{
		this.players.remove(player.getName());
	}

	public String getArenaRegionString()
	{
		return String.format("%s-%s", this.world.getName(), this.region.getId());
	}

	private int getRandomBetween(int low, int high)
	{
		return low + (int)(Math.random() * ((high - low) + 1));
	}

	public boolean isAvailable()
	{
		return this.isSetup;
	}

	private boolean isSetup;
	private RunsafeWorld world;
	private int teleportY;
	private IOutput output;
	private WorldGuardInterface worldGuard;
	private ProtectedRegion region;
	private List<String> players = new ArrayList<String>();
}
