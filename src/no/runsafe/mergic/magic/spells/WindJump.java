package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class WindJump implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Wind Jump";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.WIND;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public String getDescription()
	{
		return "Rush forward with the power of the wind.";
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		RunsafeLocation playerLoc = player.getLocation();
		player.setVelocity(playerLoc.getDirection().multiply(3));
		playerLoc.Play(Sound.Player.Breath, 10, -1);
	}
}
