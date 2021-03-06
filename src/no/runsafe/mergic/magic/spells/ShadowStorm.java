package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class ShadowStorm extends Storm
{
	public ShadowStorm(KillManager manager)
	{
		super(Item.BuildingBlock.CoalBlock, manager);
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
