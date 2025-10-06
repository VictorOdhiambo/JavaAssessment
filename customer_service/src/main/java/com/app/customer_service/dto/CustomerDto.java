package com.app.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

        private UUID id;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Identity number is required")
        private String identityNumber;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotNull(message = "DOB is required")
        private LocalDate dob;

        private LocalDateTime createdDate;

        private int status;

        private String verificationCode;

        private String hashPassword;

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public @NotBlank(message = "First name is required") String getFirstName() {
                return firstName;
        }

        public void setFirstName(@NotBlank(message = "First name is required") String firstName) {
                this.firstName = firstName;
        }

        public @NotBlank(message = "Last name is required") String getLastName() {
                return lastName;
        }

        public void setLastName(@NotBlank(message = "Last name is required") String lastName) {
                this.lastName = lastName;
        }

        public @NotBlank(message = "Identity number is required") String getIdentityNumber() {
                return identityNumber;
        }

        public void setIdentityNumber(@NotBlank(message = "Identity number is required") String identityNumber) {
                this.identityNumber = identityNumber;
        }

        public @NotBlank(message = "Phone number is required") String getPhoneNumber() {
                return phoneNumber;
        }

        public void setPhoneNumber(@NotBlank(message = "Phone number is required") String phoneNumber) {
                this.phoneNumber = phoneNumber;
        }

        public @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String getEmail() {
                return email;
        }

        public void setEmail(@Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email) {
                this.email = email;
        }

        public @NotNull(message = "DOB is required") LocalDate getDob() {
                return dob;
        }

        public void setDob(@NotNull(message = "DOB is required") LocalDate dob) {
                this.dob = dob;
        }

        public LocalDateTime getCreatedDate() {
                return createdDate;
        }

        public void setCreatedDate(LocalDateTime createdDate) {
                this.createdDate = createdDate;
        }

        public int getStatus() {
                return status;
        }

        public void setStatus(int status) {
                this.status = status;
        }

        public String getVerificationCode() {
                return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
                this.verificationCode = verificationCode;
        }

        public String getHashPassword() {
                return hashPassword;
        }

        public void setHashPassword(String hashPassword) {
                this.hashPassword = hashPassword;
        }
}
