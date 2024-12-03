# 기능 목록

## 보유 상품 출력

- [x]  보유 상품 출력
- [x]  파일 입력
    - `src/main/resources/products.md`과 `src/main/resources/promotions.md` 파일을 이용한다.
    - 두 파일 모두 내용의 형식을 유지한다면 값은 수정할 수 있다.
- [x]  프로모션 재고만 있을 경우 일반 재고는 재고없음을 출력해야한다.
- [x]  환영 인사와 함께 상품명, 가격, 프로모션 이름, 재고를 안내한다.
    - 만약 재고가 0개라면 `재고 없음`을 출력한다.
    - 프로모션 O → 프로모션 X 순서
        - 프로모션이 없는 것도 있음
    - 가나다 순 X, 파일 저장 순서
        - 파일에 프로모션만 있으면 일반 재고는 재고 없음으로 표시
            - 일반 재고는 무조건 출력
        - 일반 재고만 있으면 일반 재고만 출력

```json
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])

```

### 도메인

- Product
    - 상품명, 가격
    - 프로모션
- 상품과 프로모션은 항상 함께 관리되기 때문
- Inventory : 전체 상품 재고 관리
    - `Map<String, ProductStock> inventory;`
        - 상품명 - 재고
- 상품명으로 재고를 확인하기 때문
- `ProductStock` : 개별 상품 재고 관리
    - 상품(Product)
    - 프로모션(객체)
        - 굳이 동기화할 필요 없으므로 객체로 참조
    - 재고(stock)
- 상품과 재고는 항상 같이 관리되기 때문
- stock
    - 프로모션 재고/ 일반 재고

### 프로모션

- 프로모션 이름 → 식별자
- 구매 수량
- 증정 수량
- 시작 날짜
- 종료 날짜

## 구매 상품, 수량 입력

- [x]  구매 상품명, 수량 입력
    - [x]  `[콜라-10],[사이다-3]`
- [x]  예외
    - [x]  주문 형식이 아닐때
    - [x]  상품명
        - [x]  형식
        - [x]  존재하지 않은 상품명
        - [x]  중복된 상품명
    - [x]  수량
        - [x]  숫자 형식이 아닐때
        - [x]  0 이하일때
        - [x]  재고보다 구매 수량이 많을때
- 예외 메세지
    - `Exception`이 아닌 `IllegalArgumentException`, `IllegalStateException` 등과 같은 명확한 유형을 처리한다.
    - 구매할 상품과 수량 형식이 올바르지 않은 경우: `[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.`
    - 존재하지 않는 상품을 입력한 경우: `[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.`
    - 기타 잘못된 입력의 경우: `[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.`

## 프로모션 할인 계산

- 예외 메세지
    - `Exception`이 아닌 `IllegalArgumentException`, `IllegalStateException` 등과 같은 명확한 유형을 처리한다.
    - 구매 수량이 재고 수량을 초과한 경우: `[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.`
    - 기타 잘못된 입력의 경우: `[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.`

### 프로모션 할인

- [x]  오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용
- [x]  N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태
    - [x]  1+1 또는 2+1 프로모션
    - [x]  **동일 상품에 여러 프로모션이 적용되지 않는다.**
- [x]  프로모션 재고 내에서만 적용된다.
    - [x]  프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.
    - ex ) 2+1 프로모션 : 프로모션 재고가 8개이고 일반 재고가 충분할 경우 프로모션 적용은 6개만 된다.
1. 프로모션 기간 확인 : Product에게 프로모션 기간 내이니?
    1. 기간 내 : 프로모션 재고 → 일반 재고 사용
    2. 기간 외 : 일반 재고 사용
2. 프로모션 상품 구매
    1. 수량 검증 → Inventory
        1. 프로모션 적용 가능시 고객이 수량보다 적게 가져온 경우 안내
        2. 프로모션 재고 부족시 일부 수량에 대해 정가 결제 안내
3. 일반 상품 구매
    1. 일반 재고 사용
- [x]  프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내한다.
    - ex) 1+1 프로모션 : 1개를 가져온 경우 1개를 안내한다.
        - 3개를 가져온 경우 1개를 안내한다.
    - ex) 2+1 프로모션 : 2개를 가져온 경우 1개를 안내한다.
        - 5개를 가져온 경우 1개를 안내한다. (5-3 == 2 → 1개 증정)
        - 8개 (8-3*2==2 → 1개 증정)
        - `n % (buy+get) == buy`
    - [x]  혜택 수량까지 구매시 재고가 부족한 경우 안내하지 않는다.
    - [x]  안내 후 재고를 처리한다.

```json
현재 {상품명}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
```

- [x]  프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.
    - ex ) 1+1 프로모션 : 프로모션 재고 5, 일반 재고 충분할 경우, 6개 구매시 2개는 정가로 결제해야함을 안내한다. `(6- (5 / 2 **2)`
    - ex ) 2+1 프로모션 : 프로모션 재고 5, 일반 재고 충분할 경우, 6개 구매시 3개는 정가로 결제해야함을 안내한다. 6-5/3*3 → 3
    - `(구매할 수량 - (프로모션 재고 /(buy+get)*(buy+get)`
        - 10 - 7/3*3 = 4
        - [x]  안내 후 결과를 처리한다.

```json
현재 {상품명} {수량}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
```

### 재고 관리

- [x]  각 상품의 재고 수량을 고려 → 결제 가능 여부 확인
    - [x]  고객이 상품을 구매할 때마다, 결제된 수량만큼 해당 상품의 재고에서 차감

### 도메인

- PromotionProcessor : 프로모션 적용 로직
    - ProductStock 필드
        - 상품, 재고, 프로모션 관리

## 멤버십 계산

- [x]  멤버십 할인 여부 입력
- [x]  예외
    - [x]  Y 또는 N 문자가 아닐때
- [x]  멤버십 계산

### 멤버십 할인

- [x]  프로모션 미적용 금액의 **30%**를 할인
    - [x]  프**로모션 적용 후 남은 금액**에 대해 **멤버십 할인을 적용**
    - [x]  프로모션 혜택이 없거나 부분적으로 없을 경우만 멤버십 비용에 포함된다.
- [x]  멤버십 할인의 최대 한도는 **8,000원**

```json
멤버십 할인을 받으시겠습니까? (Y/N)
```

### 도메인

- MembershipCalculator

## 영수증 출력

- [x]  영수증 출력

### 영수증

- [x]  구매 내역과 산출한 금액 정보
    - 고객의 구매 내역과 할인을 요약
- [x]  최종 결제 금액 : 사용자가 입력한 상품의 가격과 수량을 기반
    - [x]  총구매액 : 상품별 가격 * 수량
    - [x]  최종 결제 금액은 총 구매액에 프로모션, 멤버십 할인 정책 반영
- 영수증 항목
    - [x]  구매 상품 내역: 구매한 상품명, 수량, 가격
        - 구매 수량 + 증정 수량
    - [x]  증정 상품 내역: 프로모션에 따라 무료로 제공된 증정 상품의 목록
        - 증정 수량
    - 금액 정보
        - [x]  총구매액: 구매한 상품의 총 수량과 총 금액
        - [x]  행사할인: **프로모션**에 의해 할인된 금액
            - 증정품 금액
        - [x]  멤버십할인: **멤버십**에 의해 추가로 할인된 금액
            - 프로모션 미적용 금액 30%
            - 정가로 결제 : 프로모션 미적용에 포함
            - 8000원 미만
        - [x]  내실돈: **최종 결제 금액**
            - 총구매액 - 행사할인 - 멤버십할인

```json
==============W 편의점================
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
=============증	정===============
콜라		1
====================================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000

```

### 도메인

- receipt

## 재입력

- [x]  재입력 여부 입력
    - [x]  영수증 출력 후 추가 구매를 진행할지 또는 종료할지를 선택할 수 있다.
- [x]  Y 시 결제 재시도 수행
    - [x]  재고 초기화 X
- [x]  N 시 종료
- [x]  예외
    - [x]  Y또는 N 문자가 아닐 경우

### 재입력

- 사용자가 잘못된 값을 입력할 경우 `IllegalArgumentException`를 발생시키고, "[ERROR]"로 시작하는 에러 메시지를 출력 후 그 부분부터 입력을 다시 받는다.

```json
감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
```

### 예상 출력

```json
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-3],[에너지바-5]

멤버십 할인을 받으시겠습니까? (Y/N)
Y

==============W 편의점================
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
=============증	정===============
콜라		1
====================================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 7개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-10]

현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
N

==============W 편의점================
상품명		수량	금액
콜라		10 	10,000
=============증	정===============
콜라		2
====================================
총구매액		10	10,000
행사할인			-2,000
멤버십할인			-0
내실돈			 8,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 재고 없음 탄산2+1
- 콜라 1,000원 7개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[오렌지주스-1]

현재 오렌지주스은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
Y

==============W 편의점================
상품명		수량	금액
오렌지주스		2 	3,600
=============증	정===============
오렌지주스		1
====================================
총구매액		2	3,600
행사할인			-1,800
멤버십할인			-0
내실돈			 1,800

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
N

```

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/cddf8fe3-af24-40a9-86fe-9002941d2cc5/c2ab2964-f70b-4153-9d0c-6cdc197e5ad6/image.png)
