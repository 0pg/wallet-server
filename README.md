# Ethereum wallet api server

실행 커맨드 (server port: 8080 / db port: 5432)

```bash
docker-compose build
docker-compose up
```

패키지 구조
```bash
wallet-api
├── domain
│   ├── entity
│   │   └── event
│   ├── exception
│   └── program
└── server
    ├── adaptor
    ├── config
    ├── controller
    ├── entity
    ├── mapper
    ├── port
    ├── service
    ├── exception
    └── util
```

API 
- Transaction
  - `/transaction`
    - `/ POST` 송금 트랜잭션 생성 후 트랜잭션 해시 반환
      - `srcAddress` 송금 지갑 주소 (hex string format)
      - `dstAddress` 수신 지갑 주소 (hex string format)
      - `password` 송금 지갑 주소 비밀번호 (32byte string)
      - `amount` 송금하려는 ethereum 수량 (Wei)
    - '/event' 
      - `/ GET` 트랜잭션 상태 변화 이벤트들 조회
        - `start (query string) (optional)` 조회 범위 시작 날짜 (ISO_DATE_TIME format)
        - `end (query string) (optional)` 조회 범위 종료 날짜 (ISO_DATE_TIME format)
        - `size (query string) (optional)` 조회하려는 레코드 수 (integer)
      - `/{transactionId} GET` 트랜잭션 상태 변화 이벤트 조회
        - `transactionId` (path variable) 조회하려는 트랜잭션 해시 값 (hex string format)
- Wallet
  - `/wallet`
    - `/ POST` 지갑 생성 후 지갑 주소 반환
      - `password` 지갑 비밀번호 (32byte string)

