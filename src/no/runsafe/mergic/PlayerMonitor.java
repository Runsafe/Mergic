package no.runsafe.mergic;

import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.api.event.player.IPlayerJoinEvent;
import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerJoinEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.Map;

public class PlayerMonitor implements IPlayerCustomEvent, IPlayerJoinEvent, IPlayerQuitEvent, IPlayerDeathEvent
{
	public PlayerMonitor(Graveyard graveyard, Arena arena, Game game, Lobby lobby)
	{
		this.graveyard = graveyard;
		this.arena = arena;
		this.game = game;
		this.lobby = lobby;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		// Did we trigger a region leave event?
		if (event.getEvent().equals("region.leave"))
		{
			// See if the region the player left is the arena region.
			Map<String, String> data = (Map<String, String>) event.getData();
			if (arena.getArenaRegionString().equals(String.format("%s-%s", data.get("world"), data.get("region"))))
			{
				RunsafePlayer player = event.getPlayer();

				// Check if the player is actually in the game.
				if (arena.playerIsInGame(player))
					this.game.removePlayerFromGame(player); // Throw them from the game.
			}
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		RunsafePlayer player = event.getPlayer();

		// Check if the player is inside the arena when they shouldn't be.
		if (!this.arena.playerIsInGame(player) && this.arena.playerIsInPhysicalArena(player))
			this.lobby.teleportPlayerToLobby(player); // Teleport them to the lobby!
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		RunsafePlayer player = event.getPlayer();

		// The player is logging out whilst still in the arena, drop their in-game status.
		if (this.arena.playerIsInGame(player))
			this.arena.removePlayer(player);
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		RunsafePlayer player = event.getEntity();
		if (this.arena.playerIsInGame(player))
		{
			player.setHealth(20D); // Keep the player alive.
			event.setDrops(new ArrayList<RunsafeMeta>()); // Drop no items!
			this.graveyard.teleportPlayerToGraveyard(player); // Teleport player to graveyard.
			player.sendColouredMessage("You have died! You will respawn shortly.");
		}
	}

	private Graveyard graveyard;
	private Arena arena;
	private Game game;
	private Lobby lobby;
}
