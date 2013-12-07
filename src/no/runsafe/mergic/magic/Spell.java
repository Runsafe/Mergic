package no.runsafe.mergic.magic;

import no.runsafe.framework.api.player.IPlayer;

public interface Spell
{
	public int getCooldown();
	public String getName();
	public MagicSchool getSchool();
	public SpellType getType();
	public String getDescription();
	public void onCast(IPlayer player);
}
