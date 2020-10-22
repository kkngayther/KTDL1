import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class main {
	public static String dongLenh = "";
	public static String duongDan = "";
	public static ArrayList<String> cauLenh = new ArrayList<String>();

	public static ArrayList<Integer> arrayIndexNumeric = new ArrayList<Integer>();
	public static ArrayList<Integer> arrayIndexNominal = new ArrayList<Integer>();
	public static String[][] matrix;
	public static int column, row;
	
	public static void main(String[] args) throws IOException{
		Scanner scanDongLenh = new Scanner(System.in);
		System.out.println("Nhap dong lenh: ");
		dongLenh = scanDongLenh.nextLine();
		tachCauLenh();
		//scanDongLenh.close();
		
		Scanner scanInput = new Scanner(Paths.get(cauLenh.get(2).trim()), "UTF-8");
		ArrayList<String> array = new ArrayList<String>();
		int start, end;
		int w, h = 0;
		column = 1;
		while(scanInput.hasNextLine()) {
			array.add(scanInput.nextLine());
		}
		scanInput.close();
		
		for (int i = 0; i < array.get(0).length(); i++) {
			if (array.get(0).charAt(i) == ',') {
				column++;
			}
		}
		row = array.size();
		matrix = new String[row][column];
		for (int i = 0; i < row; i++) {
			end = -1;
			w = 0;
			for (int j = 0; j < array.get(i).length(); j++) {
				if (array.get(i).charAt(j) == ',') {
					start = end + 1;
					end = j;
					matrix[h][w] = array.get(i).substring(start, end);
					w++;
				}
			}
			matrix[h][w] = array.get(i).substring(end + 1);
			h++;
		}
		kieuDuLieu();

		PrintWriter printWriter;
		
		switch (cauLenh.get(1).trim()) {
			case "summary":
				duongDan = cauLenh.get(4).trim();
				printWriter = new PrintWriter(duongDan, "UTF-8");
				printWriter.println(summary());
				printWriter.close();
				break;
			case "replace":
				duongDan = cauLenh.get(4).trim();
				printWriter = new PrintWriter(duongDan, "UTF-8");
				printWriter.println(logReplace());
				printWriter.close();
				
				duongDan = cauLenh.get(3).trim();
				printWriter = new PrintWriter(duongDan, "UTF-8");
				printWriter.println(outReplace());
				printWriter.close();
				
				break;
			case "discretize":
		
				break;
			case "normalize":
				Scanner scanner = new Scanner(System.in);
				phuongPhapChuanHoa();
				int chon = scanner.nextInt();
				if (chon == 1) {
					duongDan = cauLenh.get(3).trim();
					printWriter = new PrintWriter(duongDan, "UTF-8");
					printWriter.println(chuanHoaMinMax());
					printWriter.close();
				}
				else if (chon == 2) {
					duongDan = cauLenh.get(3).trim();
					printWriter = new PrintWriter(duongDan, "UTF-8");
					printWriter.println(chuanHoaZScore());
					printWriter.close();
				}
				break;
		}	
	}
	
	public static void kieuDuLieu() {
		for (int j = 0; j < column; j++) 
			for (int i = 1; i < row; i++) {
				if (!matrix[i][j].equalsIgnoreCase("?")) {
					if (kiemTraNumeric(matrix[i][j])) {
						arrayIndexNumeric.add(j);
						break;
					}
					else {
						arrayIndexNominal.add(j);
						break;
					}
				}
			}
	}
	
	public static void tachCauLenh() {
		int end = 0;
		for (int i = 0; i < dongLenh.length(); i++) {
			if (dongLenh.charAt(i) == ' ') {
				int start = end;
				end = i + 1;
				cauLenh.add(dongLenh.substring(start, end));
			}
		}
		cauLenh.add(dongLenh.substring(end));
	}
	
	public static void xuat() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				System.out.print(matrix[i][j] + '\t');
			}
			System.out.println();
		}
	}
	// 1
	public static String xuatKieuDuLieu(int index) {
		String ketQua = "";
		for (int j = 0; j < row; j++) {
			if (!matrix[j][index].equalsIgnoreCase("?")) {
				if (kiemTraNumeric(matrix[j][index])) {
					ketQua = "Numeric";
				}
				else {
					ketQua = "Nominal";
				}
			}
		}
		return ketQua;
	}
	public static boolean kiemTraNumeric(String temp) {
		try {
			Double num = Double.parseDouble(temp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static String summary() {
		String summary = "";
		summary += "# Số mẫu: " + (row - 1) + '\n'
				 + "# Số thuộc tính: " + column + '\n';
		
		for (int i = 0; i < column; i++) {
			summary += "# thuộc tính " + (i + 1) + ": " + matrix[0][i] + '\t' + xuatKieuDuLieu(i) + '\n';
		}
		return summary;
	}

	// 2.
	public static void replaceNominal() {
		for (int i = 0; i < arrayIndexNominal.size(); i++) {
			int index = arrayIndexNominal.get(i);
			String rep = timGiaTriXuatHienNhieuNhat(index); 
			for (int j = 1; j < row; j++) {
				if (matrix[j][index].equalsIgnoreCase("?")) {
					matrix[j][index] = rep;
				}
			}
		}
	}	
	public static String timGiaTriXuatHienNhieuNhat(int index) {
		String ketQua = "";
		int count, countKQ = 0;
		
		for (int i = 1; i < row; i++) {
			if (!matrix[i][index].equalsIgnoreCase("?") && i < row - 1) {
				count = 1;
				for (int j = i + 1; j < row; j++) {
					if (matrix[i][index].equalsIgnoreCase(matrix[j][index])) {
						count++;
					}
				}
				if (count > countKQ) {
					countKQ = count;
					ketQua = matrix[i][index];
				}
			}
			else if (i == row - 1){
				count = 1;
				if (count > countKQ) {
					countKQ = count;
					ketQua = matrix[i][index];
				}
			}
		}
		return ketQua;
	}
	
	public static void replaceNumeric() {
		for (int i = 0; i < arrayIndexNumeric.size(); i++) {
			int index = arrayIndexNumeric.get(i);
			double rep = timGiaTriTrungBinh(index);
			for (int j = 1; j < row; j++) {
				if (matrix[j][index].equalsIgnoreCase("?")) {
					matrix[j][index] = Double.toString(rep);
				}
			}
		}

	}
	public static double timGiaTriTrungBinh(int index) {
		double ketQua = 0;
		double sum = 0;
		int count = 0;
		for (int i = 1; i < row; i++) {
			if (!matrix[i][index].equalsIgnoreCase("?")) {
				sum += Double.parseDouble(matrix[i][index]);
				count++;
			}
		}
		ketQua = (double)Math.round((sum / count) * 100)/100;
		return ketQua;
	}
	
	public static String outReplace() {
		replaceNominal();
		replaceNumeric();
		String ketQua = "";
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (j < column - 1) {
					ketQua += matrix[i][j] + ",";
				}
				else {
					ketQua += matrix[i][j];
				}
			}
			ketQua += '\n';
		}
		return ketQua;
	}
	public static String logReplace() {
		String ketQuaNumeric = "";
		for (int j = 0; j < arrayIndexNumeric.size(); j++) {
			int count = 0;
			int index = arrayIndexNumeric.get(j);
			for (int i = 1; i < row; i++) {
				if (matrix[i][index].equalsIgnoreCase("?")) {
					if (count == 0) {
						ketQuaNumeric += "# thuộc tính: " + matrix[0][index];
					}
					count++;
				}
			}
			if (ketQuaNumeric.equalsIgnoreCase("")) {	
			}
			else {
				ketQuaNumeric += ", " + count + ", " + timGiaTriTrungBinh(index) + '\n';
			}
		}
		String ketQuaNominal = "";
		for (int j = 0; j < arrayIndexNominal.size(); j++) {
			int count = 0;
			int index = arrayIndexNominal.get(j);
			for (int i = 1; i < row; i++) {
				if (matrix[i][index].equalsIgnoreCase("?")) {
					if (count == 0) {
						ketQuaNominal += "# thuộc tính: " + matrix[0][index];
					}
					count++;
				}
			}
			if (ketQuaNominal.equalsIgnoreCase("")) {	
			}
			else {
				ketQuaNominal += ", " + count + ", " + timGiaTriXuatHienNhieuNhat(index) + '\n';
			}
		}
		return ketQuaNumeric + ketQuaNominal;
	}
	// 3.
	
	
	
	// 4.
	public static String chuanHoaMinMax() {
		for (int j = 0; j < arrayIndexNumeric.size(); j++) {
			int index = arrayIndexNumeric.get(j);
			double numMin = timMin(index);
			double numMax = timMax(index);
			for (int i = 1; i < row; i++) {
				double temp = Double.parseDouble(matrix[i][index]);
				temp = (temp - numMin) / (numMax - numMin);
				temp = (double)Math.round(temp * 100)/100;
				matrix[i][index] = Double.toString(temp);
			}
		}
		String ketQua = "";
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (j < column - 1) {
					ketQua += matrix[i][j] + ",";
				}
				else {
					ketQua += matrix[i][j];
				}
			}
			ketQua += '\n';
		}
		return ketQua;
	}
	public static double timMin(int index) {
		double ketQua = Double.parseDouble(matrix[1][index]);
		for (int i = 2; i < row; i++)
			if (Double.parseDouble(matrix[i][index]) < ketQua)
				ketQua = Double.parseDouble(matrix[i][index]);
		return ketQua;
	}
	public static double timMax(int index) {
		double ketQua = Double.parseDouble(matrix[1][index]);
		for (int i = 2; i < row; i++)
			if (Double.parseDouble(matrix[i][index]) > ketQua)
				ketQua = Double.parseDouble(matrix[i][index]);
		return ketQua;
	}

	public static String chuanHoaZScore() {
		for (int j = 0; j < arrayIndexNumeric.size(); j++) {
			int index = arrayIndexNumeric.get(j);
			double giaTri = tinhDoLechChuan(index);
			double trungBinh = timGiaTriTrungBinh(index);
			for (int i = 1; i < row; i++) {
				double temp = Double.parseDouble(matrix[i][index]);
				temp = (temp - trungBinh) / giaTri;
				temp = (double)Math.round(temp * 100)/100;
				matrix[i][index] = Double.toString(temp);
			}
		}
		String ketQua = "";
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (j < column - 1) {
					ketQua += matrix[i][j] + ",";
				}
				else {
					ketQua += matrix[i][j];
				}
			}
			ketQua += '\n';
		}
		return ketQua;
	}
	public static double tinhDoLechChuan(int index) {
		double ketQua = 0;
		double sum = 0;
		double trungBinh = timGiaTriTrungBinh(index);
		for (int i = 1; i < row; i++) {
			double temp = Double.parseDouble(matrix[i][index]);
			sum += (temp - trungBinh) * (temp - trungBinh);
		}
		sum = sum / (row - 1);
		ketQua = (double)Math.round(sum * 100)/100;
		return ketQua;
	}
	
	public static void phuongPhapChuanHoa() {
		System.out.println("Chon phuong phap chuan hoa: ");
		System.out.println("1: Min-max");
		System.out.println("2: Z-score");
	}
}
