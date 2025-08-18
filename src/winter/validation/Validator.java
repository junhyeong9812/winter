package winter.validation;

/**
 * 객체 검증을 수행하는 인터페이스
 * Spring의 Validator 인터페이스와 유사한 구조
 */
public interface Validator {

    /**
     * 이 검증기가 주어진 클래스를 지원하는지 확인
     *
     * @param clazz 검증할 객체의 클래스
     * @return 지원 가능하면 true
     */
    boolean supports(Class<?> clazz);

    /**
     * 객체를 검증하고 결과를 BindingResult에 저장
     *
     * @param target 검증할 객체
     * @param bindingResult 검증 결과를 저장할 객체
     */
    void validate(Object target, BindingResult bindingResult);
}