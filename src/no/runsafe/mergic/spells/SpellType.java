package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.Item;

public enum SpellType
{
	PROJECTILE("Right click to attack with this wand.", Item.Materials.Stick),
	GUARDIAN("Right click to summon this guardian.", Item.Special.Crafted.EnchantedBook);

	private SpellType(String text, Item castItem)
	{
		this.text = text;
		this.castItem = castItem;
	}

	public String getText()
	{
		return this.text;
	}

	public Item getCastItem()
	{
		return this.castItem;
	}

	private String text;
	private Item castItem;
}
