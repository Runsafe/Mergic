package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class ShadowStorm extends Storm
{
	public ShadowStorm(IServer server, KillManager killManager)
	{
		super(server, Item.BuildingBlock.StainedClay.Blue, killManager);
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
