package winter.dispatcher;

import winter.view.ModelAndView;

/*
* 모든 컨트롤러는 이 인터페이스를 구현해야 하며,
* 논리 뷰 이름과 모델 데이터를 함께 담은 ModelAndView를 반환한다.*/
public interface Controller {
    ModelAndView handle();
}
