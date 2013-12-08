package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class BasicFireball implements Spell
{
	@Override
	public int getCooldown()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "Basic Fireball";
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
	public String getDescription()
	{
		return "Fires a small fireball.";
	}

	@Override
	public void onCast(IPlayer player)
	{
		player.Fire(ProjectileEntity.SmallFireball);
		player.getLocation().playSound(Sound.Creature.Ghast.Fireball, 2, 1); // Play a fireball sound.
	}
}
