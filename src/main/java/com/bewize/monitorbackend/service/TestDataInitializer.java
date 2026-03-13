package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.school.Level;
import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.DiscountRepository;
import com.bewize.monitorbackend.repository.LevelRepository;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private static final int TARGET_LEVELS = 6;
    private static final int TARGET_STUDENTS = 80;
    private static final int TARGET_DISCOUNTS = 35;
    private static final int TARGET_ORDERS = 150;

    private final StudentRepository studentRepository;
    private final LevelRepository levelRepository;
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        seedIfNeeded();
    }

    private void seedIfNeeded() {
        List<Level> levels = ensureLevels();
        List<Student> students = ensureStudents(levels);
        List<Discount> discounts = ensureDiscounts();
        ensureOrdersAndSubscriptions(students, discounts);

        System.out.println("Test data initialization finished");
        System.out.println("Students: " + studentRepository.count());
        System.out.println("Discounts: " + discountRepository.count());
        System.out.println("Orders: " + orderRepository.count());
        System.out.println("Subscriptions: " + subscriptionRepository.count());
        System.out.println("Levels: " + levelRepository.count());
    }

    private List<Level> ensureLevels() {
        List<Level> existing = levelRepository.findAll();
        if (existing.size() >= TARGET_LEVELS) {
            return existing;
        }

        String[] levelNames = {
                "First Year",
                "Second Year",
                "Third Year",
                "Fourth Year",
                "Fifth Year",
                "Sixth Year"
        };
        Cycle[] cycles = {Cycle.ELEMENTARY_SCHOOL, Cycle.MIDDLE_SCHOOL, Cycle.HIGH_SCHOOL};

        for (int i = existing.size(); i < TARGET_LEVELS; i++) {
            Level level = new Level();
            level.setLevelName(levelNames[i % levelNames.length]);
            level.setCycle(cycles[i % cycles.length]);
            existing.add(levelRepository.save(level));
        }

        return existing;
    }

    private List<Student> ensureStudents(List<Level> levels) {
        List<Student> existing = new ArrayList<>(studentRepository.findAll());
        if (existing.size() >= TARGET_STUDENTS) {
            return existing;
        }

        String[] firstNames = {
                "Ahmed", "Fatima", "Mohamed", "Khadija", "Youssef", "Amina", "Omar", "Salma", "Hassan", "Nadia",
                "Rachid", "Zineb", "Khalid", "Laila", "Amine", "Sara", "Mehdi", "Houda", "Samir", "Imane",
                "Karim", "Meryem", "Hamza", "Asmaa", "Adil", "Wafa", "Issam", "Hajar", "Taha", "Loubna"
        };
        String[] lastNames = {
                "Alami", "Bennani", "Tazi", "ElFassi", "Chraibi", "Benjelloun", "Kettani", "Berrada", "Sqalli", "Filali",
                "Bouzidi", "Mansouri", "Lahlou", "Cherkaoui", "Benkirane", "Amrani", "Idrissi", "Ziani", "Naciri", "Belkadi"
        };

        int startIndex = existing.size();
        for (int i = startIndex; i < TARGET_STUDENTS; i++) {
            Student student = new Student();
            student.setCne(String.format("CNE%06d", i + 1));
            student.setFirstName(firstNames[i % firstNames.length]);
            student.setLastName(lastNames[i % lastNames.length]);
            student.setEmail(String.format(
                    "%s.%s%d@example.com",
                    firstNames[i % firstNames.length].toLowerCase(),
                    lastNames[i % lastNames.length].toLowerCase(),
                    i
            ));
            student.setPhone(String.format("06%08d", 10000000 + i));
            student.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            student.setSingupDate(toDate(LocalDate.now().minusDays(i)));
            student.setLastVisit(toDate(LocalDate.now().minusDays(i % 30)));
            student.setBirthday(toDate(LocalDate.of(2007 - (i % 6), ((i % 12) + 1), ((i % 28) + 1))));
            student.setLevel(levels.get(i % levels.size()));
            student.setActive(i % 6 != 0);
            student.setStoreCountry("MA");
            student.setLocationCountry("Morocco");
            student.setLocationCity(i % 2 == 0 ? "Casablanca" : "Rabat");

            existing.add(studentRepository.save(student));
        }

        return existing;
    }

    private List<Discount> ensureDiscounts() {
        List<Discount> existing = new ArrayList<>(discountRepository.findAll());
        if (existing.size() >= TARGET_DISCOUNTS) {
            return existing;
        }

        String[] prefixes = {"PROMO", "SUMMER", "WINTER", "SPRING", "FLASH", "VIP", "SPECIAL", "STUDENT", "SCHOOL", "EVENT"};

        for (int i = existing.size(); i < TARGET_DISCOUNTS; i++) {
            Discount discount = new Discount();
            discount.setCode(String.format("%s-%d", prefixes[i % prefixes.length], 100 + i));
            discount.setPercentage(5 + (i % 6) * 5);
            discount.setStartDate(LocalDateTime.now().minusDays(75L - i));
            discount.setEndDate(LocalDateTime.now().plusDays(30L + i));
            existing.add(discountRepository.save(discount));
        }

        return existing;
    }

    private void ensureOrdersAndSubscriptions(List<Student> students, List<Discount> discounts) {
        long existingOrders = orderRepository.count();
        if (students.isEmpty() || existingOrders >= TARGET_ORDERS) {
            return;
        }

        OrderType[] orderTypes = OrderType.values();
        OrderStatus[] orderStatuses = OrderStatus.values();
        PlanType[] planTypes = PlanType.values();
        float[] baseAmounts = {99.99f, 149.99f, 199.99f, 249.99f, 299.99f, 399.99f, 499.99f};

        Random random = new Random(42);
        for (long i = existingOrders; i < TARGET_ORDERS; i++) {
            Student student = students.get((int) (i % students.size()));
            Discount discount = (i % 4 == 0 || discounts.isEmpty()) ? null : discounts.get((int) (i % discounts.size()));

            float initialAmount = baseAmounts[(int) (i % baseAmounts.length)];
            float finalAmount = Order.calculateAmount(initialAmount, discount);

            Order order = new Order();
            order.setCode(String.format("ORD-%06d", i + 1));
            order.setType(orderTypes[(int) (i % orderTypes.length)]);
            order.setStatus(orderStatuses[(int) (i % orderStatuses.length)]);
            order.setPlanType(planTypes[(int) (i % planTypes.length)]);
            order.setDate(LocalDateTime.now().minusDays((TARGET_ORDERS - i) + random.nextInt(7)));
            order.setAmount(finalAmount);
            order.setDiscount(discount);
            order.setStudent(student);
            order.setTransactionId("TRX-" + (100000 + i));

            Order savedOrder = orderRepository.save(order);

            Subscription subscription = new Subscription();
            subscription.setStartDate(savedOrder.getDate());
            subscription.setEndDate(savedOrder.getDate().plusMonths(1 + (i % 12)));
            subscription.setOrder(savedOrder);
            subscriptionRepository.save(subscription);
        }
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
