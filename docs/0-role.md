# 📘 0-role.md

## 🧭 Git 브랜치 전략 및 작업 흐름 규칙

Winter 프레임워크를 기능 단위로 구조적으로 구현하기 위해, **기능 중심 브랜치 전략**을 사용한다. 각 기능은 독립된 브랜치에서 구현 후 `main` 브랜치로 머지하고, 다음 기능은 `main`에서 새 브랜치를 파서 시작한다.

---

## ✅ 기본 브랜치 흐름

### 1. 기능 브랜치 생성 및 작업

```bash
git checkout -b <feature-branch-name>
# 예: git checkout -b dispatcher
```

### 2. 커밋

```bash
git add .
git commit -m "feat: <설명>"
# 예: git commit -m "feat: 기본 Dispatcher 구조 구현"
```

### 3. `main` 브랜치로 머지

```bash
git checkout main
git merge <feature-branch-name>
git push origin main
```

### 4. 다음 기능 브랜치 생성 (main 기준)

```bash
git checkout -b <next-feature-branch>
# 예: git checkout -b handler-mapping
```

---

## 📁 브랜치/파일명 예시

| 기능                  | 브랜치명                 | 문서 파일명                    |
| ------------------- | -------------------- | ------------------------- |
| Dispatcher 구현       | dispatcher           | docs/1-DISPATCHER.md      |
| HandlerMapping 분리   | handler-mapping      | docs/2-HANDLER-MAPPING.md |
| Controller 인터페이스 설계 | controller-interface | docs/3-CONTROLLER.md      |

---

## 🎯 목적

* 브랜치 단위 책임 분리 (SRP)
* 각 기능마다 문서로 기록 (기능-문서 1:1 대응)
* 프레임워크 설계 흐름의 히스토리화

---

이 규칙은 Winter 프로젝트 전체 기간 동안 유지하며, 모든 구조적 기능 구현의 기준으로 삼는다.
