package no.runsafe.mergic;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.magic.MagicSchool;

public class MagicClassHandler
{
	public MagicSchool getPlayerSchool(IPlayer player)
	{
		return MagicSchool.FIRE;
	}
}
