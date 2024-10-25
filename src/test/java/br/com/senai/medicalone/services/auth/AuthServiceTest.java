package br.com.senai.medicalone.services.auth;

import br.com.senai.medicalone.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;

    @BeforeEach
    public void setUp() {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        user = new User();
    }

    @Test
    public void testGetAuthenticatedPatientId_Success() {
        user.setPatientId(1L);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long patientId = AuthService.getAuthenticatedPatientId();
            assertNotNull(patientId);
            assertEquals(1L, patientId);
        }
    }

    @Test
    public void testGetAuthenticatedPatientId_NoPatientAssociated() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(RuntimeException.class, AuthService::getAuthenticatedPatientId);
        }
    }

    @Test
    public void testGetAuthenticatedPatientId_UserNotFound() {
        when(authentication.getPrincipal()).thenReturn(new Object());
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(UsernameNotFoundException.class, AuthService::getAuthenticatedPatientId);
        }
    }
}