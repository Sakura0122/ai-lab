package com.sakura.comtroller;

import com.sakura.service.CodeReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/template")
public class TemplateCodeReviewController {

    private final CodeReviewService codeReviewService;

    public TemplateCodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping("/review")
    public String review(
            @RequestParam(defaultValue = "Java") String language,
            @RequestParam String code
    ) {
        return codeReviewService.review(language, code);
    }
}