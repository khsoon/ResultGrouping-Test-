# ResultGrouping-Test-
Test Version

그룹화 테스트버전 (여러 기준으로 테스트한 버전)
* 두 가지 기준인 LCS비교와 유사도를 사용할 수 있는 소스
* 여러개의 엑셀파일을 읽고, 결과를 종합한 테이블을 엑셀파일로 출력할 수 있는 소스





< 실제 테스트 방법 >

1. 판정 기준 및 모드로 '4'를 선택
    - 결과테이블을 출력하지 않으려면 1,2,3 선택
    - 유사도 기준을 직접 지정하고 싶으면 2번 선택 (ex) 기준으로 유사도 67%를 선택하고 싶을 때)
    - LCS 비교 기준은 LCS가 정확히 일치할 경우에 같다고 판단하는 기준이지만, 완전하게 이 방법을 사용하기 위해서는 해결해야하는 문제가        많음, 성능 또한 확인해본 결과 좋지 않음, 현재로써는 사용하기 적합하지 않음
2. 분석 대상으로 'W/E'을 선택
    - W/E = Warnnig and Error 만 비교
    - 로그 전체를 비교하는 것보다 성능이 좋은 것을 확인함
3. 분석할 앱의 개수 입력
4. 분석할 입의 이름 입력
(실행 결과 폴더내 이미지 파일 확인)
