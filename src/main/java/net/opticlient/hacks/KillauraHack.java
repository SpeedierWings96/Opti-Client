/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.HandleInputListener;
import net.opticlient.events.RenderListener;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;
import net.opticlient.settings.AttackSpeedSliderSetting;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.settings.EnumSetting;
import net.opticlient.settings.PauseAttackOnContainersSetting;
import net.opticlient.settings.SliderSetting;
import net.opticlient.settings.SliderSetting.ValueDisplay;
import net.opticlient.settings.SwingHandSetting;
import net.opticlient.settings.SwingHandSetting.SwingHand;
import net.opticlient.settings.filterlists.EntityFilterList;
import net.opticlient.util.BlockUtils;
import net.opticlient.util.EntityUtils;
import net.opticlient.util.RenderUtils;
import net.opticlient.util.RotationUtils;

@SearchTags({"kill aura", "ForceField", "force field", "CrystalAura",
	"crystal aura", "AutoCrystal", "auto crystal"})
public final class KillauraHack extends Hack
	implements UpdateListener, HandleInputListener, RenderListener
{
	private final SliderSetting range = new SliderSetting("Range",
		"Determines how far Killaura will reach to attack entities.\n"
			+ "Anything that is further away than the specified value will not be attacked.",
		5, 1, 10, 0.05, ValueDisplay.DECIMAL);
	
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final SliderSetting speedRandMS =
		new SliderSetting("Speed randomization",
			"Helps you bypass anti-cheat plugins by varying the delay between"
				+ " attacks.\n\n" + "\u00b1100ms is recommended for Vulcan.\n\n"
				+ "0 (off) is fine for NoCheat+, AAC, Grim, Verus, Spartan, and"
				+ " vanilla servers.",
			100, 0, 1000, 50, ValueDisplay.INTEGER.withPrefix("\u00b1")
				.withSuffix("ms").withLabel(0, "off"));
	
	private final EnumSetting<Priority> priority = new EnumSetting<>("Priority",
		"Determines which entity will be attacked first.\n"
			+ "\u00a7lDistance\u00a7r - Attacks the closest entity.\n"
			+ "\u00a7lAngle\u00a7r - Attacks the entity that requires the least head movement.\n"
			+ "\u00a7lHealth\u00a7r - Attacks the weakest entity.",
		Priority.values(), Priority.ANGLE);
	
	private final SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	
	private final SwingHandSetting swingHand = new SwingHandSetting(
		SwingHandSetting.genericCombatDescription(this), SwingHand.CLIENT);
	
	private final CheckboxSetting damageIndicator = new CheckboxSetting(
		"Damage indicator",
		"Renders a colored box within the target, inversely proportional to its remaining health.",
		true);
	
	private final PauseAttackOnContainersSetting pauseOnContainers =
		new PauseAttackOnContainersSetting(true);
	
	private final CheckboxSetting checkLOS =
		new CheckboxSetting("Check line of sight",
			"Ensures that you don't reach through blocks when attacking.\n\n"
				+ "Slower but can help with anti-cheat plugins.",
			false);
	
	private final EntityFilterList entityFilters =
		EntityFilterList.genericCombat();
	
	private Entity target;
	private Entity renderTarget;
	
	public KillauraHack()
	{
		super("Killaura");
		setCategory(Category.COMBAT);
		
		addSetting(range);
		addSetting(speed);
		addSetting(speedRandMS);
		addSetting(priority);
		addSetting(fov);
		addSetting(swingHand);
		addSetting(damageIndicator);
		addSetting(pauseOnContainers);
		addSetting(checkLOS);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		// disable other killauras
		OPTI.getHax().aimAssistHack.setEnabled(false);
		OPTI.getHax().clickAuraHack.setEnabled(false);
		OPTI.getHax().crystalAuraHack.setEnabled(false);
		OPTI.getHax().fightBotHack.setEnabled(false);
		OPTI.getHax().killauraLegitHack.setEnabled(false);
		OPTI.getHax().multiAuraHack.setEnabled(false);
		OPTI.getHax().protectHack.setEnabled(false);
		OPTI.getHax().triggerBotHack.setEnabled(false);
		OPTI.getHax().tpAuraHack.setEnabled(false);
		
		speed.resetTimer(speedRandMS.getValue());
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(HandleInputListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(HandleInputListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		
		target = null;
		renderTarget = null;
	}
	
	@Override
	public void onUpdate()
	{
		speed.updateTimer();
		if(!speed.isTimeToAttack())
			return;
		
		if(pauseOnContainers.shouldPause())
			return;
		
		Stream<Entity> stream = EntityUtils.getAttackableEntities();
		double rangeSq = range.getValueSq();
		stream = stream.filter(e -> MC.player.squaredDistanceTo(e) <= rangeSq);
		
		if(fov.getValue() < 360.0)
			stream = stream.filter(e -> RotationUtils.getAngleToLookVec(
				e.getBoundingBox().getCenter()) <= fov.getValue() / 2.0);
		
		stream = entityFilters.applyTo(stream);
		
		target = stream.min(priority.getSelected().comparator).orElse(null);
		renderTarget = target;
		if(target == null)
			return;
		
		OPTI.getHax().autoSwordHack.setSlot(target);
		
		Vec3d hitVec = target.getBoundingBox().getCenter();
		if(checkLOS.isChecked() && !BlockUtils.hasLineOfSight(hitVec))
		{
			target = null;
			return;
		}
		
		OPTI.getRotationFaker().faceVectorPacket(hitVec);
	}
	
	@Override
	public void onHandleInput()
	{
		if(target == null)
			return;
		
		MC.interactionManager.attackEntity(MC.player, target);
		swingHand.swing(Hand.MAIN_HAND);
		
		target = null;
		speed.resetTimer(speedRandMS.getValue());
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		if(renderTarget == null || !damageIndicator.isChecked())
			return;
		
		float p = 1;
		if(renderTarget instanceof LivingEntity le && le.getMaxHealth() > 1e-5)
			p = 1 - le.getHealth() / le.getMaxHealth();
		float red = p * 2F;
		float green = 2 - red;
		float[] rgb = {red, green, 0};
		int quadColor = RenderUtils.toIntColor(rgb, 0.25F);
		int lineColor = RenderUtils.toIntColor(rgb, 0.5F);
		
		Box box = EntityUtils.getLerpedBox(renderTarget, partialTicks);
		if(p < 1)
			box = box.contract((1 - p) * 0.5 * box.getLengthX(),
				(1 - p) * 0.5 * box.getLengthY(),
				(1 - p) * 0.5 * box.getLengthZ());
		
		RenderUtils.drawSolidBox(matrixStack, box, quadColor, false);
		RenderUtils.drawOutlinedBox(matrixStack, box, lineColor, false);
	}
	
	private enum Priority
	{
		DISTANCE("Distance", e -> MC.player.squaredDistanceTo(e)),
		
		ANGLE("Angle",
			e -> RotationUtils
				.getAngleToLookVec(e.getBoundingBox().getCenter())),
		
		HEALTH("Health", e -> e instanceof LivingEntity
			? ((LivingEntity)e).getHealth() : Integer.MAX_VALUE);
		
		private final String name;
		private final Comparator<Entity> comparator;
		
		private Priority(String name, ToDoubleFunction<Entity> keyExtractor)
		{
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
