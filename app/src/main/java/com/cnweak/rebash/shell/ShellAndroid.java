package com.cnweak.rebash.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Context;
import android.os.FileObserver;
import android.text.TextUtils;
import android.util.Log;

import com.cnweak.rebash.shell.exception.ShellExecuteException;

/**
 * A Shell of android
 * 
 * @author holmes
 * 
 */
public class ShellAndroid implements Shell {
    public static boolean DEBUG = true;
    public static final String TAG = "ShellAndroid";

    public static final String CFLAG_TOOL_FILE_NAME = "cflag";
    public static final String CFLAG_TOOL_X86_FILE_NAME = "cflag_x86";
    public static final String FLAG_FILE_NAME = "flag_file";
    public static final int STILL_RUNNING = -1024;
    public static final int PROCESS_NEVER_CREATED = -18030;
    public static final int UNKNOWN_USER_ID = -1024;

    private static final AtomicInteger FLAG_ID = new AtomicInteger(0);

    private Process mProcess;
    private InputStream mReadStream, mErrorStream;
    private OutputStream mWriteStream;
    private String mFlagFile;
    private String mFlagTrigger;
    private String mFlagCmd;
    private byte[] mCmdSeparator;

    private CmdTerminalObserver mTerminalObserver;

    private final byte[] mLock = new byte[0];
    private final AtomicBoolean mCmdAlreadyFinished = new AtomicBoolean(true);

    private StringBuilder mLastResultBuilder = new StringBuilder(512);
    private String mLastResult = null;

    private AtomicBoolean mHasRoot = new AtomicBoolean(false);

    private IdContext mIdContext;
    
    /**  */
    private boolean mCheckSu = true;
    /** Its closed */
    private boolean mIsClosed = false;
    /** block mode */
    private boolean mIsInBlockMode = true;
    
    private Chmod mChmod;

    /** cmd exec timeout */
    private long mWaitTimeout = 0l;
    
    /**
     * Construct
     * @param chmod use to change cflag mode, 
     * 		  if it it null, the {@link DefaultChmod} will be used
     */
    public ShellAndroid(Chmod chmod) {
        mCmdSeparator = " ; ".getBytes();
        if (chmod == null){
        	chmod = new DefaultChmod(this);
        }
        mChmod = chmod;
        init();
    }
    
    /**
     * Get current {@link Chmod} implementation associate with this Shell
     * @return
     */
    public Chmod getChmod(){
    	return mChmod;
    }
    
    public void setCheckSu(boolean check){
        mCheckSu = check;
    }

    /**
     * Set cmd wait timeout.
     * @param timeout 0 always wait.
     */
    public void setWaitTimeout(long timeout){
        mWaitTimeout = timeout;
    }

    public long getWaitTimeout(){
        return mWaitTimeout;
    }

    /**
     * initialize command terminal flag tool
     * 
     * @param context
     * @throws NullPointerException if context is null
     * @return
     */
    public String initFlag(Context context) {
    	if (context == null){
    		throw new NullPointerException("context can not be null");
    	}

        File flagFile = context.getFileStreamPath(FLAG_FILE_NAME + FLAG_ID.incrementAndGet());
        if (!flagFile.exists()) {
            try {
                flagFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File cFlag = null;
        AbsReleaser releaser = CFlagRelease.getReleaser(context);
        if (releaser != null){
            try {
                cFlag = releaser.release();
            } catch (Exception e) {
                e.printStackTrace();
                mIsInBlockMode = false;
            }
        }else{
            mIsInBlockMode = false;
        }
        if (cFlag != null){
            mFlagTrigger = cFlag.getAbsolutePath();
            mChmod.setChmod(mFlagTrigger, "777");
        }else if (releaser != null){
            String cflagName = releaser.getCFlagName();
            cFlag = context.getFileStreamPath(cflagName);
            mFlagTrigger = cFlag.getAbsolutePath();
            mChmod.setChmod(mFlagTrigger, "777");
        }
        return flagFile.getAbsolutePath();
    }
    
    /**
     * initialize command terminal flag tool
     * with exist cflag
     * @param context
     * @param cFlag a exist cflag
     * @return
     */
    public String initFlag(Context context, File cFlag){
        File flagFile = context.getFileStreamPath(FLAG_FILE_NAME + FLAG_ID.incrementAndGet());
        if (!flagFile.exists()) {
            try {
                flagFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (cFlag != null){
        	mFlagTrigger = cFlag.getAbsolutePath();
        }else{
        	mIsInBlockMode = false;
        }
        mChmod.setChmod(mFlagTrigger, "777");
        return flagFile.getAbsolutePath();
    }

    /**
     * initialize command terminal flag tool
     * with Android internal tool, not use cflag.
     * so if you don't want use(extract) cflag, you need call
     * this method instead of other initFlag methods
     * @param context
     * @return
     */
    public String initFlagMinimum(Context context){
        File flagFile = context.getFileStreamPath(FLAG_FILE_NAME + FLAG_ID.incrementAndGet());
        if (!flagFile.exists()) {
            try {
                flagFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mFlagTrigger = "cat";
        return flagFile.getAbsolutePath();
    }

    /**
     * set command terminal flag file, which triggered by flag tool
     * 
     * @param file
     */
    public void setFlagFile(String file) {
        mFlagFile = file;
        mFlagCmd = mFlagTrigger + " " + mFlagFile;
        if (DEBUG){
            Log.d(TAG, "flag cmd: " + mFlagCmd);
        }
        if (mTerminalObserver != null) {
            mTerminalObserver.stopWatching();
        }
        mTerminalObserver = new CmdTerminalObserver(mFlagFile);
        mTerminalObserver.startWatching();
    }

    private void init() {
        String initCommand = "/system/bin/sh";
        try {
        	ProcessBuilder pb =  new ProcessBuilder(initCommand).redirectErrorStream(true);
        	pb.directory(new File("/"));
            Process process = pb.start();
            mProcess = process;
            mReadStream = process.getInputStream();
            mErrorStream = process.getErrorStream();
            mWriteStream = process.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIsClosed = false;
    }

    /**
     * Block mode. only in block mode, can get cmds result.
     * @see {@link #getLastResult()}
     * @return
     */
    public boolean isInBlockMode(){
        return mIsInBlockMode;
    }

    @Override
    public boolean close() {
        mIsClosed = true;

        if (mProcess != null) {
            try {
                mReadStream.close();
                mErrorStream.close();
                mWriteStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
				mProcess.destroy();
			} catch (Exception e) {
				// This is Auto-generated catch block
			}
            if (DEBUG) {
                Log.d(TAG, "**Shell destroyed**");
            }
        }
        if (mTerminalObserver != null) {
            // if has multi instance, the observer
            // will invalid for one of instance closeed
            mTerminalObserver.stopWatching();
            //mTerminalObserver.close();
            //mTerminalObserver = null;
        }
        synchronized (mLock) {
            mLock.notifyAll();
        }
        return true;
    }
    
    @Override
    public boolean isClosed() {
    	// This is Auto-generated method stub
    	return mIsClosed;
    }

    @Override
    public boolean exec(boolean asRoot, String... arrParam) {
        execute(arrParam);
        return true;
    }

    @Override
    public void checkRoot() {
        if (!mHasRoot.get()) {
            int id = checkId();
            if (id == 0 && (mIdContext == null || mIdContext.isRootRole())) {
                mHasRoot.set(true);
                return;
            }
            if (mCheckSu){
                execute("su");
            }
            id = checkId();
            if (id == 0 && (mIdContext == null || mIdContext.isRootRole())) {
                mHasRoot.set(true);
            }
        }
    }

    @Override
    public boolean hasRoot() {
        return mHasRoot.get();
    }

    @Override
    public boolean exitRoot() {
        if (hasRoot()) {
            execute("exit");
            if (checkId() == 0) {
                // still root shell,
                // so exit it
                return exitRoot();
            }
            mHasRoot.set(false);
            return true;
        }
        return false;
    }

    /**
     * Check the id of current user in the shell
     * @return
     */
    public int checkId() {
        execute("id");
        String idStrOrigin = getLastResult();
        if (idStrOrigin  != null){
            idStrOrigin = idStrOrigin.trim();
        }
        final String idStr = idStrOrigin;
        if (!TextUtils.isEmpty(idStr)
        		&& idStr.startsWith("uid=")
        		&& idStr.length() > 4) {
        	// uid=0(root) gid=0(root)
            int endPos = idStr.indexOf('(');
            int id;
            if (endPos != -1){
            	try {
					id = Integer.valueOf(idStr.substring(4, endPos));
				} catch (NumberFormatException e) {
					// This is Auto-generated catch block
					e.printStackTrace();
					id = UNKNOWN_USER_ID;
				}
            }else{
            	// if "(" can not found.
            	// so try to found first char which no a digit
            	final int len = idStr.length();
            	int firstNoDigit = -1;
            	for (int i = 4; i < len; i ++){
            		char ic = idStr.charAt(i);
            		if (!Character.isDigit(ic)){
            			firstNoDigit = i;
            			break;
            		}
            	}
            	if (firstNoDigit != -1){
            		try {
						id = Integer.valueOf(idStr.substring(4, firstNoDigit));
					} catch (NumberFormatException e) {
						// This is Auto-generated catch block
						e.printStackTrace();
						id = UNKNOWN_USER_ID;
					}
            	}else{
            		try {
						id = Integer.valueOf(idStr.substring(4));
					} catch (NumberFormatException e) {
						// This is Auto-generated catch block
						e.printStackTrace();
						id = UNKNOWN_USER_ID;
					}
            	}
            }
            
            // for SELinux
            // Text "context=u:r:init:s0" at last of idStr
            int contextPos = idStr.lastIndexOf("context=");
            if (contextPos > -1){
                // SELinux
                int contextEnd = idStr.indexOf(' ', contextPos);
                String contextStr;
                if (contextEnd == -1){
                    contextStr = idStr.substring(contextPos + 8);
                }else{
                    contextStr = idStr.substring(contextPos + 8,  contextEnd);
                }
                if (DEBUG) Log.d(TAG, "" + contextStr);
                if (mIdContext == null){
                    mIdContext = new IdContext(contextStr);
                }else{
                    mIdContext.update(contextStr);
                }
                
                //if (DEBUG) Log.d(TAG, String.format("u:%s, r:%s, role:%s, s:%s", 
                //        mIdContext.getU(), mIdContext.getR(), mIdContext.getRoll(), mIdContext.getS()));
            }
            return id;
        }
        return UNKNOWN_USER_ID;
    }
    
    /**
     * Get id context, may null if no in SELinux
     * @return
     */
    public IdContext getIdContext(){
        return mIdContext;
    }

    /**
     * change file mode.
     * it probably only be used for chmod 777 cflg;
     * @param file
     * @param mode
     */
    void chmodWithSh(String file, String mode){
        String cmd = "chmod " + mode + " " + file;
        byte[] rawCmd = cmd.getBytes();

        try {
            mWriteStream.write(rawCmd);

            mWriteStream.write(10);
            mWriteStream.flush();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // It is Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new ShellExecuteException("Input cmd error, Shell maybe closed. cmd: " + cmd, e);
        }
    }

    /**
     * internal execute
     * 
     * @param cmds
     */
    private void execute(String... cmds) {
        if (mIsInBlockMode){
            mCmdAlreadyFinished.set(false);
            // below is for test
//            synchronized (mLock){
//                if (!mCmdAlreadyFinished.get()){
//                    // wait for previous cmd finish
//                    try {
//                        mLock.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                mCmdAlreadyFinished.set(false);
//            }
        }

        for (int i = 0; i < cmds.length; i++) {
            String cmd = cmds[i].trim();
            cmd = filterCmdEndChars(cmd);
            if (DEBUG) Log.d(TAG, "cmd: " + cmd);
            byte[] rawCmd = cmd.getBytes();

            // clean the result for new command
            mLastResultBuilder.delete(0, mLastResultBuilder.length());
            mLastResult = null;

            try {
                mWriteStream.write(rawCmd);
                //mWriteStream.write(10);
                //mWriteStream.flush();
                
                // we will target notify flag twice
                // 1. for cmd terminal
                mWriteStream.write(mCmdSeparator);
                mWriteStream.write(mFlagCmd.getBytes());
                mWriteStream.write(10);
                mWriteStream.flush();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // It is Auto-generated catch block
                    e.printStackTrace();
                }
                // 2. for the pipe of sh or su
                mWriteStream.write(mFlagCmd.getBytes());
                mWriteStream.write(10);
                mWriteStream.flush();
            } catch (IOException e) {
                throw new ShellExecuteException("Input cmd error, Shell maybe closed. cmd: " + cmd, e);
            }

            if (mIsInBlockMode){
                synchronized (mLock) {
                    if (!mCmdAlreadyFinished.get()) {
                        // fix bug. for some reason,
                        // unlock will happen before enter lock black
                        try {
                            if (mWaitTimeout > 0){
                                mLock.wait(mWaitTimeout);
                                // for other thread exec cmd
                                mLock.notifyAll();
                            }else {
                                mLock.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * filter some chars end of cmd.
     * like ";"
     * @return
     */
    private String filterCmdEndChars(String cmd){
    	int len = cmd.length();
    	if (len > 0 && cmd.lastIndexOf(';') == len - 1){
    		String c = cmd.substring(0, len - 1);
    		c = c.trim();
    		return filterCmdEndChars(c);
    	}
    	return cmd;
    }

    /**
     * Start collect command out put result
     */
    public void printOutput() {
        Thread thread = new Thread(new OutputRunnable());
        thread.setName("Shell output");
        thread.start();
    }

    /**
     * Command result out put runnable of thread
     * 
     * @author holmes
     * 
     */
    private class OutputRunnable implements Runnable {

        @Override
        public void run() {
        	InputStream input = mReadStream;
            if (input != null){
            	byte[] buff = new byte[4096];
            	int readed;
            	try {
            		while ((readed = input.read(buff)) > 0) {
            			printBuff(buff, readed);
            		}
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            }else{
            	Log.e(TAG, "Shell process may not created, InputStream is null");
            }

            if (!mIsClosed){
                // if no close
                // so some exception happend with sh
                close();
            }

            if (DEBUG) {
                Log.d(TAG, "**over**");
            }
        }

        private void printBuff(byte[] buff, int length) {
            String buffStr = new String(buff, 0, length);
            if (DEBUG) Log.d(TAG, "~:" + buffStr);
            mLastResultBuilder.append(buffStr);
        }
    } 

    public int getExitValue() {
        if (mProcess != null) {
            try {
                return mProcess.exitValue();
            } catch (IllegalThreadStateException e) {
                return STILL_RUNNING;
            }
        }
        return PROCESS_NEVER_CREATED;
    }

    /**
     * A observer for command finished
     * 
     * @author holmes
     * 
     */
    private class CmdTerminalObserver extends FileObserver {
        @SuppressWarnings("unused")
        protected final String mWatchedFile;

        private boolean mIsClosed = false;

        public CmdTerminalObserver(String file) {
            super(file, OPEN);
            mWatchedFile = file;
        }

        @Override
        public void onEvent(int event, String path) {
            if (mIsClosed){
                return;
            }
            // Log.d(TAG, mWatchedFile + " opened");
            mLastResult = mLastResultBuilder.toString();
            synchronized (mLock) {
                mLock.notify();
                mCmdAlreadyFinished.set(true);
            }
            if (DEBUG){
                Log.d(TAG, "** cmd finish **");
            }
        }

        /**
         * Close. for multi ShellAndroid instance
         */
        public void close(){
            mIsClosed = true;
        }

    }

    /**
     * Get last command result
     * @return
     */
    public String getLastResult() {
        return mLastResult;
    }
}
