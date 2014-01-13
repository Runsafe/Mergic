package no.runsafe.mergic.achievements;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class ApprenticeWizard extends RunsafeCustomEvent
{
	public ApprenticeWizard(IPlayer player)
	{
		super(player, "achievement.apprenticeWizard");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
