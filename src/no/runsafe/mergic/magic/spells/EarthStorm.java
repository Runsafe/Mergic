package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class EarthStorm extends Storm
{
	public EarthStorm(IServer server, KillManager manager)
	{
		super(server, Item.BuildingBlock.Grass, manager);
	}

	@Override
	public String getName()
	{
		return "Earth Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.EARTH;
	}
}
