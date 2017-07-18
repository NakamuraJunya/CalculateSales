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

			File file = new File(args[0],"branch.out");

			br = new BufferedReader(new FileReader(file));

			String a ;

			while((a = br.readLine())!= null){

				String[] Branch = a.split(",",0);
				branchNameMap.put(Branch[0],Branch[1]);
				branchSaleMap.put(Branch[0],0L);

				if(!Branch[0].matches("\\d{3}")){
					System.out.println("支店定義ファイルのフォーマットが不正です");

					return;
				}

			}

		}catch (FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");

			return;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");

		} finally{
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("予期せぬエラーが発生しました");
				}
		}
		//商品定義ファイル
		try{

			File file = new File(args[0],"commodity.lst");

			br = new BufferedReader(new FileReader(file));

			String a ;

			while((a = br.readLine())!= null){

				String[] commodity = a.split(",",0);
				commodityNameMap.put(commodity[0],commodity[1]);
				commoditySaleMap.put(commodity[0],0L);

				if(!commodity[0].matches("\\w{8}")){
					System.out.println("商品定義ファイルのフォーマットが不正です");

					return;
				}
			}

		}catch (FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");

		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("予期せぬエラーが発生しました");
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

				int name=Integer.parseInt(salesList1.get(i).getName().substring(0,8));;

				salesList2.add(name);

			}
		}
		for(int t =0; t<salesList2.size()-1; t++){

			//System.out.println(salesList2.get(1));

			int  name1 = salesList2.get(t+1) - salesList2.get(t);
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

					salesList3.add(List);;

				}

				if (!branchNameMap.containsKey(salesList3.get(0))){
					System.out.println("<" + salesList1.get(i) .getName() +  ">" + "の支店コードが不正です");
					return;
				}

				if (!commodityNameMap.containsKey(salesList3.get(1))){
					System.out.println("<" + salesList1.get(i) .getName() +  ">" + "の商品コードが不正です");
					return;
				}

				if (salesList3.size() >3){
					System.out.println("<" + salesList1.get(i) .getName() +  ">" + "のフォーマットが不正です");
					return;
				}
				if(!salesList3.get(2).matches("^\\d{0,9}$")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				Long branch=branchSaleMap.get(salesList3.get(0));
				Long commodity=commoditySaleMap.get(salesList3.get(1));

				//System.out.println(commodity += Long.parseLong(salesList2.get(2)));//MapとListの出力確認

				branch += Long.parseLong(salesList3.get(2));
				commodity += Long.parseLong(salesList3.get(2));

				branchSaleMap.put(salesList3.get(0),branch);
				commoditySaleMap.put(salesList3.get(1),commodity);
				//System.out.println(salesList3.get(2));

				if(!salesList3.get(2).matches("\\d{0,10}")){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
			}
		}catch (FileNotFoundException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");

		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("予期せぬエラーが発生しました");
				}
		}
		//支店ならびに商品別集計ファイルの作成
		File newfile = new File(args[0],"branch.out");
		try {
			if (newfile.createNewFile()) {
				System.out.println("ファイルの作成に成功しました。");
			} else {
				System.out.println("ファイルの作成に失敗しました。");
			}
		} catch (IOException e) {
			System.out.println("例外が発生しました。");
			System.out.println(e);
		}
		File newfiles = new File(args[0],"Commodity.out");
		try {
			if (newfiles.createNewFile()) {
				System.out.println("ファイルの作成に成功しました。");
			} else {
				System.out.println("ファイルの作成に失敗しました。");
			}
		} catch (IOException e) {
			System.out.println("例外が発生しました。");
			System.out.println(e);
		}

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
				bw.write(g.getKey() +"," +  branchNameMap.get(g.getKey()) +"," + (g.getValue()) + "\r\n");
			}
			bw.close();

		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");

		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("予期せぬエラーが発生しました");
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
				bw.write(g.getKey() +"," +  commodityNameMap.get(g.getKey()) +"," + (g.getValue()) + "\r\n");
			}
			bw.close();

		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");

		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("予期せぬエラーが発生しました");
				}
		}
	}
}







