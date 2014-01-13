package no.runsafe.mergic.achievements;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class MasterOfMagic extends RunsafeCustomEvent
{
	public MasterOfMagic(IPlayer player)
	{
		super(player, "achievement.masterOfMagic");
	}
	@Override
	public Object getData()
	{
		return null;
	}
}
