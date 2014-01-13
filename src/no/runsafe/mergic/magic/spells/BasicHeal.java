package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.WorldEffect;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class BasicHeal implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Basic Heal";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.GENERIC;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.HEAL;
	}

	@Override
	public String getDescription()
	{
		return "Heals you for 3 hearts of damage.";
	}

	@Override
	public void onCast(IPlayer player)
	{
		player.heal(6); // Heal the player for 3 hearts.
		player.getEyeLocation().playEffect(WorldEffect.HEART, 1, 4, 50); // Play a heart effect for healing!
	}
}
