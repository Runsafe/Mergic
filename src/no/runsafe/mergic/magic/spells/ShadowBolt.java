package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class ShadowBolt extends Bolt
{
	public ShadowBolt(IScheduler scheduler, KillManager manager)
	{
		super(Item.BuildingBlock.CoalBlock, scheduler, manager);
	}

	@Override
	public String getName()
	{
		return "Shadow Bolt";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.SHADOW;
	}
}
