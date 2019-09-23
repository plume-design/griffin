/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.apache.griffin.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;

public class SecurityConfig {

    @Configuration
    @EnableWebSecurity
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

        private static final Logger LOGGER = LoggerFactory
        .getLogger(SecurityConfig.class);

        @Value("${login.strategy}") private String strategy;
        @Value("${ldap.url}") private String url;
        @Value("${ldap.userSearchBase}") private String userSearchBase;
        @Value("${ldap.userSearchPattern}") private String userSearchPattern;
        @Value("${ldap.groupSearchBase}") private String groupSearchBase;
        @Value("${ldap.groupSearchPattern}") private String groupSearchPattern;
        @Value("${ldap.bindDN:}") private String managerDn;
        @Value("${ldap.bindPassword:}") private String managerPassword;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            switch (strategy) {
                case "ldap":
                    http.authorizeRequests()
                        .antMatchers(
                            "/",
                            "/index.html",
                            "/*.ico", "/*.eot", "/*.svg", "/*.ttf", "/*.woff", "/*.woff2",
                            "/*bundle*",
                            "/assets/**",
                            "/login").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    break;
                default:
                    http.csrf().disable().authorizeRequests().anyRequest().anonymous().and().httpBasic().disable();
            }
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            try {
                auth.ldapAuthentication()
                    .userDnPatterns("uid={0}")
                    .userSearchBase(userSearchBase)
                    .userSearchFilter(userSearchPattern)
                    .groupSearchBase(groupSearchBase)
                    .groupSearchFilter(groupSearchPattern)
                    .contextSource()
                    .url(url)
                    .managerDn(managerDn)
                    .managerPassword(managerPassword);
            }
            catch (Exception ex) {
                LOGGER.error("Authentication exception: ", ex);
                throw ex;
            }
        }

        @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

}
