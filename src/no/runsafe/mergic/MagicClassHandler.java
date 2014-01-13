package no.runsafe.mergic;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.magic.MagicSchool;

public class MagicClassHandler
{
	public boolean playerCanUse(IPlayer player, MagicSchool school)
	{
		return school == getPlayerSchool(player) || school == MagicSchool.GENERIC;
	}

	private MagicSchool getPlayerSchool(IPlayer player)
	{
		return MagicSchool.FIRE;
	}
}
