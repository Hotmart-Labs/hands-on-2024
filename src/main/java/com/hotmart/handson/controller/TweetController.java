package com.hotmart.handson.controller;

import com.hotmart.handson.model.Tweet;
import com.hotmart.handson.model.TweetRef;
import com.hotmart.handson.model.UserRef;
import com.hotmart.handson.service.TweetService;
import com.hotmart.handson.vo.ErrorVO;
import com.hotmart.handson.vo.TweetCreateVO;
import com.hotmart.handson.vo.TweetVO;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;
    private final ModelMapper modelMapper;

    @GetMapping(
            value = "/tweets",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's timeline", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = TweetVO.class))
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
    public SseEmitter getUserTimeline(@RequestParam("username") String username,
                                      @RequestParam("requester") String requester) throws Throwable {
        List<Tweet> tweets = tweetService.getUserTimeline(username, requester);
        List<TweetVO> tweetVoList = new ArrayList<>();

        for(Tweet tweet : tweets){
            tweetVoList.add(modelMapper.map(tweet, TweetVO.class));
        }

        tweetVoList.sort(Comparator.comparing(TweetVO::getCreatedAt));

        SseEmitter emitter = new SseEmitter();

        try {
            for (TweetVO tweetVO : tweetVoList) {
                emitter.send(tweetVO);
            }
            emitter.complete();
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    @PostMapping(
            value = "/tweets",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new tweet", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(schema = @Schema(implementation = TweetVO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
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
    public TweetVO create(@RequestPart("tweet") @Valid TweetCreateVO request,
                                @RequestPart(value = "media", required = false) MultipartFile media) throws Throwable {

        var tweet = Tweet.builder()
                .content(request.getContent())
                .author(UserRef.builder()
                        .username(request.getUsername())
                        .build())
                .build();

        if (request.getTweetReplied() != null) {
            tweet.setParent(TweetRef.builder()
                    .uuid(request.getTweetReplied())
                    .build());
        }

        return modelMapper.map(tweetService.create(tweet, Optional.ofNullable(media)), TweetVO.class);
    }

    /**
     * TODO Challenge #4 - Implement retweet
     */
}
