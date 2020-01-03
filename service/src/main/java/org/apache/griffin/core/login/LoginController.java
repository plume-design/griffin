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

package org.apache.griffin.core.login;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class LoginController {
    @Value("${login.strategy}") private String strategy;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> map) {
        switch (strategy) {
            case "ldap":
                String user = map.get("username");
                String pass = map.get("password");
                if(!this.authenticate(user, pass)){
                    return null;
                }
        }
        // anonymous ("default") or authenticated user ("ldap")
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> message = new HashMap<>();
        message.put("griffinUser", auth.getName());
        message.put("status", 0);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private boolean authenticate(String user, String pass){
        try {
            UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(user, pass);
            Authentication ldapAuth = authenticationManager.authenticate(authReq);
            if (ldapAuth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(ldapAuth);
                return true;
            }
        } catch (Exception e) { }
        return false;
    }
}
