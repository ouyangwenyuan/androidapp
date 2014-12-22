package org.cocos2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


import org.cocos2d.nodes.CCDirector;


import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class LocalFileStore {
	private final int STORE_VERSION = 1;

	public InputStream getConfigFileInput(String fileName){
		InputStream is = null;
		try {
			is = new FileInputStream(new File(resourceRoot,fileName));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		if(is != null){
			return is;
		}
		return getAssetFileInput(DIR_CONF+fileName);
	}
	public InputStream getDataFileInput(String fileName){
		InputStream is = getContextFileInput(fileName);
		if(is != null){
			return is;
		}
		return getExternalFileInput(fileName);
	}
	public InputStream getTmpFileInput(String fileName){
		try {
			if(tmpRoot == null)return null;
			return new FileInputStream(new File(tmpRoot,fileName));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	public InputStream getResourceFileInput(String fileName){
		InputStream is = null;
		try {
			is = new FileInputStream(new File(resourceRoot,fileName));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		if(is != null){
			return is;
		}
		return getAssetFileInput(fileName);
	}
	public OutputStream getResourceFileOutput(String fileName){
		try {
			return new FileOutputStream(new File(resourceRoot,fileName));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}	
	public File getResourceFile(String fileName){
		try {
			return new File(resourceRoot,fileName);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}	
	public OutputStream getDataFileOutput1(String fileName){
		return getExternalFileOutput(fileName);
	}	
	public OutputStream getDataFileOutput2(String fileName){
		return getContextFileOutput(fileName);
	}	
	
	public OutputStream getTmpaFileOutput(String fileName){
		try {
			if(tmpRoot == null)return null;
			File file = new File(tmpRoot,fileName);
			tmpPath = file.getAbsolutePath();
			return new FileOutputStream(file);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}	
	private String tmpPath = null;
	public String getTmpaPath(){
		return tmpPath;
	}
	
	public InputStream getAssetFileInput(String fileName){
		try {
			return context.getAssets().open(fileName);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	public InputStream getContextFileInput(String fileName){
		try {
			return context.openFileInput(fileName);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	public OutputStream getContextFileOutput(String fileName){
		try {
			return context.openFileOutput(fileName,Context.MODE_PRIVATE);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	public InputStream getExternalFileInput(String fileName){
		try {
			Log.v("====","externalRoot="+externalRoot.getAbsolutePath()+",fileName="+fileName);
			if(externalRoot == null)return null;
			File file = new File(externalRoot,fileName);
			if(!file.exists())return null;
			return new FileInputStream(new File(externalRoot,fileName));

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		Log.v("====","NULL,e");
		return null;
	}

	public OutputStream getExternalFileOutput(String fileName){
		try {
			if(externalRoot == null)return null;
			return new FileOutputStream(new File(externalRoot,fileName));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	
	
	
	private final static String DIR_ROOT = ".";
	private final static String DIR_CONF = "conf/";
	private final static String DIR_IMG = "images/";
	private final static String DIR_TMP = "tmp";
	private final static String INTERNALROOT = "//data/data/";
	
	private boolean isSDCardExist;
	private Context context = null;
	
	private File externalRoot = null;
	private File tmpRoot = null;
	private File resourceRoot = null;
	
	private static LocalFileStore instance = null;
	protected LocalFileStore(){
		isSDCardExist =Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		context = CCDirector.theApp;
		if(isSDCardExist){
			externalRoot = getDir(Environment.getExternalStorageDirectory(),DIR_ROOT);
			tmpRoot = getDir(externalRoot,DIR_TMP);
		}
		resourceRoot = getDir(new File(INTERNALROOT),String.valueOf(STORE_VERSION));
	}
	private File getDir(File parent,String dir){
		if(parent == null)return null;
		File fileDir = new File(parent,dir);
		if(!fileDir.exists()){
			fileDir.mkdir();
		}
		return fileDir;
	}
	public static LocalFileStore instance(){
		if(instance == null){
			instance = new LocalFileStore();
		}
		return instance;
	}
}
