package com.pharmacy.service;

import com.pharmacy.config.JwtUtils;
import com.pharmacy.dto.AuthResponse;
import com.pharmacy.dto.RegisterRequest;
import com.pharmacy.entity.User;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import com.pharmacy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerNormalizesIdentifiersBeforeSavingAccount() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("  newcustomer  ");
        request.setEmail("  NewCustomer@Example.com ");
        request.setPassword("secure-password");
        request.setFullName("  New Customer ");
        request.setPhone(" 1234567890 ");

        when(userRepository.existsByUsername("newcustomer")).thenReturn(false);
        when(userRepository.existsByEmail("newcustomer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secure-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });
        when(jwtUtils.generateToken("newcustomer", "CUSTOMER")).thenReturn("token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertEquals("newcustomer", savedUser.getValue().getUsername());
        assertEquals("newcustomer@example.com", savedUser.getValue().getEmail());
        assertEquals("New Customer", savedUser.getValue().getFullName());
        assertEquals("1234567890", savedUser.getValue().getPhone());
        assertEquals("newcustomer", response.getUsername());
        assertEquals("newcustomer@example.com", response.getEmail());
    }
}
