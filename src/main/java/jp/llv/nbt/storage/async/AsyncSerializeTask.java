/*
 * Copyright 2017 SakuraServerDev All rights reserved.
 */
package jp.llv.nbt.storage.async;

import jp.llv.nbt.IncompatiblePlatformException;

/**
 *
 * @author SakuraServerDev
 * @param <R> result type
 */
public interface AsyncSerializeTask<R> {
    
    void step();
    
    boolean isFinished();
    
    R getResult() throws IncompatiblePlatformException;
    
    long getEstimatedStepsRemaining();
    
}
