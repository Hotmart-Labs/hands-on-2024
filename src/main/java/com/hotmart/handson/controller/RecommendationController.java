package com.hotmart.handson.controller;

import com.hotmart.handson.model.User;
import com.hotmart.handson.service.RecommendationService;
import com.hotmart.handson.vo.ErrorVO;
import com.hotmart.handson.vo.UserStatsVO;
import com.hotmart.handson.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    private final ModelMapper modelMapper;

    @GetMapping(
            value = "/users/latest",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get latest created users", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = UserVO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unknown error",
                    content = @Content(schema = @Schema(implementation = ErrorVO.class))
            )
    })
    public SseEmitter getLatestUsers(@RequestParam("username") String username) throws Throwable {
        List<User> users = recommendationService.findLatestUsers(username);
        SseEmitter emitter = new SseEmitter();

        try {
            for (User user : users) {
                emitter.send(modelMapper.map(user, UserVO.class));
            }
            emitter.complete();
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }

        return emitter;
    }
}
