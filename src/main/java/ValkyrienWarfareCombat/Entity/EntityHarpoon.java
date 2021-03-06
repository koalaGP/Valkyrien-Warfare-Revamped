package ValkyrienWarfareCombat.Entity;

import java.util.Random;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareCombat.ValkyrienWarfareCombatMod;
import ValkyrienWarfareCombat.Network.PacketHarpoon;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityHarpoon extends EntityArrow{

	//TODO make below two vars configurable
	public static final int MAX_DMG = 20;//10 hearts
	public static final int MIN_DMG = 5;//2.5 hearts

	public EntityHarpoonGun origin;

	public EntityHarpoon(World worldIn) {
		super(worldIn);
	}

	public EntityHarpoon(World worldObj, Vector velocityVector, EntityHarpoonGun origin){
		super(worldObj);
		origin.harpoon = this;
		this.origin = origin;
		this.setVelocity(velocityVector.X, velocityVector.Y, velocityVector.Z);
		this.setRotation(origin.rotationYaw, origin.rotationPitch);
		prevRotationYaw = origin.rotationYaw;
		prevRotationPitch = origin.rotationPitch;
		this.setPosition(origin.posX, origin.posY, origin.posZ);
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		if (!worldObj.isRemote){
			if(origin != null && origin.isDead){
				setDead();
			}
		}
	}

	@Override
	public void onHit(RayTraceResult raytrace){
		Entity entity = raytrace.entityHit;

		if(entity == null){ // minecraft dust: don't breathe this
			super.onHit(raytrace);
		}


		if(!this.worldObj.isRemote && (this.origin == null || this.origin.isDead)){
			this.kill();
		}

		if(entity == null){//its a block
			if(this.motionX != 0 && this.motionY != 0 && this.motionZ != 0 && !this.worldObj.isRemote){
				ValkyrienWarfareCombatMod.instance.network.sendToAll(new PacketHarpoon(this.origin.getEntityId(), this.getEntityId()));
			}

			PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(this.worldObj, new BlockPos(this));
			if(wrapper != null && wrapper.wrapping.coordTransform != null){
				wrapper.wrapping.fixEntity(this, new Vector(this));
				wrapper.wrapping.queueEntityForMounting(this);
			}
		}
		else if(!(entity.equals(origin) || entity.equals(this))){//it hit an entity that isn't a harpoon gun
			DamageSource damagesource;

			if (shootingEntity == null){
				damagesource = DamageSource.causeArrowDamage(this, this);
			}else{
				damagesource = DamageSource.causeArrowDamage(this, shootingEntity);
			}

			if (entity.attackEntityFrom(damagesource, new Random().nextInt(MAX_DMG+1-MIN_DMG)+MIN_DMG)){

				if (entity instanceof EntityLivingBase){

					EntityLivingBase liveEnt = (EntityLivingBase) entity;

					if (shootingEntity instanceof EntityLivingBase){
						EnchantmentHelper.applyThornEnchantments(liveEnt, shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments(liveEnt, shootingEntity);
					}

					arrowHit(liveEnt);
				}
			}
		}
	}

	@Override
	protected ItemStack getArrowStack(){
		return null;
	}

}
