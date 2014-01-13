package no.runsafe.mergic;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.magic.MagicSchool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MagicClassHandler
{
	public MagicClassHandler()
	{
		availableSchools = Arrays.asList(MagicSchool.values());
		availableSchools.remove(MagicSchool.GENERIC);
	}

	public boolean playerCanUse(IPlayer player, MagicSchool school)
	{
		return school == getPlayerSchool(player) || school == MagicSchool.GENERIC;
	}

	private MagicSchool getPlayerSchool(IPlayer player)
	{
		String playerName = player.getName();
		if (!schools.containsKey(playerName))
			applyRandomClass(player);

		return schools.get(playerName);
	}

	public void applyRandomClass(IPlayer player)
	{
		schools.put(player.getName(), availableSchools.get(random.nextInt(availableSchools.size())));
	}

	private final Random random = new Random();
	private final HashMap<String, MagicSchool> schools = new HashMap<String, MagicSchool>(0);
	private final List<MagicSchool> availableSchools;
}
