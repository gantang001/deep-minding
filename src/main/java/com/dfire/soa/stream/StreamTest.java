package com.dfire.soa.stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


/**
 * @author gantang
 * @Date 2017/7/18
 */
public class StreamTest {

    private Map<Integer, String> map = new ConcurrentHashMap<>();

    private List<String> list;

    private Object result;

    @FunctionalInterface
    interface Generator<T> {
        T get();

        default String uuId() {
            return UUID.randomUUID().toString();
        }
    }

    //private Generator<Double> random = ()->Math.random();

    private Generator<Double> random = Math::random;

    @Before
    public void before() {
        map.putAll(Stream.generate(UUID::randomUUID).limit(Byte.MAX_VALUE).distinct().collect(Collectors.toMap(uuid -> uuid.hashCode() * 2, UUID::toString)));

        list = Stream.iterate(1, item -> item + 1).limit(10).map(o -> random.uuId().substring(o)).collect(toList());
    }

    @After
    public void after() {
        Stream.of(result).forEach(System.out::println);
    }

    @Test
    public void filter() {
        result = list.parallelStream().filter(a -> !a.endsWith("0")).reduce((a, b) -> a + b);
    }

    @Test
    public void distinct() {
        result = list.stream().peek(System.out::println).distinct().mapToInt(String::hashCode).sorted().summaryStatistics();
    }

    @Test
    public void map() {
        result = list.parallelStream().map(s -> new Blue(s.hashCode(), s)).map(Blue::toString).skip(5).collect(toList());
    }

    @Test
    public void transform() {

        List<Object> nums = Arrays.asList(0b01, 0x0L, .03d, .0_1D);

        result = nums.stream().filter(num -> (num.hashCode() ^ 2) > 0).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        //两种不同集合类型间转换
        List<Blue> blues = list.stream().map(Blue::new).collect(toList());
        //listToMap转换
        Map<String, Blue> map = list.stream().collect(Collectors.toMap(o -> o, Blue::new));
    }

    @Test
    public void reduce() throws Exception {
        Blue blue = new Blue(String.valueOf(random.get()));
        Stream.of(1, 2, 3, 4, 5).reduce((a, b) -> a + b).ifPresent(blue::setId);
    }

    @Test
    public void sort() {
        result = list.stream().map(Blue::new).sorted((a, b) -> a.uuid.length() > b.uuid.length() ? 0 : -1).findFirst().orElse(new Blue(StringUtils.EMPTY));
    }


    // flatMap: 将多个Stream连接成一个Stream，这时候不是用新值取代Stream的值，与map有所区别，这是重新生成一个Stream对象取而代之。

    @Test
    public void flatMap() {
        result = Stream.of(list, Arrays.asList(random.uuId(), random.uuId())).flatMap(s -> s.stream().map(d -> d.replace("-", ""))).collect(toList());
    }

    @Test
    public void groupBy() {
        Stream<Person> stream = Stream.of(new Person("1", "aa", "12"), new Person("1", "bb", "13"), new Person("3", "cc", "14"));
        System.out.println(stream.collect(groupingBy(x -> x.id)));

        Map<String, List<Person>> tempMap = Stream.of(new Person("1", "aa", "12"), new Person("1", "bb", "13"), new Person("3", "cc", "14"))
                .collect(groupingBy(x -> x.id));
        tempMap.entrySet().forEach(System.out::println);

        result = Stream.of(1, 2, 3, 4).collect(groupingBy(it -> it > 3));
    }

    @Test
    public void forUpdate() {
        //以后for循环可以这样写
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            list.add(i);
        }
        Stream.iterate(1, i -> i + 1).limit(Short.MAX_VALUE).collect(toList());
        for (Integer val : list) {
            System.out.println(val);
        }
        list.forEach(System.out::println);
    }

    @Test
    public void threadSafeTest() {

    }

    private class Blue {
        String uuid;
        int id;

        Blue(String uuid) {
            this.uuid = uuid;
        }

        Blue(int id, String uuid) {
            this.uuid = uuid;
            this.id = id;
        }

        void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Blue{" +
                    "uuid='" + uuid + '\'' +
                    ", id=" + id +
                    '}';
        }
    }


    @Test
    public void demo() {
        //入参
        List<InvoiceInstance> list = Collections.emptyList();
        long todayZeroTime = 250, yesterdayZeroTime = 350;
        int year = 2017, month = 7, day = 20;
        //出参
        List<Report> reports = new ArrayList<>();
        AtomicInteger cas = new AtomicInteger();

        Map<String, List<InvoiceInstance>> groupByEntityIdMap = list.stream().distinct().filter(o -> o != null).
                filter(o -> o.getStatus() == 3 && o.getOutTime() >= yesterdayZeroTime && o.getOutTime() <= todayZeroTime)
                .collect(groupingBy(InvoiceInstance::getEntityId));

        result = groupByEntityIdMap.entrySet().stream().map(o -> new Report(o.getKey(),
                o.getValue().stream().filter(inv -> inv.getIsRed() != 2).mapToLong(InvoiceInstance::getTotalAmount).sum(),
                o.getValue().stream().peek(item -> {
                    if (item.getIsRed() == 2) cas.getAndIncrement();
                }).filter(invoice -> invoice.getIsRed() != 2).count() + cas.get() * 2, year, month, day))
                .sorted(((o1, o2) -> o1.totalAmount > o2.totalAmount ? 0 : -1)).peek(reports::add).mapToLong(Report::getTotalAmount).summaryStatistics();

        Stream.of(reports).forEach(System.out::println);
    }

    private class Report {
        String entityId;
        long totalAmount;
        long totalCount;
        int year, month, day;

        Report(String entityId, long totalAmount, long totalCount, int year, int month, int day) {
            this.entityId = entityId;
            this.totalAmount = totalAmount;
            this.totalCount = totalCount;
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(long totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

    private class Person {
        String id;
        String name;
        String age;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public Person() {
        }

        public Person(String id, String name, String age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", age='" + age + '\'' +
                    '}';
        }
    }
}
