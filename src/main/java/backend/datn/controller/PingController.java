package backend.datn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class PingController {

    @RequestMapping
    public String ping() {
        return "ping ping pong pong";
    }
}
