package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class WaterStorm extends Storm
{
	public WaterStorm(KillManager manager)
	{
		super(Item.BuildingBlock.LapisLazuli, manager);
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
