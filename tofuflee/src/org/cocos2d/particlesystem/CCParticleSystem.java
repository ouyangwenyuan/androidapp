package org.cocos2d.particlesystem;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.protocols.CCTextureProtocol;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.types.ccPointSprite;
import org.cocos2d.types.util.CGPointUtil;
import org.cocos2d.types.util.PoolHolder;
import org.cocos2d.types.util.ccColor4FUtil;
import org.cocos2d.utils.pool.OneClassPool;

// typedef void (*CC_UPDATE_PARTICLE_IMP)(id, SEL, tCCParticle*, CGPoint);

/** Particle System base class
 Attributes of a Particle System:
	- emmision rate of the particles
	- Gravity Mode (Mode A):
		- gravity
		- direction
		- speed +-  variance
		- tangential acceleration +- variance
		- radial acceleration +- variance
	- Radius Mode (Mode B):
		- startRadius +- variance
		- endRadius +- variance
		- rotate +- variance
	- Properties common to all modes:
		- life +- life variance
		- start spin +- variance
		- end spin +- variance
		- start size +- variance
		- end size +- variance
		- start color +- variance
		- end color +- variance
		- life +- variance
		- blending function
	- texture

 cocos2d also supports particles generated by Particle Designer (http://particledesigner.71squared.com/).
 'Radius Mode' in Particle Designer uses a fixed emit rate of 30 hz. Since that can't be guarateed in cocos2d,
 cocos2d uses a another approach, but the results are almost identical. 

 cocos2d supports all the variables used by Particle Designer plus a bit more:
	- spinning particles (supported when using CCQuadParticleSystem)
	- tangential acceleration (Gravity mode)
	- radial acceleration (Gravity mode)
	- radius direction (Radius mode) (Particle Designer supports outwards to inwards direction only)

 It is possible to customize any of the above mentioned properties in runtime. Example:

 @code
	emitter.radialAccel = 15;
	emitter.startSpin = 0;
 @endcode

 */
public abstract class CCParticleSystem extends CCNode implements CCTextureProtocol, UpdateCallback {

	/** The Particle emitter lives forever */
	public static final int	kCCParticleDurationInfinity = -1;

	/** The starting size of the particle is equal to the ending size */
	public static final int	kCCParticleStartSizeEqualToEndSize = -1;

	/** The starting radius of the particle is equal to the ending radius */
	public static final int	kCCParticleStartRadiusEqualToEndRadius = -1;

	// backward compatible
	public static final int	kParticleStartSizeEqualToEndSize = kCCParticleStartSizeEqualToEndSize;
	public static final int	kParticleDurationInfinity = kCCParticleDurationInfinity;

	/** Gravity mode (A mode) */
	public static final int		kCCParticleModeGravity = 0;

	/** Radius mode (B mode) */
	public static final int			kCCParticleModeRadius = 1;


	/** @typedef tCCPositionType
	 possible types of particle positions
	 */
	/** If the emitter is repositioned, the living particles won't be repositioned */
	public static final int	kCCPositionTypeFree = 0;
	
	/** Living particles are attached to the world but will follow the emitter repositioning.
	 Use case: Attach an emitter to an sprite, and you want that the emitter follows the sprite.
	 */
	public static final int	kCCPositionTypeRelative = 1;
	
	/** If the emitter is repositioned, the living particles will be repositioned too */
	public static final int	kCCPositionTypeGrouped = 2;

	/** @struct tCCParticle
    Structure that contains the values of each particle
	 */
	static class CCParticle {
		static class ParticleModeA {
			CGPoint		dir = new CGPoint();
			float		radialAccel;
			float		tangentialAccel;
		}

		// Mode B: radius mode
		static class ParticleModeB {
			float		angle;
			float		degreesPerSecond;
			float		radius;
			float		deltaRadius;
		}

		CGPoint				pos = new CGPoint();
		CGPoint				startPos = new CGPoint();

		ccColor4F	color = new ccColor4F();
		ccColor4F	deltaColor = new ccColor4F();

		float		size;
		float		deltaSize;

		float		rotation;
		float		deltaRotation;

		float		timeToLive;

		ParticleModeA		modeA;
		ParticleModeB		modeB;
	}


	// Mode A:Gravity + Tangential Accel + Radial Accel
	class ModeA {
		// gravity of the particles
		CGPoint gravity = CGPoint.zero();

		// The speed the particles will have.
		float speed;
		// The speed variance
		float speedVar;

		// Tangential acceleration
		float tangentialAccel;
		// Tangential acceleration variance
		float tangentialAccelVar;

		// Radial acceleration
		float radialAccel;
		// Radial acceleration variance
		float radialAccelVar;
	};

	// Mode B: circular movement (gravity, radial accel and tangential accel don't are not used in this mode)
	class ModeB {
		// The starting radius of the particles
		float startRadius;
		// The starting radius variance of the particles
		float startRadiusVar;
		// The ending radius of the particles
		float endRadius;
		// The ending radius variance of the particles
		float endRadiusVar;			
		// Number of degress to rotate a particle around the source pos per second
		float rotatePerSecond;
		// Variance in degrees for rotatePerSecond
		float rotatePerSecondVar;
	};

	protected int id;
	
	// Optimization
	//Method	updateParticleImp;
	// String	updateParticleSel;


	// is the particle system active ?
	protected boolean active;

	// duration in seconds of the system. -1 is infinity
	protected float duration;

	// time elapsed since the start of the system (in seconds)
	protected float elapsed;

	// start ize of the particles
	float startSize;
	public void setStartSize(float s) {
		startSize = s;
	}
	
	// start Size variance
	float startSizeVar;
	public void setStartSizeVar(float ssv) {
		startSizeVar = ssv;
	}
	
	// End size of the particle
	float endSize;
	public void setEndSize(float s) {
		endSize = s;
	}
	
	// end size of variance
	float endSizeVar;
	public void setEndSizeVar(float esv) {
		endSizeVar = esv;
	}

	// start angle of the particles
	float startSpin;
	public void setStartSpin(float s) {
		startSpin = s;
	}
	
	// start angle variance
	float startSpinVar;
	public void setStartSpinVar(float ssv) {
		startSpinVar = ssv;
	}
	
	// End angle of the particle
	float endSpin;
	public void setEndSpin(float es) {
		endSpin = es;
	}
	
	// end angle ariance
	float endSpinVar;
	public void setEndSpinVar(float esv) {
		endSpinVar = esv;
	}
	
	/// Gravity of the particles
	protected CGPoint centerOfGravity = CGPoint.zero();
	public void setCenterOfGravity(CGPoint p) {
		centerOfGravity = CGPoint.make(p.x, p.y);
	}
	
	public CGPoint getCenterOfGravity() {
		return CGPoint.ccp(centerOfGravity.x, centerOfGravity.y);
	}
	
	// position is from "superclass" CocosNode
	// Emitter source position
	protected CGPoint source = CGPoint.zero();

	// Position variance
	protected CGPoint posVar = CGPoint.zero();
	public void setPosVar(CGPoint pv){
		posVar = CGPoint.make(pv.x, pv.y);
	}

	// The angle (direction) of the particles measured in degrees
	protected float angle;
	public void setAngle(float a) {
		angle = a;
	}
	
	// Angle variance measured in degrees;
	protected float angleVar;
	public void setAngleVar(float av) {
		angleVar = av;
	}

//	// The speed the particles will have.
//	protected float speed;
//	// The speed variance
//	protected float speedVar;
//
//	// Tangential acceleration
//	protected float tangentialAccel;
//
//	// Tangential acceleration variance
//	protected float tangentialAccelVar;
//
//	// Radial acceleration
//	protected float radialAccel;
//
//	// Radial acceleration variance
//	protected float radialAccelVar;

	// Size of the particles
	protected float size;

	// Size variance
	protected float sizeVar;

	// How many seconds will the particle live
	protected float life;
	// Life variance
	protected float lifeVar;
	public void setLifeVar(float lv) {
		lifeVar = lv;
	}

	// Start color of the particles
	protected ccColor4F startColor = new ccColor4F();
	public void setStartColor(ccColor4F sc) {
		startColor = new ccColor4F(sc);
	}
	public ccColor4F getStartColor() {
		return new ccColor4F(startColor);
	}

	// Start color variance
	protected ccColor4F startColorVar = new ccColor4F();
	public void setStartColorVar(ccColor4F scv) {
		startColorVar = new ccColor4F(scv);
	}
	public ccColor4F getStartColorVar() {
		return new ccColor4F(startColorVar);
	}

	// End color of the particles
	protected ccColor4F endColor = new ccColor4F();
	public void setEndColor(ccColor4F ec) {
		endColor = new ccColor4F(ec);
	}

	// End color variance
	protected ccColor4F endColorVar = new ccColor4F();
	public void setEndColorVar(ccColor4F ecv) {
		endColorVar = new ccColor4F(ecv);
	}

	// blend function
	ccBlendFunc	blendFunc = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

	// movment type: free or grouped
//	protected int	positionType;

	// Whether or not the node will be auto-removed when there are not particles
	protected boolean autoRemoveOnFinish_;
	
	// Array of particles
	protected CCParticle particles[];

	// Maximum particles
	protected int totalParticles;
	public int getTotalParticles() {
		return totalParticles;
	}

	// Count of active particles
	protected int particleCount;

//	// additive color or blend
//	protected boolean blendAdditive;

	// color modulate
	protected boolean colorModulate;

	// How many particles can be emitted per second
	protected float emissionRate;
	public void setEmissionRate(float er) {
		emissionRate = er;
	}
	
	protected float emitCounter;

	// Texture of the particles
	protected CCTexture2D texture;

	// Different modes
	int emitterMode = -1;
	public void setEmitterMode(int em) {
		if (emitterMode == em)
			return;
		emitterMode = em;
		if (em == kCCParticleModeGravity) {
			modeA = new ModeA();
			if (modeB != null)
				modeB = null;
		} else {
			modeB = new ModeB();
			if (modeA != null)
				modeA = null;
		}	
	}

	ModeA modeA;
	ModeB modeB;

	// Array of (x,y,size,color)
	ccPointSprite vertices[];

	// Array of colors
	//CCColorF	colors[];

	// Array of pointsizes
	//float pointsizes[];

	// vertices buffer id
	protected int verticesID = -1;

	// colors buffer id
	protected int colorsID;

	//  particle idx
	protected int particleIdx;
	
	public void setAutoRemoveOnFinish(boolean ar) {
		autoRemoveOnFinish_ = ar;
	}

    //! whether or not the system is full
    public boolean isFull() {
        return (particleCount == totalParticles);
    }

    public void setTangentialAccel(float t) {
        assert (emitterMode == kCCParticleModeGravity):"Particle Mode should be Gravity";
        modeA.tangentialAccel = t;
    }

    public float getTangentialAccel() {
        assert emitterMode == kCCParticleModeGravity: "Particle Mode should be Gravity";
        return modeA.tangentialAccel;
    }

    public void setTangentialAccelVar(float t) {
        assert emitterMode == kCCParticleModeGravity: "Particle Mode should be Gravity";
        modeA.tangentialAccelVar = t;
    }

    public float getTangentialAccelVar() {
        assert emitterMode == kCCParticleModeGravity: "Particle Mode should be Gravity";
        return modeA.tangentialAccelVar;
    }

    public void setRadialAccel(float t) {
        assert emitterMode == kCCParticleModeGravity: "Particle Mode should be Gravity";
        modeA.radialAccel = t;
    }

    public float getRadialAccel() {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        return modeA.radialAccel;
    }

    public void setRadialAccelVar(float t) {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        modeA.radialAccelVar = t;
    }

    public float getRadialAccelVar() {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        return modeA.radialAccelVar;
    }

    public void setGravity(CGPoint g) {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        modeA.gravity.set(g);
    }
    
    /**
	 * Gravity value
	 */
	public CGPoint getGravity() {
		return modeA.gravity;
	}

    public CGPoint gravity() {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        return modeA.gravity;
    }

    public void setSpeed(float speed) {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        modeA.speed = speed;
    }

    public float getSpeed() {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        return modeA.speed;
    }

    public void setSpeedVar(float speedVar) {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        modeA.speedVar = speedVar;
    }

    public float getSpeedVar() {
        assert emitterMode == kCCParticleModeGravity:"Particle Mode should be Gravity";
        return modeA.speedVar;
    }

    public void setStartRadius(float startRadius) {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        modeB.startRadius = startRadius;
    }

    public float startRadius() {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        return modeB.startRadius;
    }

    public void setStartRadiusVar(float startRadiusVar) {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        modeB.startRadiusVar = startRadiusVar;
    }

    public float startRadiusVar() {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        return modeB.startRadiusVar;
    }

    public void setEndRadius(float endRadius) {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        modeB.endRadius = endRadius;
    }

    public float endRadius() {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        return modeB.endRadius;
    }

    public void setEndRadiusVar(float endRadiusVar) {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        modeB.endRadiusVar = endRadiusVar;
    }

    public float endRadiusVar() {
        assert emitterMode == kCCParticleModeRadius:"Particle Mode should be Radius";
        return modeB.endRadiusVar;
    }

	/**
	 * Is the emitter active
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Quantity of particles that are being simulated at the moment
	 */
	public int getParticleCount() {
		return particleCount;
	}

	public void setRotatePerSecond(float degrees) {
		// NSAssert( emitterMode_ == kCCParticleModeRadius, @"Particle Mode should be Radius");
		modeB.rotatePerSecond = degrees;
	}
	
	public float rotatePerSecond() {
		// NSAssert( emitterMode_ == kCCParticleModeRadius, @"Particle Mode should be Radius");
		return modeB.rotatePerSecond;
	}

	public void setRotatePerSecondVar(float degrees) {
		// NSAssert( emitterMode_ == kCCParticleModeRadius, @"Particle Mode should be Radius");
		modeB.rotatePerSecondVar = degrees;
	}

	public float rotatePerSecondVar() {
		// NSAssert( emitterMode_ == kCCParticleModeRadius, @"Particle Mode should be Radius");
		return modeB.rotatePerSecondVar;
	}

	
	/**
	 * How many seconds the emitter wil run. -1 means 'forever'
	 */

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * Source location of particles respective to emitter location
	 */
	public CGPoint getSource() {
		return source;
	}

	public void setSource(CGPoint source) {
		this.source = source;
	}

	/**
	 * Position variance of the emitter
	 */
	public CGPoint getPosVar() {
		return posVar;
	}

	/**
	 * life, and life variation of each particle
	 */
	public float getLife() {
		return life;
	}

	public void setLife(float life) {
		this.life = life;
	}

	//    /** life variance of each particle */
	//    protected float lifeVar;
	//    /** angle and angle variation of each particle */
	//    protected float angle;
	//    /** angle variance of each particle */
	//    protected float angleVar;
	//    /** speed of each particle */
	//    protected float speed;
	//    /** speed variance of each particle */
	//    protected float speedVar;
	//    /** tangential acceleration of each particle */
	//    protected float tangentialAccel;
	//    /** tangential acceleration variance of each particle */
	//    protected float tangentialAccelVar;
	//    /** radial acceleration of each particle */
	//    protected float radialAccel;
	//    /** radial acceleration variance of each particle */
	//    protected float radialAccelVar;
	//    /** size in pixels of each particle */
	//    protected float size;
	//    /** size variance in pixels of each particle */
	//    protected float sizeVar;
	//    /** start color of each particle */
	//    protected CCColorF startColor;
	//    /** start color variance of each particle */
	//    protected CCColorF startColorVar;
	//    /** end color and end color variation of each particle */
	//    protected CCColorF endColor;
	//    /** end color variance of each particle */
	//    protected CCColorF endColorVar;
	//    /** emission rate of the particles */
	//    protected float emissionRate;
	//    /** maximum particles of the system */
	//    protected int totalParticles;

//	public static final int kPositionTypeFree = 1;
//	public static final int kPositionTypeGrouped = 2;

	// movement type: free or grouped
	private	int positionType_;

	public int getPositionType() {
		return positionType_;
	}

	public void setPositionType(int type) {
		positionType_ = type;
	}


	/**
	 * texture used to render the particles
	 */

	public CCTexture2D getTexture() {
		return texture;
	}

	public void setTexture(CCTexture2D tex) {
		texture = tex;

		// If the new texture has No premultiplied alpha, AND the blendFunc hasn't been changed, then update it
		if( texture != null && ! texture.hasPremultipliedAlpha() &&		
				( blendFunc.src == ccConfig.CC_BLEND_SRC && blendFunc.dst == ccConfig.CC_BLEND_DST ) ) {

			blendFunc.src = GL10.GL_SRC_ALPHA;
			blendFunc.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
		}
	}

	//! Initializes a system with a fixed number of particles
	protected CCParticleSystem(int numberOfParticles) {
		totalParticles = numberOfParticles;

		particles = new CCParticle[totalParticles];

		for (int i = 0; i < totalParticles; i++) {
			particles[i] = new CCParticle();
		}

		// default, active
		active = true;

        // default movement type;
		positionType_ = kCCPositionTypeFree;

		// by default be in mode A:
		this.setEmitterMode(kCCParticleModeGravity);

		// default: modulate
		// XXX: not used
		//	colorModulate = YES;

		autoRemoveOnFinish_ = false;

		// profiling
		// Optimization: compile udpateParticle method
		/* updateParticleSel = "updateQuad";

		// updateParticleImp = null;
		try {
			updateParticleImp = this.getClass().getMethod(updateParticleSel, new Class[]{CCParticle.class, CGPoint.class});
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		// udpate after action in run!
		this.scheduleUpdate(1);
	}

	private void initParticle(CCParticle particle) {
        // timeToLive
        // no negative life. prevent division by 0
        particle.timeToLive = Math.max(0, life + lifeVar * ccMacros.CCRANDOM_MINUS1_1() );

        // position
        particle.pos.set(centerOfGravity.x + posVar.x * ccMacros.CCRANDOM_MINUS1_1(),
        					centerOfGravity.y + posVar.y * ccMacros.CCRANDOM_MINUS1_1());

        // Color
//        ccColor4F start = new ccColor4F();
        float start_r = Math.min(1, Math.max(0, startColor.r + startColorVar.r * ccMacros.CCRANDOM_MINUS1_1() ) );
        float start_g = Math.min(1, Math.max(0, startColor.g + startColorVar.g * ccMacros.CCRANDOM_MINUS1_1() ) );
        float start_b = Math.min(1, Math.max(0, startColor.b + startColorVar.b * ccMacros.CCRANDOM_MINUS1_1() ) );
        float start_a = Math.min(1, Math.max(0, startColor.a + startColorVar.a * ccMacros.CCRANDOM_MINUS1_1() ) );

//        ccColor4F end = new ccColor4F();
        float end_r = Math.min(1, Math.max(0, endColor.r + endColorVar.r * ccMacros.CCRANDOM_MINUS1_1() ) );
        float end_g = Math.min(1, Math.max(0, endColor.g + endColorVar.g * ccMacros.CCRANDOM_MINUS1_1() ) );
        float end_b = Math.min(1, Math.max(0, endColor.b + endColorVar.b * ccMacros.CCRANDOM_MINUS1_1() ) );
        float end_a = Math.min(1, Math.max(0, endColor.a + endColorVar.a * ccMacros.CCRANDOM_MINUS1_1() ) );

        ccColor4FUtil.set(particle.color, start_r, start_g, start_b, start_a);
        
        ccColor4FUtil.set(particle.deltaColor,
        		(end_r - start_r) / particle.timeToLive,
        		(end_g - start_g) / particle.timeToLive,
        		(end_b - start_b) / particle.timeToLive,
        		(end_a - start_a) / particle.timeToLive);

        // size
        float startS = Math.max(0, startSize + startSizeVar * ccMacros.CCRANDOM_MINUS1_1() ); // no negative size

        particle.size = startS;
        if( endSize == kCCParticleStartSizeEqualToEndSize )
            particle.deltaSize = 0;
        else {
            float endS = endSize + endSizeVar * ccMacros.CCRANDOM_MINUS1_1();
            endS = Math.max(0, endS);
            particle.deltaSize = (endS - startS) / particle.timeToLive;
        }

        // rotation
        float startA = startSpin + startSpinVar * ccMacros.CCRANDOM_MINUS1_1();
        float endA = endSpin + endSpinVar * ccMacros.CCRANDOM_MINUS1_1();
        particle.rotation = startA;
        particle.deltaRotation = (endA - startA) / particle.timeToLive;

        // position
        if( positionType_ == kCCPositionTypeFree ) {
        	this.convertToWorldSpace(0, 0, particle.startPos);
        } else if( positionType_ == kCCPositionTypeRelative ) {
        	particle.startPos.set(position_);
        }
		
        // direction
        float a = ccMacros.CC_DEGREES_TO_RADIANS( angle + angleVar * ccMacros.CCRANDOM_MINUS1_1() );	

        // Mode Gravity: A
        if (emitterMode == kCCParticleModeGravity) {
            float s = modeA.speed + modeA.speedVar * ccMacros.CCRANDOM_MINUS1_1();

            if (particle.modeA == null) {
            	particle.modeA = new CCParticle.ParticleModeA();
            }
            
            // direction
            particle.modeA.dir.set((float)Math.cos(a), (float)Math.sin(a));
            CGPointUtil.mult(particle.modeA.dir, s);

            // radial accel
            particle.modeA.radialAccel = modeA.radialAccel + modeA.radialAccelVar * ccMacros.CCRANDOM_MINUS1_1();

            // tangential accel
            particle.modeA.tangentialAccel = modeA.tangentialAccel + modeA.tangentialAccelVar * ccMacros.CCRANDOM_MINUS1_1();
        }

        // Mode Radius: B
        else {
            // Set the default diameter of the particle from the source position
            float startRadius = modeB.startRadius + modeB.startRadiusVar * ccMacros.CCRANDOM_MINUS1_1();
            float endRadius = modeB.endRadius + modeB.endRadiusVar * ccMacros.CCRANDOM_MINUS1_1();

            if (particle.modeB == null) {
            	particle.modeB = new CCParticle.ParticleModeB();
            }
            
            particle.modeB.radius = startRadius;

            if( modeB.endRadius == kCCParticleStartRadiusEqualToEndRadius )
                particle.modeB.deltaRadius = 0;
            else
                particle.modeB.deltaRadius = (endRadius - startRadius) / particle.timeToLive;

            particle.modeB.angle = a;
            particle.modeB.degreesPerSecond = ccMacros.CC_DEGREES_TO_RADIANS(modeB.rotatePerSecond + modeB.rotatePerSecondVar * ccMacros.CCRANDOM_MINUS1_1());
        }
	}

	//! stop emitting particles. Running particles will continue to run until they die
	public void stopSystem() {
		active = false;
		elapsed = duration;
		emitCounter = 0;
	}

	//! Kill all living particles.
	public void resetSystem() {
		active = true;
		elapsed = 0;
		for (particleIdx = 0; particleIdx < particleCount; ++particleIdx) {
			CCParticle p = particles[particleIdx];
			p.timeToLive = 0;
		}
	}

    // ideas taken from:
    //	 . The ocean spray in your face [Jeff Lander]
    //		http://www.double.co.nz/dust/col0798.pdf
    //	 . Building an Advanced Particle System [John van der Burg]
    //		http://www.gamasutra.com/features/20000623/vanderburg_01.htm
    //   . LOVE game engine
    //		http://love2d.org/
    //
    // Radius mode support, from 71 squared
    //		http://particledesigner.71squared.com/
    //
    // IMPORTANT: Particle Designer is supported by cocos2d, but
    // 'Radius Mode' in Particle Designer uses a fixed emit rate of 30 hz. Since that can't be guarateed in cocos2d,
    //  cocos2d uses a another approach, but the results are almost identical. 
    //


    /** creates an initializes a CCParticleSystem from a plist file.
      This plist files can be creted manually or with Particle Designer:
        http://particledesigner.71squared.com/
      @since v0.99.3
    */
//    public static CCParticleSystem particleWithFile(String plistFile) {
//    //    return new CCParticleSystem(plistFile);
//    	return null;
//    }


    /** initializes a CCParticleSystem from a plist file.
      This plist files can be creted manually or with Particle Designer:
        http://particledesigner.71squared.com/
      @since v0.99.3
    */
    protected CCParticleSystem(String plistFile) {
        /*
    	String *path = [CCFileUtils fullPathFromRelativePath:plistFile];
        NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:path];

        NSAssert( dict != nil, @"Particles: file not found");
        */
    }


    /** initializes a CCQuadParticleSystem from a NSDictionary.
     @since v0.99.3
     */
    public CCParticleSystem(HashMap<?,?> dictionary) {
    	/*
        int maxParticles = [[dictionary valueForKey:@"maxParticles"] intValue];
        // self, not super
        if ((self=[self initWithTotalParticles:maxParticles] ) ) {

            // angle
            angle = [[dictionary valueForKey:@"angle"] floatValue];
            angleVar = [[dictionary valueForKey:@"angleVariance"] floatValue];

            // duration
            duration = [[dictionary valueForKey:@"duration"] floatValue];

            // blend function 
            blendFunc_.src = [[dictionary valueForKey:@"blendFuncSource"] intValue];
            blendFunc_.dst = [[dictionary valueForKey:@"blendFuncDestination"] intValue];

            // color
            float r,g,b,a;

            r = [[dictionary valueForKey:@"startColorRed"] floatValue];
            g = [[dictionary valueForKey:@"startColorGreen"] floatValue];
            b = [[dictionary valueForKey:@"startColorBlue"] floatValue];
            a = [[dictionary valueForKey:@"startColorAlpha"] floatValue];
            startColor = (ccColor4F) {r,g,b,a};

            r = [[dictionary valueForKey:@"startColorVarianceRed"] floatValue];
            g = [[dictionary valueForKey:@"startColorVarianceGreen"] floatValue];
            b = [[dictionary valueForKey:@"startColorVarianceBlue"] floatValue];
            a = [[dictionary valueForKey:@"startColorVarianceAlpha"] floatValue];
            startColorVar = (ccColor4F) {r,g,b,a};

            r = [[dictionary valueForKey:@"finishColorRed"] floatValue];
            g = [[dictionary valueForKey:@"finishColorGreen"] floatValue];
            b = [[dictionary valueForKey:@"finishColorBlue"] floatValue];
            a = [[dictionary valueForKey:@"finishColorAlpha"] floatValue];
            endColor = (ccColor4F) {r,g,b,a};

            r = [[dictionary valueForKey:@"finishColorVarianceRed"] floatValue];
            g = [[dictionary valueForKey:@"finishColorVarianceGreen"] floatValue];
            b = [[dictionary valueForKey:@"finishColorVarianceBlue"] floatValue];
            a = [[dictionary valueForKey:@"finishColorVarianceAlpha"] floatValue];
            endColorVar = (ccColor4F) {r,g,b,a};

            // particle size
            startSize = [[dictionary valueForKey:@"startParticleSize"] floatValue];
            startSizeVar = [[dictionary valueForKey:@"startParticleSizeVariance"] floatValue];
            endSize = [[dictionary valueForKey:@"finishParticleSize"] floatValue];
            endSizeVar = [[dictionary valueForKey:@"finishParticleSizeVariance"] floatValue];


            // position
            float x = [[dictionary valueForKey:@"sourcePositionx"] floatValue];
            float y = [[dictionary valueForKey:@"sourcePositiony"] floatValue];
            position_ = ccp(x,y);
            posVar.x = [[dictionary valueForKey:@"sourcePositionVariancex"] floatValue];
            posVar.y = [[dictionary valueForKey:@"sourcePositionVariancey"] floatValue];


            emitterMode_ = [[dictionary valueForKey:@"emitterType"] intValue];

            // Mode A: Gravity + tangential accel + radial accel
            if( emitterMode_ == kCCParticleModeGravity ) {
                // gravity
                mode.A.gravity.x = [[dictionary valueForKey:@"gravityx"] floatValue];
                mode.A.gravity.y = [[dictionary valueForKey:@"gravityy"] floatValue];

                //
                // speed
                mode.A.speed = [[dictionary valueForKey:@"speed"] floatValue];
                mode.A.speedVar = [[dictionary valueForKey:@"speedVariance"] floatValue];

                // radial acceleration
                mode.A.radialAccel = [[dictionary valueForKey:@"radialAcceleration"] floatValue];
                mode.A.radialAccelVar = [[dictionary valueForKey:@"radialAccelVariance"] floatValue];

                // tangential acceleration
                mode.A.tangentialAccel = [[dictionary valueForKey:@"tangentialAcceleration"] floatValue];
                mode.A.tangentialAccelVar = [[dictionary valueForKey:@"tangentialAccelVariance"] floatValue];
            }


            // or Mode B: radius movement
            else if( emitterMode_ == kCCParticleModeRadius ) {
                float maxRadius = [[dictionary valueForKey:@"maxRadius"] floatValue];
                float maxRadiusVar = [[dictionary valueForKey:@"maxRadiusVariance"] floatValue];
                float minRadius = [[dictionary valueForKey:@"minRadius"] floatValue];

                mode.B.startRadius = maxRadius;
                mode.B.startRadiusVar = maxRadiusVar;
                mode.B.endRadius = minRadius;
                mode.B.endRadiusVar = 0;
                mode.B.rotatePerSecond = [[dictionary valueForKey:@"rotatePerSecond"] floatValue];
                mode.B.rotatePerSecondVar = [[dictionary valueForKey:@"rotatePerSecondVariance"] floatValue];

            } else {
                NSAssert( NO, @"Invalid emitterType in config file");
            }

            // life span
            life = [[dictionary valueForKey:@"particleLifespan"] floatValue];
            lifeVar = [[dictionary valueForKey:@"particleLifespanVariance"] floatValue];				

            // emission Rate
            emissionRate = totalParticles/life;

            // texture		
            // Try to get the texture from the cache
            NSString *textureName = [dictionary valueForKey:@"textureFileName"];
            NSString *textureData = [dictionary valueForKey:@"textureImageData"];

            self.texture = [[CCTextureCache sharedTextureCache] addImage:textureName];

            if ( ! texture_ && textureData) {
                // if it fails, try to get it from the base64-gzipped data			
                unsigned char *buffer = NULL;
                int len = base64Decode((unsigned char*)[textureData UTF8String], [textureData length], &buffer);
                NSAssert( buffer != NULL, @"CCParticleSystem: error decoding textureImageData");

                unsigned char *deflated = NULL;
                int deflatedLen = inflateMemory(buffer, len, &deflated);
                free( buffer );

                NSAssert( deflated != NULL, @"CCParticleSystem: error ungzipping textureImageData");
                NSData *data = [[NSData alloc] initWithBytes:deflated length:deflatedLen];
                UIImage *image = [[UIImage alloc] initWithData:data];

                self.texture = [[CCTextureCache sharedTextureCache] addCGImage:[image CGImage] forKey:textureName];
                [data release];
                [image release];
            }

            NSAssert( [self texture] != NULL, @"CCParticleSystem: error loading the texture");
        }

        return self;
        */
    }

	//! Add a particle to the emitter
	public boolean addParticle() {
		if (isFull())
			return false;
		CCParticle particle = particles[particleCount];
		initParticle(particle);
		particleCount++;
		return true;
	}

    public void update(float dt) {
        if( active && emissionRate != 0 ) {
            float rate = 1.0f / emissionRate;
            emitCounter += dt;
            while( particleCount < totalParticles && emitCounter > rate ) {
                addParticle();
                emitCounter -= rate;
            }

            elapsed += dt;
            if(duration != -1 && duration < elapsed)
                stopSystem();
        }

        particleIdx = 0;

        OneClassPool<CGPoint> pointPool = PoolHolder.getInstance().getCGPointPool();
        CGPoint currentPosition = pointPool.get();
        CGPoint tmp = pointPool.get();
        CGPoint radial = pointPool.get();
        CGPoint tangential = pointPool.get();
        
        if( positionType_ == kCCPositionTypeFree ) {
            convertToWorldSpace(0, 0, currentPosition);
        } else if( positionType_ == kCCPositionTypeRelative ) {
    		currentPosition.set(position_);
    	}

        while( particleIdx < particleCount ) {
            CCParticle p = particles[particleIdx];
            // life
            p.timeToLive -= dt;
            if( p.timeToLive > 0 ) {
                // Mode A: gravity, direction, tangential accel & radial accel
                if( emitterMode == kCCParticleModeGravity ) {
//                    CGPoint tmp, radial, tangential;

                	CGPointUtil.zero(radial);
                    // radial acceleration
                    if(p.pos.x != 0 || p.pos.y != 0)
                    	CGPointUtil.normalize(p.pos, radial);
                    tangential.set(radial);
                    CGPointUtil.mult(radial, p.modeA.radialAccel);

                    // tangential acceleration
                    float newy = tangential.x;
                    tangential.x = -tangential.y;
                    tangential.y = newy;
                    CGPointUtil.mult(tangential, p.modeA.tangentialAccel);

                    // (gravity + radial + tangential) * dt
                    CGPointUtil.add(radial, tangential, tmp);
                    CGPointUtil.add(tmp, modeA.gravity);
                    CGPointUtil.mult(tmp, dt);
                    CGPointUtil.add(p.modeA.dir, tmp);
                    CGPointUtil.mult(p.modeA.dir, dt, tmp);
                    CGPointUtil.add( p.pos, tmp );
                }
                // Mode B: radius movement
                else {				
                    // Update the angle and radius of the particle.
                    p.modeB.angle  += p.modeB.degreesPerSecond * dt;
                    p.modeB.radius += p.modeB.deltaRadius * dt;

                    p.pos.x = - (float)Math.cos(p.modeB.angle) * p.modeB.radius;
                    p.pos.y = - (float)Math.sin(p.modeB.angle) * p.modeB.radius;
                }

                // color
                p.color.r += (p.deltaColor.r * dt);
                p.color.g += (p.deltaColor.g * dt);
                p.color.b += (p.deltaColor.b * dt);
                p.color.a += (p.deltaColor.a * dt);

                // size
                p.size += (p.deltaSize * dt);
                p.size = Math.max( 0, p.size );

                // angle
                p.rotation += (p.deltaRotation * dt);
                CGPoint	newPos;

                if( positionType_ == kCCPositionTypeFree || positionType_ == kCCPositionTypeRelative ) {
                    CGPoint diff = tmp;
                    CGPointUtil.sub(currentPosition, p.startPos, diff);
                    CGPointUtil.sub(p.pos, diff, diff);
                    newPos = diff;
                } else {
                    newPos = p.pos;
                }
                
                this.updateQuad(p, newPos);
                /* try {
					updateParticleImp.invoke(this, new Object[]{p, newPos});
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

                // update particle counter
                particleIdx++;

            } else {
                // life < 0
                if( particleIdx != particleCount-1 ) {
                	CCParticle tmpPart = particles[particleIdx]; 
                    particles[particleIdx] = particles[particleCount-1];
                    particles[particleCount-1] = tmpPart;
                }
                particleCount--;

                if( particleCount == 0 && autoRemoveOnFinish_ ) {
                    unscheduleUpdate();
                    this.getParent().removeChild(this, true);
                    return;
                }
            }
        }
        
        pointPool.free(currentPosition);
        pointPool.free(tmp);
        pointPool.free(radial);
        pointPool.free(tangential);

        postStep();
    }


    //! should be overriden by subclasses
    public void updateQuad(CCParticle particle, CGPoint pos) {
        ;
    }

    public void postStep() {
        // should be overriden
    }

    public void setBlendAdditive(boolean additive) {
        if( additive ) {
            blendFunc.src = GL10.GL_SRC_ALPHA;
            blendFunc.dst = GL10.GL_ONE;
        } else {
            if( texture!=null && ! texture.hasPremultipliedAlpha() ) {
                blendFunc.src = GL10.GL_SRC_ALPHA;
                blendFunc.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
            } else {
                blendFunc.src = ccConfig.CC_BLEND_SRC;
                blendFunc.dst = ccConfig.CC_BLEND_DST;
            }
        }
    }

    public boolean getBlendAdditive() {
        return( blendFunc.src == GL10.GL_SRC_ALPHA && blendFunc.dst == GL10.GL_ONE);
    }

}


