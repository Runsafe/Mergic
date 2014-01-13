package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class NatureStorm extends Storm
{
	public NatureStorm(IServer server, KillManager killManager)
	{
		super(server, Item.BuildingBlock.Wood.Oak, killManager);
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
