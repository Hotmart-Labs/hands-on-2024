package com.hotmart.handson.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.service.RelationshipService;
import com.hotmart.handson.vo.ErrorVO;
import com.hotmart.handson.vo.RelationshipCreateVO;
import com.hotmart.handson.vo.RelationshipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    private final ObjectMapper objectMapper;

    @PostMapping("/users/{username}/relationships")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Follow an user", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(schema = @Schema(implementation = Relationship.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or follower not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public RelationshipVO follow(@PathVariable("username") String username,
                                       @RequestBody @Valid RelationshipCreateVO request) throws Throwable {

        return objectMapper.convertValue(relationshipService.follow(username, request.getFollower()), RelationshipVO.class);
    }

    @DeleteMapping("/users/{username}/relationships/{follower}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Unfollow an user", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = Relationship.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or follower not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public RelationshipVO unfollow(@PathVariable("username") String user,
                                         @PathVariable("follower") String follower) throws Throwable {

        return objectMapper.convertValue(relationshipService.unfollow(user, follower), RelationshipVO.class);
    }
}
