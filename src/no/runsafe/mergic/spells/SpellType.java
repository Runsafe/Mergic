package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.Item;

public enum SpellType
{
	PROJECTILE("Left click to attack with this wand.", Item.Materials.Stick, InteractType.LEFT_CLICK),
	GUARDIAN("Right click to summon this guardian.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK);

	private SpellType(String text, Item castItem, InteractType type)
	{
		this.text = text;
		this.castItem = castItem;
		this.type = type;
	}

	public String getText()
	{
		return this.text;
	}

	public Item getCastItem()
	{
		return this.castItem;
	}

	public InteractType getInteractType()
	{
		return this.type;
	}

	private String text;
	private Item castItem;
	private InteractType type;
}
