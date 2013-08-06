package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.player.RunsafePlayer;

public interface Spell
{
	public int getCooldown();
	public String getName();
	public MagicSchool getSchool();
	public void onCast(RunsafePlayer player);
}
