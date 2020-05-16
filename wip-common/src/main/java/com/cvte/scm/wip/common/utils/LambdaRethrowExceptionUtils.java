package com.cvte.scm.wip.common.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * 该工具类主要为了解决 Lambda 无法处理受检异常的问题
 *
 * @author : jf
 * Date    : 2020.04.07
 * Time    : 12:28
 * Email   ：jiangfeng7128@cvte.com
 */
@SuppressWarnings("unused")
public final class LambdaRethrowExceptionUtils {

    @FunctionalInterface
    public interface ConsumerWithException<T, E extends Exception> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface IntFunctionWithException<R, E extends Exception> {
        R apply(int t) throws E;
    }

    @FunctionalInterface
    public interface SupplierWithException<T, E extends Exception> {
        T get() throws E;
    }

    /**
     * rethrowConsumer(name -> System.out.println(Class.forName(name)))
     */
    public static <T, E extends Exception> Consumer<T> rethrowConsumer(ConsumerWithException<T, E> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * rethrowFunction(i -> new String(new byte[]{(byte)i.intValue()}, "UTF-8"))
     */
    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(FunctionWithException<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * rethrowIntFunction(i -> new String(new byte[]{(byte)i}, "UTF-8"))
     */
    public static <T, E extends Exception> IntFunction<T> rethrowIntFunction(IntFunctionWithException<T, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * rethrowSupplier(() -> new String(new byte[]{98}, "UTF-8"))
     */
    public static <T, E extends Exception> Supplier<T> rethrowSupplier(SupplierWithException<T, E> function) {
        return () -> {
            try {
                return function.get();
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

}