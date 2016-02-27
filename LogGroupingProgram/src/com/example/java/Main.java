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

	// (�߰�) 4�� ������̺� ��� ��带 ���� ����
	public static Vector<Integer> failNumberGroup = new Vector<Integer>();
	public static Vector<Vector<Vector<Integer>>> lcsCompareGroup = new Vector<Vector<Vector<Integer>>>();
	public static Vector<Vector<Vector<Vector<Integer>>>> sCompareGroup = new Vector<Vector<Vector<Vector<Integer>>>>();
	
	// ���� �Լ�
	public static void main(String[] args) {
		
		// 1 : ���� ���� �� �ʱ�ȭ
		Scanner scan = new Scanner(System.in);
		int cflag = 0;		// �׷�ȭ ���� ���� �� ���
		int oflag = 0;		// �� ���
		int appNumber = 0;	// ���� ��
		Vector<Vector<String>> appLogFileGroup = new Vector<Vector<String>>(); // ���� �α׿������� �̸��� ����
		
		// 2 : ���α׷� �ݺ� ����
		while(true){		
			
			// 2-1 : ���� ���� ����
			System.out.println("======= �׷�ȭ ���� ���� ���� =======");		
			System.out.println("1. ���絵 / �ڵ�");
			System.out.println("2. ���絵 / ���������");
			System.out.println("3. LCS ��");
			System.out.println("4. (�߰�) ������̺� ��� ���");
			System.out.println("=============================");
			System.out.print("��ȣ�� �������ּ��� (���� -1) : ");
			cflag = scan.nextInt();
			scan.nextLine();
			
			// 2-2 : �� ��� ����
			System.out.println("======= �� ��� ���� =======");
			System.out.println("1. �α� ��ü ��");
			System.out.println("2. Warning + Error�� ��");
			System.out.println("===========================");
			System.out.print("��ȣ�� �������ּ��� : ");
			oflag = scan.nextInt();
			scan.nextLine();
			
			// 2-3 : flag�� ��ȯ �� ���� �Է�
			if(cflag==-1)		// ����
				break;
			else if(cflag==1)	// 1��
				cflag=-1;
			else if(cflag==2){	// 2��
				System.out.print("����� ������ �Է����ּ��� : ");
				cflag = scan.nextInt();
				scan.nextLine();
			}
			else if(cflag==3)	// 3��
				cflag=-3;
			else if(cflag==4)	// 4��
				cflag=-4;
				
			// 2-4 : �м��� ���� �� �Է�
			System.out.print("�м��� ���� ���� �Է����ּ��� : ");
			appNumber = scan.nextInt();
			scan.nextLine();
			
			// 2-5 : �м��� ���� �α������� �̸� �Է�
			int tmp = appNumber;
			while(tmp!=0){
				tmp--;
				System.out.print((appNumber-tmp) + "��° ���� �α������� �̸��� �Է����ּ���(������ �������� ��� ����� ����) : ");
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
						
			
			// (�߰�) 4�� ������̺� ��� ��带 ���� �κ�			
			int[] fileArray = new int[appNumber];
			for(int i=0; i<appNumber; i++){
				fileArray[i]=0;
			}
			for(int i=0; i<9; i++){
				sCompareGroup.addElement(new Vector<Vector<Vector<Integer>>>());
			}
			
			// 2-6 : �м� ����
			tmp = appNumber;
			while(tmp!=0){
				
				// �� �м� �ð� ����
				Date startTime = new Date();
				System.out.println(appLogFileGroup.get(appNumber-tmp) + " �۾� ����...");

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
				
				// �� �м� �ð� ���
				Date endTime = new Date();
				long lTime = endTime.getTime() - startTime.getTime();
				System.out.println(appLogFileGroup.get(appNumber-tmp) + " �۾� ���� : " + lTime + "(ms)");
				System.out.println();
				
				tmp--;
			}
			
			
			// (�߰�) 4�� ������̺� ��� ��带 ���� �κ�
			// ================================================================================================ //
			// 										������̺� ��ºκ�				   								//
			
			if(cflag==-4){
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("������̺�");
				XSSFRow row = null;
				XSSFCell cell = null;		
			
				row = sheet.createRow((short)0);
				cell = row.createCell(0);	cell.setCellValue("��ȣ");
				cell = row.createCell(1);	cell.setCellValue("�� �̸�");
				cell = row.createCell(2);	cell.setCellValue("�����α� ��");
				cell = row.createCell(3);	cell.setCellValue("��밪");
				cell = row.createCell(4);	cell.setCellValue("LCS��");
				cell = row.createCell(5);	cell.setCellValue("���絵");
				
				for(int i=0; i<appNumber; i++){
					row = sheet.createRow((short)i+1);
					cell = row.createCell(0);	cell.setCellValue(i+1);
					cell = row.createCell(1);	cell.setCellValue(appLogFileGroup.get(i).get(0));
				
					if(fileArray[i]==1){
						cell = row.createCell(2);	cell.setCellValue(failNumberGroup.get(i));
						cell = row.createCell(3);	cell.setCellValue("");
				
						String group = "";	
					
						if(oflag==1){ // W,E�񱳽� ���� �α� ���� �۶����� ����(�Ͻ���)
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
					fileoutputstream = new FileOutputStream("������̺�.xlsx");
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
			
			
			// 2-7 : ���� �ʱ�ȭ
			appLogFileGroup.removeAllElements();		
		}		
		scan.close();		
	}
	
	// ���� ���� �Է�, ���ڿ� �� �� ���� ������ ���� �Լ�
	public static List readFile(Vector<String> appLogFile, int oflag){
		
		/*
		// �ӽ�
		Vector<Integer> wel = new Vector<Integer>();
		*/
		
		// 1 : ���� ���� �� �ʱ�ȭ
		List list = null;	// ����Ʈ
		int rowIndex = 0;	// �� �ε���
		int startRow = 0;	// ������
		long allTime = 0;	// ��ü ���� �ð�
		int node = 1;		// �м� ��� ��
		
		/*
		// (�߰�)������̺� Ȯ�ο� �����α���ºκ�
		XSSFWorkbook workbook2 = new XSSFWorkbook();
		XSSFSheet sheet2 = workbook2.createSheet("���");
		XSSFRow row2 = null;
		XSSFCell cell2 = null;
		int l = 0;
		int fnum = 0;
		*/
		
		System.out.println("�м� ����...");
		
		// 2: �� �α� ���� �м�
		for(int i=0; i<appLogFile.size(); i++){

			// 2-1 : ���� �Է�
			FileInputStream file = null;
			try {
				file = new FileInputStream(appLogFile.get(i));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
			// 2-2 : ���� ���� �Է�
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		
			// 2-3 : ���� ���ϳ� (ù��°) ��Ʈ ��������
			XSSFSheet sheet = workbook.getSheetAt(0);
				
		
			// 2-4 : �м�
			// 2-4-1 : �������� ��ü/���� Ž��
			int rows = sheet.getPhysicalNumberOfRows();
			for (rowIndex=0; rowIndex<rows; rowIndex++){
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					XSSFCell cell = row.getCell(0);
		
					// 2-4-2 : ���ڿ� ����
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
				
					// 2-4-3 : (i,0)�� "Level"(ó��) �Ǵ� "------------------------------------------------------------"(������)�϶�(adb ��ɾ��� �����϶�) ������ �ӽ� ����
					if (value.equals("Level") || value.equals("------------------------------------------------------------")) {
						startRow = rowIndex;
					}
				
					// 2-4-4 : ��� Ž�� ���� �� (i,0)�� "result : ErrorExit"�϶�(Failure �϶�) ��� ���� �� ������ ����(������, ����)
					// 2-4-5 : ����Ʈ�� ����
					else if (value.equals("result : ErrorExit")) {        
						if (list == null) {
							list = new List();
							list.add(startRow,rowIndex-startRow);
						}
						else {
							// ��� �м� �ð� ����
							Date startTime = new Date();	
			
							list.add(startRow,rowIndex-startRow);

							// 2-4-6 : ����Ʈ�� ���� ��� (Failure)�� ���� ��� 1:1 ���絵 �� ���� �� ��� ����
							String newCase = "";
							String oldCase = "";
							String lcs = "";
							int lcsLength;
							
							newCase = saveString(oflag, workbook, startRow, rowIndex-startRow);

							for (int j=0; j<list.getListLength()-1; j++) {								
								oldCase = saveString(oflag, workbook, list.getStartRow(j), list.getRowLength(j));
						
								// ���ڿ� �� �ð� ����
								Date startTime2 = new Date();
							
								lcs = compareString(newCase.length(), oldCase.length(), newCase, oldCase);
								lcsLength = lcs.length();
							
								// ���ڿ� �� �ð� ���
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
						
							// ��� �м� �ð� ���
							Date endTime = new Date();
							long lTime = endTime.getTime() - startTime.getTime();
							System.out.println("		" + node + "��° ��� �м� �ð� : " + lTime + "(ms)\n");
							allTime = allTime + lTime;
							node++;
						}
						
						/*
						// (�߰�) ������̺� Ȯ�ο� �α����� ��ºκ�		
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
										if (value3.equalsIgnoreCase("false")) // ��ĭ�϶�
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
		// �ӽ�
		String aaa = "";
		for(int aa=0; aa<wel.size(); aa++){
			aaa = aaa + " " + wel.get(aa);
		}
		row2 = sheet2.createRow((short)l);
		cell2 = row2.createCell(0);
		cell2.setCellValue(aaa);
		
		
		// (�߰�) ������̺� Ȯ�ο� ������� �κ�
		FileOutputStream fileoutputstream = null;
		try {
			fileoutputstream = new FileOutputStream(appLogFile.get(0) + " �����α�����" + ".xlsx");
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
		
		// readFile �� �м� �ð� ���
		System.out.println("�м� ���� : " + allTime + "(ms)");
		System.out.println();
					
		return list;
	}
	
	// ���ڿ� ���� �Լ� *
	private static String saveString(int oflag, XSSFWorkbook workbook, int startRow, int failLength) {
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		StringBuilder loadString = new StringBuilder();
		
		if(oflag==1){ // �α���ü �񱳽�
			// Result�� ���� ���ڿ��� ���� : �� 0,4,5,6
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
		
		if(oflag==2){ // warning + error�� �񱳽�
			// Result�� ���� ���ڿ��� ���� : �� 0,4,5,6
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

	// LCS ���� �Լ� : LCS Algorithm *
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
	
	// LCS ���� ���� �Լ� : LCS Algorithm *
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

	// ���ڿ� ������ �Լ� : LCS Algorithm *
	public static String reverse(String S){
		
		StringBuffer a = new StringBuffer();
			
		for(int i=S.length()-1; i>=0; i--){
			a.append(S.charAt(i));
		}
			
		return a.toString();
	}
	
	// ��� ���� �� �α� ���� ���� ��� �Լ�
	private static void writeFile(Vector<String> appLogFile, int cflag, int oflag, List list) {
		
		Vector<Vector<Integer>> print = new Vector<Vector<Integer>>();
		
		System.out.println("��� ����...");
	
		
		//============================================================================================================================== //
		// 1 : �Ǵ� ���� : ����	/ �ڵ�																										 //
		//============================================================================================================================== //

		if(cflag==-1 || cflag==-4){

			int printFile[] = {70,80,90,95,96,97,98,99,100};
			
			// ����� failure ����
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
							// i : ���, j : �񱳴��, (i>j)
							
							int insertNode=-1;
							for(int k=0; k<group.size(); k++){
								Vector<Integer> btmp = group.get(k);
								for(int z=0; z<btmp.size(); z++){
									// ��
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
		// 2 : �Ǵ� ���� : ����	/ ���������																									 //
		//============================================================================================================================== //
				
		if(cflag>=0){	
			// ����� failure ����
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
		// 3 : �Ǵ� ���� : LCS ��																											 //
		//============================================================================================================================== //
		
		if(cflag==-3){
			
			// LCS �񱳸� ���� �غ� //
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
			// �غ� ��... //
			// lcsVector		 : ����Ʈ�� ������ִ� LCS���� �� �ϳ��� ���Ϳ� �����, ����Ʈ �տ������� ������� ����
			// firstNodeNumber	 : LCS�� ������ ù��° Failure�� ��ȣ, LCS�� ����Ǿ��� ����� ��ȣ
			// secondeNodeNumber : LCS�� ������ �ι�° Failure�� ��ȣ
			// lcsVector(0)�� ����� LCS�� firstNodeNumber(0)�� secondNodeNumber(0)�� ���� ����� Failure���� LCS (firstNodeNumber(i) > secondNodeNumber(i))
		
		
			// LCS���� �� //
			Vector<Integer> firstLCS = new Vector<Integer>();		// lcsVector���� ���� LCS�� ù��° LCS�� �ε����� ����		// ���� ������ firstLCS = 2, secondLCS = 3
			Vector<Integer> secondLCS = new Vector<Integer>();		// lcsVector���� ���� LCS�� �ι�° LCS�� �ε����� ����

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
			// �� ��... //
			// ���� LCS�� ���� �ε����� firstLCS�� secondLCS�� ����� (firstLCS(i) < secondLCS(i))
		
			
			// ���� LCS�� ���� ��� �׷�ȭ //
			Vector<Vector<Integer>> sameLCSGroup = new Vector<Vector<Integer>>();
			for(int i=0; i<firstLCS.size(); i++){
				Vector<Integer> sameLCS = new Vector<Integer>();
				Vector<Integer> sameLCS2 = new Vector<Integer>();	// �ߺ����ź���
			
				// ���� �� ����
				sameLCS.addElement(firstNodeNumber.get(firstLCS.elementAt(i)));
				sameLCS.addElement(secondNodeNumber.get(firstLCS.elementAt(i)));
				sameLCS.addElement(firstNodeNumber.get(secondLCS.elementAt(i)));
				sameLCS.addElement(secondNodeNumber.get(secondLCS.elementAt(i)));
				sameLCS.sort(null);
			
				// �ߺ�����
				for(int j=0; j<sameLCS.size(); j++){
					if(j==0)
						sameLCS2.addElement(sameLCS.get(j));
					else{
						if(sameLCS.get(j-1)==sameLCS.get(j)){}
						else
							sameLCS2.addElement(sameLCS.get(j));
					}
				}
			
				// �� �� ����
				if(i==0)
					sameLCSGroup.addElement(sameLCS2);
				else{
					// ��
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
				
					// ����
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
						
						// ���� ���� �� ��ġ
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
			
			
			// ��� ��� ���� �� ��� //
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
		// ���� ���� ��� �κ�																												 //
		//============================================================================================================================== //
		
		int lotation = 0;
		while(lotation!=print.size()){
			Vector<Integer> printN = print.get(lotation);
			lotation++;
				
			FileInputStream file = null;
			XSSFWorkbook workbook = null;
			
			XSSFWorkbook workbook2 = new XSSFWorkbook();
			XSSFSheet sheet2 = workbook2.createSheet("���");
			XSSFRow row2 = null;
			XSSFCell cell2 = null;
			int l = 0;
			int k = 0; // ���ͳ� ��� �ε���
			
			
			int overfile = 0;
			for(int y=0; y<appLogFile.size(); y++){
				
				// ���� �Է�
				try {
					file = new FileInputStream(appLogFile.get(y));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// ���� ���� �Է�	
				try {
					workbook = new XSSFWorkbook(file);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// ���� ���ϳ� (ù��°)��Ʈ ��������
				XSSFSheet sheet = workbook.getSheetAt(0);


				for(int i=overfile; i<list.getListLength(); i++){		// i : ����Ʈ�� ��� �ε���
					if(k<printN.size() && i==(int)printN.get(k)){ 		// ��� ��� ����
						
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
									if (value3.equalsIgnoreCase("false")) // ��ĭ�϶�
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
			
			} // ~���� �� �ٸ� ���� �ݺ� 
			
			row2 = sheet2.createRow((short)l);
			for(int i=0; i<printN.size(); i++){		
				cell2 = row2.createCell(i);
				cell2.setCellValue(printN.get(i));
			}
			l++;
			
		
			// ���� ���
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
				fileoutputstream = new FileOutputStream(appLogFile.get(0) + " " + printV + " ������� " + ".xlsx");
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
			
			System.out.println(appLogFile + " " + printV + " ������� ��� ����");
			
		}
			
		
		System.out.println("��� ����");
		System.out.println();
		
	}	
		
}
