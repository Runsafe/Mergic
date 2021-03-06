package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class NatureStorm extends Storm
{
	public NatureStorm(KillManager manager)
	{
		super(Item.BuildingBlock.Wood.Oak, manager);
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
