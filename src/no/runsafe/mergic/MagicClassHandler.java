package no.runsafe.mergic;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.magic.MagicSchool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagicClassHandler
{
	public MagicClassHandler()
	{
		availableSchools = new ArrayList<MagicSchool>();
		availableSchools.addAll(Arrays.asList(MagicSchool.values()));
		availableSchools.remove(MagicSchool.GENERIC);
	}

	public boolean playerCanUse(IPlayer player, MagicSchool school)
	{
		return school == getPlayerSchool(player) || school == MagicSchool.GENERIC;
	}

	private MagicSchool getPlayerSchool(IPlayer player)
	{
		if (!schools.containsKey(player))
			applyRandomClass(player);

		return schools.get(player);
	}

	public void applyRandomClass(IPlayer player)
	{
		schools.put(player, availableSchools.get(random.nextInt(availableSchools.size())));
	}

	private final Random random = new Random();
	private final ConcurrentHashMap<IPlayer, MagicSchool> schools = new ConcurrentHashMap<IPlayer, MagicSchool>(0);
	private final List<MagicSchool> availableSchools;
}
