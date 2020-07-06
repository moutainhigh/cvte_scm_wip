package com.cvte.scm.wip.common.utils;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * @author : jf
 * Date    : 2020.02.24
 * Time    : 11:58
 * Email   ：jiangfeng7128@cvte.com
 */
public class LambdaUtils {

    private LambdaUtils() {
        throw new IllegalAccessError(LambdaUtils.class.getName());
    }

    /**
     * 对数据流进行笛卡尔积处理。
     * <p>
     * 具体例子如下所示：
     * {@code BinaryOperator<String> aggregator = String::concat;}
     * {@code Supplier<Stream<String>>[] suppliers = new Supplier[]{() -> Stream.of("A", "B"), () -> Stream.of("C", "D")};}
     * {@code cartesian(aggregator, suppliers).collect(Collectors.toList()).forEach(s -> System.out::print(s + "，"));}
     * 则打印结果为：AC，AD，BC，BD
     */
    @SafeVarargs
    public static <T> Stream<T> cartesian(BinaryOperator<T> aggregator, Supplier<Stream<T>>... suppliers) {
        return stream(suppliers).reduce((a, b) -> () -> a.get().flatMap(t1 -> b.get().map(t2 -> aggregator.apply(t1, t2))))
                .orElse(Stream::empty).get();
    }

    /**
     * 将对象列表转换成目标列表
     *
     * @param list
     * @param function
     * @return java.util.List<R>
     **/
    public static <T, R> List<R> mapToList(List<T> list, Function<T, R> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }
}
