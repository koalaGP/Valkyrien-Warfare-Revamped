package ValkyrienWarfareCombat.Entity;

import java.util.Random;

import ValkyrienWarfareBase.API.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityHarpoon extends EntityArrow{

	//TODO make below two vars configurable
	public static final int MAX_DMG = 20;//10 hearts
	public static final int MIN_DMG = 5;//2.5 hearts

	private EntityHarpoonGun origin;

	public EntityHarpoon(World worldIn) {
		super(worldIn);
	}

	public EntityHarpoon(World worldObj, Vector velocityVector, EntityHarpoonGun origin){
		super(worldObj);
		this.origin=origin;
		this.setVelocity(velocityVector.X, velocityVector.Y, velocityVector.Z);
	}

	@Override
	public void onHit(RayTraceResult raytrace){

		Entity entity = raytrace.entityHit;

		if (entity == null){

			

		}else{//it hit an entity

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

					this.setDead();
				}
			}
		}

	}

	@Override
	protected ItemStack getArrowStack(){
		return null;
	}

}
