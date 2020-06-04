package de.pdmitriev.test.staffbase.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.savedrequest.NullRequestCache


@Configuration
@EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authenticationProvider(authProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/**").authenticated()
                    .antMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .and().requestCache().requestCache(NullRequestCache())
                .and().httpBasic().authenticationEntryPoint(Http403ForbiddenEntryPoint())
                .and().csrf().disable()
    }

    @Bean
    fun authProvider(): AuthenticationProvider {
        return DummyAuthenticationProvider()
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return DummyUserDetailsService()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    class DummyUserDetailsService: UserDetailsService {
        override fun loadUserByUsername(username: String?): UserDetails {
            return User
                    .builder()
                    .username(username)
                    .password("")
                    .build()
        }

    }

    class DummyAuthenticationProvider : AuthenticationProvider {
        @Throws(AuthenticationException::class)
        override fun authenticate(authentication: Authentication): Authentication? {
            val name: String = authentication.getName()
            return UsernamePasswordAuthenticationToken(name, "")
        }

        override fun supports(authentication: Class<*>): Boolean {
            return authentication == UsernamePasswordAuthenticationToken::class.java
        }
    }
}