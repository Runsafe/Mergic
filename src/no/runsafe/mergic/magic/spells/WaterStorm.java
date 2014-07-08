package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class WaterStorm extends Storm
{
	public WaterStorm(IServer server, KillManager manager)
	{
		super(server, Item.Unavailable.StationaryWater, manager);
	}

	@Override
	public String getName()
	{
		return "Water Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.WATER;
	}
}
