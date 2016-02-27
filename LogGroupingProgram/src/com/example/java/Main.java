package com.example.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

	// (추가) 4번 결과테이블 출력 모드를 위한 변수
	public static Vector<Integer> failNumberGroup = new Vector<Integer>();
	public static Vector<Vector<Vector<Integer>>> lcsCompareGroup = new Vector<Vector<Vector<Integer>>>();
	public static Vector<Vector<Vector<Vector<Integer>>>> sCompareGroup = new Vector<Vector<Vector<Vector<Integer>>>>();
	
	// 메인 함수
	public static void main(String[] args) {
		
		// 1 : 변수 선언 및 초기화
		Scanner scan = new Scanner(System.in);
		int cflag = 0;		// 그룹화 판정 기준 및 모드
		int oflag = 0;		// 비교 대상
		int appNumber = 0;	// 앱의 수
		Vector<Vector<String>> appLogFileGroup = new Vector<Vector<String>>(); // 앱의 로그엑셀파일 이름을 저장
		
		// 2 : 프로그램 반복 실행
		while(true){		
			
			// 2-1 : 판정 기준 선택
			System.out.println("======= 그룹화 판정 기준 선택 =======");		
			System.out.println("1. 유사도 / 자동");
			System.out.println("2. 유사도 / 사용자지정");
			System.out.println("3. LCS 비교");
			System.out.println("4. (추가) 결과테이블 출력 모드");
			System.out.println("=============================");
			System.out.print("번호를 선택해주세요 (종료 -1) : ");
			cflag = scan.nextInt();
			scan.nextLine();
			
			// 2-2 : 비교 대상 선택
			System.out.println("======= 비교 대상 선택 =======");
			System.out.println("1. 로그 전체 비교");
			System.out.println("2. Warning + Error만 비교");
			System.out.println("===========================");
			System.out.print("번호를 선택해주세요 : ");
			oflag = scan.nextInt();
			scan.nextLine();
			
			// 2-3 : flag값 변환 및 기준 입력
			if(cflag==-1)		// 종료
				break;
			else if(cflag==1)	// 1번
				cflag=-1;
			else if(cflag==2){	// 2번
				System.out.print("사용할 기준을 입력해주세요 : ");
				cflag = scan.nextInt();
				scan.nextLine();
			}
			else if(cflag==3)	// 3번
				cflag=-3;
			else if(cflag==4)	// 4번
				cflag=-4;
				
			// 2-4 : 분석할 앱의 수 입력
			System.out.print("분석할 앱의 수를 입력해주세요 : ");
			appNumber = scan.nextInt();
			scan.nextLine();
			
			// 2-5 : 분석할 앱의 로그파일의 이름 입력
			int tmp = appNumber;
			while(tmp!=0){
				tmp--;
				System.out.print((appNumber-tmp) + "번째 앱의 로그파일의 이름을 입력해주세요(파일이 여러개일 경우 띄어쓰기로 구분) : ");
				String tstr = scan.nextLine();
				Vector<String> tgp = new Vector<String>();
				int tmp2 = 0;
				
				for(int i=0; i<tstr.length(); i++){
					if(tstr.charAt(i)=='.'){
						tgp.addElement(tstr.substring(tmp2, i+5));
						tmp2 = i + 6;
					}		
				}
				appLogFileGroup.addElement(tgp);
			}
						
			
			// (추가) 4번 결과테이블 출력 모드를 위한 부분			
			int[] fileArray = new int[appNumber];
			for(int i=0; i<appNumber; i++){
				fileArray[i]=0;
			}
			for(int i=0; i<9; i++){
				sCompareGroup.addElement(new Vector<Vector<Vector<Integer>>>());
			}
			
			// 2-6 : 분석 실행
			tmp = appNumber;
			while(tmp!=0){
				
				// 앱 분석 시간 측정
				Date startTime = new Date();
				System.out.println(appLogFileGroup.get(appNumber-tmp) + " 작업 시작...");

				List list = null;
				list = readFile(appLogFileGroup.get(appNumber-tmp), oflag);
								
				if(list!=null){
					writeFile(appLogFileGroup.get(appNumber-tmp), cflag, oflag, list);
					fileArray[appNumber-tmp]=1;
					failNumberGroup.addElement(list.getListLength());
				}
				else{
					failNumberGroup.addElement(null);
					lcsCompareGroup.addElement(null);
					for(int i=0; i<9; i++){
						sCompareGroup.get(i).addElement(null);
					}
				}
				
				// 앱 분석 시간 출력
				Date endTime = new Date();
				long lTime = endTime.getTime() - startTime.getTime();
				System.out.println(appLogFileGroup.get(appNumber-tmp) + " 작업 종료 : " + lTime + "(ms)");
				System.out.println();
				
				tmp--;
			}
			
			
			// (추가) 4번 결과테이블 출력 모드를 위한 부분
			// ================================================================================================ //
			// 										결과테이블 출력부분				   								//
			
			if(cflag==-4){
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("결과테이블");
				XSSFRow row = null;
				XSSFCell cell = null;		
			
				row = sheet.createRow((short)0);
				cell = row.createCell(0);	cell.setCellValue("번호");
				cell = row.createCell(1);	cell.setCellValue("앱 이름");
				cell = row.createCell(2);	cell.setCellValue("에러로그 수");
				cell = row.createCell(3);	cell.setCellValue("기대값");
				cell = row.createCell(4);	cell.setCellValue("LCS비교");
				cell = row.createCell(5);	cell.setCellValue("유사도");
				
				for(int i=0; i<appNumber; i++){
					row = sheet.createRow((short)i+1);
					cell = row.createCell(0);	cell.setCellValue(i+1);
					cell = row.createCell(1);	cell.setCellValue(appLogFileGroup.get(i).get(0));
				
					if(fileArray[i]==1){
						cell = row.createCell(2);	cell.setCellValue(failNumberGroup.get(i));
						cell = row.createCell(3);	cell.setCellValue("");
				
						String group = "";	
					
						if(oflag==1){ // W,E비교시 에러 로그 많은 앱때문에 뺐어(일시적)
							for(int j=0; j<lcsCompareGroup.get(i).size(); j++){
								Vector<Integer> tgp = lcsCompareGroup.get(i).get(j);
								for(int k=0; k<tgp.size(); k++ ){
									group = group.concat(tgp.get(k)+" ");
								}		
								if(j!=lcsCompareGroup.get(i).size()-1)
									group = group.concat("/ ");	
							}
							cell = row.createCell(4);	cell.setCellValue(group);
							group="";
						}
				
						int ntmp[] = new int[9];
						ntmp[0]=70;ntmp[1]=80;ntmp[2]=90;ntmp[3]=95;
						ntmp[4]=96;ntmp[5]=97;ntmp[6]=98;ntmp[7]=99;ntmp[8]=100;
						
						for(int j=0; j<9; j++){
							group = group.concat(ntmp[j]+" : ");
							for(int k=0; k<sCompareGroup.get(j).get(i).size(); k++){
								Vector<Integer> vtmp = sCompareGroup.get(j).get(i).get(k);
								for(int l=0; l<vtmp.size(); l++ ){
									group = group.concat(vtmp.get(l)+" ");
								}		
								if(k!=sCompareGroup.get(j).get(i).size()-1)
									group = group.concat("/ ");
							}
							group = group.concat("\n");
						}
					
						cell = row.createCell(5);	cell.setCellValue(group);
					}
				}
			
			
				FileOutputStream fileoutputstream = null;
				try {
					fileoutputstream = new FileOutputStream("결과테이블.xlsx");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					workbook.write(fileoutputstream);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					fileoutputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}
			
			failNumberGroup.removeAllElements();
			lcsCompareGroup.removeAllElements();
			for(int i=0; i<9; i++){
				sCompareGroup.get(i).removeAllElements();
			}	
			
			// ================================================================================= //
			
			
			// 2-7 : 변수 초기화
			appLogFileGroup.removeAllElements();		
		}		
		scan.close();		
	}
	
	// 엑셀 파일 입력, 문자열 비교 및 관련 데이터 저장 함수
	public static List readFile(Vector<String> appLogFile, int oflag){
		
		/*
		// 임시
		Vector<Integer> wel = new Vector<Integer>();
		*/
		
		// 1 : 변수 선언 및 초기화
		List list = null;	// 리스트
		int rowIndex = 0;	// 행 인덱스
		int startRow = 0;	// 시작행
		long allTime = 0;	// 전체 측정 시간
		int node = 1;		// 분석 노드 수
		
		/*
		// (추가)결과테이블 확인용 에러로그출력부분
		XSSFWorkbook workbook2 = new XSSFWorkbook();
		XSSFSheet sheet2 = workbook2.createSheet("결과");
		XSSFRow row2 = null;
		XSSFCell cell2 = null;
		int l = 0;
		int fnum = 0;
		*/
		
		System.out.println("분석 시작...");
		
		// 2: 앱 로그 파일 분석
		for(int i=0; i<appLogFile.size(); i++){

			// 2-1 : 파일 입력
			FileInputStream file = null;
			try {
				file = new FileInputStream(appLogFile.get(i));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
			// 2-2 : 엑셀 파일 입력
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		
			// 2-3 : 엑셀 파일내 (첫번째) 시트 가져오기
			XSSFSheet sheet = workbook.getSheetAt(0);
				
		
			// 2-4 : 분석
			// 2-4-1 : 엑셀파일 전체/순차 탐색
			int rows = sheet.getPhysicalNumberOfRows();
			for (rowIndex=0; rowIndex<rows; rowIndex++){
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);
		
					// 2-4-2 : 문자열 저장
					String value = "";
					if (cell == null) {
						continue;
					} 
					else {
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_FORMULA:
							value = cell.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						}
					}
				
					// 2-4-3 : (i,0)이 "Level"(처음) 또는 "------------------------------------------------------------"(나머지)일때(adb 명령어의 시작일때) 시작행 임시 저장
					if (value.equals("Level") || value.equals("------------------------------------------------------------")) {
						startRow = rowIndex;
					}
				
					// 2-4-4 : 계속 탐색 진행 후 (i,0)이 "result : ErrorExit"일때(Failure 일때) 노드 생성 후 데이터 저장(시작행, 길이)
					// 2-4-5 : 리스트에 삽입
					else if (value.equals("result : ErrorExit")) {        
						if (list == null) {
							list = new List();
							list.add(startRow,rowIndex-startRow);
						}
						else {
							// 노드 분석 시간 측정
							Date startTime = new Date();	
			
							list.add(startRow,rowIndex-startRow);

							// 2-4-6 : 리스트내 비교할 노드 (Failure)가 있을 경우 1:1 유사도 비교 진행 및 결과 저장
							String newCase = "";
							String oldCase = "";
							String lcs = "";
							int lcsLength;
							
							newCase = saveString(oflag, workbook, startRow, rowIndex-startRow);

							for (int j=0; j<list.getListLength()-1; j++) {								
								oldCase = saveString(oflag, workbook, list.getStartRow(j), list.getRowLength(j));
						
								// 문자열 비교 시간 측정
								Date startTime2 = new Date();
							
								lcs = compareString(newCase.length(), oldCase.length(), newCase, oldCase);
								lcsLength = lcs.length();
							
								// 문자열 비교 시간 출력
								Date endTime2 = new Date();
								long lTime2 = endTime2.getTime() - startTime2.getTime();
								System.out.println("	" + node + "(" + newCase.length() + ") & " + j + "("+ oldCase.length() + ") : " + lTime2 + "(ms)");							
								
								double bigSimilarity = 0;
								double smallSimilarity = 0;
								if (newCase.length()<=oldCase.length()){
									bigSimilarity = (double)lcsLength / (double)oldCase.length() * 100;
									smallSimilarity = (double)lcsLength / (double)newCase.length() * 100;
								}
								else{
									bigSimilarity = (double)lcsLength / (double)newCase.length() * 100;
									smallSimilarity = (double)lcsLength / (double)oldCase.length() * 100;
								}
							
								list.saveData(list.getListLength()-1, newCase.length(), oldCase.length(), lcs, lcsLength, bigSimilarity, smallSimilarity);
							}
						
							// 노드 분석 시간 출력
							Date endTime = new Date();
							long lTime = endTime.getTime() - startTime.getTime();
							System.out.println("		" + node + "번째 노드 분석 시간 : " + lTime + "(ms)\n");
							allTime = allTime + lTime;
							node++;
						}
						
						/*
						// (추가) 결과테이블 확인용 로그파일 출력부분		
						row2 = sheet2.createRow((short)l);
						cell2 = row2.createCell(0);
						cell2.setCellValue("Failure : " + fnum + ", StartRow : " + startRow);
						l++;fnum++;
						int wenum =0;
						
						for(int j=0; j<rowIndex-startRow-3; j++){
							row = sheet.getRow(startRow+3+j);
							if (row != null) {
								cell = row.getCell(0);	// 0
		
								String value3 = "";
								if (cell != null) {		
									switch (cell.getCellType()) {
									case XSSFCell.CELL_TYPE_FORMULA:
										value3 = cell.getCellFormula();
										break;
									case XSSFCell.CELL_TYPE_NUMERIC:
										value3 = cell.getNumericCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_STRING:
										value3 = cell.getStringCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_BLANK:
										value3 = cell.getBooleanCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_ERROR:
										value3 = cell.getErrorCellValue() + "";
										break;
									}
								}
								
								if(value3.equals("W") || value3.equals("E")){
									wenum++;
									row2 = sheet2.createRow((short)l);
									cell2 =row2.createCell(0);
									cell2.setCellValue(value3);
									l++;

									for(int z=4; z<7; z++){	// 4,5,6
										XSSFCell cell3 = row.getCell(z);
	
										value3 = "";
										if (cell3 != null) {		
											switch (cell3.getCellType()) {
											case XSSFCell.CELL_TYPE_FORMULA:
												value3 = cell3.getCellFormula();
												break;
											case XSSFCell.CELL_TYPE_NUMERIC:
												value3 = cell3.getNumericCellValue() + "";
												break;
											case XSSFCell.CELL_TYPE_STRING:
												value3 = cell3.getStringCellValue() + "";
												break;
											case XSSFCell.CELL_TYPE_BLANK:
												value3 = cell3.getBooleanCellValue() + "";
												break;
											case XSSFCell.CELL_TYPE_ERROR:
												value3 = cell3.getErrorCellValue() + "";
												break;
											}
										}
										if (value3.equalsIgnoreCase("false")) // 빈칸일때
											value3 = "";
	
										cell2 =row2.createCell(z-3);
										cell2.setCellValue(value3);
									}
								}
							}
						}
						
						row2 = sheet2.createRow((short)l);
						cell2 = row2.createCell(0);
						cell2.setCellValue("W/E Length : " + wenum); wel.addElement(wenum);
						l++;
						*/
					}	
				}
			}
		}
		
		/*
		// 임시
		String aaa = "";
		for(int aa=0; aa<wel.size(); aa++){
			aaa = aaa + " " + wel.get(aa);
		}
		row2 = sheet2.createRow((short)l);
		cell2 = row2.createCell(0);
		cell2.setCellValue(aaa);
		
		
		// (추가) 결과테이블 확인용 에러출력 부분
		FileOutputStream fileoutputstream = null;
		try {
			fileoutputstream = new FileOutputStream(appLogFile.get(0) + " 에러로그파일" + ".xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook2.write(fileoutputstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileoutputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			workbook2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		// readFile 총 분석 시간 출력
		System.out.println("분석 종료 : " + allTime + "(ms)");
		System.out.println();
					
		return list;
	}
	
	// 문자열 생성 함수 *
	private static String saveString(int oflag, XSSFWorkbook workbook, int startRow, int failLength) {
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		StringBuilder loadString = new StringBuilder();
		
		if(oflag==1){ // 로그전체 비교시
			// Result의 내용 문자열로 저장 : 열 0,4,5,6
			int rowIndex = startRow+3;
			for(int i=0; i<failLength-3; i++){
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);	// 0
				
					String value = "";
					if (cell != null) {		
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_FORMULA:
							value = cell.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						}
					}
					loadString.append(value);
				
				
					for(int j=4; j<7; j++){	// 4,5,6
						XSSFCell cell2 = row.getCell(j);
					
						String value2 = "";
						if (cell2 != null) {		
							switch (cell2.getCellType()) {
							case XSSFCell.CELL_TYPE_FORMULA:
								value2 = cell2.getCellFormula();
								break;
							case XSSFCell.CELL_TYPE_NUMERIC:
								value2 = cell2.getNumericCellValue() + "";
								break;
							case XSSFCell.CELL_TYPE_STRING:
								value2 = cell2.getStringCellValue() + "";
								break;
							case XSSFCell.CELL_TYPE_BLANK:
								value2 = cell2.getBooleanCellValue() + "";
								break;
							case XSSFCell.CELL_TYPE_ERROR:
								value2 = cell2.getErrorCellValue() + "";
								break;
							}
						}
						loadString.append(value2);
					}
				
				}
				rowIndex++;
			}
		}
		
		if(oflag==2){ // warning + error만 비교시
			// Result의 내용 문자열로 저장 : 열 0,4,5,6
			int rowIndex = startRow+3;
			for(int i=0; i<failLength-3; i++){
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);	// 0
					
					String value = "";
					if (cell != null) {		
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_FORMULA:
							value = cell.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						}
					}
					
					if(value.equals("W") || value.equals("E")){
						loadString.append(value);
					
					
						for(int j=4; j<7; j++){	// 4,5,6
							XSSFCell cell2 = row.getCell(j);
						
							String value2 = "";
							if (cell2 != null) {		
								switch (cell2.getCellType()) {
								case XSSFCell.CELL_TYPE_FORMULA:
									value2 = cell2.getCellFormula();
									break;
								case XSSFCell.CELL_TYPE_NUMERIC:
									value2 = cell2.getNumericCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_STRING:
									value2 = cell2.getStringCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_BLANK:
									value2 = cell2.getBooleanCellValue() + "";
									break;
								case XSSFCell.CELL_TYPE_ERROR:
									value2 = cell2.getErrorCellValue() + "";
									break;
								}
							}
							loadString.append(value2);
						}
					}	
				}
				rowIndex++;
			}
		}
		
		return loadString.toString();
		
	}

	// LCS 생성 함수 : LCS Algorithm *
	private static String compareString(int m, int n, String A, String B){
		
		// ===================================================================== //
		// 1. If problem is trivial, solve it :                                  //
		// ===================================================================== //
		
		String C = "";
		char[] c = new char[1];

		if(n==0){}
		else if(m==1){
			for(int j=0; j<n; j++){
				if(A.charAt(0)==B.charAt(j)){
					c[0] = A.charAt(0);
					C = new String(c);
					break;
				}				
			}
		}
		
		
		// ===================================================================== //
		// 2. Otherwise, split problem :                                         //
		// ===================================================================== //
		
		else{
			int i = m/2;
			
			// ===================================================================== //
			// 3. Evaluate L(i,j) and L*(i,j) [j = 0...n] :                          //
			// ===================================================================== //
			
			int[] L1 = new int[n+1];
			int[] L2 = new int[n+1];
						
			compare(i,n,A.substring(0,i),B.substring(0,n),L1);
			compare(m-i,n,reverse(A.substring(i,m)),reverse(B.substring(0,n)),L2);
			
			
			// ===================================================================== //
			// 4. Find j such that L(i,j) + L*(i,j) = L(m,n) using theorem :         //
			// ===================================================================== //
						
			int max = 0;
			int k = 0;
			for(int j=0; j<=n; j++){
				if(max<L1[j]+L2[n-j]){
					max = L1[j] + L2[n-j];
					k = j;
				}
			}
			
			
			// ===================================================================== //
			// 5. Solve simpler problems :                                           //
			// ===================================================================== //
			
			String C1 = "";
			String C2 = "";
						
			C1 = compareString(i,k,A.substring(0, i),B.substring(0, k));
			C2 = compareString(m-i,n-k,A.substring(i, m),B.substring(k, n));
			
			
			// ===================================================================== //
			// 6. Give output :                                                      //
			// ===================================================================== //
			
			C = C1 + C2;
			
		}
		
		return C;
		
	}
	
	// LCS 길이 생성 함수 : LCS Algorithm *
	private static void compare(int m, int n, String A, String B, int[] LL){
		
		int [][] K = new int [2][n+1];
		
		for(int j=0; j<=n; j++){
			K[1][j] = 0;
		}
					
		for(int i=1; i<=m; i++){
			for(int j=0; j<=n; j++){
				K[0][j] = K[1][j];
			}
			for(int j=1; j<=n; j++){		
				if(A.charAt(i-1)==B.charAt(j-1)){
					K[1][j] = K[0][j-1] + 1;
				}
				else{
					if(K[1][j-1]>=K[0][j])
						K[1][j] = K[1][j-1];
					else
						K[1][j] = K[0][j];
				}
			}
		}
		
		for(int j=0; j<=n; j++){
			LL[j]=K[1][j];
		}
		
	}

	// 문자열 리버스 함수 : LCS Algorithm *
	public static String reverse(String S){
		
		StringBuffer a = new StringBuffer();
			
		for(int i=S.length()-1; i>=0; i--){
			a.append(S.charAt(i));
		}
			
		return a.toString();
	}
	
	// 결과 도출 및 로그 엑셀 파일 출력 함수
	private static void writeFile(Vector<String> appLogFile, int cflag, int oflag, List list) {
		
		Vector<Vector<Integer>> print = new Vector<Vector<Integer>>();
		
		System.out.println("출력 시작...");
	
		
		//============================================================================================================================== //
		// 1 : 판단 기준 : 비율	/ 자동																										 //
		//============================================================================================================================== //

		if(cflag==-1 || cflag==-4){

			int printFile[] = {70,80,90,95,96,97,98,99,100};
			
			// 출력할 failure 선택
			for(int p=0; p<9; p++){	
				//
				Vector<Vector<Integer>> group = new Vector<Vector<Integer>>();
				Vector<Integer> v1 = new Vector<Integer>();
				int flag;
				
				for(int i=0; i<list.getListLength(); i++){
					flag = 0;
					Vector<Double> v2 = list.getBigSimilarity(i);
					
					for(int j=0; j<v2.size(); j++){
						if((Double)v2.get(j)>=printFile[p]){
							flag = 1;
							// i : 노드, j : 비교대상, (i>j)
							
							int insertNode=-1;
							for(int k=0; k<group.size(); k++){
								Vector<Integer> btmp = group.get(k);
								for(int z=0; z<btmp.size(); z++){
									// 비교
									if(btmp.get(z)==j){
										insertNode=k;
										break;
									}
								}
								if(insertNode>=0)
									break;
							}
							group.get(insertNode).add(i);
							
							//
							break;
						}
					}
			
					if(flag==0){
						v1.addElement(i);
						Vector<Integer> tmp = new Vector<Integer>();
						tmp.addElement(i);
						group.addElement(tmp);
					}
				}
				//
				
				sCompareGroup.get(p).addElement(group);
				
				print.addElement(v1);		
			}
		}
			
		
					
		//============================================================================================================================== //
		// 2 : 판단 기준 : 비율	/ 사용자정의																									 //
		//============================================================================================================================== //
				
		if(cflag>=0){	
			// 출력할 failure 선택
			Vector<Integer> v1 = new Vector<Integer>();
			int flag;
			
			for(int i=0; i<list.getListLength(); i++){
				flag = 0;
				Vector<Double> v2 = list.getBigSimilarity(i);
						
				for(int j=0; j<v2.size(); j++){
					if((Double)v2.get(j)>=cflag){
						flag = 1;	
						break;
					}
				}
		
				if(flag==0)
					v1.addElement(i);
			}
			
			print.addElement(v1);
		}
		
		
		
		//============================================================================================================================== //
		// 3 : 판단 기준 : LCS 비교																											 //
		//============================================================================================================================== //
		
		if(cflag==-3){
			
			// LCS 비교를 위한 준비 //
			Vector<String> lcsVector = new Vector<String>();
			Vector<Integer> firstNodeNumber = new Vector<Integer>();
			Vector<Integer> secondNodeNumber = new Vector<Integer>();
		
			for(int i=0; i<list.getListLength(); i++){
				Vector<String> v = list.getLcs(i);
				for(int j=0; j<v.size(); j++){
					lcsVector.addElement(v.get(j));
					firstNodeNumber.addElement(i);
					secondNodeNumber.addElement(j);
				}
			}
			// 준비 후... //
			// lcsVector		 : 리스트에 흩어져있던 LCS들이 이 하나의 벡터에 저장됨, 리스트 앞에서부터 순서대로 저장
			// firstNodeNumber	 : LCS를 생성한 첫번째 Failure의 번호, LCS가 저장되었던 노드의 번호
			// secondeNodeNumber : LCS를 생성한 두번째 Failure의 번호
			// lcsVector(0)에 저장된 LCS는 firstNodeNumber(0)과 secondNodeNumber(0)에 각각 저장된 Failure들의 LCS (firstNodeNumber(i) > secondNodeNumber(i))
		
		
			// LCS끼리 비교 //
			Vector<Integer> firstLCS = new Vector<Integer>();		// lcsVector에서 같은 LCS의 첫번째 LCS의 인덱스를 저장		// 위의 예에서 firstLCS = 2, secondLCS = 3
			Vector<Integer> secondLCS = new Vector<Integer>();		// lcsVector에서 같은 LCS의 두번째 LCS의 인덱스를 저장

			for(int i=0; i<lcsVector.size(); i++){
				for(int j=i+1; j<lcsVector.size(); j++){
					if(lcsVector.get(i).equals(lcsVector.get(j))){					
						int flag=0;						
						if(flag==0){
							firstLCS.addElement(i);
							secondLCS.addElement(j);
						}
					}
				}
			}
			// 비교 후... //
			// 같은 LCS는 같은 인덱스의 firstLCS와 secondLCS에 저장됨 (firstLCS(i) < secondLCS(i))
		
			
			// 같은 LCS를 가진 노드 그룹화 //
			Vector<Vector<Integer>> sameLCSGroup = new Vector<Vector<Integer>>();
			for(int i=0; i<firstLCS.size(); i++){
				Vector<Integer> sameLCS = new Vector<Integer>();
				Vector<Integer> sameLCS2 = new Vector<Integer>();	// 중복제거벡터
			
				// 삽입 및 정렬
				sameLCS.addElement(firstNodeNumber.get(firstLCS.elementAt(i)));
				sameLCS.addElement(secondNodeNumber.get(firstLCS.elementAt(i)));
				sameLCS.addElement(firstNodeNumber.get(secondLCS.elementAt(i)));
				sameLCS.addElement(secondNodeNumber.get(secondLCS.elementAt(i)));
				sameLCS.sort(null);
			
				// 중복제거
				for(int j=0; j<sameLCS.size(); j++){
					if(j==0)
						sameLCS2.addElement(sameLCS.get(j));
					else{
						if(sameLCS.get(j-1)==sameLCS.get(j)){}
						else
							sameLCS2.addElement(sameLCS.get(j));
					}
				}
			
				// 비교 및 삽입
				if(i==0)
					sameLCSGroup.addElement(sameLCS2);
				else{
					// 비교
					int flag=-1;
					for(int j=0; j<sameLCSGroup.size(); j++){
						Vector<Integer> tmp = sameLCSGroup.get(j);		
						for(int k=0; k<tmp.size(); k++){
							if(sameLCS2.size()==3 && (tmp.get(k)==sameLCS2.get(0) || tmp.get(k)==sameLCS2.get(1) || tmp.get(k)==sameLCS2.get(2))){
								flag=j;
								break;
							}
							else if(sameLCS2.size()==4 && (tmp.get(k)==sameLCS2.get(0) || tmp.get(k)==sameLCS2.get(1)  || tmp.get(k)==sameLCS2.get(2) || tmp.get(k)==sameLCS2.get(3))){
								flag=j;
								break;
							}
						}
						if(flag!=-1)
							break;
					}
				
					// 삽입
					if(flag==-1)
						sameLCSGroup.addElement(sameLCS2);
					else{
						Vector<Integer> tmp = sameLCSGroup.get(flag);
						for(int j=0; j<sameLCS2.size(); j++){
							tmp.addElement(sameLCS2.get(j));
						}
						tmp.sort(null);
						Vector<Integer> tmp2 = new Vector<Integer>();
						for(int j=0; j<tmp.size(); j++){
							if(j==0)
								tmp2.addElement(tmp.get(j));
							else{
								if(tmp.get(j-1)==tmp.get(j)){}
								else
									tmp2.addElement(tmp.get(j));
							}
						}
						
						// 내용 변경 후 대치
						Vector<Vector<Integer>> groupTmp = new Vector<Vector<Integer>>();
						for(int j=0; j<sameLCSGroup.size(); j++){
							if(j!=flag)
								groupTmp.addElement(sameLCSGroup.get(j));
							else
								groupTmp.addElement(tmp2);
						}			
						sameLCSGroup.removeAllElements();
						for(int j=0; j<groupTmp.size(); j++){
							sameLCSGroup.addElement(groupTmp.get(j));
						}
					}
				}
			}
			
			
			// 출력 대상 선정 및 출력 //
			int nodeNumber = list.getListLength();
			int[] node = new int[nodeNumber];
		
			for(int i=0; i<nodeNumber; i++)
				node[i]=1;		
			for(int i=0; i<sameLCSGroup.size(); i++){
				Vector<Integer> tmp = sameLCSGroup.get(i);
				for(int j=0; j<tmp.size(); j++){
					if(j==0){}
					else{
						node[tmp.get(j)]=0;
					}
				}
				System.out.println();
			}
			Vector<Integer> printNode = new Vector<Integer>();
			for(int i=0; i<nodeNumber; i++){
				if(node[i]==1)
					printNode.addElement(i);
			}
			print.addElement(printNode);
				
			
			int fg=0;
			Vector<Vector<Integer>> newSameLCSGroup = new Vector<Vector<Integer>>();
			
			for(int i=0; i<nodeNumber; i++){
				if(node[i]==1){
					fg=0;
					for(int j=0; j<sameLCSGroup.size(); j++){
						if(i==sameLCSGroup.get(j).get(0)){
							fg=1;
							newSameLCSGroup.addElement(sameLCSGroup.get(j));
							break;
						}
					}
					if(fg==0){
						Vector<Integer> tmp = new Vector<Integer>();
						tmp.addElement(i);
						newSameLCSGroup.addElement(tmp);
					}
				}
			}
			lcsCompareGroup.addElement(newSameLCSGroup);
		}
				
		
			
		//============================================================================================================================== //
		// 엑셀 파일 출력 부분																												 //
		//============================================================================================================================== //
		
		int lotation = 0;
		while(lotation!=print.size()){
			Vector<Integer> printN = print.get(lotation);
			lotation++;
				
			FileInputStream file = null;
			XSSFWorkbook workbook = null;
			
			XSSFWorkbook workbook2 = new XSSFWorkbook();
			XSSFSheet sheet2 = workbook2.createSheet("결과");
			XSSFRow row2 = null;
			XSSFCell cell2 = null;
			int l = 0;
			int k = 0; // 벡터내 노드 인덱스
			
			
			int overfile = 0;
			for(int y=0; y<appLogFile.size(); y++){
				
				// 파일 입력
				try {
					file = new FileInputStream(appLogFile.get(y));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// 엑셀 파일 입력	
				try {
					workbook = new XSSFWorkbook(file);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 엑셀 파일내 (첫번째)시트 가져오기
				XSSFSheet sheet = workbook.getSheetAt(0);


				for(int i=overfile; i<list.getListLength(); i++){		// i : 리스트내 노드 인덱스
					if(k<printN.size() && i==(int)printN.get(k)){ 		// 출력 노드 선택
						
						if(list.getStartRow(i)<list.getStartRow(i-1)){
							overfile=i;
							break;
						}
						
						row2 = sheet2.createRow((short)l);
						cell2 = row2.createCell(0);
						cell2.setCellValue("Failure " + k);
				
						l++;
				
						int startPoint = list.getStartRow(i) + 3;
						for(int j =0; j<list.getRowLength(i)-3; j++){
							XSSFRow row = sheet.getRow(startPoint);
							if (row != null) {
								XSSFCell cell = row.getCell(0);	// 0
								
								String value = "";
								if (cell != null) {		
									switch (cell.getCellType()) {
									case XSSFCell.CELL_TYPE_FORMULA:
										value = cell.getCellFormula();
										break;
									case XSSFCell.CELL_TYPE_NUMERIC:
										value = cell.getNumericCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_STRING:
										value = cell.getStringCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_BLANK:
										value = cell.getBooleanCellValue() + "";
										break;
									case XSSFCell.CELL_TYPE_ERROR:
										value = cell.getErrorCellValue() + "";
										break;
									}
								}
						
								row2 = sheet2.createRow((short)l);
								cell2 =row2.createCell(0);
								cell2.setCellValue(value);
								l++;
						
								for(int z=4; z<7; z++){	// 4,5,6
									XSSFCell cell3 = row.getCell(z);
							
									String value3 = "";
									if (cell3 != null) {		
										switch (cell3.getCellType()) {
										case XSSFCell.CELL_TYPE_FORMULA:
											value3 = cell3.getCellFormula();
											break;
										case XSSFCell.CELL_TYPE_NUMERIC:
											value3 = cell3.getNumericCellValue() + "";
											break;
										case XSSFCell.CELL_TYPE_STRING:
											value3 = cell3.getStringCellValue() + "";
											break;
										case XSSFCell.CELL_TYPE_BLANK:
											value3 = cell3.getBooleanCellValue() + "";
											break;
										case XSSFCell.CELL_TYPE_ERROR:
											value3 = cell3.getErrorCellValue() + "";
											break;
										}
									}
									if (value3.equalsIgnoreCase("false")) // 빈칸일때
										value3 = "";
							
									cell2 =row2.createCell(z-3);
									cell2.setCellValue(value3);
								
								}
							
							}
						
							startPoint++;
						
						}
					
						row2 = sheet2.createRow((short)l);
						cell2 =row2.createCell(0);
						cell2.setCellValue("");
						l++;
					
						k++;
					
					}
				}
			
			} // ~같은 앱 다른 파일 반복 
			
			row2 = sheet2.createRow((short)l);
			for(int i=0; i<printN.size(); i++){		
				cell2 = row2.createCell(i);
				cell2.setCellValue(printN.get(i));
			}
			l++;
			
		
			// 파일 출력
			String printV = null;
			if(cflag==-1){
				if(lotation==1)
					printV = "70";
				else if(lotation==2)
					printV = "80";
				else if(lotation==3)
					printV = "90";
				else if(lotation==4)
					printV = "95";
				else if(lotation==5)
					printV = "96";
				else if(lotation==6)
					printV = "97";
				else if(lotation==7)
					printV = "98";
				else if(lotation==8)
					printV = "99";
				else if(lotation==9)
					printV = "100";
			}
			else if(cflag==-3)
				printV = "LCS";
			else if(cflag==-4){
				if(lotation==1)
					printV = "70";
				else if(lotation==2)
					printV = "80";
				else if(lotation==3)
					printV = "90";
				else if(lotation==4)
					printV = "95";
				else if(lotation==5)
					printV = "96";
				else if(lotation==6)
					printV = "97";
				else if(lotation==7)
					printV = "98";
				else if(lotation==8)
					printV = "99";
				else if(lotation==9)
					printV = "100";
				else if(lotation==10)
					printV = "LCS";
			}
			else
				printV = "" + cflag;
			
			
			FileOutputStream fileoutputstream = null;
			try {
				fileoutputstream = new FileOutputStream(appLogFile.get(0) + " " + printV + " 결과파일 " + ".xlsx");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				workbook2.write(fileoutputstream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileoutputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				workbook2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println(appLogFile + " " + printV + " 결과파일 출력 성공");
			
		}
			
		
		System.out.println("출력 종료");
		System.out.println();
		
	}	
		
}
