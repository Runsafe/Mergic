package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class IceStorm extends Storm
{
	public IceStorm(IServer server, KillManager manager)
	{
		super(server, Item.BuildingBlock.Ice, manager);
	}

	@Override
	public String getName()
	{
		return "Ice Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FROST;
	}
}
