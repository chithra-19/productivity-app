package com.climbup.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return user.isAccountNonExpired(); }
    @Override
    public boolean isAccountNonLocked() { return user.isAccountNonLocked(); }
    @Override
    public boolean isCredentialsNonExpired() { return user.isCredentialsNonExpired(); }
    @Override
    public boolean isEnabled() { return user.isEnabled(); }

    public User getUser() {
        return user;
    }
}
