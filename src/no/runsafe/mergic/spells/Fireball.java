package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class Fireball implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
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
