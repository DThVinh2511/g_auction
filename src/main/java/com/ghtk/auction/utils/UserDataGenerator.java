package com.ghtk.auction.utils;

import com.ghtk.auction.repository.UserRepository;
import com.github.javafaker.Faker;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserGender;
import com.ghtk.auction.enums.UserRole;
import com.ghtk.auction.enums.UserStatus;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;


@RequiredArgsConstructor
public class UserDataGenerator {

    private final UserRepository userRepository;

    public void fakeData() {
        Faker faker = new Faker();
        List<User> users = new ArrayList<>();

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Set the maximum date to December 31, 2002
        LocalDate maxDate = LocalDate.of(2002, 12, 31);

        // Set the minimum date to 100 years ago to ensure the user is at least 20 years old
        LocalDate minDate = maxDate.minusYears(20);

        for (int i = 0; i < 100; i++) {
            // Generate a random date of birth before 2003
            LocalDate dateOfBirth = generateRandomDateOfBirth(minDate, maxDate);

            // Generate Vietnamese-specific data
            String fullName = generateVietnameseName();
            String email = generateEmail(fullName);
            String phoneNumber = "09" + faker.number().digits(8);
            String address = generateVietnameseAddress();

            User user = User.builder()
                    .email(email)
                    .password(faker.internet().password())
                    .fullName(fullName)
                    .dateOfBirth(dateOfBirth)
                    .gender(faker.bool().bool() ? UserGender.MALE : UserGender.FEMALE)
                    .address(address)
                    .statusAccount(UserStatus.values()[faker.number().numberBetween(0, UserStatus.values().length)])
                    .avatar("v1724396340/user-avatarsvgpng.png")
                    .createdAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 365)))
                    .role(UserRole.USER)
                    .isVerified(true)
                    .phone(phoneNumber)
                    .build();
            userRepository.save(user);
        }

        // Print or save users to the database
        users.forEach(user -> System.out.println(user.toString()));
    }

    private static LocalDate generateRandomDateOfBirth(LocalDate minDate, LocalDate maxDate) {
        // Ensure minDate is before maxDate
        if (minDate.isAfter(maxDate)) {
            throw new IllegalArgumentException("minDate must be before maxDate");
        }

        // Calculate the number of days between minDate and maxDate
        long daysBetween = maxDate.toEpochDay() - minDate.toEpochDay();

        // Ensure that daysBetween is positive
        if (daysBetween <= 0) {
            throw new IllegalArgumentException("daysBetween must be positive");
        }

        // Generate a random date of birth between minDate and maxDate
        long randomDays = ThreadLocalRandom.current().nextLong(daysBetween + 1); // +1 to include the end date
        return minDate.plusDays(randomDays);
    }

    private static String generateVietnameseName() {
        // Lists of Vietnamese last names, middle names, and first names
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Huỳnh", "Vũ", "Đỗ", "Bùi", "Đặng", "Hồ"};
        String[] middleNames = {"Thị", "Văn", "Như", "Đức", "Quốc", "Trung", "Hữu"};
        String[] firstNames = {"An", "Bích", "Cường", "Dũng", "Giang", "Hà", "Hùng", "Khoa", "Lan", "Mai"};

        Faker faker = new Faker();
        String lastName = lastNames[faker.number().numberBetween(0, lastNames.length)];
        String middleName = middleNames[faker.number().numberBetween(0, middleNames.length)];
        String firstName = firstNames[faker.number().numberBetween(0, firstNames.length)];

        return lastName + " " + middleName + " " + firstName;
    }

    private static String generateEmail(String fullName) {
        // Split the full name into components
        String[] nameParts = fullName.split(" ");

        if (nameParts.length < 2) {
            // Return a default email if the name is not complete
            return "user@example.vn";
        }

        // Extract last name, middle name, and first name
        String lastName = nameParts[0];
        String middleName = nameParts[1];
        String firstName = nameParts[nameParts.length - 1];

        // Generate email in the format: firstname_lastname@example.vn
        // Normalize and remove accents from names
        String emailFirstName = removeAccents(firstName.toLowerCase());
        String emailMiddleName = removeAccents(middleName.toLowerCase().substring(0, 1));
        String emailLastName = removeAccents(lastName.toLowerCase().substring(0, 1));

        String email = emailFirstName + emailMiddleName + emailLastName + "@example.vn";

        return email;
    }

    private static String removeAccents(String input) {
        // Remove accents from characters
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(normalized).replaceAll("").replace('đ', 'd');
    }
    private static String generateVietnameseAddress() {
        // Lists of Vietnamese provinces and districts
        String[] provinces = {"Hà Nội", "Hồ Chí Minh", "Hải Phòng", "Đà Nẵng", "Cần Thơ", "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Ninh", "Bến Tre"};
        String[] districts = {"Ba Đình", "Hoàn Kiếm", "Hồng Bàng", "Ngũ Hành Sơn", "Cái Răng", "Thủ Đức", "Tân Bình", "Gò Vấp", "Tân An", "Long Biên"};
        String[] streets = {"Phố Huế", "Hàng Bông", "Nguyễn Trãi", "Lê Duẩn", "Hàm Nghi", "Nguyễn Văn Cừ", "Trần Hưng Đạo", "Bùi Thị Xuân", "Chùa Bộc", "Láng Hạ"};

        Faker faker = new Faker();
        String province = provinces[faker.number().numberBetween(0, provinces.length)];
        String district = districts[faker.number().numberBetween(0, districts.length)];
        String street = streets[faker.number().numberBetween(0, streets.length)];
        String houseNumber = faker.number().digits(3);

        // Generate a full address
        return houseNumber + " " + street + ", " + district + ", " + province;
    }
}
