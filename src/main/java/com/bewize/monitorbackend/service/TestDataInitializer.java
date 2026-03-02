package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.school.Level;
import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.enums.*;
import com.bewize.monitorbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final LevelRepository levelRepository;
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (studentRepository.count() == 0) {
            initializeTestData();
        }
    }

    private void initializeTestData() {
        // Arrays of sample data for variety
        String[] firstNames = { "Ahmed", "Fatima", "Mohamed", "Khadija", "Youssef", "Amina", "Omar", "Salma", "Hassan",
                "Nadia",
                "Rachid", "Zineb", "Khalid", "Laila", "Amine", "Sara", "Mehdi", "Houda", "Samir", "Imane",
                "Karim", "Meryem", "Hamza", "Asmaa", "Adil", "Wafa", "Issam", "Hajar", "Taha", "Loubna" };
        String[] lastNames = { "Alami", "Bennani", "Tazi", "El Fassi", "Chraibi", "Benjelloun", "Kettani", "Berrada",
                "Sqalli", "Filali",
                "Bouzidi", "Mansouri", "Lahlou", "Cherkaoui", "Benkirane", "Amrani", "Idrissi", "Ziani", "Naciri",
                "Belkadi" };
        Cycle[] cycles = { Cycle.ELEMENTARY_SCHOOL, Cycle.MIDDLE_SCHOOL, Cycle.HIGH_SCHOOL };
        String[] levelNames = { "1ère année", "2ème année", "3ème année", "4ème année", "5ème année", "6ème année" };

        // Create multiple Levels
        Level[] levels = new Level[6];
        for (int i = 0; i < 6; i++) {
            Level level = new Level();
            level.setLevelName(levelNames[i]);
            level.setCycle(cycles[i % 3]);
            levels[i] = levelRepository.save(level);
        }

        // Create 50 Students for pagination testing
        Student[] students = new Student[50];
        for (int i = 0; i < 50; i++) {
            Student student = new Student();
            student.setCne(String.format("CNE%06d", i + 1));
            student.setFirstName(firstNames[i % firstNames.length]);
            student.setLastName(lastNames[i % lastNames.length]);
            student.setEmail(String.format("%s.%s%d@example.com",
                    firstNames[i % firstNames.length].toLowerCase(),
                    lastNames[i % lastNames.length].toLowerCase().replace(" ", ""), i));
            student.setPhone(String.format("06%08d", 10000000 + i));
            student.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            student.setSingupDate(java.sql.Date.valueOf(LocalDate.now().minusDays(i * 2)));
            student.setLevel(levels[i % levels.length]);
            student.setActive(i % 5 != 0); // 80% active
            students[i] = studentRepository.save(student);
        }

        // Create 30 Discounts for pagination testing
        String[] discountPrefixes = { "PROMO", "SUMMER", "WINTER", "SPRING", "FLASH", "VIP", "SPECIAL", "STUDENT",
                "BACK2SCHOOL", "HOLIDAY" };
        Discount[] discounts = new Discount[30];
        for (int i = 0; i < 30; i++) {
            Discount discount = new Discount();
            discount.setCode(String.format("%s%d", discountPrefixes[i % discountPrefixes.length], 2024 + (i / 10)));
            discount.setPercentage(5 + (i % 6) * 5); // 5%, 10%, 15%, 20%, 25%, 30%
            discount.setStartDate(LocalDateTime.now().minusDays(60 - i));
            discount.setEndDate(LocalDateTime.now().plusDays(30 + i * 2));
            discounts[i] = discountRepository.save(discount);
        }

        // Create 60 Subscriptions for pagination testing
        Subscription[] subscriptions = new Subscription[60];
        for (int i = 0; i < 60; i++) {
            Subscription subscription = new Subscription();
            subscription.setStartDate(LocalDateTime.now().minusDays(90 - i));
            subscription.setEndDate(LocalDateTime.now().plusMonths(3 + (i % 12)));
            subscriptions[i] = subscriptionRepository.save(subscription);
        }

        // Create 100 Orders for pagination testing
        OrderType[] orderTypes = OrderType.values();
        OrderStatus[] orderStatuses = OrderStatus.values();
        PlanType[] planTypes = PlanType.values();
        float[] amounts = { 0.0f, 99.99f, 149.99f, 199.99f, 299.99f, 399.99f, 499.99f, 599.99f };

        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setCode(String.format("ORD-%05d", i + 1));
            order.setType(orderTypes[i % orderTypes.length]);
            order.setStatus(orderStatuses[i % orderStatuses.length]);
            order.setPlanType(planTypes[i % planTypes.length]);
            order.setDate(LocalDateTime.now().minusDays(100 - i));
            order.setAmount(amounts[i % amounts.length]);
            order.setSubscription(subscriptions[i % subscriptions.length]);
            order.setStudent(students[i % students.length]);
            if (i % 3 != 0) { // 66% have discount
                order.setDiscount(discounts[i % discounts.length]);
            }
            orderRepository.save(order);
        }

        System.out.println("✅ Test data initialized successfully!");
        System.out.println("📊 Students: " + studentRepository.count());
        System.out.println("🎟️ Discounts: " + discountRepository.count());
        System.out.println("📦 Orders: " + orderRepository.count());
        System.out.println("🔄 Subscriptions: " + subscriptionRepository.count());
        System.out.println("📚 Levels: " + levelRepository.count());
    }
}