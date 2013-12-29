package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.magic.MagicSchool;

public class NatureStorm extends Storm
{
	public NatureStorm(IServer server)
	{
		super(server, Item.BuildingBlock.Podzol);
	}

	@Override
	public String getName()
	{
		return "Nature Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.NATURE;
	}
}
