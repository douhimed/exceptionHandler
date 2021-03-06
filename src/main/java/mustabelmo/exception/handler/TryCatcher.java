package mustabelmo.exception.handler;

import mustabelmo.exception.handler.functional.CatchBlock;
import mustabelmo.exception.handler.functional.FinallyBlock;
import mustabelmo.exception.handler.functional.TryBlock;
import mustabelmo.exception.handler.utils.ClassMap;

import java.util.Collections;
import java.util.List;

public class TryCatcher {
    /**
     * specifies if the try catch block is already executed or not
     */
    private boolean executed;
    /**
     * the default exception
     */
    private static final Class<?> DEFAULT = Throwable.class;
    /**
     * the try block
     */
    private TryBlock tryBlock;

    /**
     * the finally block
     */
    private FinallyBlock finallyBlock;

    /**
     * the catchBlockMap of the exceptions and their correspondent catch blocks
     */
    private ClassMap catchBlockMap;

    /**
     * Constructor of the TryCatcher class
     * init the catchBlockMap holding the exceptions types and their correspondent catch blocks
     */
    public TryCatcher() {
        catchBlockMap = new ClassMap();
        catchBlockMap.put(DEFAULT, throwable -> {
        });
    }

    /**
     * Constructor of the TryCatcher class
     *
     * @param tryBlock the try block
     */
    public TryCatcher(TryBlock tryBlock) {
        this();
        this.tryBlock = tryBlock;
    }

    /**
     * Constructor of the TryCatcher class
     *
     * @param tryBlock   the try block
     * @param catchBlock the catch block
     */
    public TryCatcher(TryBlock tryBlock, CatchBlock catchBlock) {
        this(tryBlock);
        defaultCatch(catchBlock);
    }

    /**
     * Execute the try block and the associated catch blocks,
     * and also the finally block
     */
    public TryCatcher execute() {
        if (!executed) {
            try {
                tryBlock.perform();

            } catch (Throwable throwable) {
                CatchBlock currentCatchBlock = catchBlockMap.get(throwable.getClass());
                if (currentCatchBlock == null) {
                    currentCatchBlock = catchBlockMap.get(DEFAULT);
                }
                currentCatchBlock.handle(throwable);
            } finally {
                if (finallyBlock != null) {
                    finallyBlock.onFinalize();
                }
            }
            executed = true;
        }
        return this;
    }

    /**
     * Assign the specific catch block whenever this kind of exception is caught
     *
     * @param exceptionClass the exception class
     * @param catchBlock     the correspondent catch block
     * @param <T>            the type of the exception
     * @return the current instance
     */
    public <T> TryCatcher when(Class<T> exceptionClass, CatchBlock catchBlock) {
        return catchWhen(catchBlock, Collections.singletonList(exceptionClass));
    }


    /**
     * Assign the specific catch block whenever one of these exceptions is caught
     *
     * @param exceptionClasses the exception classes
     * @param catchBlock       the correspondent catch block
     * @return the current instance
     */
    public TryCatcher catchWhen(CatchBlock catchBlock, List<Class<?>> exceptionClasses) {
        for (Class<?> exceptionClass : exceptionClasses) {
            catchBlockMap.put(exceptionClass, catchBlock);
        }
        return this;
    }

    /**
     * In order to override the default catch block for the default exception
     *
     * @param catchBlock the catch block
     * @return the current instance of TryCatcher
     */
    public TryCatcher defaultCatch(CatchBlock catchBlock) {
        catchBlockMap.put(DEFAULT, catchBlock);
        return this;
    }

    /**
     * Assign the finally block to the try catch process
     *
     * @param finallyBlock finally block
     * @return the current instance of TryCatcher
     */
    public TryCatcher finallyBlock(FinallyBlock finallyBlock) {
        this.finallyBlock = finallyBlock;
        return this;
    }

    /**
     * Execute another try catch block
     *
     * @param tryCatcher another try catch block to execute.
     */
    public TryCatcher then(TryCatcher tryCatcher) {
        tryCatcher.execute();
        return tryCatcher;
    }

    public static TryCatcher of(TryBlock tryBlock, CatchBlock catchBlock) {
        return new TryCatcher(tryBlock, catchBlock);
    }

    public static TryCatcher of(TryBlock tryBlock) {
        return new TryCatcher(tryBlock);
    }
}
