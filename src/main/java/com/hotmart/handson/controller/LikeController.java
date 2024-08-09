package com.hotmart.handson.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotmart.handson.model.Like;
import com.hotmart.handson.service.LikeService;
import com.hotmart.handson.vo.ErrorVO;
import com.hotmart.handson.vo.LikeCreateVO;
import com.hotmart.handson.vo.LikeVO;
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
public class LikeController {

    private final LikeService likeService;

    private final ObjectMapper objectMapper;

    @PostMapping("/tweets/{tweet}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Unfollow an user", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(schema = @Schema(implementation = Like.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tweet or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public LikeVO like(@PathVariable("tweet") String tweetId,
                             @RequestBody @Valid LikeCreateVO request) throws Throwable {

        return objectMapper.convertValue(likeService.like(tweetId, request.getUser()), LikeVO.class);
    }

    /**
     * TODO Challenge #2 - Implement tweet dislike
     */
    @DeleteMapping("/tweets/{tweet}/likes/{username}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Unfollow an user", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(schema = @Schema(implementation = Like.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tweet or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public LikeVO dislike(@PathVariable("tweet") String tweetId,
                              @PathVariable("username") String username) throws Throwable {

        return null;
    }
}
