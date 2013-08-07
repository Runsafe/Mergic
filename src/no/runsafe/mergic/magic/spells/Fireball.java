package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class Fireball implements Spell
{
	@Override
	public int getCooldown()
	{
		return 2;
	}

	@Override
	public String getName()
	{
		return "Fireball";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FIRE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		player.Fire(ProjectileEntity.Fireball);
	}
}
