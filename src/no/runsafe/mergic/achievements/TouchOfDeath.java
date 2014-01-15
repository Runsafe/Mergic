package no.runsafe.mergic.achievements;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class TouchOfDeath extends RunsafeCustomEvent
{
	public TouchOfDeath(IPlayer player)
	{
		super(player, "achievement.touchOfDeath");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
