/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.opticlient.Category;
import net.opticlient.ai.PathFinder;
import net.opticlient.ai.PathPos;
import net.opticlient.ai.PathProcessor;
import net.opticlient.commands.PathCmd;
import net.opticlient.events.RenderListener;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.DontSaveState;
import net.opticlient.hack.Hack;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.settings.SliderSetting;
import net.opticlient.settings.SliderSetting.ValueDisplay;
import net.opticlient.settings.filterlists.EntityFilterList;
import net.opticlient.settings.filterlists.FollowFilterList;
import net.opticlient.util.ChatUtils;
import net.opticlient.util.FakePlayerEntity;

@DontSaveState
public final class FollowHack extends Hack
	implements UpdateListener, RenderListener
{
	private Entity entity;
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	
	private final SliderSetting distance =
		new SliderSetting("Distance", "How closely to follow the target.", 1, 1,
			12, 0.5, ValueDisplay.DECIMAL);
	
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	private final EntityFilterList entityFilters = FollowFilterList.create();
	
	public FollowHack()
	{
		super("Follow");
		
		setCategory(Category.MOVEMENT);
		addSetting(distance);
		addSetting(useAi);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	public String getRenderName()
	{
		if(entity != null)
			return "Following " + entity.getName().getString();
		return "Follow";
	}
	
	@Override
	protected void onEnable()
	{
		OPTI.getHax().fightBotHack.setEnabled(false);
		OPTI.getHax().protectHack.setEnabled(false);
		OPTI.getHax().tunnellerHack.setEnabled(false);
		
		if(entity == null)
		{
			Stream<Entity> stream =
				StreamSupport.stream(MC.world.getEntities().spliterator(), true)
					.filter(e -> !e.isRemoved())
					.filter(e -> e instanceof LivingEntity
						&& ((LivingEntity)e).getHealth() > 0
						|| e instanceof AbstractMinecartEntity)
					.filter(e -> e != MC.player)
					.filter(e -> !(e instanceof FakePlayerEntity));
			
			stream = entityFilters.applyTo(stream);
			
			entity = stream
				.min(Comparator
					.comparingDouble(e -> MC.player.squaredDistanceTo(e)))
				.orElse(null);
			
			if(entity == null)
			{
				ChatUtils.error("Could not find a valid entity.");
				setEnabled(false);
				return;
			}
		}
		
		pathFinder = new EntityPathFinder();
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
		ChatUtils.message("Now following " + entity.getName().getString());
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		
		pathFinder = null;
		processor = null;
		ticksProcessing = 0;
		PathProcessor.releaseControls();
		
		if(entity != null)
			ChatUtils
				.message("No longer following " + entity.getName().getString());
		
		entity = null;
	}
	
	@Override
	public void onUpdate()
	{
		// check if player died
		if(MC.player.getHealth() <= 0)
		{
			if(entity == null)
				ChatUtils.message("No longer following entity");
			setEnabled(false);
			return;
		}
		
		// check if entity died or disappeared
		if(entity.isRemoved() || entity instanceof LivingEntity
			&& ((LivingEntity)entity).getHealth() <= 0)
		{
			entity = StreamSupport
				.stream(MC.world.getEntities().spliterator(), true)
				.filter(LivingEntity.class::isInstance)
				.filter(
					e -> !e.isRemoved() && ((LivingEntity)e).getHealth() > 0)
				.filter(e -> e != MC.player)
				.filter(e -> !(e instanceof FakePlayerEntity))
				.filter(e -> entity.getName().getString()
					.equalsIgnoreCase(e.getName().getString()))
				.min(Comparator
					.comparingDouble(e -> MC.player.squaredDistanceTo(e)))
				.orElse(null);
			
			if(entity == null)
			{
				ChatUtils.message("No longer following entity");
				setEnabled(false);
				return;
			}
			
			pathFinder = new EntityPathFinder();
			processor = null;
			ticksProcessing = 0;
		}
		
		if(useAi.isChecked())
		{
			// reset pathfinder
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder();
				processor = null;
				ticksProcessing = 0;
			}
			
			// find path
			if(!pathFinder.isDone() && !pathFinder.isFailed())
			{
				PathProcessor.lockControls();
				OPTI.getRotationFaker()
					.faceVectorClient(entity.getBoundingBox().getCenter());
				pathFinder.think();
				pathFinder.formatPath();
				processor = pathFinder.getProcessor();
			}
			
			// process path
			if(!processor.isDone())
			{
				processor.process();
				ticksProcessing++;
			}
		}else
		{
			// jump if necessary
			if(MC.player.horizontalCollision && MC.player.isOnGround())
				MC.player.jump();
			
			// swim up if necessary
			if(MC.player.isTouchingWater() && MC.player.getY() < entity.getY())
				MC.player.setVelocity(MC.player.getVelocity().add(0, 0.04, 0));
			
			// control height if flying
			if(!MC.player.isOnGround()
				&& (MC.player.getAbilities().flying
					|| OPTI.getHax().flightHack.isEnabled())
				&& MC.player.squaredDistanceTo(entity.getX(), MC.player.getY(),
					entity.getZ()) <= MC.player.squaredDistanceTo(
						MC.player.getX(), entity.getY(), MC.player.getZ()))
			{
				if(MC.player.getY() > entity.getY() + 1D)
					MC.options.sneakKey.setPressed(true);
				else if(MC.player.getY() < entity.getY() - 1D)
					MC.options.jumpKey.setPressed(true);
			}else
			{
				MC.options.sneakKey.setPressed(false);
				MC.options.jumpKey.setPressed(false);
			}
			
			// follow entity
			OPTI.getRotationFaker()
				.faceVectorClient(entity.getBoundingBox().getCenter());
			double distanceSq = Math.pow(distance.getValue(), 2);
			MC.options.forwardKey
				.setPressed(MC.player.squaredDistanceTo(entity.getX(),
					MC.player.getY(), entity.getZ()) > distanceSq);
		}
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		PathCmd pathCmd = OPTI.getCmds().pathCmd;
		pathFinder.renderPath(matrixStack, pathCmd.isDebugMode(),
			pathCmd.isDepthTest());
	}
	
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
	
	private class EntityPathFinder extends PathFinder
	{
		public EntityPathFinder()
		{
			super(BlockPos.ofFloored(entity.getPos()));
			setThinkTime(1);
		}
		
		@Override
		protected boolean checkDone()
		{
			Vec3d center = Vec3d.ofCenter(current);
			double distanceSq = Math.pow(distance.getValue(), 2);
			return done = entity.squaredDistanceTo(center) <= distanceSq;
		}
		
		@Override
		public ArrayList<PathPos> formatPath()
		{
			if(!done)
				failed = true;
			
			return super.formatPath();
		}
	}
}
