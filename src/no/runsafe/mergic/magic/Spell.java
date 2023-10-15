package no.runsafe.mergic.magic;

import no.runsafe.framework.api.player.IPlayer;

public interface Spell
{
	int getCooldown();
	String getName();
	MagicSchool getSchool();
	SpellType getType();
	String getDescription();
	void onCast(IPlayer player);
}
