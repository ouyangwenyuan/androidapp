package com.kitty.usedengine;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kitty.global.GlobalConfig;

public class TofuSprite  {
	
	public static final int PTM_RATIO =32;
	public static void setSpeed(CCSprite sprite,float x, float y,World world){
		BodyDef boxDef = new BodyDef();
		boxDef.type =BodyType.DynamicBody;
		CGPoint worldSpace = sprite.convertToWorldSpace(sprite.getPosition().x, sprite.getPosition().y);
		boxDef.position.set(worldSpace.x, worldSpace.y);
		
		boxDef.bullet = true;
		Body box = world.createBody(boxDef);
		box.setUserData(sprite);
		
		 PolygonShape blockShape = new PolygonShape();
		    
//		    float size = 0.06f;
//		    Vector2 vertices[] = {
//		       new Vector2(size ,-2*size),
//		       new Vector2(2*size,-size),
//		       new Vector2(2*size,size),
//		        
//		       new Vector2(size,2*size),
//		       new Vector2(-size,2*size),
//		       new Vector2(-2*size,size),
//		       new Vector2(-2*size,-size),
//		       new Vector2(-size,-2*size)
//		    };
		    blockShape.setAsBox(sprite.getTextureRect().size.width, sprite.getTextureRect().size.height);
		    
		    FixtureDef boxShapeDef = new FixtureDef();
		    boxShapeDef.shape = blockShape;
		    boxShapeDef.density =80.0f;
		    boxShapeDef.friction=0.1f;
		    boxShapeDef.restitution =0.9f;
		    Fixture fixture = box.createFixture(boxShapeDef);		    
		    Vector2 force = new Vector2(x, y);
		    box.applyLinearImpulse(force, box.getPosition());
	}

	public static World createWorld(){
		Vector2 gravity = new Vector2(0.0f, -5.0f);
		World world= new World(gravity, true);
		
		world.setContactListener(new ContactListener() {
			
			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}
		});
		
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		Body groundBody = world.createBody(groundDef);
		PolygonShape groundBox =new PolygonShape();
		groundBox.setAsEdge(new Vector2(0, 87/PTM_RATIO), new Vector2(GlobalConfig.deviceWidth/PTM_RATIO,87/PTM_RATIO));
		groundBody.createFixture(groundBox, 0);
		
		return world;
	}

}
