package org.cocos2d.particlesystem;

import java.lang.ref.WeakReference;
import java.nio.ShortBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.opengl.GLResourceHelper;
import org.cocos2d.opengl.GLResourceHelper.Resource;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.utils.BufferProvider;
import org.cocos2d.utils.FastFloatBuffer;
import org.cocos2d.utils.PlistParser;

/** CCQuadParticleSystem is a subclass of CCParticleSystem

 It includes all the features of ParticleSystem.

 Special features and Limitations:	
  - Particle size can be any float number.
  - The system can be scaled
  - The particles can be rotated
  - On 1st and 2nd gen iPhones: It is only a bit slower that CCPointParticleSystem
  - On 3rd gen iPhone and iPads: It is MUCH faster than CCPointParticleSystem
  - It consumes more RAM and more GPU memory than CCPointParticleSystem
  - It supports subrects
 @since v0.8
 */
public class CCParticleSystemQuad extends CCQuadParticleSystem {
	
    public static CCParticleSystem particleWithFile(String plistFile) {
    	HashMap<String,Object> dict  = PlistParser.parse(plistFile);
    	int maxParticles =getInt(dict,"maxParticles");
        return new CCParticleSystemQuad(maxParticles,dict);
    }

    private static int getInt(HashMap<?,?> dictionary,String key){
    	try {
    		return (int)Float.parseFloat(dictionary.get(key).toString());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return 0;
		}
    }
    private static float getFloat(HashMap<?,?> dictionary,String key){
    	try {
			return Float.parseFloat(dictionary.get(key).toString());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return 0;
		}
    }
    private static String getString(HashMap<?,?> dictionary,String key){
    	try {
			return dictionary.get(key).toString();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "";
		}
    }
    public CCParticleSystemQuad(int maxParticles,HashMap<?,?> dict) {
        super(maxParticles);
        // angle
        angle = getFloat(dict,"angle");
        angleVar = getFloat(dict,"angleVariance");

        // duration
        duration = getFloat(dict,"duration");

        // blend function 
        blendFunc.src = getInt(dict,"blendFuncSource");
        blendFunc.dst = getInt(dict,"blendFuncDestination");

        // color
        float r,g,b,a;

        r = getFloat(dict,"startColorRed");
        g = getFloat(dict,"startColorGreen");
        b = getFloat(dict,"startColorBlue");
        a = getFloat(dict,"startColorAlpha");
        startColor = new ccColor4F(r,g,b,a);

        r = getFloat(dict,"startColorVarianceRed");
        g = getFloat(dict,"startColorVarianceGreen");
        b = getFloat(dict,"startColorVarianceBlue");
        a = getFloat(dict,"startColorVarianceAlpha");
        startColorVar = new ccColor4F(r,g,b,a);

        r = getFloat(dict,"finishColorRed");
        g = getFloat(dict,"finishColorGreen");
        b = getFloat(dict,"finishColorBlue");
        a = getFloat(dict,"finishColorAlpha");
        endColor = new ccColor4F(r,g,b,a);

        r = getFloat(dict,"finishColorVarianceRed");
        g = getFloat(dict,"finishColorVarianceGreen");
        b = getFloat(dict,"finishColorVarianceBlue");
        a = getFloat(dict,"finishColorVarianceAlpha");
        endColorVar = new ccColor4F(r,g,b,a);

        // particle size
        startSize = getFloat(dict,"startParticleSize");
        startSizeVar = getFloat(dict,"startParticleSizeVariance");
        endSize = getFloat(dict,"finishParticleSize");
        endSizeVar = getFloat(dict,"finishParticleSizeVariance");


        // position
        float x = getFloat(dict,"sourcePositionx");
        float y = getFloat(dict,"sourcePositiony");
        position_ = ccp(x,y);
        posVar.x = getFloat(dict,"sourcePositionVariancex");
        posVar.y = getFloat(dict,"sourcePositionVariancey");


        emitterMode = getInt(dict,"emitterType");

        // Mode A: Gravity + tangential accel + radial accel
        if( emitterMode == kCCParticleModeGravity ) {
            // gravity
        	modeA.gravity.x = getFloat(dict,"gravityx");
            modeA.gravity.y = getFloat(dict,"gravityy");

            //
            // speed
            modeA.speed = getFloat(dict,"speed");
            modeA.speedVar = getFloat(dict,"speedVariance");

            // radial acceleration
            modeA.radialAccel = getFloat(dict,"radialAcceleration");
            modeA.radialAccelVar = getFloat(dict,"radialAccelVariance");

            // tangential acceleration
            modeA.tangentialAccel = getFloat(dict,"tangentialAcceleration");
            modeA.tangentialAccelVar = getFloat(dict,"tangentialAccelVariance");
        }


        // or Mode B: radius movement
        else if( emitterMode == kCCParticleModeRadius ) {
            float maxRadius = getFloat(dict,"maxRadius");
            float maxRadiusVar = getFloat(dict,"maxRadiusVariance");
            float minRadius = getFloat(dict,"minRadius");

            modeB.startRadius = maxRadius;
            modeB.startRadiusVar = maxRadiusVar;
            modeB.endRadius = minRadius;
            modeB.endRadiusVar = 0;
            modeB.rotatePerSecond = getFloat(dict,"rotatePerSecond");
            modeB.rotatePerSecondVar = getFloat(dict,"rotatePerSecondVariance");

        } else {
//            NSAssert( NO, @"Invalid emitterType in config file");
        }

        // life span
        life = getFloat(dict,"particleLifespan");
        lifeVar = getFloat(dict,"particleLifespanVariance");				

        // emission Rate
        emissionRate = totalParticles/life;

        // texture		
        // Try to get the texture from the cache
        String textureName = getString(dict,"textureFileName");
//        String textureData = getString(dict,"textureImageData");
        setTexture(CCTextureCache.sharedTextureCache().addImage(textureName));
        
    }


}

