package winter.controller;

import winter.annotation.Controller;
import winter.annotation.ModelAttribute;
import winter.annotation.RequestMapping;
import winter.form.ValidatedUserForm;
import winter.validation.BindingResult;
import winter.validation.Valid;
import winter.view.ModelAndView;

/**
 * 검증 기능을 테스트하는 컨트롤러
 */
@Controller
public class ValidationController {

    /**
     * 검증 폼 페이지 표시
     */
    @RequestMapping("/validation/form")
    public ModelAndView showValidationForm() {
        ModelAndView mv = new ModelAndView("validation-form");
        mv.addAttribute("userForm", new ValidatedUserForm());
        return mv;
    }

    /**
     * 사용자 등록 처리 (검증 포함)
     * @Valid 어노테이션으로 자동 검증 수행
     * BindingResult로 검증 결과 수신
     */
    @RequestMapping(value = "/validation/register", method = "POST")
    public ModelAndView registerUser(@Valid @ModelAttribute ValidatedUserForm userForm,
                                     BindingResult bindingResult) {

        // 검증 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("validation-form");
            mv.addAttribute("userForm", userForm);
            mv.addAttribute("errors", bindingResult.getFieldErrorMessages());
            mv.addAttribute("hasErrors", true);
            mv.addAttribute("errorCount", bindingResult.getErrorCount());
            return mv;
        }

        // 검증 성공 시
        ModelAndView mv = new ModelAndView("validation-success");
        mv.addAttribute("user", userForm);
        mv.addAttribute("message", "사용자 등록이 완료되었습니다!");
        return mv;
    }

    /**
     * AJAX를 위한 검증 API
     * JSON 형태로 검증 결과 반환
     */
    @RequestMapping(value = "/api/validation/check", method = "POST")
    public ModelAndView validateUserApi(@Valid @ModelAttribute ValidatedUserForm userForm,
                                        BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView("json");

        if (bindingResult.hasErrors()) {
            mv.addAttribute("success", false);
            mv.addAttribute("errors", bindingResult.getFieldErrorMessages());
            mv.addAttribute("errorCount", bindingResult.getErrorCount());
        } else {
            mv.addAttribute("success", true);
            mv.addAttribute("message", "검증 성공");
        }

        return mv;
    }

    /**
     * 개별 필드 검증 예제
     */
    @RequestMapping("/validation/field")
    public ModelAndView validateField() {
        ModelAndView mv = new ModelAndView("field-validation");
        return mv;
    }

    /**
     * 커스텀 검증 로직이 포함된 예제
     */
    @RequestMapping(value = "/validation/custom", method = "POST")
    public ModelAndView customValidation(@Valid @ModelAttribute ValidatedUserForm userForm,
                                         BindingResult bindingResult) {
        // 기본 어노테이션 검증 후 추가 비즈니스 로직 검증

        // 이메일 중복 체크 (예제)
        if (userForm.getEmail() != null && userForm.getEmail().equals("admin@example.com")) {
            bindingResult.addFieldError("email", userForm.getEmail(), "이미 사용 중인 이메일입니다");
        }

        // 비밀번호 확인 로직 (예제)
        if (userForm.getPassword() != null && userForm.getPassword().equals("password")) {
            bindingResult.addFieldError("password", userForm.getPassword(), "너무 간단한 비밀번호입니다");
        }

        // 나이 범위 체크 (예제)
        if (userForm.getAge() != null && (userForm.getAge() < 14 || userForm.getAge() > 100)) {
            bindingResult.addFieldError("age", userForm.getAge(), "나이는 14세 이상 100세 이하여야 합니다");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("validation-form");
            mv.addAttribute("userForm", userForm);
            mv.addAttribute("errors", bindingResult.getFieldErrorMessages());
            mv.addAttribute("hasErrors", true);
            mv.addAttribute("customValidation", true);
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("validation-success");
            mv.addAttribute("user", userForm);
            mv.addAttribute("message", "커스텀 검증을 포함한 등록이 완료되었습니다!");
            return mv;
        }
    }

    /**
     * 검증 통계 페이지
     */
    @RequestMapping("/validation/stats")
    public ModelAndView validationStats() {
        ModelAndView mv = new ModelAndView("validation-stats");

        // 검증 통계 정보 (예제 데이터)
        mv.addAttribute("totalValidations", 150);
        mv.addAttribute("successfulValidations", 120);
        mv.addAttribute("failedValidations", 30);
        mv.addAttribute("mostCommonErrors", new String[]{
                "이메일 형식 오류", "비밀번호 길이 부족", "필수 필드 누락"
        });

        return mv;
    }
}