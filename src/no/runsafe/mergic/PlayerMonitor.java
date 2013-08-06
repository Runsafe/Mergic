package no.runsafe.mergic;

import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.Map;

public class PlayerMonitor implements IPlayerCustomEvent
{
	public PlayerMonitor(Arena arena, Game game)
	{
		this.arena = arena;
		this.game = game;
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

	private Arena arena;
	private Game game;
}
