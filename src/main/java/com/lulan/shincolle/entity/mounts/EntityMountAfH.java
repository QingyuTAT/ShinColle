package com.lulan.shincolle.entity.mounts;

import java.util.List;

import com.lulan.shincolle.ai.EntityAIShipCarrierAttack;
import com.lulan.shincolle.ai.EntityAIShipRangeAttack;
import com.lulan.shincolle.ai.path.ShipMoveHelper;
import com.lulan.shincolle.ai.path.ShipPathNavigate;
import com.lulan.shincolle.entity.BasicEntityMountLarge;
import com.lulan.shincolle.entity.BasicEntityShip;
import com.lulan.shincolle.reference.ID;
import com.lulan.shincolle.reference.Values;
import com.lulan.shincolle.utility.CalcHelper;
import com.lulan.shincolle.utility.ParticleHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityMountAfH extends BasicEntityMountLarge
{
	
    public EntityMountAfH(World world)
    {
		super(world);
		this.setSize(1.9F, 1.3F);
		this.seatPos = new float[] {0.59F, -0.25F, 0F};
		this.seatPos2 = new float[] {-0.85F, 1F, -1.12F};
        this.shipNavigator = new ShipPathNavigate(this);
		this.shipMoveHelper = new ShipMoveHelper(this, 45F);
	}
    
    @Override
    public void initAttrs(BasicEntityShip host)
    {
        this.host = host;
		
        //設定位置
        this.posX = host.posX;
        this.posY = host.posY;
        this.posZ = host.posZ;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.setPosition(this.posX, this.posY, this.posZ);
 
        //設定基本屬性
        this.setupAttrs();
        
		if (this.getHealth() < this.getMaxHealth()) this.setHealth(this.getMaxHealth());
				
		//設定AI
		this.setAIList();
	}
    
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//client side
		if (this.world.isRemote)
		{
			if (this.ticksExisted % 8 == 0)
			{
				//嘴巴冒紅煙特效
				float[] partPos1 = CalcHelper.rotateXZByAxis(0F, -1.0F, this.renderYawOffset * Values.N.DIV_PI_180, 1F);
				float[] partPos2 = CalcHelper.rotateXZByAxis(0F, -1.8F, this.renderYawOffset * Values.N.DIV_PI_180, 1F);
				ParticleHelper.spawnAttackParticleAt(this.posX + partPos1[1], this.posY + 0.9F, this.posZ + partPos1[0], 
							0D, 0.1D, 0D, (byte)18);
				ParticleHelper.spawnAttackParticleAt(this.posX + partPos2[1], this.posY + 0.9F, this.posZ + partPos2[0], 
							0D, 0.1D, 0D, (byte)18);
			}
		}
	}

	@Override
	public void setAIList()
	{
		super.setAIList();
		
		//use range attack
		this.tasks.addTask(10, new EntityAIShipCarrierAttack(this));
		this.tasks.addTask(11, new EntityAIShipRangeAttack(this));
	}

	@Override
	public int getTextureID()
	{
		return ID.ShipMisc.AirfieldMount;
	}

	@Override
	protected void setRotationByRider()
	{
	  	//sync rotation
		List<Entity> riders = this.getPassengers();
		
		for (Entity rider : riders)
		{
			if (rider instanceof BasicEntityShip)
			{
				rider.rotationYaw = ((BasicEntityShip) rider).renderYawOffset;
				
				this.prevRotationYawHead = ((EntityLivingBase) rider).prevRotationYawHead;
				this.rotationYawHead = ((EntityLivingBase) rider).rotationYawHead;
				this.prevRenderYawOffset = ((EntityLivingBase) rider).prevRenderYawOffset;
				this.renderYawOffset = ((EntityLivingBase) rider).renderYawOffset;
				this.prevRotationYaw = rider.prevRotationYaw;
				this.rotationYaw = rider.rotationYaw;
			}
		}//end for sync rotation
	}

	
}