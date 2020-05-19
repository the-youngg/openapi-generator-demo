package com.sendroids.openapi.api;

import com.sendroids.openapi.domain.Auth;
import com.sendroids.openapi.domain.User;
import com.sendroids.openapi.service.UserService;
import com.sendroids.openapi.service.impl.UserDetailsServiceImpl;
import io.swagger.annotations.ApiParam;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-18T16:51:45.956642+08:00[Asia/Shanghai]")

@Controller
public class UserApiController implements UserApi {

    private final NativeWebRequest request;
    private final UserService userService;
    private UserDetailsServiceImpl userDetailsService;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(NativeWebRequest request, UserService userService, UserDetailsServiceImpl userDetailsService) {
        this.request = request;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }



    @Override
    public ResponseEntity<Auth> issueToken(
            @NotNull @Valid final @RequestParam(value = "username", required = true) String username,
            @NotNull @Valid @RequestParam(value = "password", required = true) String password
    ) {
        val dbDser = userDetailsService.getUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("无效的用户名")
        );
        String token = userDetailsService.issueToken(dbDser);
        Auth auth = new Auth();
        auth.setUserId(dbDser.getId());
        auth.setToken(token);

        return ResponseEntity.ok().body(auth);
    }

    @Override
    public ResponseEntity<User> getUserById(
            @PathVariable Long userId
    ) {
        val dbUser = userService.getUserById(userId).get();
        return ResponseEntity.ok().body(dbUser);
    }

    @Override
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        val dbUser = userService.save(user);
        return ResponseEntity.ok().body(dbUser);
    }
}
