package no.runsafe.mergic.magic;

import no.runsafe.framework.minecraft.Item;

public enum SpellType
{
	STORM("Right click to conjure a storm.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK),
	PROJECTILE("Left click to attack with this wand.", Item.Materials.Stick, InteractType.LEFT_CLICK),
	GENERIC("Right click to activate this spell.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK),
	WARD("Right click to activate this protection/ward.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK),
	GUARDIAN("Right click to summon this guardian.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK),
	HEAL("Right click to heal using this spell.", Item.Special.Crafted.EnchantedBook, InteractType.RIGHT_CLICK);

	SpellType(String text, Item castItem, InteractType type)
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

	private final String text;
	private final Item castItem;
	private final InteractType type;
}
