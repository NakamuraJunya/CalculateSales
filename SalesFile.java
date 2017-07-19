package jp.alhinc.nakamura_junya.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class SalesFile {
	public static void main(String args[]) {

		if (args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//Map一覧
		HashMap< String , String> branchNameMap = new HashMap < String , String>();
		HashMap< String , String> commodityNameMap = new HashMap < String , String>();
		HashMap< String , Long> branchSaleMap = new HashMap < String , Long>();
		HashMap< String , Long> commoditySaleMap = new HashMap < String , Long>();

		BufferedReader br = null;
		BufferedWriter bw = null;

		//支店定義ファイル
		try{

			File file = new File(args[0],"branch.lst");

			if (!file.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return;
			}

			br = new BufferedReader(new FileReader(file));

			String branchreadfile;

			while((branchreadfile = br.readLine())!= null){

				String[] branch = branchreadfile.split(",");

				if(!branch[0].matches("\\d{3}")||(branch.length !=2)){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branchNameMap.put(branch[0],branch[1]);
				branchSaleMap.put(branch[0],0L);
			}

		}catch (FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally{
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}
		//商品定義ファイル
		try{

			File file = new File(args[0],"commodity.lst");
			if (!file.exists()) {
				System.out.println("商品定義ファイルが存在しません");
				return;
			}

			br = new BufferedReader(new FileReader(file));

			String commodityreadfile ;

			while((commodityreadfile = br.readLine())!= null){

				String[] commodity = commodityreadfile.split(",");

				if(!commodity[0].matches("\\w{8}")||(commodity.length !=2)){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityNameMap.put(commodity[0],commodity[1]);
				commoditySaleMap.put(commodity[0],0L);
			}

		}catch (FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}

		//売上集計リスト
		File rcdCord = new File(args[0]);
		File files[] = rcdCord.listFiles();
		ArrayList <File> salesList1 = new ArrayList<File>();
		ArrayList <Integer> salesList2 = new ArrayList<Integer>();

		for (int i=0; i<files.length; i++) {

			if(files[i].getName().matches("\\d{8}.rcd$")&&(files[i].isFile())){

				salesList1.add(files[i]);

				int name=Integer.parseInt(files[i].getName().substring(0,8));

				salesList2.add(name);

			}
		}
		for(int i =0; i<salesList2.size()-1; i++){

			//System.out.println(salesList2.get(1));

			int  name1 = salesList2.get(i+1) - salesList2.get(i);
			if( name1!=1 ){
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}
		try{
			for (int i=0; i<salesList1.size(); i++) {

				br = new BufferedReader(new FileReader(salesList1.get(i)));

				String List;
				ArrayList <String> salesList3 = new ArrayList<String>();

				while ((List = br.readLine()) != null) {

					salesList3.add(List);
				}
				if (salesList3.size()!=3){
					System.out.println(salesList1.get(i) .getName() + "のフォーマットが不正です");
					return;
				}
				if(!(salesList3.get(2).matches("[0-9]*"))){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				if (!branchNameMap.containsKey(salesList3.get(0))){
					System.out.println(salesList1.get(i) .getName() + "の支店コードが不正です");
					return;
				}

				if (!commodityNameMap.containsKey(salesList3.get(1))){
					System.out.println(salesList1.get(i) .getName() + "の商品コードが不正です");
					return;
				}

				Long branch=branchSaleMap.get(salesList3.get(0));
				Long commodity=commoditySaleMap.get(salesList3.get(1));

				//System.out.println(commodity += Long.parseLong(salesList2.get(2)));//MapとListの出力確認

				branch += Long.parseLong(salesList3.get(2));
				commodity += Long.parseLong(salesList3.get(2));

				if(!(branch<=9999999999L)||!(commodity<=9999999999L)){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				branchSaleMap.put(salesList3.get(0),branch);
				commoditySaleMap.put(salesList3.get(1),commodity);
				//System.out.println(salesList3.get(2));
			}
		}catch (FileNotFoundException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}
		//支店ならびに商品別集計ファイルの作成
		File newfile = new File(args[0],"branch.out");

		File newfiles = new File(args[0],"commodity.out");

		List<Map.Entry<String,Long>> branchentries =new ArrayList<Map.Entry<String,Long>>(branchSaleMap.entrySet());

		Collections.sort(branchentries, new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());}

		});

		//支店別集計ファイルの出力
		try{

			File file = new File(args[0],"branch.out");
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Entry<String,Long> g : branchentries) {
				bw.write(g.getKey() +"," +  branchNameMap.get(g.getKey()) +"," + (g.getValue()));
				bw.newLine();
			}
			bw.close();

		}catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}

		List<Map.Entry<String,Long>> commodityentries =new ArrayList<Map.Entry<String,Long>>(commoditySaleMap.entrySet());

		Collections.sort(commodityentries, new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());}

		});

		//商品別集計ファイルの出力
		try{

			File file = new File(args[0],"Commodity.out");
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Entry<String,Long> g : commodityentries) {
				bw.write(g.getKey() +"," +  commodityNameMap.get(g.getKey()) +"," + (g.getValue()));
				bw.newLine();
			}
			bw.close();

		}catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
		}
	}
}



