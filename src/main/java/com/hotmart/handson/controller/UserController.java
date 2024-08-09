package com.hotmart.handson.controller;

import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.model.User;
import com.hotmart.handson.service.RelationshipService;
import com.hotmart.handson.service.UserService;
import com.hotmart.handson.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RelationshipService relationshipService;
    private final ModelMapper modelMapper;

    @PostMapping(
            value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(schema = @Schema(implementation = UserVO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username already exists",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public UserVO createUser(@RequestBody @Valid UserCreateVO request) {
        var profile = modelMapper.map(request, User.class);
        return modelMapper.map(userService.create(profile), UserVO.class);
    }

    @GetMapping(
            value = "/users/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by username", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = UserVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public UserVO getUser(@PathVariable("username") String username,
                          @RequestParam(value = "user", required = false) String followerUsername) throws Throwable {

        User user = userService.findByUsernameOrThrow(username, EntityNotFoundException::new);
        Optional<User> follower = Optional.empty();
        if (followerUsername != null) {
            follower = Optional.of(userService.findByUsernameOrThrow(followerUsername, EntityNotFoundException::new));
        }

        if(follower.isPresent()) {
            Optional<Relationship> relationship = relationshipService.findByUserAndFollower(user.getUuid(), follower.get().getUuid());
            user.setFollowing(relationship.isPresent());
        }

        return modelMapper.map(user, UserVO.class);
    }

    @GetMapping(
            value = "/users/{username}/stats",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's statistics", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = UserVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public SseEmitter getUserStats(@PathVariable("username") String username) throws Throwable {
        User user = userService.findByUsernameOrThrow(username, EntityNotFoundException::new);

        SseEmitter emitter = new SseEmitter();

        try {
            emitter.send(modelMapper.map(user.getStats(), UserStatsVO.class));
            emitter.complete();
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }

        return emitter;
    }
}
