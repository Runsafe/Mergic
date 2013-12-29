package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.magic.MagicSchool;

public class ShadowStorm extends Storm
{
	public ShadowStorm(IServer server)
	{
		super(server, Item.BuildingBlock.StainedClay.Blue);
	}

	@Override
	public String getName()
	{
		return "Shadow Storm";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.SHADOW;
	}
}
